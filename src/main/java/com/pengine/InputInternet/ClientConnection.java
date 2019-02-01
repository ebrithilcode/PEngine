package com.pengine.InputInternet;

import processing.net.Client;
import static com.pengine.PEngine.APPLET;


class ClientConnection extends Thread {

    Client myClient;
    String ip = "localhost";
    int port = 8001;
    Data lastInput;

    boolean newInput = false;

    Data inputBuffer;

    ClientConnection() {}
    public void run() {
        while (true) {
            Data possibleNewInput = listen();
            if (possibleNewInput!=null) lastInput = possibleNewInput;
        }
    }
    Data listen() {
        if (myClient.available()>0) {
            byte[] received = myClient.readBytesUntil('\r');
            APPLET.println("listening");
            if (inputBuffer!=null) {
                APPLET.println("Not null");
                String sending = "";
                if (inputBuffer instanceof TransformList) sending+= (char) 0;
                if (inputBuffer instanceof Keyset) sending += (char) 1;
                sending += inputBuffer.toString();
                myClient.write(sending.toString());
            } else myClient.write('\r');
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
    Data getMyData() {
        newInput = false;
        return lastInput;
    }
    void connect() {
        APPLET.println("Connecting to: "+ip);
        myClient = new Client(APPLET, ip, port);
    }


}