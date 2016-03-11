package preciousband.precious.comnet.aalto.preciousband.model;

import android.util.Log;

import java.util.Observable;


public class MiBand extends Observable {

	public String mBTAddress;
	public int mSteps;
	public String mName;
	public Battery mBattery;
	public LeParams mLeParams;
	private static final String TAG = "MiBand";
	
	
	public void setName(String name) {
		mName = name;
		Log.i(TAG,"setting " + name + " as BLE name");
		setChanged();
		notifyObservers();
	}
	
	public void setSteps(int steps) {
		mSteps = steps;
		Log.i(TAG,"setting "+steps+" steps");
		setChanged();
		notifyObservers();
	}
	
	public void setBattery(Battery battery) {
		mBattery = battery;
		Log.i(TAG,battery.toString());
		setChanged();
		notifyObservers();
	}

	public void setLeParams(LeParams params) {
		mLeParams = params;
	}
	
}
