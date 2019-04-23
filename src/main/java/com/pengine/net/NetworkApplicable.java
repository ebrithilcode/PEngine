package com.pengine.net;

public interface NetworkApplicable {

    short getID(); //TODO maybe int or long

    byte[] getMessage();

    void onMessage(byte[] message);

}
