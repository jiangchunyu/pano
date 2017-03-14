package com.vb.pano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vb.pano.bean.Config;
import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends BaseActivity {
    private Button backBtn;
    private Button addMode;
    private TextView modeNameView;
    private RelativeLayout layout1, layout2, layout3, layout4, layout5, layout6;
    private TextView setAllView, resetView;
    private String ip, config;
    private LinearLayout rootView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initTopBar();
        initView();
    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在重置中，请稍后");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        modeNameView = (TextView) findViewById(R.id.modename);
        rootView = (LinearLayout) findViewById(R.id.activity_device);
        layout1 = (RelativeLayout) findViewById(R.id.zwt1);
        layout2 = (RelativeLayout) findViewById(R.id.zwt2);
        layout3 = (RelativeLayout) findViewById(R.id.zwt3);
        layout4 = (RelativeLayout) findViewById(R.id.zwt4);
        layout5 = (RelativeLayout) findViewById(R.id.zwt5);
        layout6 = (RelativeLayout) findViewById(R.id.zwt6);
        setAllView = (TextView) findViewById(R.id.setAll);
        resetView = (TextView) findViewById(R.id.resetAll);
        ip = getIntent().getStringExtra("ip");
        config = getIntent().getStringExtra("config");
        modeNameView.setText("模式名称:" + config);
        List<RelativeLayout> list = new ArrayList<>();
        list.add(layout1);
        list.add(layout2);
        list.add(layout3);
        list.add(layout4);
        list.add(layout5);
        list.add(layout6);
        setAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceActivity.this, SettingActivity.class);
                intent.putExtra("config", config);
                intent.putExtra("ip", ip);
                intent.putExtra("num", 0);
                startActivity(intent);
            }
        });
        //恢复默认参数设置
        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Config> configs = ParamsUtils.getInitConfig();
                modifyPano(ip, configs, config);
            }
        });


        for (int i = 0; i < list.size(); i++) {
            final int index = i;
            list.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DeviceActivity.this, SettingActivity.class);
                    intent.putExtra("config", config);
                    intent.putExtra("ip", ip);
                    intent.putExtra("num", index + 1);
                    startActivity(intent);
                }
            });
        }


    }

    private void modifyPano(String ip, final List<Config> configs, final String name) {
        try {
            dialog.show();
            String url = "http://" + ip + "/captureParam";
            RequestParams params = new RequestParams(url);
            List<Config> curConfigs = Utils.convertConfigs(configs);
            String curJson = ParamsUtils.convertToJson(curConfigs);
            params.setBodyContent(curJson);
            params.setAsJsonContent(true);
            params.setConnectTimeout(10000);
            x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    dialog.dismiss();
                    Utils.Toast(rootView, "连接小黑失败");
                }


                @Override
                public void onSuccess(String result) {
                    Utils.log("result:" + result);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int jsonResult = jsonObject.getInt("result");
                        if (jsonResult == 0) {
                            Utils.Toast(rootView, "模式修改成功");
                            String json = ParamsUtils.convertToJson(configs);
                            ParamsUtils.saveParams(DeviceActivity.this, name, json);
                        } else {
                            Utils.Toast(rootView, "模式修改失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.Toast(rootView, "模式修改失败");
                    }
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Utils.Toast(rootView, "模式修改失败");
        }

    }

    private void initTopBar() {
        backBtn = (Button) findViewById(R.id.backBtn);
        addMode = (Button) findViewById(R.id.add_mode);
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
        addMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceActivity.this, NewModeActivity.class);
                startActivity(intent);
            }
        });
    }
}
