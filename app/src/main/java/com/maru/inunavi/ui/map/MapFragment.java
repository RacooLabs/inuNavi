package com.maru.inunavi.ui.map;


import static android.content.Context.SENSOR_SERVICE;
import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.MainActivity.cookieManager;
import static com.maru.inunavi.MainActivity.sessionURL;
import static com.maru.inunavi.ui.map.MapFragmentState.DEFAULT_MODE;
import static com.maru.inunavi.ui.map.MapFragmentState.DETAIL_MODE;
import static com.maru.inunavi.ui.map.MapFragmentState.DIRECTION_MODE;
import static com.maru.inunavi.ui.map.MapFragmentState.DIRECTPIN_MODE;
import static com.maru.inunavi.ui.map.MapFragmentState.SEARCH_MODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maru.inunavi.ui.map.markerinfo.FloatingMarkerTitlesOverlay;
import com.maru.inunavi.ui.map.markerinfo.MarkerInfo;
import com.maru.inunavi.ui.satisfied.AnalysisRequest;
import com.maru.inunavi.ui.timetable.search.Lecture;
import com.maru.inunavi.user.LoginActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MapFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {


    private MapView mapView = null;
    private GoogleMap gMap;
    private Polyline polyline = null;
    private MapFragment mapFragment = null;
    private String url = sessionURL;

    private Boolean isLogin;

    //gps 위치 찾기
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CODE = 101;
    private LatLng myCurrentLocation = null;

    //마커 타이틀 오버레이
    private FloatingMarkerTitlesOverlay floatingMarkersOverlay;


    //선택된 마커
    private Marker pointedMarker = null;

    private Marker startMarker = null; // 출발 마커
    private Marker endMarker = null; // 도착 마커

    private final int markerSize = 48;

    // 길그릴때 사용하는 latLng 리스트
    private List<LatLng> latLngList = new ArrayList<>();

    private RecyclerView recyclerView;
    private MapSearchAdapter adapter;

    private MapFragmentState mapFragmentState = DEFAULT_MODE;

    // 검색했을 때 검새결과를 받을 리스트
    public ArrayList<Place> placeList;

    //detail 박스에 포커싱된 장소
    private Place detailFocusedPlace;

    //레이아웃 변수
    private View layout;

    private ConstraintLayout autoCompleteTextView_search_wrapper;
    private SlidingUpPanelLayout mapSlidingLayout;
    private ConstraintLayout map_frag_detail_box_wrapper;
    private ConstraintLayout navi_button_wrapper;
    private ConstraintLayout AR_button_wrapper;

    //검색창을 구성하는 레이아웃
    private ImageView map_frag_back;
    private ImageView map_frag_cancel;
    private ImageView map_frag_search_icon;
    private EditText editText_search;

    private RecyclerView searchKeywordRecyclerView;
    private SearchKeywordAdapter searchKeywordAdapter;
    public ArrayList<String> searchKeywordList;
    public ArrayList<Drawable> searchKeywordIconList;


    // 슬라이딩 패널
    private TextView map_frag_no_searchResult;

    // 추가 기능 버튼
    private ImageView navi_button;
    private ImageView AR_button;


    // 디테일 박스

    private ConstraintLayout map_frag_detail_box;

    // 디테일 박스 구성 레이아웃
    private ImageView map_frag_detail_image;
    private TextView map_frag_detail_title;
    private TextView map_frag_detail_sort;
    private TextView map_frag_detail_distance;
    private ConstraintLayout map_frag_detail_startButton;
    private ConstraintLayout map_frag_detail_endButton;

    //네비게이션 검색 박스
    private ConstraintLayout map_frag_navi_searchWrapper;
    private ImageView map_frag_navi_back;
    private EditText map_frag_navi_searchBar_Start;
    private EditText map_frag_navi_searchBar_End;
    private ImageView map_frag_navi_change;
    private TextView map_frag_navi_searchButton_now;


    //네비게이션 출발 목적지 저장 변수
    private String startPlaceCode = "NONE";
    private String endPlaceCode = "NONE";
    private LatLng startLocation = null;
    private LatLng endLocation = null;


    // 네비 브리핑 디테일 박스
    private ConstraintLayout map_frag_navi_detail_box_wrapper;
    private TextView map_frag_navi_route_button;
    private TextView map_frag_navi_detail_time;
    private TextView map_frag_navi_detail_distance;
    private TextView map_frag_navi_detail_stepCount;

    // 다음 강의실 찾기 변수


    // 다음 강의실 정보 결과 응답 POST 방식
    private Response.Listener<String> responseNextPlaceListener;

    // AR
    private String route;
    private int m_check_count = 0;
    private SensorManager m_sensor_manager;
    private Sensor m_ot_sensor;
    private double azimuth;

    private CustomARDialog dlg;


    public MapFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_sensor_manager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        // SensorManager 를 이용해서 방향 센서 객체를 얻는다.
        m_ot_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        editText_search.setText("");

        setMapFragmentMode(DEFAULT_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.map_fragment, container, false);


        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);
        mapFragment = this;

        //map_fragment를 기본적으로 구성하는 레이아웃들
        autoCompleteTextView_search_wrapper = layout.findViewById(R.id.autoCompleteTextView_search_wrapper);
        mapSlidingLayout = layout.findViewById(R.id.map_sliding_layout);
        map_frag_detail_box_wrapper = layout.findViewById(R.id.map_frag_detail_box_wrapper);
        navi_button_wrapper = layout.findViewById(R.id.navi_button_wrapper);
        AR_button_wrapper = layout.findViewById(R.id.AR_button_wrapper);

        //검색창을 구성하는 레이아웃
        map_frag_back = layout.findViewById(R.id.map_frag_back);
        map_frag_cancel = layout.findViewById(R.id.map_frag_cancel);
        map_frag_search_icon = layout.findViewById(R.id.map_frag_search_icon);
        editText_search = layout.findViewById(R.id.editText_search);


        // 슬라이딩 패널에 들어가는 spinner
        map_frag_no_searchResult = layout.findViewById(R.id.map_frag_no_searchResult);

        // 추가 기능 버튼
        navi_button = layout.findViewById(R.id.navi_button);
        AR_button = layout.findViewById(R.id.AR_button);

        // 디테일 박스
        map_frag_detail_box = layout.findViewById(R.id.map_frag_detail_box);

        // 디테일 박스 구성 레이아웃
        map_frag_detail_image = layout.findViewById(R.id.map_frag_detail_image);
        map_frag_detail_title = layout.findViewById(R.id.map_frag_detail_title);
        map_frag_detail_sort = layout.findViewById(R.id.map_frag_detail_sort);
        map_frag_detail_distance = layout.findViewById(R.id.map_frag_detail_distance);
        map_frag_detail_startButton = layout.findViewById(R.id.map_frag_detail_startButton);
        map_frag_detail_endButton = layout.findViewById(R.id.map_frag_detail_endButton);

        //네이게이션 검색 박스 레이아웃
        map_frag_navi_searchWrapper = layout.findViewById(R.id.map_frag_navi_searchWrapper);
        map_frag_navi_back = layout.findViewById(R.id.map_frag_navi_back);
        map_frag_navi_searchBar_Start = layout.findViewById(R.id.map_frag_navi_searchBar_Start);
        map_frag_navi_searchBar_End = layout.findViewById(R.id.map_frag_navi_searchBar_End);
        map_frag_navi_change = layout.findViewById(R.id.map_frag_navi_change);
        map_frag_navi_searchButton_now = layout.findViewById(R.id.map_frag_navi_searchButton_now);

        // 네비 브리핑 디테일 박스

        map_frag_navi_detail_box_wrapper = layout.findViewById(R.id.map_frag_navi_detail_box_wrapper);
        map_frag_navi_route_button = layout.findViewById(R.id.map_frag_navi_route_button);
        map_frag_navi_detail_time = layout.findViewById(R.id.map_frag_navi_detail_time);
        map_frag_navi_detail_distance = layout.findViewById(R.id.map_frag_navi_detail_distance);
        map_frag_navi_detail_stepCount = layout.findViewById(R.id.map_frag_navi_detail_stepCount);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //fetchLocation();


        //검색창 리사이클러뷰 설정

        searchKeywordRecyclerView = layout.findViewById(R.id.map_frag_search_keyword_recyclerView);
        searchKeywordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        searchKeywordList = new ArrayList<>();
        searchKeywordIconList = new ArrayList<>();

        searchKeywordList.add("header");
        searchKeywordList.add("식당");
        searchKeywordList.add("카페");
        searchKeywordList.add("편의점");
        searchKeywordList.add("편의시설");
        searchKeywordList.add("흡연실");
        searchKeywordList.add("야외시설");
        searchKeywordList.add("부속건물");
        searchKeywordList.add("footer");

        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_cancel_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_restaurant_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_local_cafe_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_local_convenience_store_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_print_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_smoking_rooms_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_sports_tennis_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_apartment_black_24dp, null));
        searchKeywordIconList.add(getResources().getDrawable(R.drawable.ic_cancel_black_24dp, null));


        searchKeywordAdapter = new SearchKeywordAdapter(searchKeywordList, searchKeywordIconList);
        searchKeywordRecyclerView.setAdapter(searchKeywordAdapter);


        searchKeywordAdapter.setOnItemClickListener(new SearchKeywordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                startSearch(searchKeywordList.get(position));

            }
        });


        //초기 모드 설정
        setMapFragmentMode(DEFAULT_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);


        //검색창의 포커스 여부 설정
        editText_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    map_frag_back.setVisibility(View.VISIBLE);
                    map_frag_cancel.setVisibility(View.VISIBLE);
                    map_frag_search_icon.setVisibility(View.INVISIBLE);

                }
            }
        });


        //검색창 검색 버튼 누를때
        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    startSearch(editText_search.getText().toString());

                }

                return false;
            }
        });


        //검색창 뒤로 가기 누를때
        map_frag_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (mapFragmentState) {

                    case DEFAULT_MODE:

                        if (editText_search.isFocused()) {
                            editText_search.clearFocus();
                            editText_search.setText("");

                            map_frag_back.setVisibility(View.INVISIBLE);
                            map_frag_cancel.setVisibility(View.INVISIBLE);
                            map_frag_search_icon.setVisibility(View.VISIBLE);

                            hideKeyboard(layout);
                            if (gMap != null) gMap.clear();
                            floatingMarkersOverlay.clearMarkers();
                        } else {
                            System.exit(0);
                        }

                        break;

                    case DIRECTPIN_MODE:
                    case DIRECTION_MODE:
                    case SEARCH_MODE:

                        setMapFragmentMode(DEFAULT_MODE, autoCompleteTextView_search_wrapper,
                                mapSlidingLayout, map_frag_detail_box_wrapper, map_frag_navi_searchWrapper,
                                navi_button_wrapper, AR_button_wrapper);

                        map_frag_back.setVisibility(View.INVISIBLE);
                        map_frag_cancel.setVisibility(View.INVISIBLE);
                        map_frag_search_icon.setVisibility(View.VISIBLE);

                        hideKeyboard(layout);
                        if (gMap != null) gMap.clear();
                        floatingMarkersOverlay.clearMarkers();

                        break;


                    case DETAIL_MODE:

                        setMapFragmentMode(SEARCH_MODE, autoCompleteTextView_search_wrapper,
                                mapSlidingLayout, map_frag_detail_box_wrapper, map_frag_navi_searchWrapper,
                                navi_button_wrapper, AR_button_wrapper);

                        if (pointedMarker != null) {
                            pointedMarker.remove();
                            pointedMarker = null;
                        }

                        break;

                }

            }
        });

        //검색창 클리어
        map_frag_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText_search.setText("");


            }
        });

        // 디테일 박스 리스너 설정

        ActivityResultLauncher<Intent> detailActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();

                            int CallType = intent.getIntExtra("CallType", 0);

                            switch (CallType) {

                                // 출발 버튼 눌렀을 때
                                case 1001:

                                    setFindRouteFromPlace(detailFocusedPlace);

                                    break;


                                // 도착 버튼 눌렀을 때

                                case 1002:

                                    setFindRouteToPlace(detailFocusedPlace);

                                    break;

                            }

                        }

                    }
                });

        map_frag_detail_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MapDetailActivity.class);

                intent.putExtra("placeCode", detailFocusedPlace.getPlaceCode());
                intent.putExtra("placeTitle", detailFocusedPlace.getTitle());
                intent.putExtra("placeSort", detailFocusedPlace.getSort());
                intent.putExtra("placeTime", detailFocusedPlace.getTime());
                intent.putExtra("placeCallNum", detailFocusedPlace.getCallNum());

                detailActivityResultLauncher.launch(intent);

            }

        });


        // 디테일 박스 출발 리스너

        map_frag_detail_startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setFindRouteFromPlace(detailFocusedPlace);

            }
        });

        // 디테일 박스 도착 리스너
        map_frag_detail_endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setFindRouteToPlace(detailFocusedPlace);

            }
        });


        //네비게이션 버튼 누를때
        navi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMapFragmentMode(DIRECTION_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                        map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

                if (gMap != null) gMap.clear();
                floatingMarkersOverlay.clearMarkers();

            }
        });

        //네비게이션 검색 박스 뒤로가기 버튼 누를때
        map_frag_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMapFragmentMode(DEFAULT_MODE, autoCompleteTextView_search_wrapper,
                        mapSlidingLayout, map_frag_detail_box_wrapper, map_frag_navi_searchWrapper,
                        navi_button_wrapper, AR_button_wrapper);


                map_frag_navi_searchBar_Start.setText("");
                map_frag_navi_searchBar_End.setText("");

                map_frag_back.setVisibility(View.INVISIBLE);
                map_frag_cancel.setVisibility(View.INVISIBLE);
                map_frag_search_icon.setVisibility(View.VISIBLE);

                hideKeyboard(layout);
                if (gMap != null) gMap.clear();
                floatingMarkersOverlay.clearMarkers();

            }
        });


        //네비게이션 검색 출발지 검색창 설정
        ActivityResultLauncher<Intent> mapNaviSearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {


                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent intent = result.getData();

                            int CallType = intent.getIntExtra("CallType", 0);

                            double latitude;
                            double longitude;


                            switch (CallType) {

                                case 1001:

                                    startPlaceCode = intent.getStringExtra("startPlaceCode");
                                    startLocation = new LatLng(intent.getDoubleExtra("startLatitude", 0), intent.getDoubleExtra("startLongitude", 0));
                                    String startPlaceTitle = intent.getStringExtra("startPlaceTitle");
                                    map_frag_navi_searchBar_Start.setText(startPlaceTitle);

                                    setStartMarker(startLocation);

                                    // 경로 그리는 메소드
                                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                        showBriefingDirection(naviInfo);

                                    }

                                    break;

                                case 1002:

                                    endPlaceCode = intent.getStringExtra("endPlaceCode");
                                    endLocation = new LatLng(intent.getDoubleExtra("endLatitude", 0), intent.getDoubleExtra("endLongitude", 0));
                                    String endPlaceTitle = intent.getStringExtra("endPlaceTitle");
                                    map_frag_navi_searchBar_End.setText(endPlaceTitle);

                                    setEndMarker(endLocation);

                                    // 경로 그리는 메소드
                                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                        showBriefingDirection(naviInfo);

                                    }

                                    break;

                                case 2001:

                                    map_frag_navi_searchBar_Start.setText("내 위치");

                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        return;
                                    }

                                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {

                                            if (location != null) {

                                                startLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                                startPlaceCode = "LOCATION";

                                                setStartMarker(startLocation);

                                                // 경로 그리는 메소드
                                                if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                                        !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                                    NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                                    showBriefingDirection(naviInfo);

                                                }


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

                                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                    return;
                                                }

                                                fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                                            }

                                        }
                                    });


                                    break;

                                case 2002:

                                    map_frag_navi_searchBar_End.setText("내 위치");


                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        return;
                                    }

                                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {

                                            if (location != null) {

                                                endLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                                endPlaceCode = "LOCATION";
                                                setEndMarker(endLocation);

                                                // 경로 그리는 메소드
                                                if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                                        !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                                    NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                                    showBriefingDirection(naviInfo);

                                                }


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

                                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                    return;
                                                }

                                                fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);


                                            }




                                        }
                                    });


                                    break;

                                case 3001:

                                    map_frag_navi_searchBar_Start.setText("직접 선택한 위치");

                                    latitude = intent.getDoubleExtra("latitude", 0);
                                    longitude = intent.getDoubleExtra("longitude", 0);

                                    startLocation = new LatLng(latitude, longitude);
                                    startPlaceCode = "LOCATION";

                                    setStartMarker(startLocation);

                                    // 경로 그리는 메소드
                                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                        showBriefingDirection(naviInfo);

                                    }

                                    break;


                                case 3002:

                                    map_frag_navi_searchBar_End.setText("직접 선택한 위치");


                                    latitude = intent.getDoubleExtra("latitude", 0);
                                    longitude = intent.getDoubleExtra("longitude", 0);

                                    endLocation = new LatLng(latitude, longitude);
                                    endPlaceCode = "LOCATION";

                                    setEndMarker(endLocation);

                                    // 경로 그리는 메소드
                                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                        showBriefingDirection(naviInfo);

                                    }

                                    break;

                            }

                        }

                    }
                });

        map_frag_navi_searchBar_Start.setClickable(true);
        map_frag_navi_searchBar_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MapNaviSearchActivity.class);
                intent.putExtra("SearchSort", "출발지 검색");
                mapNaviSearchActivityResultLauncher.launch(intent);

            }
        });

        //네비게이션 검색 도착지 검색창 누를때
        map_frag_navi_searchBar_End.setClickable(true);
        map_frag_navi_searchBar_End.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapNaviSearchActivity.class);
                intent.putExtra("SearchSort", "도착지 검색");
                mapNaviSearchActivityResultLauncher.launch(intent);
            }
        });

        //네비게이션 출발지 목적지 변경 누를때
        map_frag_navi_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmpPlaceCode = "NONE";

                tmpPlaceCode = startPlaceCode;
                startPlaceCode = endPlaceCode;
                endPlaceCode = tmpPlaceCode;

                LatLng tmpLocation = null;
                tmpLocation = startLocation;
                startLocation = endLocation;
                endLocation = tmpLocation;

                if (startLocation != null && endLocation == null) {

                    setStartMarker(startLocation);

                } else if (startLocation == null && endLocation != null) {

                    setEndMarker(endLocation);

                } else if (startLocation != null && endLocation != null) {

                    setStartMarker(startLocation);
                    setEndMarker(endLocation);

                } else {


                }

                String tmpPlaceTitle = map_frag_navi_searchBar_Start.getText().toString();
                map_frag_navi_searchBar_Start.setText(map_frag_navi_searchBar_End.getText().toString());
                map_frag_navi_searchBar_End.setText(tmpPlaceTitle);

                if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                        !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                    NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                    showBriefingDirection(naviInfo);

                }

            }
        });

        ActivityResultLauncher<Intent> showRouteActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();

                            int CallType = intent.getIntExtra("CallType", 0);


                            switch (CallType) {


                                case 4001:
                                    // 경로 안내를 완료했을 때

                                    Toast.makeText(getContext(), "목적지에 도착하였습니다.", Toast.LENGTH_LONG).show();
                                    break;


                            }

                        }

                    }
                });

        //경로 안내 시작 버튼 누를때

        map_frag_navi_route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MapNavigationActivity.class);
                intent.putExtra("endPlaceCode", endPlaceCode);
                intent.putExtra("endLocationLatitude", endLocation.latitude);
                intent.putExtra("endLocationLongitude", endLocation.longitude);
                showRouteActivityResultLauncher.launch(intent);

            }
        });


        //지금 강의실 찾기 버튼 누를 때

        //로그인 콜백 메소드
        ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();

                            String userEmail = intent.getStringExtra("userEmail");
                            String userMajor = intent.getStringExtra("userMajor");

                            //로그인 요청, 쿠키 저장

                            cookieManager.setCookie(url, "cookieKey=" + userEmail);
                            MainActivity.userMajor = userMajor;

                            MainActivity.autoLogin = true;

                            if (MainActivity.autoLogin) {

                                // 자동 로그인 데이터 저장
                                SharedPreferences auto = getContext().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLoginEdit = auto.edit();
                                autoLoginEdit.putString("userEmail", userEmail);
                                autoLoginEdit.putString("userMajor", MainActivity.userMajor);
                                autoLoginEdit.putBoolean("isAutoLogin", true);
                                autoLoginEdit.commit();

                            }

                            //GetNextPlaceBackgroundTask();

                            isLogin = true;

                            NextPlaceRequest nextPlaceRequest = new NextPlaceRequest(userEmail, responseNextPlaceListener);
                            RequestQueue queueNextPlaceAnalysis = Volley.newRequestQueue(getContext());
                            queueNextPlaceAnalysis.add(nextPlaceRequest);

                        }
                    }
                });


        map_frag_navi_searchButton_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //만약 로그인이 안되어 있으면

                isLogin = false;


                if (cookieManager.getCookie(url) == null || cookieManager.getCookie(url).equals("")) {

                    isLogin = false;

                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                            .setTitle("알림")
                            .setMessage("로그인이 필요합니다.")
                            .setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    loginActivityResultLauncher.launch(intent);

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog msgDlg = msgBuilder.create();
                    msgDlg.show();


                    return;

                } else {

                    isLogin = true;

                }


                //여기서 정보 불러오기. 무슨 정보? 다음 강의실의 플레이스 정보.

                //GetNextPlaceBackgroundTask();

                String userEmail = MainActivity.cookieManager.getCookie(url).replace("cookieKey=", "");

                NextPlaceRequest nextPlaceRequest = new NextPlaceRequest(userEmail, responseNextPlaceListener);
                RequestQueue queueNextPlaceAnalysis = Volley.newRequestQueue(getContext());
                queueNextPlaceAnalysis.add(nextPlaceRequest);


            }
        });

        //다음 강의실 정보 결과 응답 POST 방식
        responseNextPlaceListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {


                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {


                        // 정보 초기화

                        String nextPlaceCode = "";
                        String nextPlaceLocationString = "";
                        String nextPlaceTitle = "";
                        LatLng nextPlaceLocation = null;


                        nextPlaceCode = jsonResponse.getString("nextPlaceCode");
                        nextPlaceLocationString = jsonResponse.getString("nextPlaceLocationString");
                        nextPlaceTitle = jsonResponse.getString("nextPlaceTitle");

                        if (nextPlaceCode.substring(0, 2).equals("ZZ")) {

                            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                                    .setTitle("알림")
                                    .setMessage("사이버 강의입니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                            AlertDialog msgDlg = msgBuilder.create();
                            msgDlg.show();


                        } else if (nextPlaceCode.substring(0, 2).equals("NC")) {

                            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                                    .setTitle("알림")
                                    .setMessage("미추홀 캠퍼스는 지원하지 않습니다. ")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                            AlertDialog msgDlg = msgBuilder.create();
                            msgDlg.show();


                        } else {


                            String[] locationSplit = nextPlaceLocationString.split(",");

                            if (locationSplit.length == 2) {
                                nextPlaceLocation = new LatLng(Double.parseDouble(locationSplit[0]),
                                        Double.parseDouble(locationSplit[1]));
                            }

                            map_frag_navi_searchBar_Start.setText("내 위치");
                            map_frag_navi_searchBar_End.setText(nextPlaceTitle);

                            endPlaceCode = nextPlaceCode;

                            if (nextPlaceLocation != null) {
                                endLocation = nextPlaceLocation;
                            }

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }

                            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    if (location != null) {
                                        startLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                        startPlaceCode = "LOCATION";

                                        // 경로 그리는 메소드
                                        if (startLocation != null && endLocation != null && isLogin) {

                                            NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                            showBriefingDirection(naviInfo);

                                        }

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

                                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                            return;
                                        }

                                        fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                                    }


                                }
                            });

                        }


                    } else {

                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                                .setTitle("알림")
                                .setMessage("오늘 남은 강의 정보가 없습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                        AlertDialog msgDlg = msgBuilder.create();
                        msgDlg.show();

                    }


                } catch (Exception e) {


                }

            }

        };


        return layout;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();

        m_check_count = 0;
        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록한다.
        m_sensor_manager.registerListener(this, m_ot_sensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        m_sensor_manager.unregisterListener(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (getContext() != null) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                System.exit(0);
            }


        }


        gMap.setMyLocationEnabled(true);

        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        gMap.getUiSettings().setMapToolbarEnabled(false);

        // 마커 타이틀 오버레이
        floatingMarkersOverlay = layout.findViewById(R.id.map_floating_markers_overlay);
        floatingMarkersOverlay.setSource(googleMap);


        View locationButton = ((View) layout.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        View compassButton = ((View) layout.findViewWithTag("GoogleMapCompass"));
        RelativeLayout.LayoutParams rlpLocation = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        RelativeLayout.LayoutParams rlpCompass = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();


        // position on right bottom
        rlpLocation.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlpLocation.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        //rlpLocation.setMargins(0, DpToPixel(122), 0, 0);

        rlpCompass.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlpCompass.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        //rlpCompass.setMargins(0, DpToPixel(122), 0, 0);

        LatLngBounds australiaBounds = new LatLngBounds(
                new LatLng(37.37011619593982, 126.6264775804691), // SW bounds
                new LatLng(37.37958006018376, 126.63864407929854)  // NE bounds
        );

        gMap.setLatLngBoundsForCameraTarget(australiaBounds);


        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                if (!marker.getTag().equals("pointedMarker") && !marker.getTag().equals("directPinMarker")
                        && !marker.getTag().equals("startMarker") && !marker.getTag().equals("endMarker")) {

                    setMapFragmentMode(DETAIL_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                            map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

                    FindImage findImage = new FindImage();

                    map_frag_detail_image.setImageResource(findImage.getPlaceImageId(placeList.get((Integer.parseInt(marker.getTag().toString()))).getPlaceCode()));
                    map_frag_detail_title.setText(placeList.get((Integer.parseInt(marker.getTag().toString()))).getTitle());
                    map_frag_detail_sort.setText(placeList.get((Integer.parseInt(marker.getTag().toString()))).getSort());
                    map_frag_detail_distance.setText((int) (placeList.get((Integer.parseInt(marker.getTag().toString()))).getDistance()) + "m");

                    detailFocusedPlace = placeList.get(Integer.parseInt(marker.getTag().toString()));

                    if (pointedMarker != null) {
                        pointedMarker.remove();
                        pointedMarker = null;
                    }

                    pointedMarker = gMap.addMarker(new MarkerOptions().position(placeList.get((Integer.parseInt(marker.getTag().toString()))).getLocation()).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_picked)));
                    pointedMarker.setTag("pointedMarker");

                }

                return false;

            }
        });

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.37532099190484, 126.63285407077159), 17));
        //gMap.setBuildingsEnabled(false);
        if (getContext() != null) {
            gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        }


        //----------------------------------------------------------

        //뒤로 가기 버튼 리스너
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                switch (mapFragmentState) {

                    case DEFAULT_MODE:

                        if (editText_search.isFocused()) {
                            editText_search.clearFocus();
                            editText_search.setText("");

                            map_frag_back.setVisibility(View.INVISIBLE);
                            map_frag_cancel.setVisibility(View.INVISIBLE);
                            map_frag_search_icon.setVisibility(View.VISIBLE);

                            hideKeyboard(layout);
                            if (gMap != null) gMap.clear();
                            floatingMarkersOverlay.clearMarkers();

                        } else {

                            System.exit(0);

                        }

                        break;


                    case DIRECTPIN_MODE:
                    case SEARCH_MODE:
                    case DIRECTION_MODE:

                        setMapFragmentMode(DEFAULT_MODE, autoCompleteTextView_search_wrapper,
                                mapSlidingLayout, map_frag_detail_box_wrapper, map_frag_navi_searchWrapper,
                                navi_button_wrapper, AR_button_wrapper);

                        map_frag_back.setVisibility(View.INVISIBLE);
                        map_frag_cancel.setVisibility(View.INVISIBLE);
                        map_frag_search_icon.setVisibility(View.VISIBLE);


                        hideKeyboard(layout);
                        if (gMap != null) gMap.clear();
                        floatingMarkersOverlay.clearMarkers();

                        break;

                    case DETAIL_MODE:
                        setMapFragmentMode(SEARCH_MODE, autoCompleteTextView_search_wrapper,
                                mapSlidingLayout, map_frag_detail_box_wrapper, map_frag_navi_searchWrapper,
                                navi_button_wrapper, AR_button_wrapper);

                        if (pointedMarker != null) {
                            pointedMarker.remove();
                            pointedMarker = null;
                        }

                        break;


                }

            }
        };

        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        }


        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

                if (mapFragmentState == DEFAULT_MODE) {

                    setMapFragmentMode(DIRECTPIN_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                            map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

                    map_frag_detail_title.setText("직접 위치 지정");
                    map_frag_detail_sort.setText("");
                    map_frag_detail_distance.setText("");
                    map_frag_detail_image.setImageResource(0);

                    Place directPickPlace = new Place("LOCATION", "직접 선택한 위치", "", 0, latLng, "", "");

                    detailFocusedPlace = directPickPlace;

                    if (pointedMarker != null) {
                        pointedMarker.remove();
                        pointedMarker = null;
                    }

                    Marker marker = gMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_default)));
                    marker.setTag("directPinMarker");

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, gMap.getCameraPosition().zoom));


                }


            }
        });


        gMap.setPadding(0, DpToPixel(108), 0, 0);

    }


    public void hideKeyboard(View layout) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void openKeyboard(View layout) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(layout.findViewById(R.id.editText_search), 0);
    }


    // custom methods

    // default mode(only search bar), search mode( search bar, slidingPanel), detail mode ( search bar, detail box),
    // direction mode ( direction bar , direction information box)


    public void setMapFragmentMode(MapFragmentState state, ConstraintLayout searchBar, SlidingUpPanelLayout slidingPanel, ConstraintLayout detailBox
            , ConstraintLayout map_frag_navi_searchWrapper, ConstraintLayout naviButton, ConstraintLayout arButton) {

        switch (state) {

            case DEFAULT_MODE:

                if (gMap != null)
                    gMap.setPadding(0, DpToPixel(108), 0, 0);

                mapFragmentState = DEFAULT_MODE;

                editText_search.clearFocus();
                editText_search.setText("");


                searchBar.setVisibility(View.VISIBLE);
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                detailBox.setVisibility(View.GONE);
                map_frag_navi_searchWrapper.setVisibility(View.GONE);

                naviButton.setVisibility(View.VISIBLE);
                arButton.setVisibility(View.GONE);
                map_frag_navi_detail_box_wrapper.setVisibility(View.GONE);
                searchKeywordRecyclerView.setVisibility(View.VISIBLE);

                break;

            case SEARCH_MODE:

                if (gMap != null)
                    gMap.setPadding(0, DpToPixel(66), 0, DpToPixel(300));

                mapFragmentState = SEARCH_MODE;
                searchBar.setVisibility(View.VISIBLE);
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                detailBox.setVisibility(View.GONE);
                map_frag_navi_searchWrapper.setVisibility(View.GONE);

                naviButton.setVisibility(View.VISIBLE);

                arButton.setVisibility(View.GONE);
                map_frag_navi_detail_box_wrapper.setVisibility(View.GONE);
                searchKeywordRecyclerView.setVisibility(View.INVISIBLE);


                break;

            case DIRECTPIN_MODE:

                mapFragmentState = DIRECTPIN_MODE;

                if (gMap != null)
                    gMap.setPadding(0, DpToPixel(66), 0, DpToPixel(180));


                searchBar.setVisibility(View.VISIBLE);
                map_frag_back.setVisibility(View.VISIBLE);
                map_frag_search_icon.setVisibility(View.INVISIBLE);

                map_frag_detail_box.setClickable(false);
                map_frag_detail_box.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.layout_map_detail_box_non_click));

                if (gMap != null) gMap.clear();
                floatingMarkersOverlay.clearMarkers();

                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                detailBox.setVisibility(View.VISIBLE);
                map_frag_navi_searchWrapper.setVisibility(View.GONE);

                naviButton.setVisibility(View.VISIBLE);
                arButton.setVisibility(View.GONE);

                map_frag_navi_detail_box_wrapper.setVisibility(View.GONE);
                searchKeywordRecyclerView.setVisibility(View.INVISIBLE);


                break;

            case DETAIL_MODE:

                mapFragmentState = DETAIL_MODE;
                searchBar.setVisibility(View.VISIBLE);

                if (gMap != null)
                    gMap.setPadding(0, DpToPixel(66), 0, DpToPixel(180));

                map_frag_detail_box.setClickable(true);
                map_frag_detail_box.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.layout_map_detail_box));

                map_frag_back.setVisibility(View.VISIBLE);
                map_frag_search_icon.setVisibility(View.INVISIBLE);


                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                detailBox.setVisibility(View.VISIBLE);
                map_frag_navi_searchWrapper.setVisibility(View.GONE);

                naviButton.setVisibility(View.VISIBLE);
                arButton.setVisibility(View.GONE);

                map_frag_navi_detail_box_wrapper.setVisibility(View.GONE);
                searchKeywordRecyclerView.setVisibility(View.INVISIBLE);


                break;


            case DIRECTION_MODE:

                mapFragmentState = DIRECTION_MODE;

                if (gMap != null)
                    //Map.setPadding(0,DpToPixel(170), 0, DpToPixel(140));
                    gMap.setPadding(0, DpToPixel(182), 0, DpToPixel(0));


                map_frag_navi_searchBar_Start.setText("");
                map_frag_navi_searchBar_End.setText("");
                startPlaceCode = "NONE";
                endPlaceCode = "NONE";
                startLocation = null;
                endLocation = null;

                searchBar.setVisibility(View.GONE);
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                detailBox.setVisibility(View.GONE);
                map_frag_navi_searchWrapper.setVisibility(View.VISIBLE);

                naviButton.setVisibility(View.INVISIBLE);
                arButton.setVisibility(View.GONE);
                map_frag_navi_detail_box_wrapper.setVisibility(View.GONE);
                searchKeywordRecyclerView.setVisibility(View.INVISIBLE);


                break;


        }

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, DpToPixel(markerSize), DpToPixel(markerSize));
        Bitmap bitmap = Bitmap.createBitmap(DpToPixel(markerSize), DpToPixel(markerSize), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //dp에서 픽셀로 변환하는 메소드
    public int DpToPixel(int dp) {

        try {
            Resources r = getContext().getResources();

            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );

            return px;

        } catch (Exception e) {

        }

        return 0;

    }


    // 길찾기 브리핑
    public void showBriefingDirection(NaviInfo naviInfo) {

        Toast.makeText(getContext(), "경로 찾기 시작", Toast.LENGTH_SHORT).show();


        if (gMap != null)
            gMap.setPadding(0, DpToPixel(182), 0, DpToPixel(140));

        latLngList.clear();

        if (gMap != null)
            gMap.clear();

        GetRootBackgroundTask(naviInfo);

    }

    private static final int COLOR_BLACK_ARGB = 0xff02468E;
    private static final int POLYLINE_STROKE_WIDTH_PX = 14;

    // gMap 폴리라인 디자인
    private void stylePolyline(Polyline polyline) {

        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);


    }


    // 슬라이딩 패널에서 길찾기 버튼을 눌렀을 때 길찾기로 넘어가는 함수
    public void setFindRouteToPlaceFromMyLocation(Place place) {

        setMapFragmentMode(DIRECTION_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

        if (gMap != null) gMap.clear();
        floatingMarkersOverlay.clearMarkers();


        //내 위치는 출발지로, 도착지는 선택 아이템으로
        map_frag_navi_searchBar_Start.setText("내 위치");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {

                    startLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    startPlaceCode = "LOCATION";

                    endPlaceCode = place.getPlaceCode();
                    endLocation = place.getLocation();

                    String endPlaceTitle = place.getTitle();
                    map_frag_navi_searchBar_End.setText(endPlaceTitle);

                    setStartMarker(startLocation);
                    setEndMarker(endLocation);

                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                        showBriefingDirection(naviInfo);

                    }

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

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                }


            }
        });


    }

    // 특정 장소가 선택된 상태에서 출발 버튼을 눌렀을 때 길안내를 하는 함수 (여기서는 도착이 알아서 지정되지 않음)
    public void setFindRouteFromPlace(Place place) {

        setMapFragmentMode(DIRECTION_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

        if (gMap != null) gMap.clear();
        floatingMarkersOverlay.clearMarkers();

        startLocation = place.getLocation();
        startPlaceCode = place.getPlaceCode();

        endPlaceCode = "NONE";
        endLocation = null;

        String startPlaceTitle = place.getTitle();
        map_frag_navi_searchBar_Start.setText(startPlaceTitle);

        setStartMarker(startLocation);

        if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

            NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
            showBriefingDirection(naviInfo);

        }

    }


    // 특정 장소가 선택된 상태에서 도착 버튼을 눌렀을 때 길안내를 하는 함수 (여기서는 출발은 내위치로 알아서 지정됨)
    public void setFindRouteToPlace(Place place) {

        setMapFragmentMode(DIRECTION_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

        if (gMap != null) gMap.clear();
        floatingMarkersOverlay.clearMarkers();


        //내 위치는 출발지로, 도착지는 선택 아이템으로
        map_frag_navi_searchBar_Start.setText("내 위치");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {

                    startLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    startPlaceCode = "LOCATION";


                    endPlaceCode = place.getPlaceCode();
                    endLocation = place.getLocation();

                    String endPlaceTitle = place.getTitle();
                    map_frag_navi_searchBar_End.setText(endPlaceTitle);

                    setStartMarker(startLocation);
                    setEndMarker(endLocation);


                    if (!map_frag_navi_searchBar_Start.getText().toString().equals("") &&
                            !(map_frag_navi_searchBar_End.getText().toString().equals(""))) {

                        NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                        showBriefingDirection(naviInfo);

                    }

                } else {

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

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                }



            }
        });


    }

    public void setStartMarker(LatLng startLocation){

        if (startMarker != null) {
            startMarker.remove();
            startMarker = null;
        }

        if (gMap != null) {

            startMarker = gMap.addMarker(new MarkerOptions().position(startLocation).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_start)));
            startMarker.setTag("startMarker");
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation,gMap.getCameraPosition().zoom));

        }

    }

    public void setEndMarker(LatLng endLocation){

        if (endMarker != null) {
            endMarker.remove();
            endMarker = null;
        }

        if (gMap != null) {

            endMarker = gMap.addMarker(new MarkerOptions().position(endLocation).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_end)));
            endMarker.setTag("endMarker");
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLocation,gMap.getCameraPosition().zoom));

        }

    }

    public void startSearch(String keyword){

        editText_search.setText(keyword);
        editText_search.clearFocus();

        map_frag_back.setVisibility(View.VISIBLE);
        map_frag_cancel.setVisibility(View.INVISIBLE);
        map_frag_search_icon.setVisibility(View.INVISIBLE);

        hideKeyboard(layout);

        setMapFragmentMode(SEARCH_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);


        setSearchResultSlidingPanel();

        // 서버에 검색 정보 받아오기
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }else{

            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location != null){
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

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }

                            fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                        }

                    }

                });

            }catch (Exception e) {

            }

        }




    }

    public void setSearchResultSlidingPanel(){

        //리사이클러뷰 설정
        recyclerView = (RecyclerView) layout.findViewById(R.id.map_frag_recyclerview_sliding);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        placeList = new ArrayList<>();

        adapter = new MapSearchAdapter(placeList);
        recyclerView.setAdapter(adapter);

        // 검색 모드 슬라이딩 어댑터에 클릭 리스너 설정. 디테일 박스 모드로 넘어감.
        adapter.setOnItemClickListener(new MapSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                setMapFragmentMode(DETAIL_MODE, autoCompleteTextView_search_wrapper, mapSlidingLayout, map_frag_detail_box_wrapper,
                        map_frag_navi_searchWrapper, navi_button_wrapper, AR_button_wrapper);

                FindImage findImage = new FindImage();

                map_frag_detail_image.setImageResource(findImage.getPlaceImageId(placeList.get(position).getPlaceCode()));
                map_frag_detail_title.setText(placeList.get(position).getTitle());
                map_frag_detail_sort.setText(placeList.get(position).getSort());
                map_frag_detail_distance.setText((int) (placeList.get(position).getDistance()) + "m");

                detailFocusedPlace = placeList.get(position);

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeList.get(position).getLocation(), 17));

                if (pointedMarker != null) {
                    pointedMarker.remove();
                    pointedMarker = null;
                }

                if (gMap != null) {
                    pointedMarker = gMap.addMarker(new MarkerOptions().position(placeList.get(position).getLocation()).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_picked)));
                    pointedMarker.setTag("pointedMarker");
                }


            }

        });

        // 검색 모드에서 슬라이딩 패널에 있는 길찾기 버튼을 눌렀을 때 리스너 콜백

        adapter.setOnItemDirectionClickListener(new MapSearchAdapter.OnItemDirectionClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                detailFocusedPlace = placeList.get(position);
                setFindRouteToPlaceFromMyLocation(detailFocusedPlace);

            }

        });

        // 장소 검색 결과 가져오는 메소드


    }

    // 검색창에 검색어 쳤을 때
    Disposable searchBackgroundTask;

    void SearchBackgroundTask() {

        searchBackgroundTask = Observable.fromCallable(() -> {

            // doInBackground

            String searchKeyword = editText_search.getText().toString();
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



                placeList.clear();

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

                    placeList.add(place);

                    count++;

                }

                if(count == 0){

                    map_frag_no_searchResult.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                }else {

                    map_frag_no_searchResult.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                }


                adapter.notifyDataSetChanged();

                if (gMap != null) gMap.clear();
                floatingMarkersOverlay.clearMarkers();

                if (placeList != null && !placeList.isEmpty()) {

                    for (int i = 0; i < placeList.size(); i++) {

                        int color = Color.parseColor("#02468E");

                        MarkerInfo mi = new MarkerInfo(placeList.get(i).getLocation(), placeList.get(i).getTitle(), color);

                        if (gMap != null) {


                            Marker marker = gMap.addMarker(new MarkerOptions().position(mi.getCoordinates()).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_inumarker_default)));
                            marker.setTag(i);

                        }


                        //Adding the marker to track by the overlay
                        //To remove that marker, you will need to call floatingMarkersOverlay.removeMarker(id)
                        floatingMarkersOverlay.addMarker(i, mi);

                    }

                    if (gMap != null) {
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placeList.get(0).getLocation().latitude,
                                placeList.get(0).getLocation().longitude), 17));
                    }

                    gMap.setPadding(0,DpToPixel(66), 0, 0);

                }

            } catch (Exception e) {

            }

            searchBackgroundTask.dispose();

        });

    }

    ActivityResultLauncher<Intent> arResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        int CallType = intent.getIntExtra("CallType", 0);
                        switch (CallType) {
                            case 1001:
                                break;
                        }
                    }
                }
            });


    // 경로 가져오는 서버 통신 코드
    Disposable getRouteBackgroundTask;

    void GetRootBackgroundTask(NaviInfo naviInfo) {

        getRouteBackgroundTask = Observable.fromCallable(() -> {

            // doInBackground


            String target = (IpAddress.isTest ? "http://" + DemoIP_ClientTest + "/inuNavi/GetRootLive.php" :
                    "http://" + DemoIP + "/getRootLive")+ "?startPlaceCode=\"" + naviInfo.getStartPlaceCode() + "\"&endPlaceCode=\"" + naviInfo.getEndPlaceCode()
                    + "\"&startLocation=\"" + naviInfo.getStartLocation().latitude + "," + naviInfo.getStartLocation().longitude
                    + "\"&endLocation=\"" + naviInfo.getEndLocation().latitude + "," + naviInfo.getEndLocation().longitude + "\"";

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


                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;

                route = "";
                int time = 0;
                double dist = 0;
                int steps = 0;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    route = object.getString("route");
                    time = object.getInt("time");
                    dist = object.getDouble("dist");
                    steps = object.getInt("steps");

                    count++;

                }

                if(count == 0){


                }else {


                    map_frag_navi_detail_time.setText(time+"");
                    map_frag_navi_detail_distance.setText((int)dist+"m");
                    map_frag_navi_detail_stepCount.setText(steps+"걸음");

                    route.trim();
                    route.replaceAll(" ","");
                    String[] routeStringSplit = route.split(",");

                    for (int i=0;i<routeStringSplit.length;i+=2){

                        latLngList.add(new LatLng(Double.parseDouble(routeStringSplit[i])-0.00003,
                                Double.parseDouble(routeStringSplit[i+1])-0.00002));

                    }

                    if(latLngList != null && !latLngList.isEmpty()){
                        setStartMarker(latLngList.get(0));
                        setEndMarker(latLngList.get(latLngList.size()-1));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();


                        for (LatLng latLng : latLngList){
                            builder.include(latLng);
                        }

                        LatLngBounds bounds = builder.build();

                        if(gMap != null)
                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DpToPixel(48)));

                    }


                    PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).color(R.color.main_color);

                    if(gMap != null)
                        polyline = gMap.addPolyline(polylineOptions);
                    stylePolyline(polyline);

                    map_frag_navi_detail_box_wrapper.setVisibility(View.VISIBLE);

                    AR_button_wrapper.setVisibility(View.VISIBLE);

                    AR_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dlg = new CustomARDialog(getContext(), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(dlg!=null){
                                        dlg.dismiss();
                                    }


                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        return;

                                    }



                                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {

                                            if(location != null){
                                                JSONObject resultObj = new JSONObject();

                                                try {
                                                    resultObj.put("route", route);

                                                    Intent intent = new Intent(getActivity(), ARActivity.class);
                                                    intent.putExtra("arJson", resultObj.toString());
                                                    arResultLauncher.launch(intent);

                                        /*
                                        final Handler handler = new Handler(){
                                            @Override
                                            public void handleMessage(Message msg) {

                                                String json = resultObj.toString();
                                                UnityPlayer.UnitySendMessage("GameObject", "dataRecept", json);

                                            }
                                        };

                                        handler.sendEmptyMessageDelayed(0,6000);
                                        */

                                                } catch (JSONException e) {
                                                }
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

                                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                    return;
                                                }

                                                fusedLocationClient.requestLocationUpdates(mRequest, mLocationCallback, null);

                                            }

                                        }
                                    });


                                }
                            });

                            dlg.show();

                        }
                    });


                }


            } catch (Exception e) {

            }

            getRouteBackgroundTask.dispose();

        });

    }


    // 다음 시간의 강의 장소 코드와 좌표를 가져오는 서버 통신 코드
    /*Disposable getNextPlaceBackgroundTask;

    void GetNextPlaceBackgroundTask() {

        getNextPlaceBackgroundTask = Observable.fromCallable(() -> {

            // doInBackground


            String target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/GetNextPlace.php" :
                    "http://" + DemoIP + "/selectLecture")+ "?userEmail=\"" + MainActivity.cookieManager.getCookie(url).replace("cookieKey=", "") + "\"";


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



                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");


                String nextPlaceCode="";
                String nextPlaceLocationString="";
                String nextPlaceTitle="";
                LatLng nextPlaceLocation = null;

                int count = 0;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    nextPlaceCode = object.getString("nextPlaceCode");
                    nextPlaceLocationString = object.getString("nextPlaceLocationString");
                    nextPlaceTitle = object.getString("nextPlaceTitle");

                    count++;

                }

                if(count == 0){

                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                            .setTitle("알림")
                            .setMessage("다음 강의 정보가 없습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog msgDlg = msgBuilder.create(); msgDlg.show();

                }else {

                    String[] locationSplit = nextPlaceLocationString.split(",");

                    if(locationSplit.length == 2){
                        nextPlaceLocation = new LatLng(Double.parseDouble(locationSplit[0]),
                                Double.parseDouble(locationSplit[1]));
                    }

                    map_frag_navi_searchBar_Start.setText("내 위치");
                    map_frag_navi_searchBar_End.setText(nextPlaceTitle);

                    endPlaceCode = nextPlaceCode;

                    if(nextPlaceLocation != null){
                        endLocation = nextPlaceLocation;
                    }

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            startLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            startPlaceCode = "LOCATION";

                            // 경로 그리는 메소드
                            if (startLocation != null && endLocation!=null && isLogin) {

                                NaviInfo naviInfo = new NaviInfo(startPlaceCode, endPlaceCode, startLocation, endLocation);
                                showBriefingDirection(naviInfo);

                            }
                        }
                    });

                }

            } catch (Exception e) {

            }

            getNextPlaceBackgroundTask.dispose();

        });

    }*/

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }


    public void onSensorChanged(SensorEvent event)
    {
        // 방향 센서가 전달한 데이터인 경우

        // 첫번째 데이터인 방위값으로 문자열을 구성하여 텍스트뷰에 출력한다.
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            String str;
            azimuth = event.values[0];


        }

    }

}