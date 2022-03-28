package com.maru.inunavi.ui.map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.maru.inunavi.R;

public class CustomARDialog extends Dialog {

    private TextView ar_check_button;
    private Context mContext;
    private View.OnClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_ar_info_dialog);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        ar_check_button = findViewById(R.id.ar_guide_check_button);


        // 버튼 리스너 설정
        ar_check_button.setOnClickListener(clickListener);

    }

    public CustomARDialog(Context mContext, View.OnClickListener clickListener) {
        super(mContext);
        this.mContext = mContext;
        this.clickListener = clickListener;
    }


}
