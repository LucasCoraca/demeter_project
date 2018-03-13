package br.com.dronescore.oraculoapp.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private Polyline polyline;
    public static List<LatLng> list;
    private boolean markstat;
    private Polygon polygon;
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setBuildingsEnabled(true);
        mMap.setMyLocationEnabled(true);
        list = new ArrayList<LatLng>();
        markstat = false;
        Polyline line = mMap.addPolyline(new PolylineOptions().width(5).color(Color.RED));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                float[] distance = new float[5];
                if (markstat == true) {
                    marker.remove();
                }
                addmarker(new LatLng(latLng.latitude, latLng.longitude));
                list.add(latLng);
                markstat = true;
                draw();
                if (list.size() > 2) {
                    if (polygon != null) {
                        polygon.remove();
                    }
                    drawpoly();
                }
            }
        });
    }

    public void addmarker(LatLng latlong) {
        MarkerOptions options = new MarkerOptions();
        options.position(latlong).draggable(true).flat(true);
        marker = mMap.addMarker(options);
    }

    public void drawpoly() {
        PolygonOptions opts = new PolygonOptions();

        for (LatLng location : list) {
            opts.add(location);
        }

        polygon = mMap.addPolygon(opts.strokeColor(Color.rgb(236, 240, 241)).fillColor(Color.argb(100, 189, 195, 199)).strokeWidth(5));
    }

    public void draw() {
        PolylineOptions po;
        if (polyline == null) {
            po = new PolylineOptions();
            for (int i = 0, tam = list.size(); i < tam; i++) {
                po.add(list.get(i));
            }
            po.color(Color.rgb(236, 240, 241)).width(5);
            polyline = mMap.addPolyline(po);
        } else {
            polyline.setPoints(list);
        }

    }

    public void onClick4(View view) {
        list.clear();
        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        if (markstat) {
            marker.remove();
            markstat = false;
        }
    }

    public void onClick5(View view) {
        if (list.size() != 0) {
            if (markstat) {
                marker.remove();
                markstat = false;
            }
            if (list.size() == 2) {
                list.clear();
                if (polygon != null) {
                    polygon.remove();
                    polygon = null;
                }
                if (polyline != null) {
                    polyline.remove();
                    polyline = null;
                }
            } else {
                if(list.size() > 2) {
                    list.remove(list.size() - 1);
                    if (polyline != null) {
                        polyline.remove();
                        polyline = null;
                        draw();
                    }
                    if (polygon != null) {
                        polygon.remove();
                        polygon = null;
                        drawpoly();
                    }
                    if (markstat) {
                        marker.remove();
                        markstat = false;
                    }
                }
                else{
                    if(list.size()==1){
                        list.clear();
                        if (polygon != null) {
                            polygon.remove();
                            polygon = null;
                        }
                        if (polyline != null) {
                            polyline.remove();
                            polyline = null;
                        }
                        if (markstat) {
                            marker.remove();
                            markstat = false;
                        }
                    }
                }

            }
        }
    }

    public void onClick7(View v) throws ParserConfigurationException, SAXException, IOException {
        if( 1 == list.size() || list.size() < 3 ){
            Toast.makeText(getApplicationContext(), "Selecionie uma área por favor.", Toast.LENGTH_LONG).show();
            return;
        }
        if (list.size() == 0) {
            Toast.makeText(getApplicationContext(), "Nenhuma área selecionada.", Toast.LENGTH_LONG).show();
            return;}
        else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
            list = bundle.getParcelableArrayList("list");
            Intent intent = new Intent(MapsActivity.this, Send.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return;
        }
    }

    public String getStringFromDoc(org.w3c.dom.Document doc)    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            writer.flush();
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    private class getxml extends AsyncTask {

        @Override
        protected Void doInBackground(Object... params) {

            return null;
        }
    }

}




