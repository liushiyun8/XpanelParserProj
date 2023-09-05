package com.zff.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetUtils {

    public static void copyAssetDirToFiles(Context context, String dirname, String fileDirPath) throws IOException {
        File dir = new File(fileDirPath);
        dir.mkdirs();
        AssetManager assetManager = context.getAssets();
        String[] children = assetManager.list(dirname);
        int var6 = children.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String child = children[var7];
            String next = dirname + '/' + child;
            String[] grandChildren = assetManager.list(next);
            if (0 == grandChildren.length) {
                copyAssetFileToFile(context,fileDirPath+File.separator+child, next);
            } else {
                copyAssetDirToFiles(context, next ,fileDirPath+File.separator+child);
            }
        }

    }

    public static void copyAssetFileToFile(Context context, String fileDirPath, String filename) throws IOException {
        Log.d("FileUtils", "[" + filename + "] copy to [" + fileDirPath + "/" + filename + "]");
        InputStream is = context.getAssets().open(filename);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        File of = new File(fileDirPath);
        if(!of.getParentFile().exists()){
            of.getParentFile().mkdirs();
        }
        of.createNewFile();
        FileOutputStream os = new FileOutputStream(of);
        os.write(buffer);
        os.flush();
        os.getFD().sync();
        os.close();
    }
}