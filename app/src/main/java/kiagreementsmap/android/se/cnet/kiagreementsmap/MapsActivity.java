package kiagreementsmap.android.se.cnet.kiagreementsmap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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
    public BottomSheetBehavior behavior = new BottomSheetBehavior();
    public View bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //View bottomSheet = findViewById(R.id.design_bottom_sheet);
        //final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet = findViewById(R.id.design_bottom_sheet);
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
                        UniversityInfo mUniversityInfo = parseUniversityInfo(responseStr);
                        Log.d(TAG, "After university");

                        /*
                        //Run in main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateMap();
                            }
                        });*/

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
            }
        }
    }



    @Override
    public boolean onMarkerClick(final Marker marker) {
        TextView textView = (TextView) bottomSheet.findViewById(R.id.bottomsheet_text);
        textView.setText(marker.getTitle() + " - Aliquam consectetur, enim eget finibus pellentesque, nisl orci bibendum nibh, at tincidunt magna libero sit amet urna. Nullam eu nibh dictum, vestibulum dui non, sagittis dui. Maecenas viverra, lectus eget facilisis ultricies, tellus nisi ultricies risus, eu scelerisque nunc elit vitae nisi. Sed aliquam nulla ac dui fermentum, nec tincidunt dolor condimentum. Phasellus euismod et massa et molestie. Vivamus sed lectus quis est gravida volutpat. Suspendisse luctus metus id felis rhoncus tincidunt. Etiam maximus a ligula at cursus. Donec egestas mauris vel augue mollis blandit.\n" +
                "\n" +
                "Fusce elementum quis metus id consectetur. Vivamus ut enim justo. Ut suscipit, libero eget tincidunt ullamcorper, est odio varius dui, vitae rutrum nunc leo id lacus. Aliquam eget neque bibendum, vulputate neque eu, mollis urna. Nullam sed tristique nibh, eget egestas enim. Integer id gravida leo, non commodo ligula. Praesent id ipsum ut mi pellentesque vestibulum non a lorem.\n" +
                "\n" +
                "Ut congue, ante quis rhoncus tincidunt, sapien justo posuere justo, porta tempus ipsum sem ornare quam. In hac habitasse platea dictumst. Nunc hendrerit, eros sit amet ornare commodo, leo dolor blandit enim, ut consequat tortor purus ut lectus. Curabitur elit nunc, laoreet et mollis a, imperdiet eget urna. Mauris ullamcorper, velit nec maximus hendrerit, leo purus faucibus diam, venenatis sollicitudin lectus lorem ac diam. Mauris lacinia leo ac tincidunt scelerisque. Donec et arcu malesuada turpis venenatis lobortis id sit amet nibh. Proin vitae finibus est, at semper orci. Morbi tincidunt lacus felis, sed tincidunt massa lobortis vitae. Aliquam laoreet ligula non elit pretium, in egestas mi tristique. Aliquam risus purus, semper sed turpis at, suscipit faucibus sem. Pellentesque elementum orci in metus tempus, sit amet gravida felis commodo. Proin faucibus erat purus, eu maximus risus congue non. Sed pulvinar augue sem. Ut pretium ultrices tortor, id condimentum sapien elementum eget. Sed id enim dolor. Nullam commodo, massa in tristique cursus, erat ex rutrum diam, sed placerat neque tellus sit amet nibh. Vestibulum luctus aliquet sollicitudin. Curabitur vitae condimentum nisi. Nam maximus eget nisi eget hendrerit. Aliquam a cursus lorem. Ut ultricies erat ex, quis bibendum ipsum imperdiet ac. Vestibulum a placerat enim. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam in risus nec nisi luctus posuere. Morbi consequat mi sed turpis ornare semper. Sed vel nunc id massa faucibus dignissim id egestas felis. Nulla ut felis fermentum, blandit nunc id, iaculis velit. Integer auctor velit eget risus convallis, in ultrices urna elementum. Donec nec libero at erat molestie dignissim id nec eros. Donec iaculis efficitur diam vitae sollicitudin. Suspendisse potenti. In at cursus velit. Mauris egestas commodo arcu eu convallis. In eleifend elit eget neque fringilla, vitae venenatis lectus pellentesque. Mauris vitae fringilla sapien.");

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
