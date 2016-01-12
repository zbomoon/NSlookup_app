package com.nslookup;

import java.net.InetAddress;


class IPConvertTask {
    String domain;

    public IPConvertTask(String d) {
        domain = d;
    }

    public String Convert() throws Exception{
        InetAddress inetAddr;
        String ss = "";
        inetAddr = InetAddress.getByName(domain);
        byte[] addr = inetAddr.getAddress();
        for (int i = 0; i < addr.length; i++) {
            if (i > 0)
                ss += ".";
            ss += addr[i] & 0xFF;
        }
        return ss;
    }
}
