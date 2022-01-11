package com.simplesmartapps.chatsystem.data.remote.model;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public final class BroadcastNetwork {
    private final InetAddress address;
    private final String interfaceName;
    private final byte[] macAddress;

    public BroadcastNetwork(InetAddress address, String interfaceName, byte[] macAddress) {
        this.address = address;
        this.interfaceName = interfaceName;
        this.macAddress = macAddress;
    }

    public InetAddress address() {
        return address;
    }

    public String interfaceName() {
        return interfaceName;
    }

    public byte[] macAddress() {
        return macAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BroadcastNetwork) obj;
        return Objects.equals(this.address, that.address) &&
                Objects.equals(this.interfaceName, that.interfaceName) &&
                Arrays.equals(this.macAddress, that.macAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, interfaceName, Arrays.hashCode(macAddress));
    }

    @Override
    public String toString() {
        return "BroadcastNetwork[" +
                "address=" + address + ", " +
                "interfaceName=" + interfaceName +
                "macAddress=" + Arrays.toString(macAddress) + ']';
    }
}
