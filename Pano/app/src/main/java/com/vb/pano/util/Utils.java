package com.vb.pano.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vb.pano.R;
import com.vb.pano.bean.Config;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by seven on 2016/11/7.
 */

public class Utils {
    public static final String TAG = "vbpano";
    public static final String PING = "ping -c 1 -w 0.5 ";
    private NetworkInterface intf;
    public static final String IP_CONFIG="ipConfig";
    private static final String FILE_NAME="vbpano.txt";
    private static final File DIRECTORY= Environment.getExternalStorageDirectory();

    private static final String panoTestUrl="http://wap.visualbusiness.cn/pano/guestpano/index.html?panoId=";
    private static final String panoUrl="http://pano.visualbusiness.cn/single/index.html?panoId=";

    private static final String processTestUrl="https://processtest.pano.visualbusiness.cn/rest/pano/accessCode?panoId=";
    private static final String processUrl="https://process.pano.visualbusiness.cn/rest/pano/accessCode?panoId=";

    public static String getPanoUrl(boolean isOnline)
    {
        if(isOnline)
        {
            return  panoUrl;
        }
        else
        {
            return panoTestUrl;
        }
    }
    public static String getprocressUrl(boolean isOnline)
    {
        if(isOnline)
        {
            return processUrl;
        }
        else
        {
            return processTestUrl;
        }
    }
    public static String getTilesUrl(boolean isOnline,String id)
    {
        if(isOnline)
        {
            return "http://tiles.pano.visualbusiness.cn/"+id+"/sphere/thumb.jpg";
        }
        else
        {
            return "http://tilestest.pano.visualbusiness.cn/"+id+"/sphere/thumb.jpg";
        }
    }

    // 打印log
    public static void log(String msg) {
        Log.i(TAG, msg);
    }

