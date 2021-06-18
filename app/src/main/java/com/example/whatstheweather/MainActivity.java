package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    String s="";

    public void findWeather(View v){

//      To hide the keyboard when the button has been tapped

        InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        //to encode cityname(having space etc) as url

        String encodeCityName= null;
        try {
            encodeCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodeCityName + "&appid=8ac5fad9906625cfffb53264d7412cb4");

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                result = sb.toString();
                Log.i("Resilt", result);
                return result;

               } catch (Exception e){
                    e.printStackTrace();
               }
            return null;
        }

        //doingIn Background ended;//cant interact with UI,cant set text;post execute and ui thread can do that
        //On post execute parses the result
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String msg = "";
                JSONObject jsonObject = new JSONObject(result);
                String weather = jsonObject.getString("weather");
                String mainTemp = jsonObject.getString("main");
                JSONObject maintempObj=new JSONObject(mainTemp);
                Log.i("weather", weather); //is an  array
                Log.i("mainTemp", mainTemp);//not an array

                JSONArray arr = new JSONArray(weather);
               
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPartOfWeatherInfo = arr.getJSONObject(i);

                        String main = "";
                        String description = "";
                        main = jsonPartOfWeatherInfo.getString("main") + jsonPartOfWeatherInfo.getString("description");
                        description = "Temperature :"+maintempObj.getString("temp") + "\r\n"+"Pressure :"+ maintempObj.getString("pressure") +"\r\n"+"Humidity :"+ maintempObj.getString("humidity");
                        Log.i("temp", description);
                        if (main != null && description != null) {
                            msg += main + ": " + "\r\n"+description + "\r\n";
                        }
                    }
                    if (msg != "") {
                        textView.setText(msg);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_LONG).show();
            }
        }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText=(EditText)findViewById(R.id.citynameEditText);
        textView=(TextView)findViewById(R.id.textView2);


    }
}