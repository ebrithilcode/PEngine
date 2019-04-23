package com.pengine.net.rendering;

import com.pengine.net.server.ClientInstance;

import java.io.InputStream;
import java.io.OutputStream;

public interface IRenderMessage {

    void writeBytes(OutputStream out, ClientInstance client);

    void readBytes(InputStream in);

}
