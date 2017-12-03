package com.sarindev.bluetoothtutorial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1891;

    BluetoothAdapter bluetoothAdapter;
    boolean isBtEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check if device supports bluetooth
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth.");
        } else {
            // Check for bluetooth state. If turned off then ask permission to turn it on.
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                isBtEnabled = true;
                // if Bt enabled then request paired devices
                getPairedDevices();
                //discover devices nearby
                discoverBtDevices();
            }
        }

        // register for broadcasts when a new device is found
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiverBt,intentFilter);
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bluetoothDevices) {
            Log.d(TAG, "Paired device name = " + device.getName());
            Log.d(TAG, "Paired device Mac = " + device.getAddress());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_ENABLE_BT) && (resultCode == RESULT_OK)) {
            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
            isBtEnabled = true;
            getPairedDevices();
            discoverBtDevices();
        }
    }

    private BroadcastReceiver receiverBt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equalsIgnoreCase(action)){
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Discovered device name = " + device.getName());
                Log.d(TAG, "Discovered device Mac = " + device.getAddress());
            }
        }
    };

    private  void discoverBtDevices(){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the ACTION_FOUND receiver
        unregisterReceiver(receiverBt);
    }
}
