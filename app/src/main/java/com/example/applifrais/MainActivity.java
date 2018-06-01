package com.example.applifrais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText login;
    private EditText password;
    private String idUser;

    // Progress Dialog
    private ProgressDialog pDialog;

    // url de connexion
    private static String URL_CONNEXION = "http://10.0.2.2/GSB/web/app_dev.php/api/connect";

    private static final String TAG_STATUS = "status";
    private static final String TAG_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.loginEdit);
        password = findViewById(R.id.passwordedit);
        Button btConnexion = findViewById(R.id.bt_connexion);
        btConnexion.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.bt_connexion:{
                Intent it = new Intent(this.getApplicationContext(), ListeFichesActivity.class);

                it.putExtra("idUser", id);
                startActivity(it);

                new Connect().execute(login.getText().toString(), password.getText().toString());

                break;
            }
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class Connect extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        private String data;
        private boolean status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Connexion en cours...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Connexion via l'url
         * */
        protected String doInBackground(String... args) {

            String username = args[0];
            String password = args[1];
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);

            //Génération listView
            try {
                URL u = new URL(URL_CONNEXION + "/" + username + "/"+ password);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.addRequestProperty("username", username);
                conn.addRequestProperty("password", password);
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
                status = rep.getBoolean(TAG_STATUS);
                data = rep.getString(TAG_DATA);

            }catch (Exception ex){
             ex.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (status){
                        Toast.makeText(getApplicationContext(),"idUser = " + data, Toast.LENGTH_SHORT).show();

                        // Launching Fiches Activity
                        Intent i = new Intent(getApplicationContext(), ListeFichesActivity.class);
                        i.putExtra("idUser", data);
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(), "Erreur: " + data, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
