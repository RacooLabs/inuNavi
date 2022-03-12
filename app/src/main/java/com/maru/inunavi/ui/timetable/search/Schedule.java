package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.MainActivity.sessionURL;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.timetable.CalendarFragment;
import com.maru.inunavi.ui.timetable.SettingAdapter;

import org.json.JSONObject;

import java.util.ArrayList;


public class Schedule {


    private String url = sessionURL;

    public int colorCount = 0;
    public ArrayList<Lecture> lectureScheduleList = new ArrayList<>();
    public Lecture lectureSchedule[] = new Lecture[328];
    public String colors[] = {
            "#5d9134",
            "#257199",
            "#FC6935",
            "#cf9a3a",
            "#a33c4f",
            "#536174",
            "#a18c3b",
            "#694e33",
            "#798a0e",
            "#8D1E1F",
            "#3E454C",
            "#2986C3",
            "#822fbd",
            "#404040",
            "#634b47",
            "#1E588F",
            "#4F2C54",
            "#DF1B2E",
            "#00B19B",
            "#E85022"};

    private String userEmail;

    public void addSchedule(Lecture lecture){

        lectureScheduleList.add(lecture);

        if(lecture.getClasstime().equals("-") ||lecture.getClasstime().isEmpty()){

        }else{

            int startTime;
            int endTime;

            String classTime = lecture.getClasstime();
            String [] tokens=classTime.split(",");


            for(int i=0;i<tokens.length; i++){

                String startEndTime[] = tokens[i].split("-");
                startTime = Integer.parseInt(startEndTime[0]);
                endTime = Integer.parseInt(startEndTime[1]);

                for(int j=startTime; j<endTime+1;j++){

                    lectureSchedule[j] = lecture;
                    lectureSchedule[j].setColor(colors[colorCount]);
                }

            }

            colorCount++;

            if(colorCount == colors.length){
                colorCount = 0;
            }

        }

    }

    public void ResetSchedule() {

        lectureScheduleList.clear();
        lectureSchedule = new Lecture[328];
        colorCount = 0;

    }


    public boolean validate(String lectureTime){

        int startTime;
        int endTime;

        String classTime = lectureTime;

        if(classTime.equals("-") || classTime.isEmpty()){

            return true;

        }else{

            String [] tokens=classTime.split(",");

            for(int i=0;i<tokens.length; i++){

                String startEndTime[] = tokens[i].split("-");
                startTime = Integer.parseInt(startEndTime[0]);
                endTime = Integer.parseInt(startEndTime[1]);

                for(int j=startTime; j<endTime+1;j++){
                    if(lectureSchedule[j] != null){
                        return false;
                    }
                }

            }

            return true;

        }

    }

    public boolean alreadyIn(String lectureNumber){

        String classNumber = lectureNumber;

        for(int i =0;i<lectureSchedule.length;i++){

            if (lectureSchedule[i] != null) {
                if (classNumber.equals(lectureSchedule[i].getNumber())) {
                    return true;
                }
            }
        }

        for(int i =0;i<lectureScheduleList.size();i++){

            if(lectureScheduleList.get(i)!=null){
                if (classNumber.equals(lectureScheduleList.get(i).getNumber())){
                    return true;
                }
            }

        }

        return false;

    }


