package kiagreementsmap.android.se.cnet.kiagreementsmap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kiagreementsmap.android.se.cnet.kiagreementsmap.model.AgreementMap;
import kiagreementsmap.android.se.cnet.kiagreementsmap.model.University;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected OkHttpClient client = new OkHttpClient();
    private AgreementMap mAgreementMap;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get University locations
        try {
            getUniversities();
        } catch (IOException e) {
            Log.d(TAG, "-------------------------- ERROR RETRIEVING UNIS");
            Log.e(TAG, e.getMessage());
        }
    }



    protected Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void getUniversities() throws IOException {
        String url = "http://alhambra.it.ki.se/AgreementReports/AgreementReportService.asmx/GetUniversities";
        String bodyJson = "{'UniversitetIds': '',"
                + "'Country': '',"
                + "'Lasar': '16/17',"
                + "'English': false,"
                + "'AgreementType': 'UB,FOU,FO,MoU',"
                + "'AgreementOwner': 'KI,Inst,Utb',"
                + "'StudyProgram': ''"
                +"}";

        post(url, bodyJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, responseStr);
                    try {
                        mAgreementMap = parseAgreementMap(responseStr);
                        //Run in main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateMap();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.d(TAG, "Request not successful");
                }
            }
        });
    }

    private AgreementMap parseAgreementMap(String jsonData) throws JSONException{
        AgreementMap agreementMap = new AgreementMap();
        agreementMap.setUniversities(parseUniversities(jsonData));
        return agreementMap;
    }

    private University[] parseUniversities(String jsonData) throws JSONException {
        JSONObject agreementMapJson = new JSONObject(jsonData);
        JSONArray universitiesData = agreementMapJson.getJSONArray("d");
        University[] universities = new University[universitiesData.length()];
        for(int i = 0; i<universitiesData.length(); i++){
            JSONObject jsonUni = universitiesData.getJSONObject(i);
            University university = new University();
            university.setId(jsonUni.getInt("Id"));
            university.setNamn(jsonUni.getString("Namn"));
            university.setAdress(jsonUni.getString("Adress"));
            university.setOrt(jsonUni.getString("Ort"));
            university.setLand(jsonUni.getString("Land"));
            university.setLngCoord(jsonUni.getString("LngCoord"));
            university.setLatCoord(jsonUni.getString("LatCoord"));

            universities[i] = university;
            //Log.d(TAG, jsonUni.getString("Namn"));
        }
        return  universities;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void updateMap(){
        University[] unis = mAgreementMap.getUniversities();
        for(int i = 0; i<unis.length; i++){
            University u = unis[i];
            String lat = u.getLatCoord().trim();
            String lng = u.getLngCoord().trim();
            if(!lat.isEmpty() && !lng.isEmpty()){
                LatLng position = new LatLng(Double.parseDouble(lat.replace(",", ".")), Double.parseDouble(lng.replace(",", ".")));
                mMap.addMarker(new MarkerOptions().position(position).title(u.getNamn() + " " + u.getLand()));
            }
        }
    }

}
