package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.MainActivity.sessionURL;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maru.inunavi.IpAddress;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;
import com.maru.inunavi.ui.timetable.CalendarFragment;
import com.maru.inunavi.user.SignUpActivity;
import com.maru.inunavi.user.SignUpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    SearchActivity parent;

    private String userEmail = "";
    private Schedule schedule = new Schedule();

    private OnItemClickListener mListener = null ;
    private ArrayList<Lecture> mData = null;

    private String sUrl = sessionURL;


    String target;

    {
        target = IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/ScheduleList.php":
                "http://" + DemoIP +"/user/select/class";
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }



    public void setOnItemClickListener(OnItemClickListener listener) {

        this.mListener = listener ;

    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView_search_list_lectureName;
        TextView textView_search_list_professor;
        TextView textView_search_list_info;
        TextView textView_search_list_time;
        TextView tita_search_add;


        ViewHolder(Context context, View itemView) {

            super(itemView);

            textView_search_list_lectureName = itemView.findViewById(R.id.textView_search_list_lectureName);
            textView_search_list_professor = itemView.findViewById(R.id.textView_search_list_professor);
            textView_search_list_info = itemView.findViewById(R.id.textView_search_list_info);
            textView_search_list_time = itemView.findViewById(R.id.textView_search_list_time);
            tita_search_add = itemView.findViewById(R.id.tita_search_add);


        }

    }

    public SearchAdapter(ArrayList<Lecture> list, SearchActivity parent) {
        mData = list;
        this.parent = parent;
        schedule = new Schedule();
        GetUserTableInfoBackgroundTask();

    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.timetable_search_item_list, parent, false);
        SearchAdapter.ViewHolder viewHolder = new SearchAdapter.ViewHolder(context, view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {

        holder.textView_search_list_lectureName.setText(mData.get(position).getLecturename());
        holder.textView_search_list_professor.setText(mData.get(position).getProfessor());

        if(mData.get(position).getGrade().equals("전학년")){

            holder.textView_search_list_info.setText(mData.get(position).getGrade() + " " + mData.get(position).getCategory() + " " +
                    mData.get(position).getPoint() + "학점 " + mData.get(position).getNumber());

        }else{

            holder.textView_search_list_info.setText(mData.get(position).getGrade() + "학년 " + mData.get(position).getCategory() + " " +
                    mData.get(position).getPoint() + "학점 " + mData.get(position).getNumber());

        }

        holder.textView_search_list_time.setText(mData.get(position).getClassroom() + "\n" + mData.get(position).getRealTime());

        holder.tita_search_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validate = false;
                boolean alreadyIn = false;

                validate = schedule.validate(mData.get(holder.getAdapterPosition()).getClasstime());
                alreadyIn = schedule.alreadyIn(mData.get(holder.getAdapterPosition()).getNumber());

                if(alreadyIn){

                    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                    AlertDialog dialog = builder.setMessage("이미 추가한 강의입니다.").setPositiveButton("확인", null)
                            .create();
                    dialog.show();

                }else if(validate == false) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                    AlertDialog dialog = builder.setMessage("시간표가 중복됩니다.").setPositiveButton("확인", null)
                            .create();
                    dialog.show();

                }else{

                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {



                                JSONObject jsonResponse = new JSONObject(response);

                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                                    AlertDialog dialog = builder.setMessage("강의를 추가하였습니다.").setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();


                                    schedule.addSchedule(mData.get(holder.getAdapterPosition()));


                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                                    AlertDialog dialog = builder.setMessage("강의 추가를 실패하였습니다.").setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();
                                }

                            } catch (Exception e) {


                            }

                        }

                    };

                    try{

                        userEmail = (MainActivity.cookieManager.getCookie(sUrl)).replace("cookieKey=", "");

                    }catch (Exception e){

                    }

                    AddRequest addRequest = new AddRequest(userEmail, mData.get(holder.getAdapterPosition()).getNumber(),responseListener);
                    RequestQueue queue = Volley.newRequestQueue(parent);
                    queue.add(addRequest);


                }

            }
        });

    }



    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    Disposable backgroundtask;

    // 기존 시간표 불러오기.
    void GetUserTableInfoBackgroundTask() {

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
                    realTime = realTime.replaceAll("\"", "");

                    classroom_raw = classroom_raw.trim();
                    classroom_raw = classroom_raw.replaceAll("\"", "");

                    Lecture lecture = new Lecture(id, department, grade, category, number, lecturename,
                            professor, classroom_raw, classtime_raw, classroom, classtime, how, Integer.parseInt(point), realTime);

                    schedule.addSchedule(lecture);

                    count++;

                }


            } catch (Exception e) {
            }

            backgroundtask.dispose();


        });

    }

}
