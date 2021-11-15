package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ExchangeRateDatabase myExchangeRateDatabase = new ExchangeRateDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use my custom adapter to store the currencies and pass the desired layout for the items as a parameter
        BaseAdapter adapter = new CurrencyListAdapter(myExchangeRateDatabase, R.layout.spinner_item);

        // Set spinners
        Spinner spinnerFromValue = (Spinner) findViewById(R.id.fromValueSpinner);
        spinnerFromValue.setAdapter(adapter);
        spinnerFromValue.setOnItemSelectedListener(this);

        Spinner spinnerToValue = (Spinner) findViewById(R.id.toValueSpinner);
        spinnerToValue.setAdapter(adapter);
        spinnerToValue.setOnItemSelectedListener(this);

        // Set Text Views
        setMyTextViews (spinnerFromValue, spinnerToValue);

        // Per default Android does not allow access to files and network from
        // the GUI thread. So, deactivate control mechanism
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                // Refresh the rates
                Log.e("1", "TO AQUI NO SWITCH CASE");
                updateCurrencies();
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
    public void onNothingSelected(AdapterView<?> adapterView) { }

    // Calculate the conversion value from/to the selected currencies
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

    // Update the currency rates based on the website
    void updateCurrencies () {
        Log.e("2", "ENTREI NO UPDATE CURRENCIES");
        try {
            // Address as object of type URL
            URL u = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

            //Open connection to server:
            URLConnection connection = u.openConnection();

            // Get InputStream for URLConnection:
            InputStream inStream = connection.getInputStream();

            // Get character encoding (f.e. "UTF-8") :
            String encoding = connection.getContentEncoding();

            // Create parser from InputStream inStream. Parser runs through document following
            // its parts (elements, text blocks) and stops on „events“ (f.e. opening or closing tag)
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inStream, encoding);

            int eventType = parser.getEventType();

            Log.e("3", "FIZ A CONNECTION");

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("Cube".equals(parser.getName())) {  // "Cube" because it is the name of the field in the XML

                        // Get the values from each attribute
                        String currency = parser.getAttributeValue(null, "currency");

                        // If it is null it is because this "Cube" field does not contain the wanted information
                        if(currency != null) {
                            String rateString = parser.getAttributeValue(null, "rate");

                            // Convert the rate to double
                            double rate = Double.parseDouble(rateString);

                            // Set the exchange rate with the new data
                            myExchangeRateDatabase.setExchangeRate(currency, rate);

                            Log.e("8", "SAI DO SET EXCHANGE");
                        }
                    }
                }
                eventType = parser.next();
                Log.e("9", "PASSEI PRO PROXIMO COM PARSER.NEXT");
            }
        } catch (Exception e) {
            Log.e("Update Currencies", "Can't query database!");
            e.printStackTrace();
        }
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