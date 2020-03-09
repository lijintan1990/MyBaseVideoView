package com.xsl.culture.mybasevideoview.utils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.xsl.culture.mybasevideoview.model.Restore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

public class RestoreParamMng {
    private static final String storeFileName = "/360/.xsl";
    private static RestoreParamMng restoreParamMng;


    private RestoreParamMng() {
        getRestoreData();
    }

    public static RestoreParamMng getInstance() {
        if (restoreParamMng == null) {
            restoreParamMng = new RestoreParamMng();
        }
        return restoreParamMng;
    }

    public boolean getCacheState() {
        boolean needCache = true;
        getRestoreData();
        needCache = restore.getNeedCache() == 0 ? false : true;
        return needCache;
    }

    public boolean getPayState() {
        boolean needPay = true;
        getRestoreData();
        needPay = restore.getNeedPay() == 0 ? false : true;
        return needPay;
    }

    public boolean getNeedneedScreenTipsState() {
        boolean needneedScreenTips = true;
        getRestoreData();
        needneedScreenTips = restore.getNeedneedScreenTips() == 0 ? false : true;
        return needneedScreenTips;
    }

    public void setNeedScreenTipsState(boolean state) {
        restore.setNeedneedScreenTips(state ? 1 : 0);
        String data = GsonImplHelp.get().toJson(restore);
        try {
            write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNeedCacheState(boolean state) {
        restore.setNeedCache(state ? 1 : 0);
        String data = GsonImplHelp.get().toJson(restore);
        try {
            write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNeedPayState(boolean state) {
        restore.setNeedPay(state ? 1 : 0);
        String data = GsonImplHelp.get().toJson(restore);
        try {
            write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Restore restore = null;

    private Restore getRestoreData() {
        if (restore == null) {
            //文件读取
            File SDPath = Environment.getExternalStorageDirectory();//SD根目录
            File file = new File(SDPath, storeFileName);
            if (FileUtils.isFileExists(file)) {
                int dataLen = 0;
                FileInputStream inputStream = null;
//                try {
//                    inputStream = new FileInputStream(new File(SDPath, storeFileName).getAbsoluteFile());
//                    byte []data = new byte[512];
//                    try {
//                        dataLen = inputStream.read(data);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    inputStream.close();
                    String content = read(new File(SDPath, storeFileName).getAbsoluteFile());
                    Log.d("xxx", "json data len " + dataLen);
                    if (content == null || content.isEmpty()) {
                        String json = "{\n" +
                                "    \"needCache\":1,\n" +
                                "    \"needPay\":0,\n" +
                                "    \"needneedScreenTips\":1\n" +
                                "}";
//                        String json = "{needPay:1,\"needCache\":1,\"needneedScreenTips\":1}";

                        try {
                            write(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                         restore = new Restore();
                         GsonImplHelp.get().toObject(content, Restore.class);
                    }

//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            } else {
                String json = "{needPay : 1, \"needCache\" : 1,\"needneedScreenTips\" : 1}";
                restore = GsonImplHelp.get().toObject(json, Restore.class);
                try {
                    write(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return restore;
    }

    public static synchronized void write(String content) throws IOException {
        //创建一个带缓冲区的输出流
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File SDPath = Environment.getExternalStorageDirectory();//SD根目录
            File file = new File(SDPath, storeFileName);
            FileWriter writer = new FileWriter(file, false);
            writer.write(content);
            writer.close();
        }
    }

    public static String read(File file) {
        String content = null;
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        Log.e("TestFile", "ReadTxtFile: " + line);
                        content += line;
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }

        return content;

    }
}
