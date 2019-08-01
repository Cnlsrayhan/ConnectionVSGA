package rayhan.dev.trainingconnectionvsga;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.listview);
        koneksinews downloader = new koneksinews();
        String linkAPI = "https://newsapi.org/v2/top-headlines?country=id&apiKey=9a3964c835814076b3f8f3df95a370ef";

        downloader.execute(linkAPI);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                HashMap<String,Object> data = (HashMap<String, Object>)
                        parent.getAdapter().getItem(i);
                String strUrl = (String)data.get("link");

                Intent webViewIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
                startActivity(webViewIntent);

            }
        });

    }



    class koneksinews extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            loading = ProgressDialog.show
                    (MainActivity.this,"Mengambil Data","Mohontunggu...",false,false);
        }


        @Override
        protected String doInBackground(String... strings) {
            String urlAPI = strings[0];

            StringBuilder sb = new StringBuilder();

            try {
                URL urlConnection = new URL(urlAPI);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();

                connection.setRequestMethod("GET");
                connection.connect();
                InputStreamReader is = new InputStreamReader(connection.getInputStream());

                BufferedReader br = new BufferedReader(is);

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                Log.d("RESULT", sb.toString());


            } catch (MalformedURLException e) {
                Log.e("ERROR", "ERROR URL");
            } catch (IOException e) {
                Log.e("ERROR", "ERROR LAIN");
            }

            return sb.toString();
        }




        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();

            try {


                ArrayList<Map<String,Object>> arrData = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("articles");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject jo = results.getJSONObject(i);
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("title",jo.getString("title"));
                    itemMap.put("berita",jo.getString("content"));
                    itemMap.put("tanggal",jo.getString("publishedAt"));
                    itemMap.put("link",jo.getString("url"));
                    arrData.add(itemMap);

                }

                SimpleAdapter simpleAdapter =
                        new SimpleAdapter(MainActivity.this,arrData,R.layout.list_item, new String[]{"title","berita","tanggal"},
                                new int[]{R.id.titleItem,R.id.contentItem,R.id.dateItem}
                                );

                listView.setAdapter(simpleAdapter);

            } catch (JSONException e) {
                Log.e("ERROR", "ERROR PARSING JSON");
            }

            super.onPostExecute(s);


        }

    }
}