    //弹出toast
    public static void Toast(View v, String msg)
    {
        try{
            Snackbar.make(v,msg,Snackbar.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {

        }

    }
    //读取是否上传到线上
    public static boolean isUpLoadOnline(Context c)
    {
        SharedPreferences sp=c.getSharedPreferences("upload",Context.MODE_PRIVATE);
        return sp.getBoolean("online",true);
    }
    //存储是够上传到线上的状态
    public static void saveUploadOnLine(Context c,boolean upload){
        SharedPreferences sp=c.getSharedPreferences("upload",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("online",upload);
        editor.commit();
    }


    //存储当前的相机配置名称
    public static void saveIp(Context context , String ip)
    {
        SharedPreferences sp=context.getSharedPreferences(IP_CONFIG,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("ip",ip);
        editor.commit();
    }
    //获取当前的相机配置名称
    public static String getIp(Context context )
    {
        SharedPreferences sp=context.getSharedPreferences(IP_CONFIG,Context.MODE_PRIVATE);
        String ip=sp.getString("ip",null);
        return ip;
    }
    //存储设备信息到文件中
    public static void saveDevices( Context c,String s)
    {
        String devices=getDevices(c);
        devices+=s;
        SharedPreferences sp=c.getSharedPreferences("devices",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("devices",devices);
        editor.commit();
//        File file=new File(DIRECTORY,FILE_NAME);
//        if(!file.exists())
//        {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                log("文件创建失败");
//                return;
//            }
//        }
//        try {
//            FileOutputStream outputStream=new FileOutputStream(file,true);
//            OutputStreamWriter writer=new OutputStreamWriter(outputStream);
//            writer.write(json);
//            writer.flush();
//            writer.close();
//            outputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    //删除设备存储文件
    public static void deleteDevices(Context c)
    {
        SharedPreferences sp=c.getSharedPreferences("devices",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.remove("devices");
        editor.commit();
//        File file=new File(DIRECTORY,FILE_NAME);
//        if(file.exists())
//        {
//            file.delete();
//        }
    }
    //读取设备信息
    public static String  getDevices(Context c)
    {
        SharedPreferences sp=c.getSharedPreferences("devices",Context.MODE_PRIVATE);
        return sp.getString("devices","");
//        File file=new File(DIRECTORY,FILE_NAME);
//        if(!file.exists())
//        {
//            return null;
//        }
//        byte Buffer[] = new byte[1024*100];
//        //得到文件输入流
//        FileInputStream in = null;
//        ByteArrayOutputStream outputStream = null;
//        try {
//            in = new FileInputStream(file);
//            //读出来的数据首先放入缓冲区，满了之后再写到字符输出流中
//            int len = in.read(Buffer);
//            //创建一个字节数组输出流
//            outputStream = new ByteArrayOutputStream();
//            outputStream.write(Buffer,0,len);
//            //把字节输出流转String
//            return new String(outputStream.toByteArray());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            if(in!=null){
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(outputStream!=null){
//                try {
//                    outputStream.flush();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
    }


    // 获取本机ip地址
    public static String getLocalIp() {
        //固定获取wlan0的ip
        String ipaddress = "";
        try {
            NetworkInterface wlan0=NetworkInterface.getByName("wlan0");
            Enumeration<InetAddress> address=wlan0.getInetAddresses();
            while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        ipaddress = ip.getHostAddress();
                    }
                }
        } catch (SocketException e) {
            e.printStackTrace();
        }
//        try {
//            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
//            // 遍历所用的网络接口
//            while (en.hasMoreElements()) {
//                NetworkInterface networks = en.nextElement();
//                // 得到每一个网络接口绑定的所有ip
//                Enumeration<InetAddress> address = networks.getInetAddresses();
//                // 遍历每一个接口绑定的所有ip
//                while (address.hasMoreElements()) {
//                    InetAddress ip = address.nextElement();
//                    if (!ip.isLoopbackAddress()
//                            && ip instanceof Inet4Address) {
//                        ipaddress = ip.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }

        return ipaddress;

    }

    // 获取同一网段
    public static String getNetwork(String ip) {
        if (ip != null) {
            return ip.substring(0, ip.lastIndexOf("."));
        }
        return null;

    }

    // 通过exec去ping局域网ip地址
    public static String ping(String remoteIp) {
        String pingAddress = PING + remoteIp;
        Process proc = null;
        Runtime run = Runtime.getRuntime();
        try {
            proc = run.exec(pingAddress);
            int result = proc.waitFor();
            if (result == 0) {

                return remoteIp;

            } else {

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } finally {
            proc.destroy();
        }

        return null;
    }
    //获取imageoption
    public static ImageOptions getImageOption()
    {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.zwt_pic)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.zwt_pic)//加载失败后默认显示图片
                .build();
        return imageOptions;
    }
    //获取imageoption
    public static ImageOptions getPanoImageOption()
    {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setRadius(10)
                .setUseMemCache(true)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.pano_zwt2)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.pano_zwt2)//加载失败后默认显示图片
                .build();
        return imageOptions;
    }
    //判断一个数是否为整数
    public static boolean isNumeric(String str){
        if(TextUtils.isEmpty(str))
        {
            return false;
        }
        for (int i = str.length();--i>=0;){
            if(i==0)
            {
                if (!Character.isDigit(str.charAt(i))&&(str.charAt(i)!='-')){
                    return false;
                }
            }
            else
            {
                if (!Character.isDigit(str.charAt(i))){
                    return false;
                }
            }

        }
        return true;
    }



    //格式化configs

    public static List<Config> convertConfigs(List<Config> configs)
    {
        List<Config> list=new ArrayList<>();
        if(configs.size()>0)
        {
            for(Config config :configs)
            {
                Config curConfig=new Config();
                curConfig.setCameraId(config.getCameraId());
                curConfig.setIso(config.getIso());
                curConfig.setSaturation(config.getSaturation());
                curConfig.setBrightness(config.getBrightness());
                curConfig.setContrast(config.getContrast());
                curConfig.setSharpness(config.getSharpness());
                curConfig.setWb(config.getWb());
                String shutter=config.getShutter_spd();
                if(!shutter.contains("/")&&!shutter.contains("\""))
                {
                    shutter=shutter+"\"";
                    curConfig.setShutter_spd(shutter);
                }
                else
                {
                    curConfig.setShutter_spd(shutter);
                }
                list.add(curConfig);
            }
        }
        return list;
    }
}
