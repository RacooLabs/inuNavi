package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.MainActivity.sessionURL;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ScheduleListActivity extends AppCompatActivity {


    private String sUrl = sessionURL;

    private ImageView tita_schedule_list_backButton;
    private TextView tita_schedule_list_totalCount;
    private TextView tita_schedule_list_totalScore;
    private ScheduleListAdapter scheduleListAdapter;
    private TextView tita_schedule_list_info;

    private int totalCount = 0;
    private int totalScore = 0;
    
    RecyclerView recyclerView;

    private ArrayList<Lecture> scheduleList = new ArrayList<>();

    private String userEmail = MainActivity.cookieManager.getCookie(sUrl).replace("cookieKey=", "");

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ScheduleListActivity.this, MainActivity.class);
        intent.putExtra("CallType", 2001);
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);


    }

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_schedule_list_activity);

        tita_schedule_list_backButton = findViewById(R.id.tita_schedule_list_backButton);
        tita_schedule_list_totalCount = findViewById(R.id.tita_schedule_list_totalCount);
        tita_schedule_list_totalScore = findViewById(R.id.tita_schedule_list_totalScore);
        tita_schedule_list_info = findViewById(R.id.tita_schedule_list_info);

        tita_schedule_list_info.setVisibility(View.VISIBLE);

        //돌아가기 버튼
        tita_schedule_list_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                Intent intent = new Intent(ScheduleListActivity.this, MainActivity.class);
                intent.putExtra("CallType", 2001);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        ScheduleBackgroundTask();


    }

    Disposable backgroundtask;

    public void ScheduleBackgroundTask() {

        String target = IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/ScheduleList.php":
                "http://" + DemoIP +"/user/select/class";

        scheduleList.clear();

        backgroundtask = Observable.fromCallable(() -> {
            // doInBackground

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

            }

            return null;

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(___ -> "{response : []}").subscribe((result) -> {


            // onPostExecute

            try {


                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;

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
                String realTime;


                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

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
                    realTime = object.getString("realTime");

                    lecturename = lecturename.replaceAll("\"", "");
                    classtime_raw = classtime_raw.trim();
                    classtime_raw = classtime_raw.replaceAll("\"", "");
                    classtime_raw = classtime_raw.replaceAll(" ", "");
                    classtime_raw = classtime_raw.replace("[", "");
                    classtime_raw = classtime_raw.replaceAll("]", "");

                    classroom_raw = classroom_raw.trim();
                    classroom_raw = classroom_raw.replaceAll("\"", "");


                    realTime = realTime.replaceAll("\"", "");

                    Lecture lecture = new Lecture(id, department, grade, category, number, lecturename,
                            professor, classroom_raw, classtime_raw, classroom, classtime, how, Integer.parseInt(point), realTime);

                    scheduleList.add(lecture);

                    totalCount ++;

                    try {
                        totalScore += Integer.parseInt(point);

                    }catch (Exception e){

                    }

                    count++;

                }

                if(count > 0){
                    tita_schedule_list_info.setVisibility(View.GONE);
                }else{
                    tita_schedule_list_info.setVisibility(View.VISIBLE);
                }

                tita_schedule_list_totalCount.setText("과목 수 " + totalCount);
                tita_schedule_list_totalScore.setText("총 학점 " + totalScore);

                recyclerView = findViewById(R.id.tita_schedule_list_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                scheduleListAdapter =  new ScheduleListAdapter(scheduleList, this);
                recyclerView.setAdapter(scheduleListAdapter);

                scheduleListAdapter.setOnItemClickListener(new ScheduleListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleListActivity.this);
                        dlg.setTitle("정보");
                        dlg.setMessage(scheduleList.get(position).getLecturename() + "을(를) 삭제하시겠습니까?");

                        dlg.setPositiveButton("삭제",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {

                                Response.Listener<String> responseListener = new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {

                                        try {



                                            JSONObject jsonResponse = new JSONObject(response);

                                            boolean success = jsonResponse.getBoolean("success");

                                            if (success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleListActivity.this);
                                                AlertDialog dialog = builder.setMessage("강의를 삭제하였습니다.").setPositiveButton("확인", null)
                                                        .create();
                                                dialog.show();

                                                totalCount --;
                                                totalScore -= scheduleList.get(position).getPoint();

                                                if(totalCount > 0){
                                                    tita_schedule_list_info.setVisibility(View.GONE);
                                                }else{
                                                    tita_schedule_list_info.setVisibility(View.VISIBLE);
                                                }

                                                scheduleList.remove(position);
                                                scheduleListAdapter.notifyDataSetChanged();

                                                tita_schedule_list_totalCount.setText("과목 수 " + totalCount);
                                                tita_schedule_list_totalScore.setText("총 학점 " + totalScore);


                                            }else{
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleListActivity.this);
                                                AlertDialog dialog = builder.setMessage("강의 삭제를 실패하였습니다.").setPositiveButton("확인", null)
                                                        .create();
                                                dialog.show();
                                            }

                                        } catch (Exception e) {


                                        }

                                    }

                                };

                                DeleteRequest deleteRequest = new DeleteRequest(userEmail, scheduleList.get(position).getNumber(),responseListener);
                                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                                queue.add(deleteRequest);

                            }
                        });

                        dlg.setNegativeButton("취소",new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which) {



                            }
                        });

                        dlg.show();

                    }
                });


            } catch (Exception e) {


            }

            backgroundtask.dispose();

        });

    }


}
