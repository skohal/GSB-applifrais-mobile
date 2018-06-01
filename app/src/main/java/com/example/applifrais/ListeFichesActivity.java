package com.example.applifrais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ListeFichesActivity extends Activity implements AdapterView.OnItemClickListener {



    private ListView mListView;

    // Progress Dialog
    private ProgressDialog pDialog;

   // url de connexion
    private static String URL_LISTE_FICHES = "http://10.0.2.2/GSB/web/app_dev.php/api/listefiches/";
    // JSON Node names
    private static final String TAG_STATUS = "status";
    private static final String TAG_DATA = "data";

    private static final String TAG_FICHE_ID = "id";
    private static final String TAG_FICHE_MOIS = "mois";
    private static final String TAG_FICHE_MONTANT = "montantValide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_fiche);
        mListView = findViewById(R.id.ficheListe);
        mListView.setOnItemClickListener(this);

        // Hashmap for ListView
        String idUser = this.getIntent().getStringExtra("idUser");
        Toast.makeText(this.getApplicationContext(),"idUser onCreate = " + idUser, Toast.LENGTH_LONG).show();
        new ListeFichesTask().execute(idUser);

    }
        /**
         * Changement de page vers la detaille fiche
         */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View ligneFiche, int i, long l) {

        TextView idFiche = ligneFiche.findViewById(R.id.id_fiche);

        Intent it = new Intent(getApplicationContext(),DetailleFicheActivity.class);
        it.putExtra("id", idFiche.getText());
        startActivity(it);
        Toast.makeText(this.getApplicationContext(), "Id" + idFiche.getText(), Toast.LENGTH_SHORT).show();
    }

    class ListeFichesTask extends AsyncTask<String, Void,  ArrayList<HashMap<String,?>>>{

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListeFichesActivity.this);
            pDialog.setMessage("Chargement de votre liste de fiche...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected  ArrayList<HashMap<String,?>> doInBackground(String... idUsers) {


            String idUser = idUsers[0];

            ArrayList<HashMap<String,?>> fiches =new ArrayList<HashMap<String,?>>();

            //Génération listView
            try {
                URL u = new URL(URL_LISTE_FICHES + idUser);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.addRequestProperty("idUser",idUser);
                conn.addRequestProperty("format","json");
                Log.i("url", conn.toString());
                conn.connect();
                InputStream is = conn.getInputStream();

                // Read the stream
                byte[] b = new byte[4096];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (is.read(b) != -1) {
                    baos.write(b);
                }

                String JSONResp = new String(baos.toByteArray());

                JSONObject rep = new JSONObject(JSONResp);
                String status = rep.getString(TAG_STATUS);
                if(status.equals("ok")) {

                    String data = rep.getString(TAG_DATA);

                    JSONObject liste_fiches = new JSONObject(data);

                    Log.i("json", liste_fiches.toString());

                    JSONArray listeFichesJSON = liste_fiches.getJSONArray("lesfiches");

                    for (int i = 0; i < listeFichesJSON.length(); i++) {

                        HashMap<String, String> uneFiche = new HashMap<>();
                        uneFiche.put(TAG_FICHE_ID, listeFichesJSON.getJSONObject(i).getString(TAG_FICHE_ID) + "");
                        uneFiche.put(TAG_FICHE_MOIS, listeFichesJSON.getJSONObject(i).getString(TAG_FICHE_MOIS) + "");
                        uneFiche.put(TAG_FICHE_MONTANT, listeFichesJSON.getJSONObject(i).getString(TAG_FICHE_MONTANT) + "");


                        Log.i("json", uneFiche.toString());

                        fiches.add(uneFiche);
                    }
                }
                return fiches;

            }catch (Throwable t){

                t.printStackTrace();
                return null;

            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute( ArrayList<HashMap<String,?>> listeFiches) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            if(listeFiches != null && listeFiches.size()>0) {
                SimpleAdapter adapter = new SimpleAdapter(
                        ListeFichesActivity.this, listeFiches,
                        R.layout.ligne_fiche, new String[]{TAG_FICHE_ID,
                        TAG_FICHE_MOIS, TAG_FICHE_MONTANT},
                        new int[]{R.id.id_fiche, R.id.mois_fiche, R.id.montant_fiche});
                // updating listview
                mListView.setAdapter(adapter);
            }else{
                String message = "Erreur";
                Toast.makeText(ListeFichesActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

