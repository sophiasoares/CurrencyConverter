package de.thu.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        ExchangeRateDatabase myCurrencies = new ExchangeRateDatabase();

        CurrencyListAdapter adapter = new CurrencyListAdapter(myCurrencies, R.layout.list_view_item);
        ListView listView = (ListView) findViewById(R.id.activity_currency_list);
        listView.setAdapter(adapter);

    }
}