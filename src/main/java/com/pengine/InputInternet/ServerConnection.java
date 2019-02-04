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

    private String myMessageToSend;
    public ServerConnection(PEngine e) {
        alive = true;
        engine = e;
    }

    public void run() {
        while (alive) {
            buildData();
            send();
        }
    }
    public void end() {
        alive = false;
    }
    private void send() {
        myServer.write( charToByte( myMessageToSend.toCharArray() ));
        System.out.println("I just wrote a bunch of data to my clients. Uf");
        Client mc = myServer.available();
        while (mc!=null) {
            byte[] received = mc.readBytesUntil('\r');
            engine.useData(received, mc.ip());
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
            myMessageToSend = "";
            for (GameObject d: engine.engineList.getObjects()) {
                if (!d.dontSendMePlease)
                myMessageToSend += d.toString();
            }
            for (Data d: engine.engineList.getServerData()) {
                if (!d.dontSendMePlease)
                myMessageToSend += d.toString();
            }
            myMessageToSend = Data.encodeString(myMessageToSend);
            myMessageToSend += '\r';

    }

    
}
