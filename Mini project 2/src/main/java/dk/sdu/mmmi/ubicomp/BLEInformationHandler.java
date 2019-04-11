package dk.sdu.mmmi.ubicomp;

import android.content.Context;

import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class BLEInformationHandler {
    private Context c;

    public BLEInformationHandler(Context c) {
        this.c = c;
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = c.getAssets().open("beacons.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public BLEDevice getBLEInfo(RemoteBluetoothDevice device) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray beaconsArray = obj.getJSONArray("beacons");
            BLEDevice bleDevice;
            for (int i = 0; i < beaconsArray.length(); i++) {
                JSONObject beacon = beaconsArray.getJSONObject(i);
                String alias_value = beacon.getString("alias");
                if (device.getUniqueId().equals(alias_value)) {
                    bleDevice = new BLEDevice(alias_value, beacon.getString("roomName"), beacon.getString("level"), device.getRssi(), device.getTimestamp());
                    return bleDevice;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
