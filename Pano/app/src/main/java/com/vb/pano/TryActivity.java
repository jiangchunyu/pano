package com.vb.pano;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vb.pano.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class TryActivity extends BaseActivity {

    private ImageButton photoBtn;
    private ImageView previewImg;
    private ProgressDialog dialog;
    private int num;
    private String ip;
    private String panoId;
    private RelativeLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);
        initTopBar();
        initView();
    }
    private void initView()
    {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在拍摄中");
        num=getIntent().getIntExtra("num",1);
        ip=getIntent().getStringExtra("ip");
        dialog.setCanceledOnTouchOutside(false);
        rootView=(RelativeLayout)findViewById(R.id.activity_try);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        photoBtn=(ImageButton)findViewById(R.id.photo);
        previewImg=(ImageView)findViewById(R.id.previewImg);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String url="http://"+ip+"/capture";
                takePhoto(url);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            cancelPano();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void cancelPano()
    {
        if(!TextUtils.isEmpty(panoId))
        {
            String url="http://"+ip+"/cancel";
            RequestParams params = new RequestParams(url);
            params.addQueryStringParameter("panoId",panoId);
            params.setConnectTimeout(10000);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onSuccess(String result) {
                    Log.i(Utils.TAG, "onSuccess: "+result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(Utils.TAG, "onError: 取消失败");
                }

            });
        }
    }
    private void takePhoto(String url)
    {
        RequestParams params=new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.dismiss();
                Utils.Toast(rootView,"拍照失败");
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                parseJson(result);
            }
        });
    }
    private void parseJson(String json)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(json);
            JSONObject dataObject=jsonObject.getJSONObject("data");
            Log.i(Utils.TAG, "parseJson: "+dataObject.toString());
            JSONArray jsonArray=dataObject.getJSONArray("photos");
            panoId=dataObject.getString("panoId");
            String url="http://"+ip+jsonArray.getString(num-1);
            x.image().bind(previewImg,url);
        }
        catch(Exception e)
        {
            Utils.Toast(rootView,"拍照失败");
        }
    }
    private void initTopBar()
    {
        Button backBtn=(Button)findViewById(R.id.backBtn);
        Drawable drawable=getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0,0,40,60);
        backBtn.setCompoundDrawables(drawable,null,null,null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPano();finish();
            }
        });
    }
}
