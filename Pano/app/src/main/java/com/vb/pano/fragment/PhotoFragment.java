package com.vb.pano.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vb.pano.HomeActivity;
import com.vb.pano.R;
import com.vb.pano.UploadActivity;
import com.vb.pano.bean.Device;
import com.vb.pano.util.Utils;
import com.vb.pano.view.PhotoDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seven on 2016/11/9.引入了高德定位sdk
 */
public class PhotoFragment extends Fragment {

    private View view;
    private String address;
    private String panoId;
    private View baseView;
    private ImageButton takePhotoButton;
    private HomeActivity activity;
    private Device device;
    private ImageView img1, img2, img3, img4, img5, img6;
    private List<ImageView> imgs;
    private ImageButton okButton, cancleButton;
    private ProgressDialog dialog;
    private RelativeLayout rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photo, container, false);
        initView();
        return view;
    }

    private void initView() {

        x.view().inject(getActivity());
        activity = (HomeActivity) getActivity();
        device = activity.device;
        imgs = new ArrayList<>();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在拍照中");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopPhoto();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        rootView = (RelativeLayout) view.findViewById(R.id.rootView);
        img1 = (ImageView) view.findViewById(R.id.zwt1);
        img2 = (ImageView) view.findViewById(R.id.zwt2);
        img3 = (ImageView) view.findViewById(R.id.zwt3);
        img4 = (ImageView) view.findViewById(R.id.zwt4);
        img5 = (ImageView) view.findViewById(R.id.zwt5);
        img6 = (ImageView) view.findViewById(R.id.zwt6);
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);
        baseView = (View) view.findViewById(R.id.photo_loading);
        takePhotoButton = (ImageButton) view.findViewById(R.id.photo);
        okButton = (ImageButton) view.findViewById(R.id.photo_ok);
        cancleButton = (ImageButton) view.findViewById(R.id.photo_cancel);
        takePhotoButton.setOnClickListener(takePhotoClickListener);
        okButton.setOnClickListener(okClickListener);
        cancleButton.setOnClickListener(cancelClickListener);

       // addPreviewsListener(null);
    }
//
//        String[] photos = {
//            "http://c.hiphotos.baidu.com/image/pic/item/d009b3de9c82d1585e277e5f840a19d8bd3e42b2.jpg",
//            "http://b.hiphotos.baidu.com/image/pic/item/4034970a304e251f34b7c316a386c9177e3e539f.jpg",
//            "http://c.hiphotos.baidu.com/image/pic/item/4ec2d5628535e5dd95e3823172c6a7efcf1b6214.jpg",
//            "http://g.hiphotos.baidu.com/image/pic/item/8b82b9014a90f6039dd1baa93d12b31bb151edff.jpg",
//            "http://f.hiphotos.baidu.com/image/pic/item/a2cc7cd98d1001e904cc1be7bc0e7bec55e79761.jpg",
//            "http://e.hiphotos.baidu.com/image/pic/item/f7246b600c338744e7162094550fd9f9d62aa002.jpg"};
    View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            okButton.setVisibility(View.GONE);
            cancleButton.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), UploadActivity.class);
            intent.putExtra("panoId", panoId);
            intent.putExtra("ip", device.getIpAddress());
            startActivity(intent);
        }
    };
    View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            okButton.setVisibility(View.GONE);
            cancleButton.setVisibility(View.GONE);
            String url = "http://" + device.getIpAddress() + "/cancel";
            RequestParams params = new RequestParams(url);
            params.addQueryStringParameter("panoId", panoId);
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
                    Log.i(Utils.TAG, "onSuccess: " + result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(Utils.TAG, "onError: 取消失败");
                }

            });
        }
    };
    View.OnClickListener takePhotoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.show();
//            String url = "http://" + device.getIpAddress();
//            url = url + "/captureParam";
//            String name = ParamsUtils.getConfig(getActivity());
//            String json = ParamsUtils.getParams(getActivity(), name);
//            List<Config> configs = ParamsUtils.convertToBeans(json);
            String url = "http://" + device.getIpAddress();
            url = url + "/capture";
            //设置参数
            takePhoto(url);
        }
    };

