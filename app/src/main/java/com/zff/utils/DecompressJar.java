package com.zff.utils;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DecompressJar {

	public interface AsyncCallback{
		void onDecompressProgress(String message);
		void onDecompressResult(String message);
	}
	
	public boolean decompress(String srcJarPath, String dstDir){
		return decompressZip(srcJarPath, dstDir);
	}
	
	public boolean decompressJar(String srcJarPath, String dstDir){
		boolean isSuccess = false;
		File dstD = new File(dstDir);
		if(!dstD.exists()){
			dstD.mkdirs();
		}
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(srcJarPath);
			Enumeration<JarEntry> enums = jarFile.entries();
			while(enums.hasMoreElements()){
				JarEntry jarEntry = enums.nextElement();
				if(jarEntry.isDirectory()){
					File dir = new File(dstDir, jarEntry.getName());
					if(!dir.exists()){
						dir.mkdirs();
					}
				}
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dstDir, jarEntry.getName())));
				InputStream in = jarFile.getInputStream(jarEntry);
				byte[] bytes = new byte[1024];
				while(-1 != in.read(bytes)){
					bos.write(bytes);
				}
				isSuccess = true;
				if(bos != null){
					bos.close();
				}
				if(in != null){
					in.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(jarFile != null){				
				jarFile.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}

	public boolean decompressZip(String srcZipPath, String dstDir){
		boolean isSuccess = false;
		File dstD = new File(dstDir);
		if(!dstD.exists()){
			dstD.mkdirs();
		}
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(srcZipPath);
			Enumeration<ZipEntry> enums = (Enumeration<ZipEntry>) zipFile.entries();
			//Enumeration<JarEntry> enums = zipFile.entries();
			while(enums.hasMoreElements()){
				ZipEntry zipEntry = enums.nextElement();
				if(zipEntry.isDirectory()){
					File dir = new File(dstDir, zipEntry.getName());
					if(!dir.exists()){
						dir.mkdirs();
					}
				}
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dstDir, zipEntry.getName())));
				InputStream in = zipFile.getInputStream(zipEntry);
				BufferedInputStream bin = new BufferedInputStream(in);
				byte[] bytes = new byte[1024];
				while(-1 != bin.read(bytes)){
					bos.write(bytes);
				}
				isSuccess = true;
				if(bos != null){
					bos.close();
				}
				if(in != null){
					in.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(zipFile != null){				
				zipFile.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}

	public void decompressJarTask(File jarFile, final String dstDir, final AsyncCallback callback ){
		AsyncTask<File, Integer, Boolean> st = new AsyncTask<File, Integer, Boolean>(){

			@Override
			protected Boolean doInBackground(File... file) {
				// TODO Auto-generated method stub
				DecompressJar decompressJar = new DecompressJar();
				return decompressJar.decompress(file[0].getPath(), dstDir);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result){
					if(callback != null){
						callback.onDecompressResult("解压完成 "+dstDir);
					}
				}else{
					if(callback != null){
						callback.onDecompressResult("解压失败 ");
					}
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if(callback != null){
					callback.onDecompressProgress("正在解压  到 "+dstDir);
				}
			}

		};
		st.execute(jarFile);
	}
}

