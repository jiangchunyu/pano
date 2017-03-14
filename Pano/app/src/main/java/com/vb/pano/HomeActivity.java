package com.vb.pano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vb.pano.adapter.FragmentAdapter;
import com.vb.pano.bean.Config;
import com.vb.pano.bean.Device;
import com.vb.pano.fragment.GalleryFragment;
import com.vb.pano.fragment.ModeFragment;
import com.vb.pano.fragment.PhotoFragment;
import com.vb.pano.fragment.SettingFragment;
import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends FragmentActivity {

    public Device device;
    private ImageButton uploadButton;
    private TextView titleView;
    private RadioGroup radioGroup;
    private RadioButton photoButton, settingButton, modeButton, galleryButton;
    private ViewPager mPager;
    private TextView flagView;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initData();
        initTopBar();
        initPager();
        initConfig();

    }

    private void initConfig() {
        //首先从本地读取参数配置文件，然后将参数传给相机
        //如果本地没有参数配置文件，那么生成一个参数配置文件，然后将参数传给相机，并且在本地保存
        String config = ParamsUtils.getConfig(this);
        if (config != null) {
            //说明读取到了配置文件
            String json = ParamsUtils.getParams(this, config);
            if (json != null) {
                List<Config> configs=ParamsUtils.convertToBeans(json);
                modifyPano(device.getIpAddress(),configs);
//               String url="http://"+device.getIpAddress()+"/captureParam";
//               //给六个相机同时设置默认参数
//               ParamsUtils.setParamsToPano(url,configs);

            }
        } else {
            //没有读取到配置文件
            List<Config> configs = ParamsUtils.getInitConfig();
            String json = ParamsUtils.convertToJson(configs);
            ParamsUtils.saveParams(this, "自由模式", json);
            ParamsUtils.saveConfig(this, "自由模式");
//           String url="http://"+device.getIpAddress()+"/captureParam";
//           //给六个相机同时设置默认参数
//           ParamsUtils.setParamsToPano(url,configs);
             modifyPano(device.getIpAddress(),configs);
        }
    }

    private void modifyPano(String ip, final List<Config> configs) {
        try {
            String url="http://"+ip+"/captureParam";
            RequestParams params = new RequestParams(url);
            List<Config> curConfigs = Utils.convertConfigs(configs);
            String curJson = ParamsUtils.convertToJson(curConfigs);
            Utils.log(curJson);
            params.setBodyContent(curJson);
            params.setAsJsonContent(true);
            params.setConnectTimeout(20000);
            x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }


                @Override
                public void onSuccess(String result) {
                    Utils.log("result:" + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int jsonResult = jsonObject.getInt("result");
                        if (jsonResult == 0) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }

    }

    private void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        device = (Device) bundle.getSerializable("device");
        x.view().inject(this);
    }

    private void initTopBar() {
        titleView = (TextView) findViewById(R.id.title);
        flagView = (TextView) findViewById(R.id.flag);
        uploadButton = (ImageButton) findViewById(R.id.uploadImg);
        String deviceId = device.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            titleView.setText(deviceId);
        }
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = device.getIpAddress();
                Intent intent = new Intent(HomeActivity.this, UploadStatusActivity.class);
                intent.putExtra("remoteIp", ip);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryFlag();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    Timer timer = null;

    //不断轮询去获取相机状态
    private void queryFlag() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String url = "http://" + device.getIpAddress() + "/about" + "?timestamp=" + System.currentTimeMillis();
                    HttpGet httpGet = new HttpGet(url);
                    Log.i(Utils.TAG, "run: 发送请求");
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "(可用)";
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "(不可用)";
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = "(不可用)";
                    handler.sendMessage(msg);
                }
            }
        }, 500, 5000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Utils.log((String) msg.obj);
                flagView.setText((String) msg.obj);
            }
        }
    };

    private void initPager() {
        mPager = (ViewPager) findViewById(R.id.viewPager);
        radioGroup = (RadioGroup) findViewById(R.id.home_bottom);
        photoButton = (RadioButton) findViewById(R.id.photo);
        settingButton = (RadioButton) findViewById(R.id.setting);
        modeButton = (RadioButton) findViewById(R.id.mode);
        galleryButton = (RadioButton) findViewById(R.id.gallery);
        fragments.add(new PhotoFragment());
        fragments.add(new SettingFragment());
        fragments.add(new ModeFragment());
        fragments.add(new GalleryFragment());
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        mPager.setAdapter(fragmentAdapter);
        mPager.addOnPageChangeListener(pageChangeListener);
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(4);
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.photo: {
                    mPager.setCurrentItem(0);
                    break;
                }
                case R.id.setting: {
                    mPager.setCurrentItem(1);
                    break;
                }
                case R.id.mode: {
                    mPager.setCurrentItem(2);
                    break;
                }
                case R.id.gallery: {
                    mPager.setCurrentItem(3);
                    break;
                }
            }


        }
    };
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPager.setCurrentItem(position);
            switch (position) {
                case 0: {
                    photoButton.setChecked(true);
                    break;
                }
                case 1: {
                    settingButton.setChecked(true);
                    break;
                }
                case 2: {
                    modeButton.setChecked(true);
                    break;
                }
                case 3: {
                    galleryButton.setChecked(true);
                    break;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
