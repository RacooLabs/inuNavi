package com.maru.inunavi.ui.map;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.map.markerinfo.MarkerInfo;
import com.maru.inunavi.ui.recommend.RecommendAdapter;
import com.maru.inunavi.ui.timetable.SettingAdapter;
import com.maru.inunavi.ui.timetable.search.Lecture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MapDetailActivityAdapter adapter;

    private ArrayList<Place> surroundingPlaceList = new ArrayList<>();

    private TextView map_frag_detail_nearPlaceText;


    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment_detail_activity);


        ImageView map_frag_detail_activity_backButton = findViewById(R.id.map_frag_detail_activity_backButton);

        Intent intent = getIntent();

        String placeCode = intent.getStringExtra("placeCode");
        String placeTitle = intent.getStringExtra("placeTitle");
        String placeSort = intent.getStringExtra("placeSort");
        String placeTime = intent.getStringExtra("placeTime");
        String placeCallNum = intent.getStringExtra("placeCallNum");

        // 레이아웃 바인딩

        ImageView map_frag_detail_activity_img = findViewById(R.id.map_frag_detail_activity_img);
        TextView map_frag_detail_activity_title = findViewById(R.id.map_frag_detail_activity_title);
        TextView map_frag_detail_activity_sort = findViewById(R.id.map_frag_detail_activity_sort);
        TextView map_frag_detail_activity_time = findViewById(R.id.map_frag_detail_activity_time);
        TextView map_frag_detail_activity_callNum = findViewById(R.id.map_frag_detail_activity_callNum);
        ConstraintLayout map_frag_detail_activity_startButton = findViewById(R.id.map_frag_detail_activity_startButton);
        ConstraintLayout map_frag_detail_activity_endButton = findViewById(R.id.map_frag_detail_activity_endButton);
        TextView map_frag_detail_activity_callButton = findViewById(R.id.map_frag_detail_activity_callButton);

        //map_frag_detail_nearPlaceText = findViewById(R.id.map_frag_detail_nearPlaceText);

        
        //돌아가기 버튼
        map_frag_detail_activity_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });


        //이미지 로딩
        FindImage findImage = new FindImage();
        map_frag_detail_activity_img.setImageResource(findImage.getPlaceImageId(placeCode));


        // 출발 버튼 눌렀을 때
        map_frag_detail_activity_startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MapDetailActivity.this, MainActivity.class);
                intent.putExtra("CallType", 1001);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);


            }
        });

        // 도착 버튼 눌렀을 때
        map_frag_detail_activity_endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MapDetailActivity.this, MainActivity.class);
                intent.putExtra("CallType", 1002);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);

            }
        });

        //전화 걸기 버튼 눌렀을 때
        map_frag_detail_activity_callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + placeCallNum));
                startActivity(tt);

            }
        });


        // 장소 정보 출력
        map_frag_detail_activity_title.setText(placeTitle);
        map_frag_detail_activity_sort.setText(placeSort);
        map_frag_detail_activity_time.setText(placeTime);

        if(placeCallNum.isEmpty() || placeCallNum.equals("-")){
            map_frag_detail_activity_callNum.setText("-");
            map_frag_detail_activity_callButton.setVisibility(View.INVISIBLE);

        }else{
            map_frag_detail_activity_callNum.setText(PhoneNumberUtils.formatNumber(placeCallNum, Locale.getDefault().getCountry()));
            map_frag_detail_activity_callButton.setVisibility(View.VISIBLE);
        }

       /* recyclerView = findViewById(R.id.map_frag_detail_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)) ;

        adapter = new MapDetailActivityAdapter(surroundingPlaceList);

        recyclerView.setAdapter(adapter);



        adapter.setOnItemClickListener(new MapDetailActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(MapDetailActivity.this, MapDetailActivity.class);

                intent.putExtra("placeCode", surroundingPlaceList.get(position).getPlaceCode());
                intent.putExtra("placeTitle", surroundingPlaceList.get(position).getTitle());
                intent.putExtra("placeSort", surroundingPlaceList.get(position).getSort());
                intent.putExtra("placeTime", surroundingPlaceList.get(position).getTime());
                intent.putExtra("placeCallNum", surroundingPlaceList.get(position).getCallNum());
                finish();

            }
        });

        AroundPlaceBackgroundTask(placeCode);*/

    }
/*

    // 서버에 코드 넘겨줘서 주변 장소 받기
    Disposable surroundingPlaceBackgroundTask;

    void AroundPlaceBackgroundTask(String currentPlaceCode) {

        surroundingPlaceBackgroundTask = Observable.fromCallable(() -> {

            // doInBackground

            String target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/GetSurroundingPlace.php" :
                    "http://" + DemoIP + "/placeSearchList")+ "?currentPlaceCode=\"" + currentPlaceCode + "\"";

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

                surroundingPlaceList.clear();

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

                    surroundingPlaceList.add(place);

                    count++;

                }

                if(count == 0){
                    map_frag_detail_nearPlaceText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    map_frag_detail_nearPlaceText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();


            } catch (Exception e) {

            }

            surroundingPlaceBackgroundTask.dispose();

        });

    }
*/


}
