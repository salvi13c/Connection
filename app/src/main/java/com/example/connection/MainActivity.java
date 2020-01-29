package com.example.connection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView Html;
    TextView URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View v){
        try {
            URL = findViewById(R.id.url);
            new DownloadFilesTask().execute(URL.getText().toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public class DownloadFilesTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String htmlCode = "";
            String line;
            String web="https://freetexthost.net/";
            URL url;
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    htmlCode=htmlCode+line;
                }
                rd.close();
            } catch (IOException e) {
                System.out.println("get not working");
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            htmlCode=RemoveNodesAndStacks(htmlCode);
            return htmlCode;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(String result) {
            Html=findViewById(R.id.resultado);
            Html.setText(result);
        }
    }

    public String RemoveNodesAndStacks(String HtmlCode){
        HtmlCode=HtmlCode.replaceAll("<script[^>]*>.*?</script>", "");
        char [] buffer =new char[HtmlCode.length()];
        int arrayindex=0;
        boolean insideTag=false;
        boolean insideC=false; //claudator
        boolean notPermitedChar=false;
        for (int i = 0; i < buffer.length; i++){
            //si se esta dentro de un tag <html> o un claudator {} se omite el trozo del string hasta que se cierren asimismos
            if (HtmlCode.charAt(i)=='<')
            {
                insideTag=true;
                continue;
            }
            if (HtmlCode.charAt(i)=='>')
            {
                insideTag=false;
                continue;
            }
            if (HtmlCode.charAt(i)=='{')
            {
                insideC=true;
                continue;
            }
            if (HtmlCode.charAt(i)=='}') {
                insideC = false;
                continue;
            }

                if (!insideTag && !insideC){
                    if (HtmlCode.charAt(i)!='/'){
                        buffer[arrayindex]=HtmlCode.charAt(i);
                        arrayindex++;
                    }
            }
        }
        return new String(buffer,0,arrayindex);
    }

}




