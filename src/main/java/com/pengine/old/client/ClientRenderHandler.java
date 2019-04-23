package com.pengine.net.client;

import com.pengine.net.server.ServerHandler;
import com.pengine.net.rendering.AbstractRendererPackage;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientRenderHandler implements Runnable {

    private PApplet parent;

    private Socket socket;
    private ObjectInputStream serverInputStream;
    private Object[] finalData;

    private final Object lock = new Object();

    private volatile boolean terminate;

    public ClientRenderHandler(PApplet parent, String ip, int port) {
        this.parent = parent;
        terminate = false;
        parent.registerMethod("dispose", this);
        try {
            socket = new Socket(ip, port);
            serverInputStream = new ObjectInputStream(socket.getInputStream());
            while(serverInputStream.available() <= 0) {
                try {
                    Thread.sleep(500);
                }
                catch(InterruptedException e) {
                    System.err.println("Thread that was waiting for the template data was interrupted:");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            readFinalData();
            new Thread(this).start();
        } catch (UnknownHostException e) {
            System.err.format("Connection failed - unknown host: %s:%d", ip, port);
            parent.exit();
        } catch (IOException e) {
            System.err.format("An IOException occurred trying to connect to server:");
            e.printStackTrace();
            parent.exit();
        }
    }

    public void dispose() {
        terminate = true;
        try {
            if (serverInputStream != null) {
                serverInputStream.close();
                serverInputStream = null;
            }
        }
        catch(IOException e) {
            System.err.println("An IOException occurred trying to close ClientRenderHandler's  InputStream:");
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        catch(IOException e) {
            System.err.println("An IOException occurred trying to close ClientRenderHandler's socket:");
            e.printStackTrace();
        }
    }

    public void run() {
        while(!terminate) {
            try {
                if (serverInputStream.available() > 0) {
                    synchronized (lock) {
                        try {
                            AbstractRendererPackage renderTemplate = (AbstractRendererPackage) serverInputStream.readObject();
                            int dataIndex = renderTemplate.getRenderingObjectIndex();
                            renderTemplate.render(parent, (dataIndex >= 0) ? finalData[dataIndex] : null);
                        }
                        catch(ClassNotFoundException e) {
                            System.err.println("Class send by server could not be identified. Should be LinkedList:");
                            e.printStackTrace();
                        }
                        catch(StreamCorruptedException e) {
                            System.err.println("Control data in data stream by server is inconsistent. Is the connection to the server stable? Stacktrace:");
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("An IOException occurred trying to read the RenderTemplate list from the ObjectInputStream of the server:");
                e.printStackTrace();
            }
        }
    }

    public void readFinalData() throws IOException {
        try {
            int numFinalData = serverInputStream.readInt();
            finalData = new Object[numFinalData];
            for (int i=0; i < numFinalData; i++) {
                Object readObj = serverInputStream.readObject();
                if(readObj instanceof ServerHandler.PImageNetWrapper) {
                    finalData[i] = ((ServerHandler.PImageNetWrapper) readObj).getImage();
                }
                else if(readObj instanceof ServerHandler.PShapeNetWrapper) {
                    finalData[i] = ((ServerHandler.PShapeNetWrapper) readObj).getShape();
                }
                else if(readObj.getClass().isArray()) {
                    if(readObj.getClass().getComponentType() == ServerHandler.PImageNetWrapper.class) {
                        ServerHandler.PImageNetWrapper[] wrapperArray = (ServerHandler.PImageNetWrapper[]) readObj;
                        PImage[] targetArray = new PImage[wrapperArray.length];
                        for(int j=0; j<targetArray.length; j++) {
                            targetArray[i] = wrapperArray[i].getImage();
                        }
                        finalData[i] = targetArray;
                    }
                    else if(readObj.getClass().getComponentType() == ServerHandler.PShapeNetWrapper.class) {
                        ServerHandler.PShapeNetWrapper[] wrapperArray = (ServerHandler.PShapeNetWrapper[]) readObj;
                        PShape[] targetArray = new PShape[wrapperArray.length];
                        for(int j=0; j<targetArray.length; j++) {
                            targetArray[i] = wrapperArray[i].getShape();
                        }
                        finalData[i] = targetArray;
                    }
                    else {
                        finalData[i] = readObj;
                    }
                }
            }
        }
        catch (ClassNotFoundException e) {
            System.err.format("Class that was send as final data from server does not exist on client, exited sketch.");
            parent.exit();
        }
    }

}
