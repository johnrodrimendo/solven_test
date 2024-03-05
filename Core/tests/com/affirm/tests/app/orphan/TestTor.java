package com.affirm.tests.app.orphan;

import com.affirm.common.util.CryptoUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.*;

public class TestTor {
    public static void main(String[] args) throws Exception{
        SocketAddress sockAddr = new InetSocketAddress("localhost",9050);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS,sockAddr);
        URL url = new URL("https://www.google.com.pe/");
        //URL url = new URL("https://api.ipify.org/");
        URLConnection conn = url.openConnection(proxy);
        InputStream in = conn.getInputStream();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] stuff = new byte[1024];
        int readBytes = 0;
        while((readBytes = in.read(stuff))>0) {
            bout.write(stuff,0,readBytes);
        }
        byte[] result = bout.toByteArray();
        System.out.print(new String(result));

        System.out.println(CryptoUtil.encrypt("4517"));
    }
}
