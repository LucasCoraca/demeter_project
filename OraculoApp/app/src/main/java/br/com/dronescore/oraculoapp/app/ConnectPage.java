package br.com.dronescore.oraculoapp.app;

import android.content.Context;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectPage extends AppCompatActivity {
    String handshake = null;
    String name = null;
    String serialn = null;
    String Attachment = null;
    String Status = null;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_page);
        new Connect().execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.w("a", Status);
                Log.w("a", handshake);
                Log.w("a", serialn);
                if (Status != null) {
                    WifiManager mainWifiObj;
                    mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    mainWifiObj.setWifiEnabled(false);
                    startActivity(new Intent(ConnectPage.this, Page2.class));
                } else {
                    if(Status == "post-fli"){
                        startActivity(new Intent(ConnectPage.this, postflight.class));
                    }
                    else{
                        String text = "Não Foi possível se conectar com o Drone. Tem certeza de que está conectado com o Drone?";
                        Spannable centeredText = new SpannableString(text);
                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, text.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(ConnectPage.this, MainActivity.class));
                        startActivity(new Intent(ConnectPage.this, postflight.class));
                    }
                }
            }
        }, 2500);
        }
    private class Connect extends AsyncTask {

        @Override
        protected Void doInBackground(Object... params) {
            Socket soc = null;
            try {
                soc = new Socket("10.0.1.62", 1111);
                DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                dout.writeBytes("handshake");
                Thread.sleep(100);
                handshake = inFromClient.readLine();
                Thread.sleep(100);
                name = inFromClient.readLine();
                Thread.sleep(100);
                serialn = inFromClient.readLine();
                Thread.sleep(100);
                Attachment = inFromClient.readLine();
                Thread.sleep(100);
                Status = inFromClient.readLine();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
