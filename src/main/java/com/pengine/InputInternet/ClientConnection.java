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
            PApplet.println("listening");
            if (inputBuffer!=null) {
                PApplet.println("Not null");
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

    void buildObjectList() {
        if (lastInput!=null) {
            ArrayList<Transform> newPositions = ((TransformList)lastInput).positions;
            killOld(newPositions);
            for (Transform t: newPositions) {
                boolean found = false;
                for (GameObject g: engine.objects) {
                    if (t.objectID == g.objectID) {
                        found = true;
                        g.rot = t.rot;
                        g.pos = t.pos;
                    }
                    if (!found) {
                        try {
                            GameObject toAdd = engine.idToClass.get(t.classID).newInstance();
                            toAdd.pos = t.pos;
                            toAdd.rot = t.rot;
                            engine.objects.add(toAdd);
                        } catch (Exception e) {}
                    }
                }
            }
        }
    }

    void killOld(ArrayList<Transform> pos) {
        for (int i=engine.objects.size()-1;i>=0;i--) {
            GameObject g = engine.objects.get(i);
            boolean found = false;
            for (Transform t: pos) {
                if (t.objectID == g.objectID) found = true;
            }
            if (!found) engine.objects.remove(i);
        }
    }


}