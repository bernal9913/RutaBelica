package com.bermer.rutabelica;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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

public class DownloadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urlStrings) {
        String data = "";

        try {
            data = downloadUrl(urlStrings[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }

        return data;
    }

    private String downloadUrl(String urlString) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
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
            //googleMap.addPolyline(polylineOptions);
        }
    }
    private ArrayList<LatLng> parsearJson(String json) {
        ArrayList<LatLng> puntosRuta = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0); // Suponiendo que solo hay una ruta en el JSON

            JSONArray legsArray = route.getJSONArray("legs");
            for (int i = 0; i < legsArray.length(); i++) {
                JSONObject leg = legsArray.getJSONObject(i);
                JSONArray stepsArray = leg.getJSONArray("steps");
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject step = stepsArray.getJSONObject(j);
                    JSONObject startLocation = step.getJSONObject("start_location");
                    double lat = startLocation.getDouble("lat");
                    double lng = startLocation.getDouble("lng");
                    LatLng punto = new LatLng(lat, lng);
                    puntosRuta.add(punto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return puntosRuta;
    }

}
