package br.com.dronescore.oraculoapp.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Page2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
    }

    public void onClick2(View v){
        startActivity(new Intent(Page2.this, MapsActivity.class));
    }

    public void onClick3(View v){ Toast.makeText(getApplicationContext(), "Não disponível no momento.", Toast.LENGTH_LONG).show(); }


}
