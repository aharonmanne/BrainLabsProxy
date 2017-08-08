package com.ksdagile.brainlabsproxy;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;
    private static int REQUEST_ENABLE_BT = 2;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        //        Manifest.permission.READ_CONTACTS)) {
        //    Log.d(Constants.TAG, "Need to show rationale?");
        //    // Show an explanation to the user *asynchronously* -- don't block
        //    // this thread waiting for the user's response! After the user
        //    // sees the explanation, try again to request the permission.
//
        //} else {
//
        //    // No explanation needed, we can request the permission.
//
        //    ActivityCompat.requestPermissions(this,
        //            new String[]{Manifest.permission.READ_CONTACTS},
        //            MY_PERMISSIONS_READ_CONTACTS);
//
        //    // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
        //    // app-defined int constant. The callback method gets the
        //    // result of the request.
//
        //    return;
        //}

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mScanning = false;
    }

    public void onClickFind(View v) {

        scanLeDevice(mScanning);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           //ViewHolder viewHolder;
           //// General ListView optimization code.
           //if (view == null) {
           //    view = mInflator.inflate(R.layout.listitem_device, null);
           //    viewHolder = new ViewHolder();
           //    viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
           //    viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
           //    view.setTag(viewHolder);
           //} else {
           //    viewHolder = (ViewHolder) view.getTag();
           //}

           //BluetoothDevice device = mLeDevices.get(i);
           //final String deviceName = device.getName();
           //if (deviceName != null && deviceName.length() > 0)
           //    viewHolder.deviceName.setText(deviceName);
           //else
           //    viewHolder.deviceName.setText(R.string.unknown_device);
           //viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}


}