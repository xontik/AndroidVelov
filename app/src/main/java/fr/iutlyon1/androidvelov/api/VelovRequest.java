package fr.iutlyon1.androidvelov.api;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fr.iutlyon1.androidvelov.StationListAdapter;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class VelovRequest extends AsyncTask<Object, Void, VelovData> {
    private static final String API_URL = "https://api.jcdecaux.com/vls/v1/stations";

    private Object[] update;

    private final String contract;
    private final String apiKey;

    public VelovRequest(String contract, String apiKey) {
        this.contract = contract;
        this.apiKey = apiKey;
    }

    @Override
    protected VelovData doInBackground(Object... objects) {
        this.update = objects;

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

        for (Object o : update) {
            if (o instanceof StationListAdapter) {
                updateStationListAdapter((StationListAdapter) o, velovData);
            } else if (o instanceof GoogleMap) {
                updateGoogleMap((GoogleMap) o, velovData);
            } else if (o instanceof AutoCompleteTextView) {
                updateAutocompleteTextView((AutoCompleteTextView) o, velovData);
            }
        }
    }

    private void updateStationListAdapter(StationListAdapter adapter, VelovData data) {
        adapter.setItems(data);
    }

    private void updateGoogleMap(GoogleMap map, VelovData data) {
        for (VelovStationData station : data) {
            MarkerOptions marker = new MarkerOptions()
                    .position(station.getPosition())
                    .title(station.getName());

            // Si aucun stand de vélo n'est disponible, on change la couleur
            if (station.getAvailableBikeStands() == 0) {
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            map.addMarker(marker)
                .setTag(station.getNumber());
        }

        LatLng lyon = new LatLng(45.756633, 4.838630);
        map.moveCamera(CameraUpdateFactory.newLatLng(lyon));
        map.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    private void updateAutocompleteTextView(AutoCompleteTextView view, VelovData data) {
        String[] names = new String[data.size()];
        for (int i = 0, length = data.size(); i < length; i++) {
            names[i] = data.get(i).getFullName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_list_item_1,
                names);
        view.setAdapter(adapter);
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
