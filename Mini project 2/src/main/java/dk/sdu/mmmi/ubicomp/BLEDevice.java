package dk.sdu.mmmi.ubicomp;

import android.support.annotation.NonNull;

public class BLEDevice implements Comparable<BLEDevice> {

    private String alias;
    private String roomName;
    private String level;
    private int signalStrength;
    private long discovered;

    public BLEDevice(String alias, String roomName, String level, int signalStrength, long discovered) {
        this.alias = alias;
        this.roomName = roomName;
        this.level = level;
        this.signalStrength = signalStrength;
        this.discovered = discovered;
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
                ", signalStrength=" + signalStrength +
                '}';
    }
}
