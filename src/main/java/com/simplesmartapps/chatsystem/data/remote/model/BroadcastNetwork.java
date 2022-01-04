package com.simplesmartapps.chatsystem.data.remote.model;

import java.net.InetAddress;
import java.util.Objects;

public final class BroadcastNetwork {
    private final InetAddress address;
    private final String interfaceName;

    public BroadcastNetwork(InetAddress address, String interfaceName) {
        this.address = address;
        this.interfaceName = interfaceName;
    }

    public InetAddress address() {
        return address;
    }

    public String interfaceName() {
        return interfaceName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BroadcastNetwork) obj;
        return Objects.equals(this.address, that.address) &&
                Objects.equals(this.interfaceName, that.interfaceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, interfaceName);
    }

    @Override
    public String toString() {
        return "BroadcastNetwork[" +
                "address=" + address + ", " +
                "interfaceName=" + interfaceName + ']';
    }
}
