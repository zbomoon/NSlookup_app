package com.nslookup;

import java.net.InetAddress;
import java.net.UnknownHostException;


class IPConvertTask {
    String domain;

    public IPConvertTask(String d) {
        domain = d;
    }

    public String Convert() {
        InetAddress inetAddr;
        String ss = "";
        try {
            inetAddr = InetAddress.getByName(domain);
            byte[] addr = inetAddr.getAddress();
            for (int i = 0; i < addr.length; i++) {
                if (i > 0)
                    ss += ".";
                ss += addr[i] & 0xFF;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ss;
    }
}
