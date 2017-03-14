package com.vb.pano;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.vb.pano.util.CodeUtil;
import com.vb.pano.util.Utils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class CodeActivity extends BaseActivity {

    //topbar
    private Button backBtn;
    private TextView titleView;
    //content
    private ImageView codeView;
    private TextView qrView;
    private String panoId;
    private boolean upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        initTopBar();
        initView();
        initData();
        createCode();
    }

    private void initData() {
        Intent intent = getIntent();
        panoId = intent.getStringExtra("panoId");
        String code = intent.getStringExtra("qrCode");
        upload=intent.getBooleanExtra("upload",true);
        if (!TextUtils.isEmpty(panoId)) {
            String url = Utils.getPanoUrl(upload) + panoId;
            try {
                Bitmap bitmap = CodeUtil.createQRCode(url, 400);
                codeView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
//        if(TextUtils.isEmpty(code))
//        {
//            qrView.setText(code);
//        }

    }

    private void createCode() {
        String url = Utils.getprocressUrl(upload)+panoId;
        Utils.log(url);
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject data = object.getJSONObject("data");

                    String code = data.getString("accessCode");
                    if (!TextUtils.isEmpty(code)) {
                        qrView.setText("提取码:"+code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        codeView = (ImageView) findViewById(R.id.qrcode);
        qrView = (TextView) findViewById(R.id.tiquma);

    }

    private void initTopBar() {
        backBtn = (Button) findViewById(R.id.backBtn);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("二维码");
        Drawable drawable = getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0, 0, 40, 60);
        backBtn.setCompoundDrawables(drawable, null, null, null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
