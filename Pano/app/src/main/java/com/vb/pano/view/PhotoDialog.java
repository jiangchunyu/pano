package com.vb.pano.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vb.pano.R;
import com.vb.pano.photo.PhotoView;

import org.xutils.common.Callback;
import org.xutils.x;

/**
 * Created by seven on 2016/12/30.
 */

public class PhotoDialog extends Dialog {

    private Context context;

    public PhotoDialog(Context context) {
        super(context);
    }

    public PhotoDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        super.show();

    }

    public static class Builder {
        private Context context;
        private PhotoView photoView;

        public Builder(Context context) {
            this.context = context;
        }


        public PhotoDialog create(String url) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final PhotoDialog dialog = new PhotoDialog(context,
                    R.style.Dialog);
            View layout = inflater.inflate(R.layout.photodialog, null);
            photoView = (PhotoView) layout.findViewById(R.id.photoview);
            photoView.enable();
            photoView.enableRotate();
            final ProgressBar bar = (ProgressBar) layout.findViewById(R.id.loading);
            bar.setVisibility(View.VISIBLE);
            x.image().bind(photoView, url, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    try{

                        BitmapDrawable bd=(BitmapDrawable)result;
                        Bitmap bitmap=bd.getBitmap();
                        int width=bitmap.getWidth();
                        int height=bitmap.getHeight();
                        if(width>height)
                        {
                            //旋转90度
                            // 创建操作图片用的matrix对象
                            Matrix matrix = new Matrix();

                            //旋转图片
                            matrix.postRotate(90,width/2,height/2);
                            bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                            Log.i("sym","应该旋转");
                        }
                        else
                        {

                            Log.i("sym","不应该旋转");
                        }
                        photoView.setImageBitmap(bitmap);
                    }
                    catch(Exception e)
                    {

                    }

                    bar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(context, "加载图片失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(layout);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int)(display.getWidth()); //设置宽度
            dialog.getWindow().setAttributes(lp);
            return dialog;
        }
    }

}
