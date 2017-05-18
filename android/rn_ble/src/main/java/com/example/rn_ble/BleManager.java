package com.example.rn_ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

class BleManager extends ReactContextBaseJavaModule implements ActivityEventListener {

	public static final String LOG_TAG = "logs";
	private static final int ENABLE_REQUEST = 539;
	public Handler Bluetooth_Handler;
	public boolean IsScanning = false;
	public Date LastActiveTime = null;
	private long SCAN_PERIOD = 1000L;
	private long ExitedTime = 10L;
	public Date StartTime = null;
	private BluetoothAdapter bluetoothAdapter;
	private Context context;
	private ReactApplicationContext reactContext;
	private Callback enableBluetoothCallback;
	public List<Beacon> Beacons = new ArrayList();


    private LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			 final  Beacon beacon = Beacon.fromScanData(device, rssi, scanRecord);
			runOnUiThread(new Runnable() {
						@Override
						public void run() {
							 IsBeaconNewEnter(beacon);
							Log.e("size",Beacons.size()+"");

						}});
		}

	};
	@ReactMethod
	public void start(Callback callback ) {
		this.StartTime = new Date();
		this.LastActiveTime = new Date();
		this.Beacons.clear();
		this.Bluetooth_Handler = new Handler();
		if (getBluetoothAdapter() == null) {
			Log.d(LOG_TAG, "No bluetooth support");
			callback.invoke("No bluetooth support");
			return;
		}else{
			if (!getBluetoothAdapter().isEnabled()) {
				getBluetoothAdapter().enable();
			}
			callback.invoke("start success");
		}
	}
	private void IsBeaconNewEnter(Beacon beacon) {
		try {
			if(beacon != null) {
				boolean ex = false;

				for(int i = 0; i < this.Beacons.size(); ++i) {
					Beacon item = (Beacon)this.Beacons.get(i);
					if(item.MacAddress.equals(beacon.MacAddress)) {
						ex = true;
						item.LastScanTime = new Date();
						item.RSSI = beacon.RSSI;
						item.MeasuredPower = beacon.MeasuredPower;
						item.Protocol = beacon.Protocol;
					}
				}
				if(!ex) {
					this.Beacons.add(new Beacon(beacon.Device, beacon.UUID, beacon.Name, beacon.MacAddress, beacon.Major, beacon.Minor, beacon.MeasuredPower, beacon.RSSI, beacon.Protocol));

				}
			}
		} catch (Exception var5) {
			Log.e("IsBeaconNewEnter", "异常：" + var5.toString());
		}
		for(int i = 0;i<Beacons.size();i++){
			Beacon localBeacon = Beacons.get(i);
			WritableMap region = Arguments.createMap();
			region.putString("UUID", localBeacon.UUID);
			region.putString("Name", localBeacon.Name);
			region.putString("Distance", MeasuringDistance.calculateAccuracy(localBeacon.MeasuredPower, localBeacon.RSSI)+"");
			sendEvent("Ble",region);
		}
	}
	public BleManager(ReactApplicationContext reactContext) {
		super(reactContext);
		context = reactContext;
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(this);

	}


	@Override
	public String getName() {
		return "BleManager";
	}

	private BluetoothAdapter getBluetoothAdapter() {
		if (bluetoothAdapter == null) {
			BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = manager.getAdapter();
		}
		return bluetoothAdapter;
	}

	public void sendEvent(String eventName,
						   @Nullable WritableMap params) {
		getReactApplicationContext()
				.getJSModule(RCTNativeAppEventEmitter.class)
				.emit(eventName, params);
	}
	private void IsBeaconExited() {
		try {
			Date ex = new Date();

			for(int i = 0; i < this.Beacons.size(); ++i) {
				Beacon item = (Beacon)this.Beacons.get(i);
				long TotalMinutes = (ex.getTime() - item.LastScanTime.getTime()) / 60000L;
				if(TotalMinutes > this.ExitedTime) {
					this.Beacons.remove(i);
				}
			}
		} catch (Exception var6) {
			Log.e("IsBeaconNewEnter", "异常：" + var6.toString());
		}

	}
	private void ScanLeDevice(boolean enable) {
		if(enable) {
			if(this.IsScanning) {
				return;
			}

			this.Bluetooth_Handler.postDelayed(new Runnable() {
				public void run() {
					  IsScanning = false;
					  getBluetoothAdapter().stopLeScan(mLeScanCallback);
					  IsBeaconExited();

				}
			}, this.SCAN_PERIOD);
			this.IsScanning = true;
			this.LastActiveTime = new Date();
			getBluetoothAdapter().startLeScan(this.mLeScanCallback);
		} else {
			this.IsScanning = false;
			getBluetoothAdapter().stopLeScan(this.mLeScanCallback);
		}

	}
	@ReactMethod
	public void scan() {
		Log.d(LOG_TAG, "scan");
	    ScanLeDevice(true);
	}

	@ReactMethod
	public void stopScan( ) {
		ScanLeDevice(false);
	}
	@ReactMethod
	public void checkState(){


		BluetoothAdapter adapter = getBluetoothAdapter();
		String state = "off";
		if (adapter != null) {
			switch (adapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					state = "on";
					break;
				case BluetoothAdapter.STATE_OFF:
					state = "off";
			}
		}

		WritableMap map = Arguments.createMap();
		map.putString("state", state);

		sendEvent("BleManagerDidUpdateState", map);
	}


	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivityResult");
		if (requestCode == ENABLE_REQUEST && enableBluetoothCallback != null) {
			if (resultCode == RESULT_OK) {
				enableBluetoothCallback.invoke();
			} else {
				enableBluetoothCallback.invoke("User refused to enable");
			}
			enableBluetoothCallback = null;
		}
	}

	@Override
	public void onNewIntent(Intent intent) {

	}

}
