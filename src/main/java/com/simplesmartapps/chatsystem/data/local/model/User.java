package com.simplesmartapps.chatsystem.data.local.model;

import java.net.InetAddress;
import java.util.Objects;

public final class User {
    private final String macAddress;
    private final String username;
    private final boolean isConnected;
    private final InetAddress ipAddress;

    public User(String macAddress, String username, InetAddress ipAddress, boolean isConnected) {
        this.macAddress = macAddress;
        this.username = username;
        this.isConnected = isConnected;
        this.ipAddress = ipAddress;
    }

    public String macAddress() {
        return macAddress;
    }

    public String username() {
        return username;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public InetAddress ipAddress() {
        return ipAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.macAddress, that.macAddress) &&
                Objects.equals(this.username, that.username) &&
                this.isConnected == that.isConnected &&
                Objects.equals(this.ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress);
    }

    @Override
    public String toString() {
        return "User[" +
                "macAddress=" + macAddress + ", " +
                "username=" + username + ", " +
                "isConnected=" + isConnected + ", " +
                "ipAddress=" + ipAddress + ']';
    }
}
