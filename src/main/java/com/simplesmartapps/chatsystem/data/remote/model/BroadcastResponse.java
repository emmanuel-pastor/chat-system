package com.simplesmartapps.chatsystem.data.remote.model;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Objects;

public final class BroadcastResponse {
    private final InetAddress address;
    private final JSONObject json;

    public BroadcastResponse(InetAddress address, JSONObject json) {
        this.address = address;
        this.json = json;
    }

    public InetAddress address() {
        return address;
    }

    public JSONObject json() {
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BroadcastResponse) obj;
        return Objects.equals(this.address, that.address) &&
                Objects.equals(this.json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, json);
    }

    @Override
    public String toString() {
        return "BroadcastResponse[" +
                "address=" + address + ", " +
                "json=" + json + ']';
    }
}
