package com.zff.xpanel.parser.view;

public class ImgViewArgs extends ViewArgs{

	private ImageDrawable imgDrawable;
	
	
	
	public ImageDrawable getImgDrawable() {
		return imgDrawable;
	}



	public void setImgDrawable(ImageDrawable imgDrawable) {
		this.imgDrawable = imgDrawable;
	}



	public static class ImageDrawable{
		public String imgPath;
		public int imgX, imgY, imgW, imgH;
	}
}
