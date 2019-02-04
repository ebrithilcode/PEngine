package com.pengine.InputInternet;

import static com.pengine.PEngine.APPLET;
import com.pengine.PEngine;
import com.pengine.GameObject;
import processing.net.Server;
import processing.net.Client;
import java.util.HashMap;


public class ServerConnection extends Thread {
    public HashMap<String, Data> clientData = new HashMap<String, Data>();

    private Server myServer;
    public int port = 8001;
    private boolean alive;

    PEngine engine;

    private Data toSend;
    public ServerConnection(PEngine e) {
        alive = true;
        engine = e;
    }

    public void run() {
        while (alive) {
            buildData();
            if (toSend!=null) {
                send();
            } else APPLET.delay(1);
        }
    }
    public void end() {
        alive = false;
    }
    private void send() {
        String sending = "";
        if (toSend instanceof TransformList) sending+= (char) 0;
        if (toSend instanceof Input) sending += (char) 1;
        sending += toSend.toString();
        myServer.write( charToByte( sending.toCharArray() ));

        Client mc = myServer.available();
        while (mc!=null) {
            byte[] received = mc.readBytesUntil('\r');
            Data input = null;
            switch(received[0]) {
                case 0:
                    input = new TransformList(received);
                case 1:
                    input = new Input(received);
            }
            clientData.put(mc.ip(), input);
            mc = myServer.available();
        }
    }
    public void startServer() {
        myServer = new Server(APPLET, port);
    }
    byte[] charToByte(char[] ch) {
        byte[] ret = new byte[ch.length];
        for (int i=0;i<ch.length;i++) {
            ret[i] = (byte) ch[i];
        }
        return ret;
    }



    void buildData() {
        String s = "";
        for (GameObject d: engine.engineList.getObjects()) {
            s += d.toString();
        }
        s = Data.encodeString(s);
        s += '\r';

    }

    
}
