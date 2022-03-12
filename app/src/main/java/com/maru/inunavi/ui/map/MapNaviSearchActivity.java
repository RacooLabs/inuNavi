package com.maru.inunavi.ui.map;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.ui.map.MapFragmentState.DETAIL_MODE;
import static com.maru.inunavi.ui.map.MapFragmentState.SEARCH_MODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.map.MapDetailActivityAdapter;
import com.maru.inunavi.ui.map.Place;
import com.maru.inunavi.ui.map.markerinfo.MarkerInfo;
import com.maru.inunavi.ui.timetable.search.SearchCSEActivity;
import com.maru.inunavi.ui.timetable.search.SearchOptionActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapNaviSearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MapNaviSearchActivityAdapter adapter;

    private ImageView map_frag_navi_search_back;
    private ImageView map_frag_navi_search_cancel;
    private ConstraintLayout map_frag_navi_search_myLocation_wrapper;
    private ConstraintLayout map_frag_navi_search_pickInMap_wrapper;
    private EditText map_frag_navi_search_searchBar;
    private TextView map_frag_navi_search_noResult;

    //gps 위치 찾기
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng myCurrentLocation = null;

    private ArrayList<Place> searchPlaceList = new ArrayList<>();

    private String searchSort;


    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment_navi_search);

        Intent intent = getIntent();
        searchSort = getIntent().getStringExtra("SearchSort");

        // 레이아웃 바인딩
        map_frag_navi_search_back = findViewById(R.id.map_frag_navi_search_back);
        map_frag_navi_search_cancel = findViewById(R.id.map_frag_navi_search_cancel);
        map_frag_navi_search_myLocation_wrapper = findViewById(R.id.map_frag_navi_search_myLocation_wrapper);
        map_frag_navi_search_pickInMap_wrapper = findViewById(R.id.map_frag_navi_search_pickInMap_wrapper);
        map_frag_navi_search_searchBar = findViewById(R.id.map_frag_navi_search_searchBar);
        map_frag_navi_search_noResult = findViewById(R.id.map_frag_navi_search_noResult);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //검색창 클리어
        map_frag_navi_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                map_frag_navi_search_searchBar.setText("");

            }
        });


        //돌아가기 버튼
        map_frag_navi_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        if(searchSort != null || !searchSort.equals("")){
            map_frag_navi_search_searchBar.setHint(searchSort);
        }

        //검색창의 포커스 여부 설정
        map_frag_navi_search_searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    map_frag_navi_search_cancel.setVisibility(View.VISIBLE);
                }else{
                    map_frag_navi_search_cancel.setVisibility(View.INVISIBLE);
                }
            }
        });


        //검색창 검색 버튼 누를때
        map_frag_navi_search_searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    startSearch(map_frag_navi_search_searchBar.getText().toString());

                }


                return false;
            }
        });



        //내 위치로 출발, 목적지 설정할 때
        map_frag_navi_search_myLocation_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(searchSort.equals("출발지 검색")){
                    Intent intent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                    intent.putExtra("CallType", 2001);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, 0);

                }else if(searchSort.equals("도착지 검색")){

                    Intent intent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                    intent.putExtra("CallType", 2002);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, 0);

                }
            }
        });

        
        //지도에서 출발, 목적지 직접 고를때

        ActivityResultLauncher<Intent> pickLocationResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent intent = result.getData();

                            int CallType = intent.getIntExtra("CallType", 0);
                            double latitude = intent.getDoubleExtra("latitude", 0);
                            double longitude = intent.getDoubleExtra("longitude", 0);

                            if(CallType == 1) {

                                if(searchSort.equals("출발지 검색")){
                                    Intent sendingIntent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                                    sendingIntent.putExtra("CallType", 3001);
                                    sendingIntent.putExtra("latitude",latitude);
                                    sendingIntent.putExtra("longitude",longitude);
                                    setResult(Activity.RESULT_OK, sendingIntent);
                                    finish();
                                    overridePendingTransition(0, 0);



                                }else if(searchSort.equals("도착지 검색")){


                                    Intent sendingIntent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                                    sendingIntent.putExtra("CallType", 3002);
                                    sendingIntent.putExtra("latitude",latitude);
                                    sendingIntent.putExtra("longitude",longitude);
                                    setResult(Activity.RESULT_OK, sendingIntent);
                                    finish();
                                    overridePendingTransition(0, 0);

                                }


                            }

                        }

                    }
                });

        map_frag_navi_search_pickInMap_wrapper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MapNaviSearchActivity.this, MapPickLocationActivity.class);
                pickLocationResultLauncher.launch(intent);

            }

        });




    }

    public void startSearch(String keyword){

        map_frag_navi_search_searchBar.setText(keyword);
        map_frag_navi_search_searchBar.clearFocus();

        hideKeyboard();

        // 서버에 검색 정보 받아오기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else{

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        myCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        SearchBackgroundTask();
                    }else{

                        LocationRequest mRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(800)
                                .setFastestInterval(500);

                        LocationCallback mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                if (locationResult == null)
                                    return;

                                for (Location location : locationResult.getLocations()) {
                                    LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    fusedLocationClient.removeLocationUpdates(this);
                                }

                            }
                        };

                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }

                        fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                    }



                }

            });
        }


        setRecyclerView();



    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(map_frag_navi_search_pickInMap_wrapper.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    void setRecyclerView(){

        recyclerView = findViewById(R.id.map_frag_navi_search_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ;

        adapter = new MapNaviSearchActivityAdapter(searchPlaceList);
        recyclerView.setAdapter(adapter);

        //리사이클러 뷰 클릭했을 때
        adapter.setOnItemClickListener(new MapNaviSearchActivityAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {

                if(searchSort.equals("출발지 검색")){
                    Intent intent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                    intent.putExtra("CallType", 1001);
                    intent.putExtra("startPlaceCode", searchPlaceList.get(position).getPlaceCode());
                    intent.putExtra("startPlaceTitle", searchPlaceList.get(position).getTitle());
                    intent.putExtra("startLatitude", searchPlaceList.get(position).getLocation().latitude);
                    intent.putExtra("startLongitude", searchPlaceList.get(position).getLocation().longitude);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, 0);

                }else if(searchSort.equals("도착지 검색")){

                    Intent intent = new Intent(MapNaviSearchActivity.this, MainActivity.class);
                    intent.putExtra("CallType", 1002);
                    intent.putExtra("endPlaceCode", searchPlaceList.get(position).getPlaceCode());
                    intent.putExtra("endPlaceTitle", searchPlaceList.get(position).getTitle());
                    intent.putExtra("endLatitude", searchPlaceList.get(position).getLocation().latitude);
                    intent.putExtra("endLongitude", searchPlaceList.get(position).getLocation().longitude);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, 0);

                }
            }

        });

    }

    // 검색창에 검색어 쳤을 때

    Disposable searchBackgroundTask;

    void SearchBackgroundTask() {

        searchBackgroundTask = Observable.fromCallable(() -> {

            // doInBackground

            String searchKeyword = map_frag_navi_search_searchBar.getText().toString();
            String myLocation = myCurrentLocation.latitude + "," + myCurrentLocation.longitude;

            String target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/PlaceSearchList.php" :
                    "http://" + DemoIP + "/placeSearchList")+ "?searchKeyword=\"" + searchKeyword + "\"&myLocation=\"" + myLocation + "\"";

            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {

            }

            return null;

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(___ -> "{response : []}").subscribe((result) -> {

            // onPostExecute

            try {



                searchPlaceList.clear();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;

                String placeCode = "NONE";
                String title = "";
                String sort = "";
                double dist = 0.0;
                LatLng location = null;
                String time = "-";
                String callNum = "-";

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    placeCode = object.getString("placeCode");
                    title = object.getString("title");
                    sort = object.getString("sort");
                    dist = object.getDouble("distance");

                    String[] locationString = object.getString("location").trim().split(",");
                    if (locationString.length == 2) {
                        location = new LatLng(Double.parseDouble(locationString[0]), Double.parseDouble(locationString[1]));
                    }
                    time = object.getString("time");
                    callNum = object.getString("callNum");


                    Place place = new Place(placeCode, title, sort, dist, location, time,
                            callNum);

                    searchPlaceList.add(place);

                    count++;

                }

                if(count == 0){

                    map_frag_navi_search_noResult.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                }else {

                    map_frag_navi_search_noResult.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                }


                adapter.notifyDataSetChanged();

            } catch (Exception e) {

            }

            searchBackgroundTask.dispose();

        });

    }



}
