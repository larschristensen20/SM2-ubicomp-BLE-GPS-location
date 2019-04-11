package com.kontakt.sample.samples;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sample.BLEDevice;
import com.kontakt.sample.BLELocationHandler;
import com.kontakt.sample.R;
import com.kontakt.sample.service.BackgroundScanService;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.TreeSet;

/**
 * This is an example of implementing a background scan using Android's Service component.
 */
public class BackgroundScanActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, BackgroundScanActivity.class);
  }
  private GoogleMap mMap;
  private MapView mapView;
  private Intent serviceIntent;
  private TextView statusText;
  private TreeSet<BLEDevice> deviceList;
  private BLELocationHandler locationHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background_scan);
    statusText = (TextView) findViewById(R.id.status_text);
    serviceIntent = new Intent(getApplicationContext(), BackgroundScanService.class);

    deviceList = new TreeSet<>();

    mapView = this.findViewById(R.id.mapView);
    mapView.onCreate(null);
    mapView.onResume();
    mapView.getMapAsync(this);
    //Setup Toolbar
    setupToolbar();
    startBackgroundService();

    locationHandler = new BLELocationHandler(this);
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

  private void setupToolbar() {
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void startBackgroundService() {
    startService(serviceIntent);
  }

  private void stopBackgroundService() {
    stopService(serviceIntent);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.start_scan_button:
        startBackgroundService();
        break;
      case R.id.stop_scan_button:
        stopBackgroundService();
        break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        stopBackgroundService();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private final BroadcastReceiver scanningBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      //Device discovered!
      int devicesCount = intent.getIntExtra(BackgroundScanService.EXTRA_DEVICES_COUNT, 0);
      RemoteBluetoothDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
      BLEDevice bleDevice = locationHandler.getBLEInfo(device.getUniqueId());
      bleDevice.setDiscovered(device.getTimestamp());
      bleDevice.setSignalStrength(device.getRssi());

      deviceList.add(bleDevice);

      statusText.setText(String.format("\n You are in: %s", bleDevice.getAlias()));
    }
  };
}
