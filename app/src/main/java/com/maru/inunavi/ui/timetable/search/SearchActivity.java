package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.timetable.CalendarFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {



    ImageView tita_search_backButton;
    EditText tita_search_searchbar;
    TextView tita_search_option_button;
    RadioGroup tita_search_radioGroup;
    TextView tita_search_info;

    private String main_keyword = "";
    private String keyword_option = "과목명";
    private String target;
    private SearchAdapter adapter;

    RecyclerView recyclerView;

    private ArrayList<Lecture> lectureList = new ArrayList<>();


    public static ArrayList<String> gradeList = new ArrayList<String>(Arrays.asList("1학년", "2학년", "3학년", "4학년" ));
    //public static ArrayList<String> categoryList = CalendarFragment.categoryList;
    public static ArrayList<String> scoreList = new ArrayList<String>(Arrays.asList("1학점", "2학점", "3학점", "4학점"));

    public static ArrayList<String> selectionCategoryList = new ArrayList<>();
    public static ArrayList<String> selectionGradeList = new ArrayList<>();
    public static ArrayList<String> selectionScoreList = new ArrayList<>();

    public static String main_keyword_option;
    public static boolean[] radioList;
    public static String major_option;
    public static String cse_option;
    public static String sort_option;
    public static String grade_option;
    public static String category_option;
    public static String score_option;


    @Override
    public void onBackPressed() {


        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.putExtra("CallType", 2001);
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);


    }

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity_search);

        tita_search_backButton = findViewById(R.id.tita_search_backButton);
        tita_search_searchbar = findViewById(R.id.tita_search_searchbar);
        tita_search_option_button = findViewById(R.id.tita_search_option_button);
        tita_search_radioGroup = findViewById(R.id.tita_search_radioGroup);
        tita_search_info = findViewById(R.id.tita_search_info);

        selectionCategoryList.clear();
        selectionGradeList.clear();
        selectionScoreList.clear();

        selectionCategoryList.addAll(CalendarFragment.categoryList);
        selectionGradeList.addAll(gradeList);
        selectionScoreList.addAll(scoreList);

        main_keyword_option = "";
        radioList= new boolean[]{true, false, false, false};
        major_option = "전체";
        cse_option = "전체";
        sort_option =  "기본";
        grade_option = "전체";
        category_option = "전체";
        score_option = "전체";

        //돌아가기 버튼
        tita_search_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {


                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("CallType", 2001);
                setResult(Activity.RESULT_OK, intent);

                finish();
                overridePendingTransition(0, 0);

            }
        });



        tita_search_searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    if(tita_search_searchbar.getText() == null){
                        main_keyword = "";
                    }else{
                        main_keyword = tita_search_searchbar.getText().toString().trim();
                    }


                    target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/LectureList.php" :
                            "http://" + DemoIP + "/selectLecture")+ "?main_keyword=\"" + main_keyword + "\"&keyword_option=\"" + keyword_option
                             + "\"&major_option=\"전체\"" + "&cse_option=\"전체\"" + "&sort_option=\"기본\"" + "&grade_option=\"전체\"" + "&category_option=\"전체\"" + "&score_option=\"전체\"";



                    SearchBackgroundTask();

                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(tita_search_searchbar.getWindowToken(), 0);

                }

                return false;
            }
        });

        
        //더 많은 옵션 콜백 리스너
        ActivityResultLauncher<Intent> searchOptionActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent intent = result.getData();
                            int CallType = intent.getIntExtra("CallType", 0);

                            String main_keyword = intent.getStringExtra("main_keyword_option");
                            String keyword_option = intent.getStringExtra("keyword_option");
                            String major_option = intent.getStringExtra("major_option");
                            String cse_option = intent.getStringExtra("cse_option");
                            String sort_option = intent.getStringExtra("sort_option");
                            String grade_option = intent.getStringExtra("grade_option");
                            String category_option = intent.getStringExtra("category_option");
                            String score_option = intent.getStringExtra("score_option");


                            target = (IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/LectureList.php" :
                                    "http://" + DemoIP + "/selectLecture")+ "?main_keyword=\"" + main_keyword + "\"&keyword_option=\"" + keyword_option
                                    + "\"&major_option=\"" + major_option + "\"&cse_option=\""+ cse_option +"\"&sort_option=\"" + sort_option + "\"&grade_option=\"" + grade_option +
                                    "\"&category_option=\"" + category_option +"\"&score_option=\"" + score_option +"\"";






                            tita_search_radioGroup.check(R.id.tita_search_radioButton1);
                            tita_search_searchbar.setText("");

                            SearchBackgroundTask();

                        }
                    }
                });


        
        //더 많은 검색 옵션 버튼
        tita_search_option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SearchActivity.this, SearchOptionActivity.class);
                searchOptionActivityResultLauncher.launch(intent);

            }
        });


        //키워드 옵션
        tita_search_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.tita_search_radioButton1:
                        keyword_option = "과목명";
                        break;

                    case R.id.tita_search_radioButton2:
                        keyword_option = "교수명";
                        break;

                    case R.id.tita_search_radioButton3:
                        keyword_option = "과목코드";
                        
                }
            }
        });

        recyclerView = findViewById(R.id.tita_search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter =  new SearchAdapter(lectureList, this);
        recyclerView.setAdapter(adapter);


    }

    Disposable backgroundtask;


    void SearchBackgroundTask() {

        backgroundtask = Observable.fromCallable(() -> {
            // doInBackground

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


                lectureList.clear();
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

                while(count < jsonArray.length()){
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
                    realTime = realTime.replaceAll("\"", "");

                    classroom_raw = classroom_raw.trim();
                    classroom_raw = classroom_raw.replaceAll("\"", "");


                    Lecture lecture = new Lecture(id, department, grade, category, number, lecturename,
                            professor, classroom_raw, classtime_raw, classroom, classtime, how, Integer.parseInt(point), realTime);
                    lectureList.add(lecture);
                    count++;

                }

                if(count == 0){

                    tita_search_info.setVisibility(View.VISIBLE);
                    tita_search_info.setText("조회된 강의가 없습니다.");

                }else{

                    tita_search_info.setVisibility(View.INVISIBLE);

                }

                adapter.notifyDataSetChanged();


            } catch (Exception e) {
            }

            backgroundtask.dispose();

        });


    }




}
