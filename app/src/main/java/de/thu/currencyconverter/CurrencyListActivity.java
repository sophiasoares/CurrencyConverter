package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        ExchangeRateDatabase myCurrencies = new ExchangeRateDatabase();

        CurrencyListAdapter adapter = new CurrencyListAdapter(myCurrencies, R.layout.list_view_item);
        ListView listView = (ListView) findViewById(R.id.activity_currency_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Another option to get the capital
                //ExchangeRate selectedEntry1 = (ExchangeRate)adapter.getItem(i);
                //String a = selectedEntry1.getCapital();

                // Get the selected item from the list view
                String selectedEntry = (String) listView.getItemAtPosition(i);

                // Convert the currency name to the capital name of the respective country
                String capitalName = ExchangeRateDatabase.getCapital(selectedEntry);

                // Take me to Google Maps
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + capitalName));

                // Check before starting the activity to see if Intent can be resolved
                if(intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            }


        });

    }

    // Set menu resource for toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu_return, menu);
        return true;
    }

    // Implement onOptionsItemSelected for the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_menu_entry_return:
                // Take me back to the Currency Converter
                Intent intent = new Intent(CurrencyListActivity.this, MainActivity.class);

                // Check before starting the activity to see if Intent can be resolved
                if(intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}

// ---- Old pieces of code in case I need them ---------

//String selectedCurrency = data.getCurrencies()[i];
//TextView currencyName = (TextView) view.findViewById(R.id.list_each_currency_name);
//String capitalName = selectedEntry.getCapital();