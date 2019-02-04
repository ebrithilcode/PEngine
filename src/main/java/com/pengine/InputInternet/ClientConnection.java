package com.pengine.InputInternet;

import processing.net.Client;
import static com.pengine.PEngine.APPLET;
import com.pengine.PEngine;
import com.pengine.GameObject;
import processing.core.PApplet;
import java.util.ArrayList;


public class ClientConnection extends Thread {

    Client myClient;
    public String ip = "localhost";
    public int port = 8001;

    private boolean alive;

    private String messageToSend = "";


    public PEngine engine;

    public ClientConnection(PEngine e) {
        alive = true;
        engine = e;
    }
    public void run() {
        while (alive) {
            buildMessage();
            listen();
        }
    }

    public void end() {
        alive = false;
    }
    void listen() {
        if (myClient.available()>0) {
            byte[] received = myClient.readBytesUntil('\r');
            myClient.write(messageToSend);
            engine.useData(received, ip);
        }
    }
    public String getMyIp() {
        return myClient.ip();
    }

    public void connect() {
        PApplet.println("Connecting to: "+ip);
        myClient = new Client(APPLET, ip, port);
    }

    void buildMessage() {
            String s = "";

            s += engine.userInput.toString();

            for (Data d: engine.engineList.getClientData()) {
                s += d.toString();
            }
            s = Data.encodeString(s);

            s += '\r';
    }


}