//    public void setParamsToPano(String url, final List<Config> configs, final String name) {
//        if (configs != null && configs.size() > 0) {
//            RequestParams params = new RequestParams(url);
//            List<Config> curConfigs = Utils.convertConfigs(configs);
//            String curJson = ParamsUtils.convertToJson(curConfigs);
//            params.setBodyContent(curJson);
//            params.setAsJsonContent(true);
//            params.setConnectTimeout(20000);
//            x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
//                @Override
//                public void onCancelled(CancelledException cex) {
//                }
//
//                @Override
//                public void onFinished() {
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//                    // Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
//                    String url = "http://" + device.getIpAddress();
//                    url = url + "/capture";
//                    //设置参数
//                    takePhoto(url);
//                }
//
//
//                @Override
//                public void onSuccess(String result) {
//                    Utils.log("result:" + result);
//                    try {
//                        JSONObject jsonObject = new JSONObject(result);
//                        int jsonResult = jsonObject.getInt("result");
//                        if (jsonResult == 0) {
//                            String jsonMsg = jsonObject.getString("msg");
//                            //  Toast.makeText(getActivity(), jsonMsg, Toast.LENGTH_SHORT).show();
//                            String json = ParamsUtils.convertToJson(configs);
//                            ParamsUtils.saveParams(getActivity(), name, json);
//                            ParamsUtils.saveConfig(getActivity(), name);
//                            String url = "http://" + device.getIpAddress();
//                            url = url + "/capture";
//                            //设置参数
//                            takePhoto(url);
//                            return;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    String url = "http://" + device.getIpAddress();
//                    url = url + "/capture";
//                    //设置参数
//                    takePhoto(url);
//                    // Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
//    }

    private void takePhoto(String url) {
        startPhoto();
        dialog.setMessage("正在拍照中");
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(80000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
                stopPhoto();
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                parseJson(result);
                stopPhoto();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                dialog.dismiss();
                stopPhoto();
            }

        });
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Utils.Toast(rootView, jsonObject.getString("msg"));
            JSONObject dataObject = jsonObject.getJSONObject("data");
            if (dataObject != null) {
                panoId = dataObject.getString("panoId");
                JSONArray jsonArray = dataObject.getJSONArray("previews");
                JSONArray photosArray = dataObject.getJSONArray("photos");
                fillPanoPreviews(jsonArray);
                addPreviewsListener(jsonArray);
                okButton.setVisibility(View.VISIBLE);
                cancleButton.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addPreviewsListener(JSONArray array) {
//        for (int i = 0; i < photos.length; i++) {
//            try {
//                final String previewUrl = photos[i];
//                final ImageView img = imgs.get(i);
//                if(!TextUtils.isEmpty(previewUrl))
//                {
//                    img.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            PhotoDialog.Builder pBuilder = new PhotoDialog.Builder(getActivity());
//                            pBuilder.create(previewUrl).show();
//
//                        }
//                    });
//                }
//                else
//                {
//                    Utils.Toast(rootView,"还没有缩略图哦");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        for (int i = 0; i < array.length(); i++) {
            try {
                final String previewUrl = "http://" + device.getIpAddress() + array.getString(i);
                final ImageView img = imgs.get(i);
                if(!TextUtils.isEmpty(previewUrl))
                {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhotoDialog.Builder pBuilder = new PhotoDialog.Builder(getActivity());
                            pBuilder.create(previewUrl).show();

                        }
                    });
                }
                else
                {
                    Utils.Toast(rootView,"还没有缩略图哦");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fillPanoPreviews(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                String previewUrl = "http://" + device.getIpAddress() + array.getString(i);
                x.image().bind(imgs.get(i), previewUrl, Utils.getImageOption());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startPhoto() {
        takePhotoButton.setClickable(false);
        okButton.setVisibility(View.GONE);
        cancleButton.setVisibility(View.GONE);
//        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.base_rotate);
//        //开启动画
//        baseView.setVisibility(View.VISIBLE);
//        baseView.startAnimation(animation);
    }

    ;

    private void stopPhoto() {
        takePhotoButton.setClickable(true);
        //baseView.clearAnimation();
        // baseView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
