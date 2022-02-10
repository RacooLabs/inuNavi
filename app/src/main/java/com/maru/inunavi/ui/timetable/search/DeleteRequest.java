package com.maru.inunavi.ui.timetable.search;

import static com.maru.inunavi.IpAddress.DemoIP;
import static com.maru.inunavi.IpAddress.DemoIP_ClientTest;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.maru.inunavi.IpAddress;

import java.util.HashMap;
import java.util.Map;

public class DeleteRequest extends StringRequest {


    final static private String URL = IpAddress.isTest ? "http://"+ DemoIP_ClientTest +"/inuNavi/LectureDelete.php" :
            "http://" + DemoIP + "/user/delete/class";


    private Map<String, String> parameters;

    public DeleteRequest(String userEmail, String lectureNumber, Response.Listener<String> listener){

        super(Method.POST, URL, listener, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("@@@", "error_addrequest_requst:24" + error);
        }
    });

        parameters = new HashMap<>();
        parameters.put("email", userEmail);
        parameters.put("class_id", lectureNumber);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
