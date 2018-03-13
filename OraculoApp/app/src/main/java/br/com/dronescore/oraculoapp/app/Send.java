package br.com.dronescore.oraculoapp.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Send extends AppCompatActivity {
    public List<LatLng> list;
    public ArrayList<Double> altitude = new ArrayList<Double>();
    public double alt;
    public boolean isfinished;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        list = getIntent().getParcelableArrayListExtra("list");
        new sendinfo().execute();
    }

    private double getElevationFromGoogleMaps(double longitude, double latitude) {
        double result = Double.NaN;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "https://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude);
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = (double)(Double.parseDouble(value)); // convert from meters to feet
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {}
        catch (IOException e) {}
        return result;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    private class sendinfo extends AsyncTask {

        @Override
        protected Void doInBackground(Object... params) {
                for(int i=0; i <= list.size()-1; i++){
                    altitude.add(getElevationFromGoogleMaps(list.get(i).longitude,list.get(i).latitude));
                }
            WifiManager mainWifiObj;
            mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            mainWifiObj.setWifiEnabled(true);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Socket soc = null;
            try {
                soc = new Socket("10.0.1.62", 1111);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
                dout.writeBytes("wsend");
                Thread.sleep(200);
                dout.writeBytes(String.valueOf(list.size()));
                Thread.sleep(200);
                for(int i=0; i<=list.size()-1; i++){
                    dout.writeBytes(String.valueOf(list.get(i).latitude));
                    Thread.sleep(200);
                    dout.writeBytes(String.valueOf(list.get(i).longitude));
                    Thread.sleep(200);
                    dout.writeBytes(String.valueOf(altitude.get(i)));
                    Thread.sleep(200);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Send.this, startflight.class));
            return null;
        }
    }


}

