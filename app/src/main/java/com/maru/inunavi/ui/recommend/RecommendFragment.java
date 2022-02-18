package com.maru.inunavi.ui.recommend;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.MainActivity.cookieManager;
import static com.maru.inunavi.MainActivity.sessionURL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.timetable.search.Lecture;
import com.maru.inunavi.ui.timetable.search.Schedule;
import com.maru.inunavi.user.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RecommendFragment extends Fragment {

    private String sUrl = sessionURL;

    private RecyclerView recyclerView;
    private RecommendAdapter adapter;
    private View root;

    private ArrayList<Lecture> recommendListType0 = new ArrayList<>();
    private ArrayList<Lecture> recommendListType1 = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        root = inflater.inflate(R.layout.recommend_fragment, container, false);
        LinearLayout frag_satisfied_login_box = root.findViewById(R.id.frag_recommend_login_box);

        ConstraintLayout constraint_frag_recommend_main = root.findViewById(R.id.constraint_frag_recommend_main);
        Button button_frag_recommend_login = root.findViewById(R.id.button_frag_recommend_login);

        recyclerView = (RecyclerView)root.findViewById(R.id.frag_recommend_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;

        ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();

                            int CallType = intent.getIntExtra("CallType", 2);
                            String userEmail = intent.getStringExtra("userEmail");

                            //로그인 요청, 쿠키 저장

                            cookieManager.setCookie(sUrl,"cookieKey="+userEmail);
                            frag_satisfied_login_box.setVisibility(View.GONE);
                            constraint_frag_recommend_main.setVisibility(View.VISIBLE);

                            MainActivity.autoLogin = true;
                            if(MainActivity.autoLogin) {
                                // 자동 로그인 데이터 저장
                                SharedPreferences auto = getContext().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLoginEdit = auto.edit();
                                autoLoginEdit.putString("userEmail", userEmail);
                                autoLoginEdit.putBoolean("isAutoLogin", true);
                                autoLoginEdit.commit();

                            }

                            RecommendBackgroundTask();

                        }
                    }
                });


        button_frag_recommend_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                loginActivityResultLauncher.launch(intent);

            }
        });

        if(cookieManager.getCookie(sUrl) != null && !cookieManager.getCookie(sUrl).equals("")){


            frag_satisfied_login_box.setVisibility(View.GONE);
            constraint_frag_recommend_main.setVisibility(View.VISIBLE);

            // 정보 초기화

            RecommendBackgroundTask();



        }else{

            //로그인 버튼 리스너
            frag_satisfied_login_box.setVisibility(View.VISIBLE);
            constraint_frag_recommend_main.setVisibility(View.INVISIBLE);

        }

        return root;
        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    Disposable recommendBackgroundTask;

    // 기존 시간표 불러오기.
    void RecommendBackgroundTask() {

        recommendListType0 = new ArrayList<>();
        recommendListType1 = new ArrayList<>();

        recommendBackgroundTask = Observable.fromCallable(() -> {
            // doInBackground

            String target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/RecommendList.php" :
                    "http://" + DemoIP + "/selectLecture");

            try {
                URL url = new URL(target);

                //HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("email", MainActivity.cookieManager.getCookie(sUrl).replace("cookieKey=", ""));

                StringBuilder postData = new StringBuilder();
                for(Map.Entry<String,Object> param : params.entrySet()) {
                    if(postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                httpURLConnection.setDoOutput(true);
                httpURLConnection.getOutputStream().write(postDataBytes);

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
                e.printStackTrace();
                Log.d("@@@search adapter 229", e.toString());
            }

            return null;

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(___ -> "{response : []}").subscribe((result) -> {

            // onPostExecute

            try {

                Log.d("@@@search adapter 258", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;

                int type;
                int id;
                String department;
                String grade;
                String category;
                String number;
                String lecturename;
                String professor;
                String classroom_raw;
                String classtime_raw;
                String classroom;
                String classtime;
                String how;
                String point;



                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    type = object.getInt("type");
                    id = object.getInt("id");
                    department = object.getString("department");
                    grade = object.getString("grade");
                    category = object.getString("category");
                    number = object.getString("number");
                    lecturename = object.getString("lecturename");
                    professor = object.getString("professor");
                    classroom_raw = object.getString("classroom_raw");
                    classtime_raw = object.getString("classtime_raw");
                    classroom = object.getString("classroom");
                    classtime = object.getString("classtime");
                    how = object.getString("how");
                    point = object.getString("point");

                    classtime_raw = classtime_raw.trim();
                    classtime_raw = classtime_raw.replaceAll("\"", "");
                    classtime_raw = classtime_raw.replaceAll(" ", "");
                    classtime_raw = classtime_raw.replace("[", "");
                    classtime_raw = classtime_raw.replaceAll("]", "");

                    Lecture lecture = new Lecture(id, department, Integer.parseInt(grade), category, number, lecturename,
                            professor, classroom_raw, classtime_raw, classroom, classtime, how, Integer.parseInt(point));


                    if(type == 0){
                        recommendListType0.add(lecture);
                    }else if (type == 1){
                        recommendListType1.add(lecture);
                    }

                    count++;

                }

                ArrayList<Lecture> totalRecommendList = new ArrayList<>();

                totalRecommendList.add(new Lecture(0,"",0,"","","거리 맞춤 추천","","","", "","","",0));
                totalRecommendList.addAll(recommendListType0);
                totalRecommendList.add(new Lecture(0,"",0,"","","개인 맞춤 추천","","","", "","","",0));
                totalRecommendList.addAll(recommendListType1);


                adapter = new RecommendAdapter(totalRecommendList, getActivity());
                recyclerView.setAdapter(adapter);


            } catch (Exception e) {
                e.printStackTrace();
            }

            recommendBackgroundTask.dispose();


        });

    }

}