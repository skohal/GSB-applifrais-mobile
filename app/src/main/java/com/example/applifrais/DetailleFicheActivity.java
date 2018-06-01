package com.example.applifrais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

    public class DetailleFicheActivity extends AppCompatActivity {


        private ListView fliste;
        private ListView hfListe;
        private TextView dateFiche;
        private TextView montantFiche;


        // Progress Dialog pDialog
        private ProgressDialog pDialog;

        // url de connexion
        private static String URL_FRAIS = "http://10.0.2.2/GSB/web/app_dev.php/api/fiche/";

        // JSON Node names
        private static final String TAG_STATUS = "status";
        private static final String TAG_DATA = "data";

        private static final String TAG_FRAIS_ID = "idFrais";
        private static final String TAG_FRAIS_LIBELLE = "libelle";
        private static final String TAG_FRAIS_MONTANT = "montant";
        private static final String TAG_FRAIS_ETAT = "etat";
        private static final String TAG_FRAIS_QUANTITE = "quantite";
        private static final String TAG_FRAIS_MONTANT_TOTAL = "montanttotal";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detaille_fiche);

            String idFiche = this.getIntent().getStringExtra("id");
            fliste = findViewById(R.id.detaille_fiche_forfait);
            hfListe = findViewById(R.id.detaille_fiche__hors_forfait);
            dateFiche = findViewById(R.id.date_fiche);
            montantFiche = findViewById(R.id.etat_fiche);

            new DetailleFicheTask().execute(idFiche);

        }

        class DetailleFicheTask extends AsyncTask<String, Void, HashMap<String, ?>> {

            /**
             * Before starting background thread Show Progress Dialog
             */

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(DetailleFicheActivity.this);
                pDialog.setMessage("Chargement de la fiche...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }


            @Override
            protected HashMap<String, ?> doInBackground(String... idFiches) {
                String idFiche = idFiches [0];

                ArrayList<HashMap<String, ?>> fraisForfaits = new ArrayList<>();
                ArrayList<HashMap<String, ?>> fraisHorsForfaits = new ArrayList<>();
                HashMap<String, Object> ficheFrais = new HashMap<>();



                //Génération listView
                try {
                    URL u = new URL(URL_FRAIS + idFiche);

                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("GET");
                    //conn.addRequestProperty("id", idFiche);
                    conn.addRequestProperty("format", "json");
                    Log.i("url test: ", conn.toString());
                    conn.connect();
                    InputStream is = conn.getInputStream();

                    byte[] b = new byte[4096];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    while (is.read(b) != -1) {
                        baos.write(b);
                    }

                    String JSONResp = new String(baos.toByteArray());

                    JSONObject rep = new JSONObject(JSONResp);
                    String status = rep.getString(TAG_STATUS);
                    if (status.equalsIgnoreCase("ok")) {

                        String data = rep.getString(TAG_DATA);
                        Log.i("Data:", data);
                        JSONObject ficheFraisJSON = new JSONObject(data);
                        int id_Fiche = ficheFraisJSON.getInt("id");
                        int mois = ficheFraisJSON.getInt("mois");
                        int annee = ficheFraisJSON.getInt("annee");
                        double montantTotal = ficheFraisJSON.getDouble("montantTotal");

                        ficheFrais.put("id", id_Fiche);
                        ficheFrais.put("mois", mois);
                        ficheFrais.put("annee", annee);
                        ficheFrais.put("montantTotal", montantTotal);

                        JSONArray fraisForfaitsJSON = ficheFraisJSON.getJSONArray("fraisForfaits");
                        JSONArray fraisHorsForfaitsJSON = ficheFraisJSON.getJSONArray("fraisHorsForfaits");


                        Log.i("json", fraisForfaitsJSON.toString());

                        for (int i = 0; i < fraisForfaitsJSON.length(); i++) {

                            HashMap<String, String> unFrais = new HashMap<>();
                            unFrais.put(TAG_FRAIS_ID, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_ID) + "");
                            unFrais.put(TAG_FRAIS_LIBELLE, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_LIBELLE) + "");
                            unFrais.put(TAG_FRAIS_MONTANT, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_MONTANT) + "");
                            unFrais.put(TAG_FRAIS_QUANTITE, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_QUANTITE) + "");
                            unFrais.put(TAG_FRAIS_MONTANT_TOTAL, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_MONTANT_TOTAL) + "");
                            unFrais.put(TAG_FRAIS_ETAT, fraisForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_ETAT) + "");


                            Log.i("json", unFrais.toString());

                            fraisForfaits.add(unFrais);
                        }
                        for (int i = 0; i < fraisHorsForfaitsJSON.length(); i++) {

                            HashMap<String, String> unFrais = new HashMap<>();
                            unFrais.put(TAG_FRAIS_ID, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_ID) + "");
                            unFrais.put(TAG_FRAIS_LIBELLE, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_LIBELLE) + "");
                            unFrais.put(TAG_FRAIS_MONTANT, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_MONTANT) + "");
                            unFrais.put(TAG_FRAIS_QUANTITE, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_QUANTITE) + "");
                            unFrais.put(TAG_FRAIS_MONTANT_TOTAL, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_MONTANT_TOTAL) + "");
                            unFrais.put(TAG_FRAIS_ETAT, fraisHorsForfaitsJSON.getJSONObject(i).getString(TAG_FRAIS_ETAT) + "");


                            Log.i("json", unFrais.toString());

                            fraisHorsForfaits.add(unFrais);
                        }

                        ficheFrais.put("fraisForfaits", fraisForfaits);
                        ficheFrais.put("fraisHorsForfaits", fraisHorsForfaits);
                    }


                    return ficheFrais;

                } catch (Throwable t) {

                    t.printStackTrace();
                    return null;

                }
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            @Override
            protected void onPostExecute(HashMap<String, ?> fichefrais) {
                // dismiss the dialog after getting all products
                pDialog.dismiss();
                // updating UI from Background Thread
                if (fichefrais != null) {
                    dateFiche.setText(fichefrais.get("mois").toString() + "/" + fichefrais.get("annee").toString());
                    montantFiche.setText(fichefrais.get("montantTotal") + " €");


                    ArrayList<HashMap<String, ?>> fraisForfaits = (ArrayList<HashMap<String, ?>>)fichefrais.get("fraisForfaits");
                    ArrayList<HashMap<String, ?>> fraisHorsForfaits = (ArrayList<HashMap<String, ?>>)fichefrais.get("fraisHorsForfaits");
                    SimpleAdapter adapter = new SimpleAdapter(
                            DetailleFicheActivity.this, fraisForfaits,
                            R.layout.fraisforfaits_fiche, new String[]{TAG_FRAIS_LIBELLE, TAG_FRAIS_MONTANT
                            , TAG_FRAIS_QUANTITE, TAG_FRAIS_MONTANT_TOTAL},
                            new int[]{R.id.libelle_frais, R.id.montant_frais, R.id.quantite_frais, R.id.montanttotal_frais});
                    // updating listview
                    fliste.setAdapter(adapter);

                    adapter = new SimpleAdapter(
                            DetailleFicheActivity.this, fraisHorsForfaits,
                            R.layout.fraisforfaits_fiche, new String[]{TAG_FRAIS_LIBELLE, TAG_FRAIS_MONTANT
                            , TAG_FRAIS_QUANTITE, TAG_FRAIS_MONTANT_TOTAL},
                            new int[]{R.id.libelle_frais, R.id.montant_frais, R.id.quantite_frais, R.id.montanttotal_frais});
                    // updating listview
                    hfListe.setAdapter(adapter);
                } else {
                    String message = "Erreur";
                    Toast.makeText(DetailleFicheActivity.this, message, Toast.LENGTH_SHORT).show();


                }

            }
        }
    }
