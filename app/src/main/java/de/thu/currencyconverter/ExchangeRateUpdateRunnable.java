package de.thu.currencyconverter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ExchangeRateUpdateRunnable implements Runnable {

    ExchangeRateDatabase myExchangeRateDatabase;
    private UpdateNotifier notifier;
    private Activity activity;

    public ExchangeRateUpdateRunnable(ExchangeRateDatabase myExchangeRateDatabase, Activity activity) {
        this.myExchangeRateDatabase = myExchangeRateDatabase;
        this.activity = activity;
        this.notifier = new UpdateNotifier(activity);
    }

    // When the thread starts, run this code
    @Override
    public void run() {
        // Synchronize because of concurrency problems
        synchronized (ExchangeRateUpdateRunnable.this) {
            updateCurrencies();
            notifier.showOrUpdateNotification(0);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(activity, "Currencies update finished!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }


    // Update the currency rates based on the website
    synchronized void updateCurrencies() {
        try {
            // Address as object of type URL
            URL u = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

            //Open connection to server
            URLConnection connection = u.openConnection();

            // Get InputStream for URLConnection
            InputStream inStream = connection.getInputStream();

            // Get character encoding (f.e. "UTF-8") :
            String encoding = connection.getContentEncoding();

            // Create parser from InputStream inStream. Parser runs through document following
            // its parts (elements, text blocks) and stops on "events" (f.e. opening or closing tag)
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inStream, encoding);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("Cube".equals(parser.getName())) {  // "Cube" because it is the name of the field in the XML

                        // Get the values from each attribute
                        String currency = parser.getAttributeValue(null, "currency");

                        // If it is null it is because this "Cube" field does not contain the wanted information
                        if (currency != null) {
                            String rateString = parser.getAttributeValue(null, "rate");

                            // Convert the rate to double
                            double rate = Double.parseDouble(rateString);

                            // Set the exchange rate with the new data
                            myExchangeRateDatabase.setExchangeRate(currency, rate);
                        }
                    }
                }
                // Go to the next search
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("Update Currencies", "Can't query database!");
            e.printStackTrace();
        }
    }

}
