package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;
import static com.maru.inunavi.MainActivity.sessionURL;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    ScheduleListActivity parent;

    private OnItemClickListener mListener = null ;
    private ArrayList<Lecture> mData = null;

    private String sUrl = sessionURL;



    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.mListener = listener ;

    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView_schedule_list_lectureName;
        TextView textView_schedule_list_professor;
        TextView textView_schedule_list_info;
        TextView textView_schedule_list_time;
        TextView tita_schedule_list_delete;


        ViewHolder(Context context, View itemView) {

            super(itemView);

            textView_schedule_list_lectureName = itemView.findViewById(R.id.textView_schedule_list_lectureName);
            textView_schedule_list_professor = itemView.findViewById(R.id.textView_schedule_list_professor);
            textView_schedule_list_info = itemView.findViewById(R.id.textView_schedule_list_info);
            textView_schedule_list_time = itemView.findViewById(R.id.textView_schedule_list_time);
            tita_schedule_list_delete = itemView.findViewById(R.id.tita_schedule_list_delete);

            tita_schedule_list_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition() ;

                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(view,pos);
                        }
                    }
                }
            });


        }

    }

    public ScheduleListAdapter(ArrayList<Lecture> list, ScheduleListActivity parent) {
        mData = list;
        this.parent = parent;

    }

    @NonNull
    @Override
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.timetable_schedule_list_item, parent, false);
        ScheduleListAdapter.ViewHolder viewHolder = new ScheduleListAdapter.ViewHolder(context, view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ScheduleListAdapter.ViewHolder holder, int position) {

        holder.textView_schedule_list_lectureName.setText(mData.get(position).getLecturename());
        holder.textView_schedule_list_professor.setText(mData.get(position).getProfessor());


        if(mData.get(position).getGrade().equals("전학년")){

            holder.textView_schedule_list_info.setText(mData.get(position).getGrade() + " " + mData.get(position).getCategory() + " " +
                    mData.get(position).getPoint() + "학점 " + mData.get(position).getNumber());

        }else{

            holder.textView_schedule_list_info.setText(mData.get(position).getGrade() + "학년 " + mData.get(position).getCategory() + " " +
                    mData.get(position).getPoint() + "학점 " + mData.get(position).getNumber());

        }

        holder.textView_schedule_list_time.setText(mData.get(position).getClassroom() + "\n" + mData.get(position).getRealTime());

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }


}
