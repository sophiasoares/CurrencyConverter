package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerFromValue;
    Spinner spinnerToValue;
    EditText calculationValue;
    ExchangeRateDatabase myExchangeRateDatabase = new ExchangeRateDatabase();
    ExchangeRateUpdateRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use my custom adapter to store the currencies and pass the desired layout for the items as a parameter
        BaseAdapter adapter = new CurrencyListAdapter(myExchangeRateDatabase, R.layout.spinner_item);

        // Set spinners
        spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        spinnerFromValue.setAdapter(adapter);
        spinnerFromValue.setOnItemSelectedListener(this);

        spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        spinnerToValue.setAdapter(adapter);
        spinnerToValue.setOnItemSelectedListener(this);

        // Initialise the calculation edit text (calculate button)
        calculationValue = (EditText) findViewById(R.id.decimalNumber);

        // Set Text Views
        setMyTextViews (spinnerFromValue, spinnerToValue);

        // Per default Android does not allow access to files and network from the GUI thread. So, deactivate control mechanism.
        // It is now commented out because we are using threads to update the currency
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
    }

    ShareActionProvider shareActionProvider;

    // Set menu resource for toolbar and add share button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareText(null);
        return true;
    }

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    // Implement onOptionsItemSelected for the menu
    // Tell the menu what to do when each of the entries is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_menu_entry_currencyList:
                // Take me to the Currency List
                Intent intent = new Intent(MainActivity.this, CurrencyListActivity.class);

                // Check before starting the activity to see if Intent can be resolved
                if(intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
                return true;

            case R.id.my_menu_entry_refreshRates:
                // Start the thread and refresh the rates
                runnable = new ExchangeRateUpdateRunnable(myExchangeRateDatabase, this);
                new Thread(runnable).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

        // Set Text Views
        // Reassign the spinners in case of a currency refreshment
        spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        setMyTextViews (spinnerFromValue, spinnerToValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    // Calculate the conversion value from/to the selected currencies
    public void myCalculateButton (View view) {
        // Get selected currencies
        // Reassign the spinners in case of a currency refreshment or a retrieve
        spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        String fromCurrency = spinnerFromValue.getSelectedItem().toString();
        spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        String toCurrency = spinnerToValue.getSelectedItem().toString();

        // Convert the amount typed in the EditText to double
        double myDecimalValue;
        if(calculationValue.getText().toString().equals("")) {
            myDecimalValue = 0.0;
        } else {
            myDecimalValue = Double.parseDouble(calculationValue.getText().toString());
        }

        // Convert the values using the method provided by ExchangeRateDatabase class
        double convertedValue = myExchangeRateDatabase.convert(myDecimalValue, fromCurrency, toCurrency);

        // Show result with 2 decimal places
        TextView result = (TextView) findViewById(R.id.convertedValue);
        result.setText(String.format("%.2f",convertedValue));
    }

    // Set the text views of the rates with the new selected rates
    void setMyTextViews (Spinner spinnerFromValue, Spinner spinnerToValue) {
        TextView fromValueText = (TextView) findViewById(R.id.fromValue);
        fromValueText.setText("From value in " + spinnerFromValue.getSelectedItem().toString());

        TextView toValueText = (TextView) findViewById(R.id.toValue);
        toValueText.setText("To value in " + spinnerToValue.getSelectedItem().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Obtain preferences - Alternative: getSharedPreferences
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        // Get editing access
        SharedPreferences.Editor editor = prefs.edit();

        String sourceCurrency = spinnerFromValue.getSelectedItem().toString();
        String targetCurrency = spinnerToValue.getSelectedItem().toString();
        String enteredValue = calculationValue.getText().toString();

        // Store key-value-pair
        editor.putString("Source Currency", sourceCurrency);
        editor.putString("Target Currency", targetCurrency);
        editor.putString("Entered Value", enteredValue);


        // Store currencies
        // Currencies as keys and rates as values
        for (String currency : myExchangeRateDatabase.getCurrencies()) {
            String rate = String.valueOf(myExchangeRateDatabase.getExchangeRate(currency));
            editor.putString(currency, rate);
        }

        // Persist data in XML file
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        // Retrieve value from preferences
        String sourceCurrency = prefs.getString("Source Currency", null);
        if(sourceCurrency != null) {
            setSpinText(spinnerFromValue, sourceCurrency);
        }

        String targetCurrency = prefs.getString("Target Currency", null);
        if(targetCurrency != null) {
            setSpinText(spinnerToValue, targetCurrency);
        }

        String enteredValue = prefs.getString("Entered Value", "");
        calculationValue.setText(enteredValue);

        // Retrieve currencies
        for (String currency : myExchangeRateDatabase.getCurrencies()) {
            String rate = prefs.getString(currency, null);
            if(rate != null) {
                myExchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(rate));
            }
        }
    }

    // Set the text of the spinner when I retrieve their value from Preferences
    public void setSpinText(Spinner spin, String text) {
        for(int i= 0; i < spin.getAdapter().getCount(); i++) {
            if(spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }

}
