package com.vb.pano;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

public class ReuseActivity extends BaseActivity {

    private CheckBox c1,c2,c3,c4,c5,c6;
    private List<Config> list;
    private Config config;
    private String name;
    private String ip;
    private int num;
    List<CheckBox> boxes=new ArrayList<>();
    private ProgressDialog dialog;
    private LinearLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reuse);
        initTopBar();
        initView();
    }
    private void initView()
    {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在复用中，请稍后");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        rootView=(LinearLayout)findViewById(R.id.activity_reuse);
        Bundle bundle=getIntent().getBundleExtra("bundle");
        config=(Config)bundle.getSerializable("config");
        name=bundle.getString("name");
        ip=bundle.getString("ip");
        String json= ParamsUtils.getParams(this,name);
        list=ParamsUtils.convertToBeans(json);
        num=bundle.getInt("num");
        Log.i(Utils.TAG, "initView: "+num);
        c1=(CheckBox)findViewById(R.id.check1);
        c2=(CheckBox)findViewById(R.id.check2);
        c3=(CheckBox)findViewById(R.id.check3);
        c4=(CheckBox)findViewById(R.id.check4);
        c5=(CheckBox)findViewById(R.id.check5);
        c6=(CheckBox)findViewById(R.id.check6);

        boxes.add(c1);
        boxes.add(c2);
        boxes.add(c3);
        boxes.add(c4);
        boxes.add(c5);
        boxes.add(c6);
        boxes.get(num-1).setChecked(true);


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {

            modifyPano(ip,getNowConfigs(),name);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    Toast.makeText(ReuseActivity.this,"连接小黑失败",Toast.LENGTH_SHORT).show();
                    ReuseActivity.this.finish();
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
                            Toast.makeText(ReuseActivity.this,"模式修改成功",Toast.LENGTH_SHORT).show();
                            String json = ParamsUtils.convertToJson(configs);
                            ParamsUtils.saveParams(ReuseActivity.this, name, json);
                            ParamsUtils.saveConfig(ReuseActivity.this,name);
                        } else {
                            Utils.Toast(rootView, "模式修改失败");
                            Toast.makeText(ReuseActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.Toast(rootView, "模式修改失败");
                        Toast.makeText(ReuseActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        ReuseActivity.this.finish();
                    }
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Utils.Toast(rootView, "模式修改失败");
            Toast.makeText(ReuseActivity.this,"模式修改失败",Toast.LENGTH_SHORT).show();
        }


    }
    private List<Config> getNowConfigs()
    {
        List<Config> configs=new ArrayList<>();
        for(int i=0;i<boxes.size();i++)
        {
            CheckBox box=boxes.get(i);
            if(box.isChecked())
            {
                Config c=new Config();
                c.setCameraId("0"+(i+1));
                c.setSharpness(config.getSharpness());
                c.setContrast(config.getContrast());
                c.setBrightness(config.getBrightness());
                c.setIso(config.getIso());
                c.setSaturation(config.getSaturation());
                c.setShutter_spd(config.getShutter_spd());
                c.setWb(config.getWb());
                configs.add(c);
            }
            else
            {
                configs.add(list.get(i));
            }
        }
        return configs;
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
//                String json=ParamsUtils.convertToJson(getNowConfigs());
//                ParamsUtils.saveParams(ReuseActivity.this,name,json);
//                ParamsUtils.saveConfig(ReuseActivity.this,name);
                modifyPano(ip,getNowConfigs(),name);
                //finish();
            }
        });
    }
}
