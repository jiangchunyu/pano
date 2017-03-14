package com.vb.pano.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vb.pano.bean.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by seven on 2016/11/14.
 * 对相机设置的参数进行保存
 */

public class ParamsUtils {

    public static final String CONFIG = "paramConfig";
    public static final String IP_CONFIG = "ipConfig";
    public static final String DIR = "vbpano";
    public static final String PARAM = "panoParam";

    //得到一个初始化的相机参数
    public static List<Config> getInitConfig() {
        List<Config> configs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Config config = new Config();
            config.setCameraId("0" + (i + 1));
            config.setBrightness(14 + "");
            config.setContrast(75 + "");
            config.setIso(400+"");
            config.setSaturation(80 + "");
            config.setSharpness(1 + "");
            config.setShutter_spd("1/20");
            config.setWb("Auto");
            configs.add(config);
        }
        return configs;
    }

    //list数组转为json字符串
    public static String convertToJson(List<Config> configs) {
        Gson gson = new Gson();
        String json = gson.toJson(configs);
        return json;
    }

    //json字符串转化为javabean数组
    public static List<Config> convertToBeans(String json) {
        Gson gson = new Gson();
        List<Config> configs = gson.fromJson(json, new TypeToken<List<Config>>() {
        }.getType());
        return configs;
    }

    //存储当前的相机配置名称
    public static void saveConfig(Context context, String modeName) {
        SharedPreferences sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mode", modeName);
        editor.commit();
    }

    //获取当前的相机配置名称
    public static String getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        String modeName = sp.getString("mode", null);
        return modeName;
    }

    //读取所有本地的相机配置参数
    public static List<String> getAllMode(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PARAM, Context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
//            File file = new File(Environment.getExternalStorageDirectory()
//                    .toString()
//                    + File.separator
//                    + DIR); // 定义File类对象
//            if (file.isDirectory()) {
//                List<String> list = new ArrayList<>();
//                File[] files = file.listFiles();
//                if (files != null && files.length > 0) {
//                    for (File f : files) {
//                        if (f.isFile()) {
//                            String fileName = f.getName();
//                            list.add(fileName);
//                        }
//                    }
//                    return list;
//                }
//
//            } else {
//                return null;
//            }
//
//        }
//        return null;
    }

    //读取相机参数存储信息
    public static String getParams(Context c, String fileName) {
        SharedPreferences sp = c.getSharedPreferences(PARAM, Context.MODE_PRIVATE);
        return sp.getString(fileName, null);
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
//            File file = new File(Environment.getExternalStorageDirectory()
//                    .toString()
//                    + File.separator
//                    + DIR
//                    + File.separator
//                    + fileName); // 定义File类对象
//            if (!file.getParentFile().exists()) { // 父文件夹不存在
//                file.getParentFile().mkdirs(); // 创建文件夹
//            }
//            if (!file.exists()) {
//                return null;
//            }
//            Scanner scan = null; // 扫描输入
//            StringBuilder sb = new StringBuilder();
//            try {
//                scan = new Scanner(new FileInputStream(file)); // 实例化Scanner
//                while (scan.hasNext()) { // 循环读取
//                    sb.append(scan.next() + "\n"); // 设置文本
//                }
//                return sb.toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (scan != null) {
//                    scan.close(); // 关闭打印流
//                }
//            }
//        } else { // SDCard不存在，使用Toast提示用户
//            return null;
//        }
//        return null;
    }

    //保存相机参数存储信息
    public static void saveParams(Context c, String fileName, String content) {

        SharedPreferences sp = c.getSharedPreferences(PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(fileName, content);
        editor.commit();
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//        {
//            File file =new File(Environment.getExternalStorageDirectory().toString()
//            +File.separator+DIR+File.separator+fileName);
//            if(!file.getParentFile().exists())
//            {
//                file.getParentFile().mkdirs();
//            }
//            if(file.exists())
//            {
//                file.delete();
//            }
//            PrintStream ps=null;
//            try {
//                file.createNewFile();
//                ps=new PrintStream(new FileOutputStream(file));
//                ps.println(content);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            finally {
//                if(ps!=null)
//                {
//                    ps.close();
//                }
//            }
//        }
    }
    //给相机设置参数
//    public static void setParamsToPano(String url,List<Config> configs)
//    {
//        if(configs!=null&&configs.size()>0)
//        {
//            for(Config config :configs)
//            {
//                RequestParams params=new RequestParams(url);
//                params.addQueryStringParameter("cameraId",config.getCameraId());
//                params.addQueryStringParameter("shutter_spd",config.getShutter_spd());
//                params.addQueryStringParameter("iso",config.getIso());
//                params.addQueryStringParameter("saturation",config.getSaturation());
//                params.addQueryStringParameter("sharpness",config.getSharpness());
//                params.addQueryStringParameter("brightness",config.getBrightness());
//                params.addQueryStringParameter("contrast",config.getContrast());
//                x.http().get(params, new Callback.CommonCallback<String>() {
//                    @Override
//                    public void onCancelled(CancelledException cex) {
//
//                    }
//
//                    @Override
//                    public void onFinished() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(String result) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(result);
//                            int jsonResult=jsonObject.getInt("result");
//                            if(jsonResult==0)
//                            {
//                                String jsonMsg=jsonObject.getString("msg");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//            }
//
//        }
//    }
}
