package com.zff.utils;

import java.io.File;


public class FileTools {

    /**
     * 查找文件
     * @param dir
     * @param fileName
     * @return
     */
    public static File findFile(File dir, String fileName){
        File[] files = dir.listFiles();
        File file = null;
        for(int i=0; i<files.length; i++){
            if(files[i].getName().equals(fileName)){
                file = files[i];
                break;
            }
        }
        return file;
    }

    /**
     * 查询文件
     * @param dir 目录
     * @param fileSuffix 文件后缀
     * @return
     */
    public static File lookupFile(File dir, String fileSuffix){
        File[] files = dir.listFiles();
        File file = null;
        if(files!=null)
        for(int i=0; i<files.length; i++){
            if(files[i].getName().endsWith(fileSuffix)){
                file = files[i];
                break;
            }
        }
        return file;
    }

    /**
     * 删除目录及目录下的所有文件
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir){
        File[] files = dir.listFiles();
        for(File f : files){
            if(f.isDirectory()){
                deleteDir(f);
            }else{
                f.delete();
            }
        }
        return dir.delete();
    }
}
