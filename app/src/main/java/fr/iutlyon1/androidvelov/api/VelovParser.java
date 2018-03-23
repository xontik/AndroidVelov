package fr.iutlyon1.androidvelov.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class VelovParser {

    private Reader in;

    VelovParser(Reader in) {
        this.in = in;
    }

    private String read() throws IOException {
        BufferedReader in = new BufferedReader(this.in);
        StringBuilder builder = new StringBuilder();

        String aux;
        while ((aux = in.readLine()) != null) {
            builder.append(aux);
        }

        return builder.toString();
    }

    public VelovData parse() throws IOException, JSONException {
        final String documentString = read();

        final JSONArray document = new JSONArray(documentString);
        final VelovData stationList = new VelovData();

        for (int i = 0, length = document.length(); i < length; i++) {
            VelovStationData station = parseStation(document.getJSONObject(i));
            stationList.add(station);
        }

        return stationList;
    }

    private VelovStationData parseStation(JSONObject object) {
        int number = object.optInt("number", -1);
        String name = object.optString("name");
        String address = object.optString("address");
        VelovStationData.Position position = parsePosition(object.optJSONObject("position"));
        boolean banking = object.optBoolean("banking");
        boolean bonus = object.optBoolean("bonus");
        String status = object.optString("status");
        String contractName = object.optString("contract_name");
        int bikeStands = object.optInt("bike_stands");
        int availableBikeStands = object.optInt("available_bike_stands");
        int availableBikes = object.optInt("available_bikes");
        long lastUpdate = object.optLong("last_update");

        return new VelovStationData(number, name, address,
                position, banking, bonus, status, contractName, bikeStands,
                availableBikeStands, availableBikes, lastUpdate);
    }

    private VelovStationData.Position parsePosition(JSONObject json) {
        double lat = json.optDouble("lat");
        double lng = json.optLong("lng");

        return new VelovStationData.Position(lat, lng);
    }
}
