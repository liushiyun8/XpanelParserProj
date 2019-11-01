package com.zff.xpanel.parser.view;


/**
 * 
 * @author 1016tx
 * 
 * 进度条args
 *
 */
public class GaugeArgs extends ViewArgs{

	public enum Orientation{
		VERTICAL,
		HORIZONTAL
	}

	protected Orientation mOrientation;
	protected int mMin = 0;
	protected int mMax = 100;
	protected int mCurrentProgress;
	
	
	public GaugeArgs() {
		super(Type.PROGRESS_BAR);
	}
	
	public int getmMin() {
		return mMin;
	}
	public void setmMin(int mMin) {
		this.mMin = mMin;
	}
	public int getmMax() {
		return mMax;
	}
	public void setmMax(int mMax) {
		this.mMax = mMax;
	}

	public int getCurrentProgress() {
		return mCurrentProgress;
	}

	public void setCurrentProgress(int mCurrentProgress) {
		this.mCurrentProgress = mCurrentProgress;
	}

	public Orientation getOrientation() {
		return mOrientation;
	}

	public void setOrientation(Orientation mOrientation) {
		this.mOrientation = mOrientation;
	}
}
