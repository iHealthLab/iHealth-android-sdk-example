package com.ihealth.demo.business.device;

import java.io.Serializable;

public class RouterBean implements Serializable {

    private String SSID;
    private String channel;
    private String security;
    private String RSSI;
    private String psd;
    private String url;
    private String pid;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "SSID='" + SSID + '\'' +
                ", channel='" + channel + '\'' +
                ", security='" + security + '\'' +
                ", RSSI='" + RSSI + '\'' +
                ", psd='" + psd + '\'' +
                ", url='" + url + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
