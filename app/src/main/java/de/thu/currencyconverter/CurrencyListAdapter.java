package de.thu.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrencyListAdapter extends BaseAdapter {

    private ExchangeRateDatabase data;

    // R.layout returns an integer value
    private int chosenLayout;

    public CurrencyListAdapter(ExchangeRateDatabase data, int layout) {
        this.data = data;
        this.chosenLayout = layout;
    }

    @Override
    public int getCount() {
        return data.getCurrencies().length;
    }

    @Override
    public Object getItem(int i) {
        return data.getCurrencies()[i];
        //return data.getExchangeRates()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Set the layout depending on what the constructor receives (chosenLayout)
            view = inflater.inflate(chosenLayout, null, false);
        }

        // Set the view to the name of the selected currency
        String selectedCurrency = data.getCurrencies()[i];
        TextView currencyName = (TextView) view.findViewById(R.id.list_each_currency_name);
        currencyName.setText(selectedCurrency);

        // Set the view to the value of the selected currency
        TextView exchangeRate = (TextView) view.findViewById(R.id.list_each_exchange_rate);
        exchangeRate.setText(String.format("%.2f",data.getExchangeRate(selectedCurrency)));

        // Generate the country name concatenating flag_ with the short name of the currency
        // The flag image resources are named following this schema: flag_<<ShortCodeOfCurrencyInLowerCase>> (f.e. flag_eur)
        String flagName = "flag_" + selectedCurrency.toLowerCase();

        // Get the id of the image by its name
        int imageId = context.getResources().getIdentifier(flagName, "drawable", context.getPackageName());

        // Set the image to the flag of the selected country
        ImageView countryFlag = (ImageView) view.findViewById(R.id.list_each_flag);
        countryFlag.setImageResource(imageId);

        return view;
        // list_each_currency_name ---> nome de cada moeda
        // list_each_exchange_rate ---> valor de cada moeda

    }
}
