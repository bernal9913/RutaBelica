package com.bermer.rutabelica;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class rutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> ubicaciones;
    private ArrayList<String> titulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        // Obtener la lista de ubicaciones del Intent
        ubicaciones = (ArrayList<LatLng>) getIntent().getSerializableExtra("ubicaciones");
        titulos = (ArrayList<String>) getIntent().getSerializableExtra("titulos");

        // Obtener referencia al MapView desde el diseño XML
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Agregar marcadores al mapa para cada ubicación en la lista
        if (ubicaciones != null && ubicaciones.size() > 1) {
            for (int i = 0; i < ubicaciones.size() - 1; i++) {
                LatLng origen = ubicaciones.get(i);
                LatLng destino = ubicaciones.get(i + 1);
                googleMap.addMarker(new MarkerOptions().position(origen).title((i + 1) + ". " + titulos.get(i)));
                dibujarRuta(origen, destino);
            }

            // Agregar marcador para la última ubicación
            LatLng ultimaUbicacion = ubicaciones.get(ubicaciones.size() - 1);
            googleMap.addMarker(new MarkerOptions().position(ultimaUbicacion).title(ubicaciones.size() + ". " + titulos.get(titulos.size() - 1)));

            // Mover la cámara del mapa para mostrar todas las ubicaciones
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng ubicacion : ubicaciones) {
                builder.include(ubicacion);
            }
            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100 es el padding en píxeles
        }
    }

    private void dibujarRuta(LatLng origen, LatLng destino) {
        String url = obtenerUrl(origen, destino);
        DescargarJsonTask descargarJsonTask = new DescargarJsonTask();
        descargarJsonTask.execute(url);
    }

    private String obtenerUrl(LatLng origen, LatLng destino) {
        String apiKey = "AIzaSyDnxtUyk9C61jBjF9y58jmhIWBCByaXESY";
        String origin = "origin=" + origen.latitude + "," + origen.longitude;
        String destination = "destination=" + destino.latitude + "," + destino.longitude;
        String mode = "mode=driving";
        String parameters = origin + "&" + destination + "&" + mode;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + apiKey;
        System.out.println(url);
        return url;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class DescargarJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String data = "";

            try {
                data = downloadUrl(urls[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ArrayList<LatLng> puntosRuta = parsearJson(result);

            // Dibujar la ruta en el mapa
            if (puntosRuta != null && puntosRuta.size() > 0) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(8);
                polylineOptions.addAll(puntosRuta);
                googleMap.addPolyline(polylineOptions);
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            bufferedReader.close();
            inputStream.close();
            connection.disconnect();

            return result.toString();
        }
        // TODO: arreglar el api key de directions
        private ArrayList<LatLng> parsearJson(String json) {
            ArrayList<LatLng> puntosRuta = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) { // Verificar si el array routes tiene elementos
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    for (int i = 0; i < legs.length(); i++) {
                        JSONObject leg = legs.getJSONObject(i);
                        JSONArray steps = leg.getJSONArray("steps");
                        for (int j = 0; j < steps.length(); j++) {
                            JSONObject step = steps.getJSONObject(j);
                            JSONObject startLocation = step.getJSONObject("start_location");
                            double lat = startLocation.getDouble("lat");
                            double lng = startLocation.getDouble("lng");
                            LatLng punto = new LatLng(lat, lng);
                            puntosRuta.add(punto);
                        }
                    }
                } else {
                    Log.e("parsearJson", "No se encontraron rutas en el JSON");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return puntosRuta;
        }
    }
}