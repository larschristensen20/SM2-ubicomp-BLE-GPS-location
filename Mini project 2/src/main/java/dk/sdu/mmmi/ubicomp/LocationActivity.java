package dk.sdu.mmmi.ubicomp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import dk.sdu.mmmi.ubicomp.service.BackgroundScanService;

/**
 * This is an example of implementing a background scan using Android's Service component.
 */
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, LocationActivity.class);
    }

    private GoogleMap mMap;
    private MapView mapView;
    private Intent serviceIntent;
    private TextView statusText;
    private TreeSet<BLEDevice> deviceList;
    private BLEInformationHandler locationHandler;
    private Timer bleListScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        statusText = findViewById(R.id.status_text);
        serviceIntent = new Intent(getApplicationContext(), BackgroundScanService.class);

        deviceList = new TreeSet<>();
        bleListScanner = new Timer();

        mapView = this.findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

        startBackgroundService();

        locationHandler = new BLEInformationHandler(this);

        startBLEListScanner();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register Broadcast receiver that will accept results from background scanning
        IntentFilter intentFilter = new IntentFilter(BackgroundScanService.ACTION_DEVICE_DISCOVERED);
        registerReceiver(scanningBroadcastReceiver, intentFilter);
        startBackgroundService();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(scanningBroadcastReceiver);
        stopBackgroundService();
        super.onPause();
    }

    private void startBackgroundService() {
        startService(serviceIntent);
    }

    private void stopBackgroundService() {
        stopService(serviceIntent);
    }


    private final BroadcastReceiver scanningBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Device discovered!
            int devicesCount = intent.getIntExtra(BackgroundScanService.EXTRA_DEVICES_COUNT, 0);
            RemoteBluetoothDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
            BLEDevice bleDevice = locationHandler.getBLEInfo(device);

            deviceList.add(bleDevice);
            manageDeviceList();

            //statusText.setText(String.format("\n You are in: %s", bleDevice.getAlias()));
            statusText.setText(String.format("\n You are in: %s", deviceList.first()));
        }
    };

    private void manageDeviceList() {
        long currentTime = System.currentTimeMillis() * 1000;

        for (BLEDevice device : deviceList) {
            if (currentTime - device.getDiscovered() > 30) {
                deviceList.remove(device);
            }
        }

        if (deviceList.isEmpty()) {
            statusText.setText("No BLE's found");
        }
    }

    private void startBLEListScanner() {
        bleListScanner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        manageDeviceList();
                    }
                });
            }

        }, 0, 30000);
    }

    private void stopBLEListScanner() {
        bleListScanner.cancel();
    }
}
