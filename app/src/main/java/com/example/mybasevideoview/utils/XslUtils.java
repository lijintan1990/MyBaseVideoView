package com.example.mybasevideoview.utils;

import android.app.Activity;
import android.view.WindowManager;

import com.example.mybasevideoview.MainPlayerActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class XslUtils {
    //true隐藏，false显示
    public static void hideStausbar(WeakReference<Activity> activityWeakReference, boolean enable) {
        Activity activity = activityWeakReference.get();
        if (enable) {

            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        }
    }

    public static String convertSecToTimeString(long lSeconds) {
        long nHour = lSeconds / 3600;
        long nMin = lSeconds % 3600;
        long nSec = nMin % 60;
        nMin = nMin / 60;

        return String.format("%02d:%02d:%02d", nHour, nMin, nSec);
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    //flie：要删除的文件夹的所在位置
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

//    public static void unZipFile(String archive, String decompressDir)throws IOException, FileNotFoundException, ZipException
//    {
//        BufferedInputStream bi;
//        ZipFile zf = new ZipFile(archive, "GBK");
//        Enumeration e = zf.getEntry()
//        while (e.hasMoreElements())
//        {
//            ZipEntry ze2 = (ZipEntry) e.nextElement();
//            String entryName = ze2.getName();
//            String path = decompressDir + "/" + entryName;
//            if (ze2.isDirectory())
//            {
//                System.out.println("正在创建解压目录 - " + entryName);
//                File decompressDirFile = new File(path);
//                if (!decompressDirFile.exists())
//                {
//                    decompressDirFile.mkdirs();
//                }
//            } else {
//                System.out.println("正在创建解压文件 - " + entryName);
//                String fileDir = path.substring(0, path.lastIndexOf("/"));
//                File fileDirFile = new File(fileDir);
//                if (!fileDirFile.exists())
//                {
//                    fileDirFile.mkdirs();
//                }
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
//                bi = new BufferedInputStream(zf.getInputStream(ze2));
//                byte[] readContent = new byte[1024];
//                int readCount = bi.read(readContent);
//                while (readCount != -1)
//                {
//                    bos.write(readContent, 0, readCount);
//                    readCount = bi.read(readContent);
//                }
//                bos.close();
//            }
//        }
//        zf.close();
//    }
}
