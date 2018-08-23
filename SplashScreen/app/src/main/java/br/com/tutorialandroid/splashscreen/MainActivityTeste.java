package br.com.tutorialandroid.splashscreen;
/*
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivityTeste extends AppCompatActivity implements SearchView.OnQueryTextListener{

    // Progress Dialog
    private ProgressDialog pDialog;

    // SearchView
    private SearchView mSearchView;
    private ListView mListView;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    //Esta Linha de código armazena ua lista de dados JSON no cadastroList
    ArrayList<HashMap<String, String>> cadastroList;

    // url to get all products List
    private static String url_all_cadastro = "http://letsgosobral.esy.es/letsgo/get_all_cadastro.php";

    // JSON Node names
    private static final String TAG_SUCESSO = "sucesso";
    private static final String TAG_CADASTRO = "cadastro";
    private static final String TAG_ID = "id";
    private static final String TAG_NOME = "nome";

    // products JSONArray
    JSONArray cadastro = null;

    ListView lista;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main_teste);

        // HashMap para a ListView
        cadastroList = new ArrayList<HashMap<String, String>>();

        // Carrega os produtos na Background Thread
        new LoadAllProducts().execute();
        lista = (ListView) findViewById(R.id.listAllProducts);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.lista);
        lista.setTextFilterEnabled(true);
        setupSearchView();
    }

    private void setupSearchView(){

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Busca rápida");

    }

    public boolean onQueryTextChange(String newText){

        if(TextUtils.isEmpty(newText)){
            lista.clearTextFilter();

        } else {
            lista.setFilterText(newText.toString());
        }

        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
*/


        /**
         * Antes de empezar el background thread Show Progress Dialog
         */
/*
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivityTeste.this);
            pDialog.setMessage("Carregando cadastros. Por Favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
*/
        /**
         * obtendo a lista de todos os produtos
         */
/*
        protected String doInBackground(String... args) {

            //Building Parameters
            List params = new ArrayList();

            //getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_cadastro, "GET", params);

            //Check your log cat for JSON response
            Log.d("Todos os cadastrados: ", json.toString());

            try {

                // Checking for SUCCESS TAG
                int sucesso = json.getInt(TAG_SUCESSO);

                if (sucesso == 1){

                    //products found
                    //Getting Array os Products

                    cadastro = json.getJSONArray(TAG_CADASTRO);

                    //looping through All Products

                    for (int i = 0; i < cadastro.length(); i++) {
                        JSONObject c = cadastro.getJSONObject(i);

                        //Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String nome = c.getString(TAG_NOME);

                        //creating new HashMap
                        HashMap map = new HashMap();

                        //adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NOME, nome);

                        cadastroList.add(map);

                    }

                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;

        }
*/
        /**
         * depois de completo é fechado o alert com Dismiss Dialog
         */
/*
        protected void onPostExecute(String file_url){

            // dismiss the dialog after getting all products
            pDialog.dismiss();

            //updating UI from Backgroung Thread
            runOnUiThread(new Runnable(){

                public void run(){
*/
                    /**
                     * Updating parsed JSON data into ListView
                     */
/*                    ListAdapter adapter = new SimpleAdapter(MainActivityTeste.this,
                            cadastroList,
                            R.layout.list_item_perfil,

                            new String[]{
                                    TAG_ID,
                                    TAG_NOME,

                            },

                            new int[]{
                                    R.id.single_post_tv_id,
                                    R.id.single_post_tv_nome,

                            }
                    );

                    //updating listView;
                    //setListAdapter(adapter);

                    lista.setAdapter(adapter);


                }

            });

        }
    }

}
*/