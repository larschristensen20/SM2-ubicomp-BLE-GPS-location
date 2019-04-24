package dk.sdu.mmmi.ubicomp;

import android.support.annotation.NonNull;

public class BLEDevice implements Comparable<BLEDevice> {

    private String alias;
    private String roomName;
    private String level;
    private int signalStrength;
    private long discovered;
    private double lat;
    private double lon;

    public BLEDevice(String alias, String roomName, String level, int signalStrength, long discovered, double lat, double lon) {
        this.alias = alias;
        this.roomName = roomName;
        this.level = level;
        this.signalStrength = signalStrength;
        this.discovered = discovered;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAlias() {
        return alias;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getLevel() {
        return level;
    }

    public long getDiscovered() {
        return discovered;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public double getLat() { return lat; }

    public double getLon() { return lon; }

    @Override
    public int compareTo(@NonNull BLEDevice o) {
        int otherStrength = o.getSignalStrength();
        return otherStrength - this.getSignalStrength();
    }

    @Override
    public String toString() {
        return "BLEDevice{" +
                "alias='" + alias + '\'' +
                ", roomName='" + roomName + '\'' +
                ", level='" + level + '\'' +
                ", signalStrength=" + signalStrength +
                ", discovered=" + discovered +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
