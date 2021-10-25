package de.thu.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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