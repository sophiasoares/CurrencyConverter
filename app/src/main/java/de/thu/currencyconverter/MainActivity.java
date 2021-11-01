package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExchangeRateDatabase myCurrencies = new ExchangeRateDatabase();

        // Use my custom adapter to store the currencies and pass the desired layout for the items as a parameter
        BaseAdapter adapter = new CurrencyListAdapter(myCurrencies, R.layout.spinner_item);

        // Set spinners
        Spinner spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        spinnerFromValue.setAdapter(adapter);
        spinnerFromValue.setOnItemSelectedListener(this);

        Spinner spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        spinnerToValue.setAdapter(adapter);
        spinnerToValue.setOnItemSelectedListener(this);

        // Set Text Views
        setMyTextViews (spinnerFromValue, spinnerToValue);

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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_menu_entry:
                // Take me to the Currency List
                Intent intent = new Intent(MainActivity.this, CurrencyListActivity.class);

                // Check before starting the activity to see if Intent can be resolved
                if(intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
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
        Spinner spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        Spinner spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        setMyTextViews (spinnerFromValue, spinnerToValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void myCalculateButton (View view) {

        // Get selected currencies
        Spinner spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        String fromCurrency = spinnerFromValue.getSelectedItem().toString();
        Spinner spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        String toCurrency = spinnerToValue.getSelectedItem().toString();

        // Convert the amount typed in the EditText to double
        EditText decimalValue = (EditText) findViewById(R.id.decimalNumber);
        double myDecimalValue;
        if(decimalValue.getText().toString().equals("")) {
            myDecimalValue = 0.0;
        } else {
            myDecimalValue = Double.parseDouble(decimalValue.getText().toString());
        }

        ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();

        // Convert the values using the method provided by ExchangeRateDatabase class
        double convertedValue = exchangeRateDatabase.convert(myDecimalValue, fromCurrency, toCurrency);

        // Show result with 2 decimal places
        TextView result = (TextView) findViewById(R.id.convertedValue);
        result.setText(String.format("%.2f",convertedValue));

    }

    void setMyTextViews (Spinner spinnerFromValue, Spinner spinnerToValue) {
        TextView fromValueText = (TextView) findViewById(R.id.fromValue);
        fromValueText.setText("From value in " + spinnerFromValue.getSelectedItem().toString());

        TextView toValueText = (TextView) findViewById(R.id.toValue);
        toValueText.setText("To value in " + spinnerToValue.getSelectedItem().toString());
    }

}

// ---- Old pieces of code in case I need them ---------

//ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_view_item, R.id.text_view, currencyName);
//ListView listView = (ListView)findViewById(R.id.my_list_view);
//listView.setAdapter(adapter);

//String[] currencyName = myCurrencies.getCurrencies();

//ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.currencyName, android.R.layout.simple_spinner_item);
//ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.list_view_item, R.id.text_view, currencyName);

//double fromValueRate = exchangeRateDatabase.getExchangeRate(spinnerFromValue.getSelectedItem().toString());
//double toValueRate = exchangeRateDatabase.getExchangeRate(spinnerToValue.getSelectedItem().toString());

//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//android.R.layout.simple_spinner_dropdown_item

// I am using my own layout for each item in the spinner (spinner_item.xml)
//ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, currencyName);