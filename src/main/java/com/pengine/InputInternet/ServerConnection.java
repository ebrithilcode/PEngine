package com.pengine.InputInternet;

import static com.pengine.PEngine.APPLET;
import processing.net.Server;
import processing.net.Client;

class ServerConnection extends Thread {
    Server myServer;
    int port = 8001;

    boolean newInput;
    Data lastInput;

    Data toSend;
    ServerConnection() {    }

    public void run() {
        while (true) {
            if (toSend!=null) {
                lastInput = send();
            } else APPLET.println("Not sending a thing");
        }
    }

    Data send() {
        String sending = "";
        if (toSend instanceof TransformList) sending+= (char) 0;
        if (toSend instanceof Keyset) sending += (char) 1;
        sending += toSend.toString();
        myServer.write( charToByte( sending.toCharArray() ));

        Client mc;
        int startTime = APPLET.millis();
        do {
            mc = myServer.available();
        } while (mc==null && APPLET.millis()-startTime<1000);
        if (mc!=null) {
            byte[] received = mc.readBytesUntil('\r');
            switch(received[0]) {
                case 0:
                    newInput = true;
                    return new TransformList(received);
                case 1:
                    newInput = true;
                    return new Keyset(received);
            }
        }
        return null;
    }
    void startServer() {
        myServer = new Server(APPLET, port);
    }
    Data getMyData() {
        newInput = false;
        return lastInput;
    }
    byte[] charToByte(char[] ch) {
        byte[] ret = new byte[ch.length];
        for (int i=0;i<ch.length;i++) {
            ret[i] = (byte) ch[i];
        }
        return ret;
    }
}
