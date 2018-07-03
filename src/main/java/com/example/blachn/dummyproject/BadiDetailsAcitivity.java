package com.example.blachn.dummyproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BadiDetailsAcitivity extends AppCompatActivity {

    public static String TAG = "BadiInfo";
    private String badiId;
    private String name;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_details);
        Intent intent = getIntent();
        //Hier holen wir die Zusatzinformationen des Intents
        badiId = intent.getStringExtra("badi");
        name = intent.getStringExtra("name");
        //Hier holen wir den TextView
        TextView text = (TextView) findViewById(R.id.badiinfos);
        //und setzten ihn als text des Badinamen
        text.setText(name);
        //hier kommt der Ladedialog
        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        //Dann werden die Badidaten von der Website wiewarm.ch geholt
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
    }

    private void getBadiTemp(String url) {
        //dieser ArrayAdapter wird später verwendet um die Temperaturen zu speichern
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //Hier starten wir einen asynchronen Task, da Android verlangt dass die Datenbearbeitung und GUI getrennt sind
        new AsyncTask<String, String, String>() {
            //der AsyncTask verlangt die Implementation der Methode doInBackground, nach dieser wird immer die Methode onPostExecute ausgeführt
            @Override
            protected String doInBackground(String[] badi) {
                //in dieser Variable wird die Antwort der Seite wiewarm.ch gespeichert.
                String msg = "";
                try {
                    URL url = new URL(badi[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Lesen des Antwortcodes der Website
                    int code = conn.getResponseCode();
                    //Nun können wir den Lade Dialog wieder ausblenden
                    mDialog.dismiss();
                    //Hier lesen wir die Nachricht der Website wiewarm uns speichern es in msg
                    msg = IOUtils.toString(conn.getInputStream());
                    //und loggen den Statuscode in der Konsole
                    Log.i(TAG, Integer.toString(code));
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                }
                return msg;
            }

            public void onPostExecute(String result) {
                try {
                    //parseBadiTemp ist eine Methode zum verarbeiten und speichern der Daten
                    List<String> badiInfos = parseBadiTemp(result);
                    //Hier wird die ListView der Badidetails geholt
                    ListView badidetails = (ListView) findViewById(R.id.badidetails);
                    //wir füller nun die Daten in den ArrayAdapter
                    temps.addAll(badiInfos);
                    //Hier wird der ArrayAdapter der ListView hinzugefügt
                    badidetails.setAdapter(temps);
                } catch(JSONException e) {
                    Log.i(TAG, e.toString());
                }
            }

            public List parseBadiTemp(String jonString)throws JSONException{
                //JSON kann nicht direkt übergeben werden, darum parsen wir dei Informationen und speichern sie in eine ArrayListe
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                JSONObject becken = jsonObj.getJSONObject("becken");
                //Das ist unser Pointer um die Daten vom JSON auszulesen
                Iterator keys = becken.keys();
                //hier holen wir Element für Element aus dem JSON Stream
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject subObj = becken.getJSONObject(key);
                    //wenn man die Antowort der Website anscahut, kann man im Element "beckennae" den Beckennamen auslesen
                    String name = subObj.getString("beckenname");
                    //und unter temp ist die Temperatur angegeben
                    String temp = subObj.getString("temp");
                    //Sobald wir die Daten haben fügen wir sie unserer Liste hinzu
                    resultList.add(name + ": " + temp + " Grad Celsius");
                }
                return resultList;
            }
        }.execute(url);
    }
}