    public void setting(TextView[] schedule_textView, RelativeLayout container, Context context, CalendarFragment calendarFragment){

        userEmail = MainActivity.cookieManager.getCookie(url).replace("cookieKey=", "");

        int positionX = 0;
        int positionY = 0;

        int sameCount = 1;

        if(context == null){
            return;
        }

        for(int i=0;i<schedule_textView.length;i++){

            if(this.lectureSchedule[i] != null){


            //schedule_textView[i].setText(lectureSchedule[i].getLecturename());
            //schedule_textView[i].setBackgroundColor(Color.BLACK);
                if (!((i>=0 && i<=15 ) || (i>=48 && i<=63) || (i>=96 && i<=111) || (i>=144 && i<=159) || (i>=192 && i<=207) || (i>=240 && i<=255) || (i>=288 && i<=335))){

                    if (sameCount == 1) {


                        positionX = (int) schedule_textView[i].getX();
                        positionY = (int) schedule_textView[i].getY();
                    }

                    if (this.lectureSchedule[i+1] == null){

                        TextView textView = new TextView(context);
                        textView.setText(lectureSchedule[i].getLecturename());
                        textView.setTextColor(Color.WHITE);
                        textView.setBackgroundColor(Color.parseColor(lectureSchedule[i].getColor()));

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(schedule_textView[i].getWidth(),
                                schedule_textView[i].getHeight() * sameCount);
                        lp.gravity = Gravity.CENTER;

                        textView.setX(positionX);
                        textView.setY(positionY);
                        textView.setLayoutParams(lp);
                        textView.setPadding(4,4,4,0);
                        container.addView(textView);

                        String lectureName = lectureSchedule[i].getLecturename();
                        String lectureNumber = lectureSchedule[i].getNumber();
                        String professor = lectureSchedule[i].getProfessor();
                        String realClassTime = lectureSchedule[i].getRealTime();
                        String classRoom = lectureSchedule[i].getClassroom();

                        textView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                                dlg.setTitle(lectureName);
                                dlg.setMessage(professor + "\n" + classRoom+ "\n" +
                                        realClassTime);


                                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                dlg.setNegativeButton("삭제",new DialogInterface.OnClickListener(){

                                    public void onClick(DialogInterface dialog, int which) {

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {

                                                try {



                                                    JSONObject jsonResponse = new JSONObject(response);

                                                    boolean success = jsonResponse.getBoolean("success");

                                                    if (success) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        AlertDialog dialog = builder.setMessage("강의를 삭제하였습니다.").setPositiveButton("확인", null)
                                                                .create();
                                                        dialog.show();

                                                        container.removeAllViewsInLayout();
                                                        calendarFragment.ScheduleBackgroundTask();



                                                    }else{
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        AlertDialog dialog = builder.setMessage("강의 삭제를 실패하였습니다.").setPositiveButton("확인", null)
                                                                .create();
                                                        dialog.show();
                                                    }

                                                } catch (Exception e) {


                                                }

                                            }

                                        };

                                        DeleteRequest deleteRequest = new DeleteRequest(userEmail, lectureNumber,responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        queue.add(deleteRequest);



                                    }
                                });

                                dlg.show();

                            }
                        });

                        sameCount = 1;

                    }else if (!this.lectureSchedule[i].getNumber().equals(this.lectureSchedule[i+1].getNumber())){

                        TextView textView = new TextView(context);
                        textView.setText(lectureSchedule[i].getLecturename());
                        textView.setTextColor(Color.WHITE);
                        textView.setBackgroundColor(Color.parseColor(lectureSchedule[i].getColor()));

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(schedule_textView[i].getWidth(),
                                schedule_textView[i].getHeight() * sameCount);
                        lp.gravity = Gravity.CENTER;

                        textView.setX(positionX);
                        textView.setY(positionY);
                        textView.setLayoutParams(lp);
                        container.addView(textView);

                        String lectureName = lectureSchedule[i].getLecturename();
                        String lectureNumber = lectureSchedule[i].getNumber();
                        String professor = lectureSchedule[i].getProfessor();
                        String realClassTime = lectureSchedule[i].getRealTime();
                        String classRoom = lectureSchedule[i].getClassroom();

                        textView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                                dlg.setTitle(lectureName); //제목
                                dlg.setMessage(professor + "\n" + classRoom+ "\n" +
                                        realClassTime);
//                버튼 클릭시 동작
                                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                dlg.setNegativeButton("삭제",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {

                                                try {



                                                    JSONObject jsonResponse = new JSONObject(response);

                                                    boolean success = jsonResponse.getBoolean("success");

                                                    if (success) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        AlertDialog dialog = builder.setMessage("강의를 삭제하였습니다.").setPositiveButton("확인", null)
                                                                .create();
                                                        dialog.show();

                                                        container.removeAllViewsInLayout();

                                                        calendarFragment.ScheduleBackgroundTask();


                                                    }else{
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        AlertDialog dialog = builder.setMessage("강의 삭제를 실패하였습니다.").setPositiveButton("확인", null)
                                                                .create();
                                                        dialog.show();
                                                    }

                                                } catch (Exception e) {


                                                }

                                            }

                                        };

                                        DeleteRequest deleteRequest = new DeleteRequest(userEmail, lectureNumber,responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        queue.add(deleteRequest);


                                    }
                                });

                                dlg.show();

                            }
                        });

                        sameCount = 1;

                    }else{

                        sameCount++;

                    }

                }


            }

        }

    }

}
