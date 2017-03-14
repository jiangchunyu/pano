package com.vb.pano;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vb.pano.bean.Device;
import com.vb.pano.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vb.pano.util.Utils.log;

public class MainActivity extends BaseActivity {

    private static final int THREAD_NUM = 50;
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
    private Button backBtn;
    private Button refreshBtn;
    private LinearLayout deviceLayout;
    private ProgressDialog dialog;
    private LinearLayout rootView;
    private Button localGallery;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestPermission();
      // test();
        initPref();
    }
    private void requestPermission()
    {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void pingIp() {
        String ipString = Utils.getLocalIp();
        String networkIp = Utils.getNetwork(ipString);
        for (int i = 0; i <= 255; i++) {

            final String remoteIp = networkIp + "." + i;
            final int index=i;
            executor.execute(new Runnable() {

                @Override
                public void run() {

                    String succIp=Utils.ping(remoteIp);
                    if(!TextUtils.isEmpty(succIp))
                    {
                        log("ping success :" + remoteIp);
                        String ip="http://"+succIp+"/about";
                        getJson(ip,remoteIp);
                    }
                    else
                    {
                        log("ping error :" + remoteIp);
                    }
                    if(index==255)
                    {
                        dialog.dismiss();
                    }
                }
            });
        }
    }
    //发送httpget请求
    private void getJson(String url,  final String remoteIp) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(2000);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                parseJson(result,remoteIp);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
        });
    }
    private void parseJson(String json,String remoteIp)
    {
        try {
            JSONObject jsonObject=new JSONObject(json);
            if(jsonObject.getInt("result")==0)
            {
                JSONObject dataObject=jsonObject.getJSONObject("data");
                String model=dataObject.getString("model");
                String deviceId=dataObject.getString("deviceId");
                String hardVer=dataObject.getString("hversion");
                String softVer=dataObject.getString("sversion");
                Device device=new Device();
                device.setDeviceId(deviceId);
                device.setHardVersion(hardVer);
                device.setSoftwareVersion(softVer);
                device.setIpAddress(remoteIp);
                device.setStatus("已搜索到");
                device.setModel(model);
                addCellView(device);
                saveDevices(device);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initPref()
    {
        //测试数据
        //test();
        String json=Utils.getDevices(this);
        if(json!=null)
        {
            log(json);
            String [] devices=json.split("@");
            if(devices==null||devices.length<=0)
            {
                return;
            }
            for(int i=0;i<devices.length;i++)
            {
                try{
                    Device device=gson.fromJson(devices[i],Device.class);
                    addCellView(device);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }

    }
    //测试数据
    private void test()
    {
        for(int i=0;i<5;i++)
        {
            Device device=new Device();
            device.setDeviceId("1"+i);
            device.setHardVersion("1.0."+i);
            device.setSoftwareVersion("1.0."+i);
            device.setIpAddress("192.168.1."+i);
            device.setModel("model"+i);
            saveDevices(device);
        }

    }
    //保存Device对象数据到本地
    private synchronized void saveDevices(Device device)
    {

        device.setStatus("已保存");
        String json=gson.toJson(device);
        Utils.saveDevices(this,json+"@");

    }
    private void addCellView(final Device device)
    {
        View view= LayoutInflater.from(this).inflate(R.layout.device_item,null);

        Button button=(Button)view.findViewById(R.id.item_btn);
        TextView txtView=(TextView)view.findViewById(R.id.item_txt);
        button.setText(device.getDeviceId());
        txtView.setText(device.getStatus());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin=20;
        view.setLayoutParams(params);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.saveIp(MainActivity.this,device.getIpAddress());
                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("device",device);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        deviceLayout.addView(view);
    }
    private void initView()
    {
        dialog = new ProgressDialog(this);
        dialog.setMessage("搜索中,请稍后");
        dialog.setCanceledOnTouchOutside(false);
        gson=new Gson();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        localGallery=(Button)findViewById(R.id.localGallery);
        rootView=(LinearLayout)findViewById(R.id.activity_main);
        deviceLayout=(LinearLayout)findViewById(R.id.main_devicesLayout);
        backBtn=(Button)findViewById(R.id.backBtn);
        refreshBtn=(Button)findViewById(R.id.main_searchBtn);
        Drawable drawable=getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0,0,40,60);
        backBtn.setCompoundDrawables(drawable,null,null,null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        localGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LocalActivity.class));
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    deviceLayout.removeAllViews();
                    Utils.deleteDevices(MainActivity.this);
                    dialog.show();
                    pingIp();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Utils.Toast(rootView,"刷新失败");
                    dialog.dismiss();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
