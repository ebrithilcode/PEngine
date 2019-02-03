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
    Data lastInput;

    boolean newInput = false;
    private boolean alive;

    Data inputBuffer;

    public PEngine engine;

    public ClientConnection(PEngine e) {
        alive = true;
        engine = e;
    }
    public void run() {
        while (alive) {
            buildInput();
            lastInput = listen();
            buildObjectList();
        }
    }

    public void end() {
        alive = false;
    }
    Data listen() {
        if (myClient.available()>0) {
            byte[] received = myClient.readBytesUntil('\r');
            if (inputBuffer!=null) {
                String sending = "";
                if (inputBuffer instanceof TransformList) sending+= (char) 0;
                if (inputBuffer instanceof Input) sending += (char) 1;
                sending += inputBuffer.toString();
                myClient.write(sending.toString());
            } else myClient.write('\r');
            switch(received[0]) {
                case 0:
                    newInput = true;
                    return new TransformList(received);
                case 1:
                    newInput = true;
                    return new Input(received);
            }
        }
        return null;
    }
    Data getMyData() {
        newInput = false;
        return lastInput;
    }
    public void connect() {
        PApplet.println("Connecting to: "+ip);
        myClient = new Client(APPLET, ip, port);
    }

    void buildInput() {
        Data inputBuffer = engine.userInput;
    }

    void buildData() {
      String s = "";
      for (Data d: engine.data) {
        s += d.toString();
      }
      s = Data.encodeString(s);
      s += '\r';

    }
    void useData(byte[] bytes) {
      bytes = Data.decodeBytes(bytes);
      int[] iterator = new int[] {0};
      while (iterator < bytes.length) {

      }
    }

    Data dataAlreadyExits()


}
