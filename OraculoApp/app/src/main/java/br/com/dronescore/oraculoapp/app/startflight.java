package br.com.dronescore.oraculoapp.app;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class startflight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startflight);
    }

    public void onClick6(View v){
        new takeoff().execute();
    }

    private class takeoff extends AsyncTask {

        @Override
        protected Void doInBackground(Object... params) {
            Socket soc = null;
            try {
                soc = new Socket("10.0.1.62", 1111);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataOutputStream dout = null;
            try {
                dout = new DataOutputStream(soc.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dout.writeBytes("takeoff");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
