
package com.wolf.weibo.httpclient.javabean;

import java.io.Serializable;

public class PreLonginBean implements Serializable {
    private static final long serialVersionUID = 1575647279708867094L;
    // {
    // "retcode": 0,
    // "servertime": 1416548468,
    // "pcid": "xd-747167b2492a39a7b9d94f11393e7f8167bd",
    // "nonce": "LTFYDH",
    // "pubkey":
    // "EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D245A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD3993CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443",
    // "rsakv": "1330428213",
    // "exectime": 2
    // }
    private int retcode;
    private long servertime;
    private String pcid;
    private String nonce;
    private String pubkey;
    private String rsakv;
    private int exectime;

    public String getRetcode() {
        return retcode + "";
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getServertime() {
        return servertime + "";
    }

    public void setServertime(long servertime) {
        this.servertime = servertime;
    }

    public String getPcid() {
        return pcid;
    }

    public void setPcid(String pcid) {
        this.pcid = pcid;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getRsakv() {
        return rsakv;
    }

    public void setRsakv(String rsakey) {
        this.rsakv = rsakey;
    }

    public String getExectime() {
        return exectime + "";
    }

    public void setExectime(int exectime) {
        this.exectime = exectime;
    }

}
