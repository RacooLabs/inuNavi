package com.maru.inunavi.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maru.inunavi.MainActivity;
import com.maru.inunavi.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_login);

        TextView signUpButton = findViewById(R.id.button_moveTo_sign_up);
        TextView textView_findingPassword = findViewById(R.id.textView_findingPassword);

        EditText editText_id = findViewById(R.id.editText_email);
        EditText editText_password = findViewById(R.id.editText_password);

        TextView textView_login_id_warning = findViewById(R.id.textView_login_id_warning);
        TextView textView_login_password_warning = findViewById(R.id.textView_login_password_warning);

        TextView button_login = findViewById(R.id.button_login);

        ImageView user_activity_login_backButton = findViewById(R.id.user_activity_login_backButton);


        //회원가입 하러가기 버튼
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        //비밀 번호 찾기 버튼

        textView_findingPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findPasswordIntent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(findPasswordIntent);
            }
        });

        //로그인 버튼
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = editText_id.getText().toString();
                String userPassword = editText_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");


                            if(success){

                                String userMajor = jsonResponse.getString("major");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("CallType", 1);
                                intent.putExtra("userEmail",userEmail);
                                intent.putExtra("userMajor", userMajor);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                                overridePendingTransition(0, 0);

                            }else{
                                setNotEditText(editText_id, textView_login_id_warning,"");
                                textView_login_id_warning.setVisibility(View.GONE);
                                setNotEditText(editText_password, textView_login_password_warning,"계정을 다시 확인하세요.");

                            }


                        }catch (Exception e){


                            setNotEditText(editText_password, textView_login_password_warning, "서버 연결 실패");

                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userEmail, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });

        // 로그인 창 닫기 버튼

        user_activity_login_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });


    }

    public void setNormalEditText(EditText e, TextView t){
        e.setBackgroundResource(R.drawable.layout_login_round_box);
        t.setVisibility(View.GONE);
    }

    public void setNotEditText(EditText e, TextView t, String msg){
        e.setBackgroundResource(R.drawable.layout_login_round_box_not);
        t.setVisibility(View.VISIBLE);
        t.setText(msg);
    }

}
