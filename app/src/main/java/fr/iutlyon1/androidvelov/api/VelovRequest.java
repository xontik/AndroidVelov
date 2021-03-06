package fr.iutlyon1.androidvelov.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import fr.iutlyon1.androidvelov.Props;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.InternetUtils;

public class VelovRequest extends AsyncTask<VelovData, Void, VelovData> {
    public interface OnTaskCompleted {
        void onTaskCompleted(VelovData fetched);
    }

    private static final String TAG = "VelovRequest";
    private static final String API_URL = "https://api.jcdecaux.com/vls/v1/stations";

    private WeakReference<Context> context;
    private final String contract;
    private final String apiKey;
    private OnTaskCompleted listener;

    private VelovData[] datas = null;

    public VelovRequest(Context context, String contract, OnTaskCompleted listener) {
        this.apiKey = Props.getInstance(context).get("API_KEY");

        this.context = new WeakReference<>(context);
        this.contract = contract;
        this.listener = listener;
    }
    
    @Override
    protected VelovData doInBackground(VelovData... datas) {
        this.datas = datas;

        if (!InternetUtils.isNetworkAvailable(context.get())) {
            return null;
        }

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

            Log.e(TAG, "doInBackground: " + e.getMessage(), e);
        }

        return velovData;
    }

    @Override
    protected void onPostExecute(VelovData velovData) {
        if (velovData == null) {
            Context context = this.context.get();
            if (context != null) {
                InternetUtils.showConnectionDialog(context);
            }

            if (listener != null) {
                listener.onTaskCompleted(null);
            }
            return;
        }

        List<VelovStationData> stations = velovData.getStations();

        Context context = this.context.get();
        if (context != null) {
            VelovData.processFavorites(this.context.get(), stations);
        }

        for (VelovData data : this.datas) {
            data.setAll(stations);
        }

        if (listener != null) {
            listener.onTaskCompleted(velovData);
        }
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
