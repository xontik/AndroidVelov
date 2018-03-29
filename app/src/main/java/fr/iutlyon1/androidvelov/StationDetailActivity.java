package fr.iutlyon1.androidvelov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import java.util.Locale;

import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationDetailActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_detail);

        VelovStationData d = (VelovStationData) getIntent().getSerializableExtra("station");
        String data = String.format(Locale.FRANCE, "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "    <body>\n" +
                        "        <h1>%s#%s</h1>\n" +
                        "        <ul>\n" +
                        "            <li><strong>Emplacement : </strong>%s</li>\n" +
                        "            <li><strong>Latitude : </strong>%f</li>\n" +
                        "            <li><strong>Longitude : </strong>%f</li>\n" +
                        "            <li><strong>Moyen de paiment :</strong>%s</li>\n" +
                        "            <li><strong>Bonus : </strong>%s</li>\n" +
                        "            <li><strong>Etat : </strong>%s</li>\n" +
                        "            <li><strong>Ville : </strong>%s</li>\n" +
                        "            <li><strong>Nombre d'empalcements : </strong>%s</li>\n" +
                        "            <li><strong>Vélos disponibles : </strong>%s</li>\n" +
                        "            <li><strong>Emplacements disponibles : </strong>%s</li>\n" +
                        "        </ul>\n" +
                        "    </body>\n" +
                        "</html>",
                d.getName(),
                d.getNumber(),
                d.getAddress(),
                d.getPosition().latitude,
                d.getPosition().longitude,
                d.isBanking(),
                d.isBonus(),
                d.getStatus(),
                d.getContractName(),
                d.getBikeStands(),
                d.getAvailableBikes(),
                d.getAvailableBikeStands());

        webView = findViewById(R.id.webView);

        webView.loadData(data, "text/html", "UTF-8");

    }


}
