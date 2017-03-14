package com.vb.pano;

import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private EditText nameEditText;

    private EditText pwdEditText;

    private Button loginBtn;

    private Toolbar toolbar;

    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;
    private static final int SECURITY_EAP = 3;

    private Context context=LoginActivity.this;
    private ConnectivityManager connManager;
    private WifiManager wifiManager;
    private LocationManager locManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar();
        initPermission();
        initView();
    }
    //申请各种权限
    private void initPermission()
    {
        ActivityCompat.requestPermissions(LoginActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION},0);
    }
    private void initView()
    {
        connManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        nameEditText=(EditText)findViewById(R.id.login_wifiname);
        pwdEditText=(EditText)findViewById(R.id.login_wifipwd);
        loginBtn=(Button)findViewById(R.id.login_btn);
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        nameEditText.setOnClickListener(editClickListener);
        loginBtn.setOnClickListener(btnClickListener);
    }
    View.OnClickListener btnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //连接check
            String name=nameEditText.getText().toString();
            String pwd=pwdEditText.getText().toString();
            if(!TextUtils.isEmpty(nameEditText.getText())&&!TextUtils.isEmpty(pwdEditText.getText()))
            {
                String url = "http://192.168.17.1";
                url = url + "/network?mode=client&ssid="+name+"&password="+pwd;
                getJson(url);
            }
            else
            {
                Toast.makeText(context,getString(R.string.login_check_toast),Toast.LENGTH_SHORT).show();
            }
        }
    };
    //发送httpget请求
    private  void getJson(String url)
    {
        final String name=nameEditText.getText().toString();
        final String pwd=pwdEditText.getText().toString();
        RequestParams params=new RequestParams(url);
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
                 if(isConnSuccess(result))
                 {
                     ConnTask task=new ConnTask();
                     task.execute(new String[]{name,pwd});
                 }
                else
                 {
                     Toast.makeText(LoginActivity.this,getString(R.string.login_changewifi_toasterror),Toast.LENGTH_SHORT).show();
                 }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this,getString(R.string.login_getjson_toasterror),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(SSID);
        if(tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
    //异步方式连接wifi
    class ConnTask extends AsyncTask<String ,Integer,Integer>
    {
        @Override
        protected Integer doInBackground(String... params) {
            int result=wifiManager.addNetwork(createWifiInfo(params[0],params[1],3));
            return result;
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            Toast.makeText(LoginActivity.this,i+"",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    //解析json数据判断是否连接成功
    private boolean isConnSuccess(String json)
    {
        try {
            JSONObject jsonObject=new JSONObject(json);
            int code=jsonObject.getInt("result");
            if(code==0)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
          return false;
    }

    private void initToolBar()
    {
        setSupportActionBar(toolbar);
    }
    //判断wifi是否打开
    private boolean isWifiOpened()
    {
        if(wifiManager.isWifiEnabled())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //过滤当前wifi列表
    private List<ScanResult> getFilterScanResult(List<ScanResult> list)
    {
        List<ScanResult> result=new ArrayList<ScanResult>();
        for(int i=0;i<list.size();i++)
        {
            ScanResult scanResult=list.get(i);
            if(!hasSameWifi(scanResult,list,i)&&!TextUtils.isEmpty(scanResult.BSSID))
            {
                result.add(scanResult);
            }
        }
        return result;
    }
    //判断是否有相同的wifi名字
    private boolean hasSameWifi(ScanResult result,List<ScanResult> list,int index)
    {
        for(int i=0;i<list.size();i++)
        {
            if(i==index)
            {
                continue;
            }
            ScanResult scanResult=list.get(i);
            if((scanResult.SSID).equals(result.SSID))
            {
                Log.i("pano", "hasSameWifi: "+scanResult.SSID+"&&"+result.SSID);
                return true;
            }
        }
        return false;
    }

    //获取当前位置的所有wifi列表
    private String[] getAllWifi()
    {

        List<ScanResult> results=wifiManager.getScanResults();
        List<ScanResult> wifiList=getFilterScanResult(results);
        Log.i("pano","wifiList:"+wifiList.size());
        if(wifiList.size()>0)
        {
            String items[]=new String[wifiList.size()];
            for(int i=0;i<wifiList.size();i++)
            {
                ScanResult scanResult=wifiList.get(i);
                items[i]=scanResult.SSID;
            }
            return items;
        }
        else
        {
            Toast.makeText(this,getString(R.string.login_toast_nowifi),Toast.LENGTH_SHORT).show();
        }
       return null;
    }
    //弹出列表对话框
    private void popListDialog(final String []items)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.login_alert_title));
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  String item=items[which];
                  nameEditText.setText(item);
                  nameEditText.setSelection(item.length());
            }
        });
        builder.create().show();
    }
    View.OnClickListener editClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //弹出wifi列表

            if(isWifiOpened())
            {

                //wifi打开的话直接获取当前位置wifi列表
                String [] list=getAllWifi();

                if(list!=null)
                {
                    popListDialog(list);

                }
            }
            else
            {
                if(!wifiManager.isWifiEnabled())
                {
                    try
                    {
                        wifiManager.setWifiEnabled(true);
                    }
                    catch(Exception e)
                    {
                        //wifi没有打开的话引导用户去打开
                        Toast.makeText(context,getString(R.string.login_toast_msg),Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    };
}
