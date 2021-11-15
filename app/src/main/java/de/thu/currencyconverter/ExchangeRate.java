package de.thu.currencyconverter;

import android.util.Log;

public class ExchangeRate {
    private String currencyName;
    private double rateForOneEuro;
    private String capital;

    public ExchangeRate(String currencyName, String capital, double rateForOneEuro) {
        this.currencyName = currencyName;
        this.rateForOneEuro = rateForOneEuro;
        this.capital = capital;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCapital() {
        return capital;
    }

    public double getRateForOneEuro() {
        return rateForOneEuro;
    }

    public void setRateForOneEuro(double rateForOneEuro) {
        Log.e("7", "CHEGUEI AQUI NO SET RATE FOR ONE EURO");
        this.rateForOneEuro = rateForOneEuro;
    }
}
