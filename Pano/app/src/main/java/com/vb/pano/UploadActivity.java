package com.vb.pano;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.zxing.WriterException;
import com.vb.pano.bean.Pano;
import com.vb.pano.util.CodeUtil;
import com.vb.pano.util.PanoHelper;
import com.vb.pano.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UploadActivity extends BaseActivity {

    private String panoId, ipAddress;
    private Button backBtn;
    private EditText addressEdit;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private Spinner timeSpinner,modeSpinner;
    private EditText nameEdit,desEdit;
    private Button saveButton;
    private ProgressDialog dialog;
    private Pano pano;

    private LinearLayout layout1;
    private RelativeLayout layout2;
    private ImageView qrCodeView;
    private TextView codeTxt;
    private RadioGroup radioGroup;
    private LinearLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initData();
        initView();

        initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                getLoc();
            }
        } else {
            getLoc();
        }
    }
    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在上传中,请耐心等待");
        dialog.setCanceledOnTouchOutside(false);
        rootView=(LinearLayout)findViewById(R.id.activity_upload);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        radioGroup=(RadioGroup)findViewById(R.id.template_choose);
        addressEdit = (EditText) findViewById(R.id.upload_address);
        timeSpinner=(Spinner)findViewById(R.id.upload_type);
        modeSpinner=(Spinner)findViewById(R.id.upload_viewmode);
        nameEdit=(EditText)findViewById(R.id.upload_name);
        desEdit=(EditText)findViewById(R.id.upload_discripe);
        saveButton=(Button)findViewById(R.id.upload_save);
        layout1=(LinearLayout)findViewById(R.id.layout1);
        layout2=(RelativeLayout)findViewById(R.id.layout2);
        qrCodeView=(ImageView)findViewById(R.id.qrcode);
        codeTxt=(TextView)findViewById(R.id.tiquma);
        saveButton.setOnClickListener(saveClickListener);
       if(isNight())
       {
           timeSpinner.setSelection(1);
       }
        else
       {
           timeSpinner.setSelection(0);
       }
        getSaveData();
    }
    private void getSaveData()
    {
        SharedPreferences sp=getSharedPreferences("pano",MODE_PRIVATE);
        String name=sp.getString("name","");
        String descrip=sp.getString("des","");
        String template=sp.getString("pto","indoor");
        String view=sp.getString("mode","平视");
        if(!TextUtils.isEmpty(name))
        {
            nameEdit.setText(name);
        }
        if(!TextUtils.isEmpty(name))
        {
            desEdit.setText(descrip);
        }
        if(template.equals("indoor"))
        {
            radioGroup.check(R.id.radio1);
        }
        if(template.equals("outdoor"))
        {
            radioGroup.check(R.id.radio2);
        }
        if(template.equals("last"))
        {
            radioGroup.check(R.id.radio3);
        }
        if(view.equals("平视"))
        {
            modeSpinner.setSelection(0);
        }
        if(view.equals("仰视"))
        {
            modeSpinner.setSelection(1);
        }
        if(view.equals("俯视"))
        {
            modeSpinner.setSelection(2);
        }

    }

    private void saveData(String name,String des,String mode,String pto)
    {
        SharedPreferences sp=getSharedPreferences("pano",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("name",name);
        editor.putString("des",des);
        editor.putString("mode",mode);
        editor.putString("pto",pto);
        editor.commit();
    }
    View.OnClickListener saveClickListener=new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              getData();
          }
      };
    private void getData()
    {
        String name=nameEdit.getText().toString();
        String address=addressEdit.getText().toString();
        String timeMode=timeSpinner.getSelectedItem().toString();
        String viewMode=modeSpinner.getSelectedItem().toString();
        int pto=radioGroup.getCheckedRadioButtonId();
        String descrip=desEdit.getText().toString();
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(address)&&!TextUtils.isEmpty(descrip))
        {
            dialog.show();
            pano=new Pano();
            pano.setName(name);
            pano.setAddress(address);
            pano.setDescrip(descrip);
            pano.setPanoId(panoId);
            pano.setUpload(Utils.isUpLoadOnline(this));
            pano.setTimeMode(timeMode);
            pano.setViewMode(viewMode);
            switch (pto)
            {
                case R.id.radio1:
                {
                    pano.setPto("indoor");
                    break;
                }
                case R.id.radio2:
                {
                    pano.setPto("outdoor");
                    break;
                }
                case R.id.radio3:
                {
                    pano.setPto("last");
                    break;
                }
            }
            saveData(pano.getName(),pano.getDescrip(),pano.getViewMode(),pano.getPto());
            uploadPano(pano);
        }
        else
        {
            Utils.Toast(rootView,"请填写照片信息");
        }

    }
    //上传全景
    private void uploadPano(Pano pano)
    {
        String url="http://"+ipAddress+"/confirm";
        RequestParams params=new RequestParams(url);
        params.setConnectTimeout(80000);
        params.addQueryStringParameter("panoId",pano.getPanoId());
        params.addQueryStringParameter("panoName",pano.getName());
        params.addQueryStringParameter("panoInfo",pano.getDescrip());
        params.addQueryStringParameter("place",pano.getAddress());
        params.addQueryStringParameter("dayNight",pano.getTimeMode());
        params.addQueryStringParameter("viewAngle",pano.getViewMode());
        params.addQueryStringParameter("pto",pano.getPto());
        params.addQueryStringParameter("test",!Utils.isUpLoadOnline(this)+"");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.dismiss();
                ex.printStackTrace();
                Utils.Toast(rootView,"上传失败");
              // accessCode("123");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(jsonObject.getInt("result")==1)
                    {
                        Utils.Toast(rootView,"上传失败，请重传");
                    }
                    else
                    {
                        accessCode(panoId);
                      //  queryPano(panoId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.Toast(rootView,"上传失败，请重传");
                }
            }
        });
    }
    private Runnable runnable;
    private void queryPano(final String id)
    {
      final long startTime= System.currentTimeMillis();
       runnable=new Runnable() {
            @Override
            public void run() {
                long endTime=System.currentTimeMillis();
                if((endTime-startTime)>=1000*60*2)
                {
                    //大于60*2秒退出循环
                    handler.sendEmptyMessage(1);
                }
                String url = "http://processtest.pano.visualbusiness.cn/rest/pano/status?panoId="+id;
                HttpGet httpGet=new HttpGet(url);
                HttpClient httpClient=new DefaultHttpClient();
                try {
                    HttpResponse response=httpClient.execute(httpGet);
                    if(response.getStatusLine().getStatusCode()==200)
                    {
                        String result= EntityUtils.toString(response.getEntity());
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=result;
                        handler.sendMessage(msg);
                    }
                    else {}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this,5000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)
            {
                handler.removeCallbacks(runnable);
                dialog.dismiss();
            }
            if(msg.what==0)
            {
                String result=(String)msg.obj;
                try {
                    JSONObject json=new JSONObject(result);
                    if(json.isNull("result") || json.getInt("result") == 1) {
                        dialog.dismiss();
                    }
                    else
                    {
                        if(!json.isNull("data")) {
                            dialog.dismiss();
                            JSONObject jsonMsg = json.getJSONObject("data");
                            int status = jsonMsg.getInt("status");
                            if(status==2)
                            {
                                String thumbnailUrl = jsonMsg.getString("thumbnail");
                                if(pano!=null)
                                {
                                    handler.removeCallbacks(runnable);
                                    pano.setThumNail(thumbnailUrl);
                                    //生成二维码提取码

                                }
                            }
                            if(status==1)
                            {
                                Log.i(Utils.TAG,"处理中....");
                            }
                            if(status==3)
                            {
                                dialog.dismiss();
                                Log.i(Utils.TAG,"处理失败....");
                                handler.removeCallbacks(runnable);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };
    //生成二维码和二维码提取码
    private void accessCode(String id)
    {
        String url = Utils.getPanoUrl(Utils.isUpLoadOnline(this))+id;
        try {
            Bitmap bitmap= CodeUtil.createQRCode(url,200);
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            qrCodeView.setImageBitmap(bitmap);
            //获取提取码
            createCode();
            pano.setQrCode("VB666");
            SimpleDateFormat format= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formatTime=format.format(new Date());
            pano.setTime(formatTime);
           // pano.setPanoId(id);
            String thumbnailUrl =Utils.getTilesUrl(Utils.isUpLoadOnline(this),id);
            pano.setThumNail(thumbnailUrl);
            //存入数据库
            PanoHelper helper=new PanoHelper(this);
            helper.insert(pano);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
    private void createCode() {

        String url = Utils.getprocressUrl(Utils.isUpLoadOnline(this))+panoId;
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
                        codeTxt.setText("提取码:"+code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(mAMapLocationListener);
    }

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            String address = amapLocation.getAddress();
            if(TextUtils.isEmpty(address))
            {
                return;
            }
            addressEdit.setText(address);
            Log.i(Utils.TAG, "onLocationChanged: address" + address);
        }
    };

    @Override
    public void
    onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoc();
                } else {
                    Utils.Toast(rootView,"请打开定位权限");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    //判断是白天还是晚上
    private boolean isNight()
    {
        long time=System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=7&&hour<=19)
        {
            return false;
        }
        return true;
    }
    private void initData() {
        panoId = getIntent().getStringExtra("panoId");
        ipAddress = getIntent().getStringExtra("ip");
        backBtn = (Button) findViewById(R.id.backBtn);
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
