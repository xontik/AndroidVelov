package fr.iutlyon1.androidvelov.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fr.iutlyon1.androidvelov.StationListAdapter;
import fr.iutlyon1.androidvelov.model.VelovData;

public class VelovRequest extends AsyncTask<StationListAdapter, Void, VelovData> {
    private static final String API_URL = "https://api.jcdecaux.com/vls/v1/stations";

    private StationListAdapter adapter;

    private final String contract;
    private final String apiKey;

    public VelovRequest(String contract, String apiKey) {
        this.contract = contract;
        this.apiKey = apiKey;
    }

    @Override
    protected VelovData doInBackground(StationListAdapter... adapters) {
        this.adapter = adapters[0];

        URL url;
        HttpsURLConnection urlConnection = null;

        VelovData velovData = null;

        try {
            url = buildURL(API_URL, "contract", contract, "apiKey", apiKey);
            if (url == null) {
                return null;
            }
            urlConnection = (HttpsURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                VelovParser parser = new VelovParser(reader);

                velovData = parser.parse();
            }

            urlConnection.disconnect();
        } catch (IOException|JSONException e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            e.printStackTrace();
            Log.e("VelovRequest", e.getMessage());
        }

        return velovData;
    }

    @Override
    protected void onPostExecute(VelovData velovData) {
        if (velovData == null) {
            return;
        }

        this.adapter.setItems(velovData);
    }

    private URL buildURL(String url, String... parameters) {
        final StringBuilder builder = new StringBuilder();

        builder.append(url);
        if (parameters.length >= 2) {
            builder.append('?');

            builder.append(parameters[0]);
            builder.append('=');
            builder.append(parameters[1]);

            for (int i = 2; i < parameters.length; i += 2) {
                builder.append('&');
                builder.append(parameters[i]);
                builder.append('=');
                builder.append(parameters[i+1]);
            }
        }

        try {
            return new URL(builder.toString());
        } catch (MalformedURLException e) {
            return null;
        }

    }
}
