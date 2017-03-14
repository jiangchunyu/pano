package com.vb.pano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingActivity extends BaseActivity {

    private String config;
    private String ip;
    private int num;
    private ProgressDialog dialog;
    private TextView titleView;
    private EditText edit1, edit2, edit3, edit4, edit5, edit6, edit7;
    private SeekBar bar1, bar2, bar3, bar4, bar5, bar6, bar7;
    private RelativeLayout layout1, layout2;
    private Button saveBtn, cancelBtn;
    private TextView tv1;
    private Button photoBtn, reuseBtn;
    private String[] isoList, shutterList, wbList;
    private String url;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initTopBar();
        initView();
        initData();
    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在设置参数中");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        rootView = (LinearLayout) findViewById(R.id.activity_setting);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        titleView = (TextView) findViewById(R.id.setting_title);
        edit1 = (EditText) findViewById(R.id.setting_saturation1);
        edit2 = (EditText) findViewById(R.id.setting_saturation2);
        edit3 = (EditText) findViewById(R.id.setting_saturation3);
        edit4 = (EditText) findViewById(R.id.setting_saturation4);
        edit5 = (EditText) findViewById(R.id.setting_saturation5);
        edit6 = (EditText) findViewById(R.id.setting_saturation6);
        edit7 = (EditText) findViewById(R.id.setting_saturation7);
        bar1 = (SeekBar) findViewById(R.id.setting_seekbar1);
        bar2 = (SeekBar) findViewById(R.id.setting_seekbar2);
        bar3 = (SeekBar) findViewById(R.id.setting_seekbar3);
        bar4 = (SeekBar) findViewById(R.id.setting_seekbar4);
        bar5 = (SeekBar) findViewById(R.id.setting_seekbar5);
        bar6 = (SeekBar) findViewById(R.id.setting_seekbar6);
        bar7 = (SeekBar) findViewById(R.id.setting_seekbar7);
        layout1 = (RelativeLayout) findViewById(R.id.content1);
        layout2 = (RelativeLayout) findViewById(R.id.content2);
        saveBtn = (Button) findViewById(R.id.save_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        tv1 = (TextView) findViewById(R.id.setAll);
        photoBtn = (Button) findViewById(R.id.photo_btn);
        reuseBtn = (Button) findViewById(R.id.reuse_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputString()) {
                    Utils.Toast(rootView, "输入值非法，请修改");
                    return;
                }
                modifyPano(ip, getConfigs(), config);
//                String json=ParamsUtils.convertToJson(getConfigs());
//                ParamsUtils.saveParams(SettingActivity.this,config,json);
//                Utils.Toast(rootView,"保存成功");
                //finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                titleView.setText("为各个相机设置统一参数");
                num = 0;
            }
        });
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://" + ip + "/captureParam";
                Config c = getConfigs().get(num - 1);

                saveConfig();
                setToPano(url, c);
            }
        });
        reuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ReuseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("config", getConfigs().get(num - 1));
                bundle.putString("name", config);
                bundle.putString("ip", ip);
                bundle.putInt("num", num);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (num != 0) {
                if (!checkInputString()) {
                    Utils.Toast(rootView, "输入值非法，请修改");
                    return true;
                }
                upload();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean checkInputString() {
        if (check1() && check2() && check3() && check4() && check5() && check6() && check7()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean check1() {
        String txt1 = edit1.getText().toString();
        if (Utils.isNumeric(txt1)) {
            if (Integer.parseInt(txt1) >= 1 && Integer.parseInt(txt1) <= 255) {
                return true;
            }
        }
        return false;
    }

    private boolean check2() {
        String txt2 = edit2.getText().toString();
        if (Utils.isNumeric(txt2)) {
            if (Integer.parseInt(txt2) >= 0 && Integer.parseInt(txt2) <= 255) {
                return true;
            }
        }
        return false;
    }

    private boolean check3() {
        String txt3 = edit3.getText().toString();
        if (Utils.isNumeric(txt3)) {
            if (Integer.parseInt(txt3) >= -255 && Integer.parseInt(txt3) <= 255) {
                return true;
            }
        }
        return false;
    }

    private boolean check4() {
        String txt4 = edit4.getText().toString();
        if (Utils.isNumeric(txt4)) {
            if (Integer.parseInt(txt4) >= 0 && Integer.parseInt(txt4) <= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean check5() {
        String txt5 = edit5.getText().toString();
        if (shutterList != null && shutterList.length > 0) {
            for (String s : shutterList) {
                if (s.equals(txt5)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean check6() {
        String txt6 = edit6.getText().toString();
        if (isoList != null && isoList.length > 0) {
            for (String s : isoList) {
                if (s.equals(txt6)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean check7() {
        String txt7 = edit7.getText().toString();
        if (wbList != null && wbList.length > 0) {
            for (String s : wbList) {
                if (s.equals(txt7)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void  upload()
    {
        List<Config> newConfigs = new ArrayList<>();
        Config c = getConfigs().get(num - 1);
        List<Config> list = ParamsUtils.convertToBeans(ParamsUtils.getParams(SettingActivity.this, config));
        for (Config con : list) {
            if (c.getCameraId().equals(con.getCameraId())) {
                newConfigs.add(c);
            } else {
                newConfigs.add(con);
            }
        }
        modifyPano(ip,newConfigs,config);
    }
    private void saveConfig() {

        List<Config> newConfigs = new ArrayList<>();
        Config c = getConfigs().get(num - 1);
        List<Config> list = ParamsUtils.convertToBeans(ParamsUtils.getParams(SettingActivity.this, config));
        for (Config con : list) {
            if (c.getCameraId().equals(con.getCameraId())) {
                newConfigs.add(c);
            } else {
                newConfigs.add(con);
            }
        }
        String json = ParamsUtils.convertToJson(newConfigs);
        ParamsUtils.saveParams(this, config, json);

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
                    Toast.makeText(SettingActivity.this,"连接小黑失败",Toast.LENGTH_SHORT).show();
                    SettingActivity.this.finish();
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
                            Toast.makeText(SettingActivity.this,"模式修改成功",Toast.LENGTH_SHORT).show();
                            String json = ParamsUtils.convertToJson(configs);
                            ParamsUtils.saveParams(SettingActivity.this, name, json);
                        } else {
                            Utils.Toast(rootView, "模式修改失败");
                            Toast.makeText(SettingActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.Toast(rootView, "模式修改失败");
                        Toast.makeText(SettingActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        SettingActivity.this.finish();
                    }
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Utils.Toast(rootView, "模式修改失败");
            Toast.makeText(SettingActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
        }

    }

    private List<Config> getConfigs() {
        List<Config> configs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Config config = new Config();
            config.setCameraId("0" + (i + 1));
            config.setSaturation(edit1.getText().toString());
            config.setContrast(edit2.getText().toString());
            config.setBrightness(edit3.getText().toString());
            config.setSharpness(edit4.getText().toString());
            config.setShutter_spd(edit5.getText().toString());
            config.setIso(edit6.getText().toString());
            config.setWb(edit7.getText().toString());
            configs.add(config);
        }
        return configs;
    }

    public void setToPano(String url, Config config) {
        if (!checkInputString()) {
            Utils.Toast(rootView, "输入值非法，请修改");
            return;
        }
        dialog.show();
        RequestParams params = new RequestParams(url);
        List<Config> list = new ArrayList<>();
        list.add(config);
        String json = ParamsUtils.convertToJson(Utils.convertConfigs(list));
        params.setAsJsonContent(true);
        params.setBodyContent(json);
        Utils.log(json);
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
                Utils.Toast(rootView, "设置参数失败");
                Intent intent = new Intent(SettingActivity.this, TryActivity.class);
                intent.putExtra("ip", ip);
                intent.putExtra("num", num);
                startActivity(intent);
            }

            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    Utils.log(result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    saveConfig();
                    Utils.Toast(rootView, msg);
                    Intent intent = new Intent(SettingActivity.this, TryActivity.class);
                    intent.putExtra("ip", ip);
                    intent.putExtra("num", num);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.Toast(rootView, "设置参数失败");
                }
            }
        });
    }

    public void setParamsToPano(String url, final List<Config> configs, final String name) {
        if (configs != null && configs.size() > 0) {
            for (int i = 0; i < configs.size(); i++) {
                Config config = configs.get(i);
                final int index = i;
                RequestParams params = new RequestParams(url);
                params.addQueryStringParameter("cameraId", config.getCameraId());
                params.addQueryStringParameter("shutter_spd", config.getShutter_spd());
                params.addQueryStringParameter("iso", config.getIso());
                params.addQueryStringParameter("saturation", config.getSaturation());
                params.addQueryStringParameter("sharpness", config.getSharpness());
                params.addQueryStringParameter("brightness", config.getBrightness());
                params.addQueryStringParameter("contrast", config.getContrast());
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        dialog.dismiss();
                        if (index == 5) {
                            Utils.Toast(rootView, "设置失败");
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int jsonResult = jsonObject.getInt("result");
                            if (jsonResult == 0) {
                                if (index == 5) {
                                    String jsonMsg = jsonObject.getString("msg");
                                    Utils.Toast(rootView, jsonMsg);
                                    String json = ParamsUtils.convertToJson(configs);
                                    ParamsUtils.saveParams(SettingActivity.this, name, json);
                                    ParamsUtils.saveConfig(SettingActivity.this, name);
                                    finish();
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Utils.Toast(rootView, "设置失败");
                    }
                });
            }

        }
    }

    private void initData() {
        Intent intent = getIntent();
        config = intent.getStringExtra("config");
        ip = intent.getStringExtra("ip");
        num = intent.getIntExtra("num", 0);
        url = "http://" + ip + "/captureParam";
        isoList = getResources().getStringArray(R.array.iso_array);
        shutterList = getResources().getStringArray(R.array.shutter_array);
        wbList = getResources().getStringArray(R.array.wb_array);
        if (TextUtils.isEmpty(config) || TextUtils.isEmpty(ip)) {
            return;
        }
        String json = ParamsUtils.getParams(SettingActivity.this, config);
        List<Config> list = ParamsUtils.convertToBeans(json);
        Config c;
        if (num == 0) {
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            titleView.setText("为各个相机设置统一参数");
            c = list.get(0);
        } else {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            titleView.setText("相机名称:" + num + "号相机");
            c = list.get(num - 1);
        }
        try {
            edit1.setText(c.getSaturation());
            edit2.setText(c.getContrast());
            edit3.setText(c.getBrightness());
            edit4.setText(c.getSharpness());
            edit5.setText(c.getShutter_spd());
            edit6.setText(c.getIso());
            edit7.setText(c.getWb());
            Log.i(Utils.TAG, "num:" + numToBar(0, c.getSaturation()));
            bar1.setProgress(numToBar(0, c.getSaturation()));
            bar2.setProgress(numToBar(1, c.getContrast()));
            bar3.setProgress(numToBar(2, c.getBrightness()));
            bar4.setProgress(numToBar(3, c.getSharpness()));
            bar5.setProgress(numToBar(4, c.getShutter_spd()));
            bar6.setProgress(numToBar(5, c.getIso()));
            bar7.setProgress(numToBar(6, c.getWb()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bar1.setOnSeekBarChangeListener(listener);
        bar2.setOnSeekBarChangeListener(listener);
        bar3.setOnSeekBarChangeListener(listener);
        bar4.setOnSeekBarChangeListener(listener);
        bar5.setOnSeekBarChangeListener(listener);
        bar6.setOnSeekBarChangeListener(listener);
        bar7.setOnSeekBarChangeListener(listener);
    }

    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.setting_seekbar1: {
                    edit1.setText(barToNum(0, progress));
                    break;
                }
                case R.id.setting_seekbar2: {
                    edit2.setText(barToNum(1, progress));
                    break;
                }
                case R.id.setting_seekbar3: {
                    edit3.setText(barToNum(2, progress));
                    break;
                }
                case R.id.setting_seekbar4: {
                    edit4.setText(barToNum(3, progress));
                    break;
                }
                case R.id.setting_seekbar5: {
                    edit5.setText(barToNum(4, progress));
                    break;
                }
                case R.id.setting_seekbar6: {
                    edit6.setText(barToNum(5, progress));
                    break;
                }
                case R.id.setting_seekbar7: {
                    edit7.setText(barToNum(6, progress));
                    break;
                }

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private int numToBar(int index, String num) {
        switch (index) {
            case 0: {
                return (Integer.parseInt(num) + 1);

            }
            case 1: {
                return Integer.parseInt(num);

            }
            case 2: {
                return (Integer.parseInt(num) + 255);

            }
            case 3: {
                return Integer.parseInt(num);

            }
            case 4: {
                for (int i = 0; i < shutterList.length; i++) {
                    if (num.equals(shutterList[i])) {
                        return i;
                    }
                }
                break;
            }
            case 5: {
                for (int j = 0; j < isoList.length; j++) {
                    if (num.equals(isoList[j])) {
                        return j;
                    }
                }
                break;

            }
            case 6: {
                for (int j = 0; j < wbList.length; j++) {
                    if (num.equals(wbList[j])) {
                        return j;
                    }
                }
                break;

            }
        }
        return 0;
    }

    private String barToNum(int index, int num) {
        switch (index) {
            case 0: {
                return (num + 1) + "";

            }
            case 1: {
                return (num) + "";

            }
            case 2: {
                return (num - 255) + "";

            }
            case 3: {
                return (num) + "";

            }
            case 4: {
                return shutterList[num];

            }
            case 5: {
                return isoList[num];

            }
            case 6: {
                return wbList[num];

            }

        }
        return 0 + "";
    }

    private void initTopBar() {
        Button backBtn = (Button) findViewById(R.id.backBtn);
        Drawable drawable = getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0, 0, 40, 60);
        backBtn.setCompoundDrawables(drawable, null, null, null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num != 0) {
                    if (!checkInputString()) {
                        Utils.Toast(rootView, "输入值非法，请修改");
                        return;
                    }
                    upload();
                }
                //finish();
            }
        });
    }
}
