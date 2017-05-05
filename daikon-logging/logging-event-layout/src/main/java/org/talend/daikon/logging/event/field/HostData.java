package org.talend.daikon.logging.event.field;

import java.net.UnknownHostException;

/**
 * Collect informations from the Host
 * 
 * @author sdiallo
 *
 */
public class HostData {

    private String hostName;

    private String hostAddress;

    public HostData() {
        try {
            setHostName(java.net.InetAddress.getLocalHost().getHostName());
            setHostAddress(java.net.InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            setHostName("unknown-host");
        }
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

}