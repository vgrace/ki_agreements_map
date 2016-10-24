package kiagreementsmap.android.se.cnet.kiagreementsmap;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kiagreementsmap.android.se.cnet.kiagreementsmap.model.Agreement;
import kiagreementsmap.android.se.cnet.kiagreementsmap.model.AgreementMap;
import kiagreementsmap.android.se.cnet.kiagreementsmap.model.University;
import kiagreementsmap.android.se.cnet.kiagreementsmap.model.UniversityInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected OkHttpClient client = new OkHttpClient();
    private AgreementMap mAgreementMap;
    private GoogleMap mMap;
    private Map<Marker, University> allMarkersMap = new HashMap<Marker, University>();
    public BottomSheetBehavior behavior = new BottomSheetBehavior();
    public RelativeLayout bottomSheet;
    public LinearLayout bottomSheetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //View bottomSheet = findViewById(R.id.design_bottom_sheet);
        //final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet = (RelativeLayout) findViewById(R.id.design_bottom_sheet);
        bottomSheetContent = (LinearLayout) findViewById(R.id.bottom_sheet_content);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get University locations
        try {
            getUniversities();
            getUniversityInfo(1024);

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

    private void getUniversityInfo(int universityId) throws IOException{
        String url = "http://alhambra.it.ki.se/AgreementReports/AgreementReportService.asmx/GetUniversityInfo";
        String bodyJson = "{" +
                "    'univid': " + universityId + "," +
                "    'Lasar': '16/17'," +
                "    'English': true," +
                "    'AgreementTypes': 'UB,FOU,FO,MoU'," +
                "    'AgreementOwners': 'KI,Inst,Utb'," +
                "    'StudyPrograms': ''" +
                "}";
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
                        final UniversityInfo mUniversityInfo = parseUniversityInfo(responseStr);
                        Log.d(TAG, "After university");
                        Log.d(TAG, mUniversityInfo.getKIBenamning());


                        //Run in main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = (TextView) bottomSheet.findViewById(R.id.bottomsheet_text);
                                int views = bottomSheet.getChildCount();
                                Log.d(TAG, "----- Nr views: " + views);
                                if(views > 1) {
                                    bottomSheet.removeViewAt(1);
                                }
                                textView.setText(mUniversityInfo.getName() + " " + mUniversityInfo.getKIBenamning() + " - " + mUniversityInfo.getLandNamn());
                                TextView urlTexView = new TextView(bottomSheet.getContext());
                                urlTexView.setText(mUniversityInfo.getWWWadress());
                                urlTexView.setPaddingRelative(5, 160, 5, 0);
                                urlTexView.setTextColor(ContextCompat.getColor(bottomSheet.getContext(), R.color.colorText));
                                bottomSheet.addView(urlTexView);

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
                Log.d(TAG, "Something went wrong");
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Responce");
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

    private UniversityInfo parseUniversityInfo(String jsonData) throws JSONException {
        JSONObject allUniInfoJson = new JSONObject(jsonData);
        JSONObject uniInfoData = allUniInfoJson.getJSONObject("d");
        UniversityInfo universityInfo = new UniversityInfo();
        universityInfo.setId(uniInfoData.getInt("Id"));
        universityInfo.setName(uniInfoData.getString("Namn"));
        universityInfo.setAdress(uniInfoData.getString("Adress"));
        universityInfo.setOrt(uniInfoData.getString("Ort"));
        universityInfo.setLand(uniInfoData.getString("Land"));
        universityInfo.setKod(uniInfoData.getString("Kod"));
        universityInfo.setPostnr(uniInfoData.getString("Postnr"));
        universityInfo.setTelefonnr(uniInfoData.getString("Telefonnr"));
        universityInfo.setWWWadress(uniInfoData.getString("WWWadress"));
        universityInfo.setInaktivt(uniInfoData.getBoolean("Inaktivt"));
        universityInfo.setKIBenamning(uniInfoData.getString("KIBenamning"));
        universityInfo.setLandNamn(uniInfoData.getString("LandNamn"));
        universityInfo.setAgreements(parseAgreements(uniInfoData.getJSONArray("Agreements")));

        return universityInfo;
    }

    private Agreement[] parseAgreements(JSONArray agreementsJson) throws JSONException {
        Agreement[] agreements = new Agreement[agreementsJson.length()];
        for(int i = 0; i<agreementsJson.length(); i++){
            JSONObject agreementJson = agreementsJson.getJSONObject(i);
            Agreement agreement = new Agreement();
            agreement.setUniversitetId(agreementJson.getInt("UniversitetId"));
            agreement.setLasar(agreementJson.getString("Lasar"));
            agreement.setDiarienummer(agreementJson.getString("Diarienummer"));
            agreement.setAType(agreementJson.getString("AType"));
            agreement.setAOwner(agreementJson.getString("AOwner"));
            agreement.setNivaUG(agreementJson.getBoolean("NivaUG"));
            agreement.setNivaA(agreementJson.getBoolean("NivaA"));
            agreement.setNivaD(agreementJson.getBoolean("NivaD"));
            agreement.setStudyProgram(agreementJson.getString("StudyProgram"));
            agreement.setExchangeProgram(agreementJson.getString("ExchangeProgram"));
            agreement.setExpirationDate(agreementJson.getString("ExpirationDate"));
            agreement.setInactive(agreementJson.getBoolean("Inactive"));

            agreements[i] = agreement;
        }
        return agreements;
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

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "Map clicked");

                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    public void updateMap(){
        University[] unis = mAgreementMap.getUniversities();
        for(int i = 0; i<unis.length; i++){
            University u = unis[i];
            String lat = u.getLatCoord().trim();
            String lng = u.getLngCoord().trim();
            if(!lat.isEmpty() && !lng.isEmpty()){
                LatLng position = new LatLng(Double.parseDouble(lat.replace(",", ".")), Double.parseDouble(lng.replace(",", ".")));
                Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(u.getNamn() + " " + u.getLand()));
                allMarkersMap.put(marker, u);
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        University uni = allMarkersMap.get(marker);
        try {
            getUniversityInfo(uni.getId());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        TextView textView = (TextView) bottomSheet.findViewById(R.id.bottomsheet_text);
        textView.setText(marker.getTitle() + " - " + uni.getId() + " loading ... ");

        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        // Retrieve the data from the marker.
        /*Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }*/
        Toast.makeText(this,
                marker.getTitle() +
                        " has been clicked ",
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, marker.getTitle());



        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

}
