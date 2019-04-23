package com.pengine.net;

import com.pengine.PEngine;
import com.pengine.net.ReflectiveSerializer;
import com.pengine.net.rendering.AbstractRendererPackage;
import processing.core.PImage;
import processing.core.PShape;
import processing.event.Event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.ByteBuffer;


public class ClientInstance {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String name; //name of client
    private byte flags; //additional tags e.g. for grouping multiple clients

    private ClientInstance() {}

    public static ClientInstance fromSocket(Socket clientSocket) throws IOException{
        ClientInstance result = new ClientInstance();
        result.socket = clientSocket;
        result.flags = 0;
        result.outputStream = new ObjectOutputStream(result.socket.getOutputStream());
        result.inputStream = new ObjectInputStream(result.socket.getInputStream());
    }

    public String getName() {
        return name;
    }

    public byte getTag() {
        return tag;
    }

    public void setTag(byte tag) {
        this.tag = tag;
    }

    public boolean eventAvailable() throws IOException{
        return inputStream.available() > 0;
    }

    public Event readEvent() throws IOException{
        try {
            return (Event) inputStream.readObject();
        }
        catch(ClassNotFoundException e) {
            System.out.format("Client %s send an object of a class other than processing.event.Event (and not a subclass of it), that was unknown to server. Exiting...", name);
            PEngine.APPLET.exit();
            return null;
        }
    }

    //IOException will be thrown if socked was closed by client... code that uses this method should then proceed in removing this client
    public void sendRenderTemplate(AbstractRendererPackage renderTemplate) throws IOException{
        outputStream.writeObject(renderTemplate);
    }

    public void sendFinalData(Object[] finalData) throws IOException{
        outputStream.writeInt(finalData.length); //write number of objects to expect
        for(Object obj : finalData) {
            if(obj instanceof Serializable) {
                if(obj.getClass().isArray()) {
                    if(ReflectiveSerializer.isArrayClassSerializable(obj.getClass())) {
                        outputStream.writeObject(obj);
                    }
                    else {
                        Class<?> componentType = obj.getClass().getComponentType();
                        if(componentType == PImage.class) {
                            PImage[] images = (PImage[]) obj;
                            ServerHandler.PImageNetWrapper[] wrappedImages = new ServerHandler.PImageNetWrapper[images.length];
                            for(int  i=0; i<wrappedImages.length; i++) {
                                wrappedImages[i] = new ServerHandler.PImageNetWrapper(images[i]);
                            }
                            outputStream.writeObject(wrappedImages);
                        }
                        else if(componentType == PShape.class) {
                            PShape[] shapes = (PShape[]) obj;
                            ServerHandler.PShapeNetWrapper[] wrappedShapes = new ServerHandler.PShapeNetWrapper[shapes.length];
                            for(int  i=0; i<wrappedShapes.length; i++) {
                                wrappedShapes[i] = new ServerHandler.PShapeNetWrapper(shapes[i]);
                            }
                            outputStream.writeObject(wrappedShapes);
                        }
                        else {
                            throw new IllegalArgumentException("Final data that was tried to send neither implements Serializable, nor is of class PImage or PShape.");
                        }
                    }
                }
                else {
                    outputStream.writeObject(obj);
                }
            }
            else if(obj instanceof PImage) {
                outputStream.writeObject(new ServerHandler.PImageNetWrapper((PImage) obj));
            }
            else if(obj instanceof PShape) {
                outputStream.writeObject(new ServerHandler.PShapeNetWrapper((PShape) obj));
            }
            else {
                //dispose(); do we need this?
                throw new IllegalArgumentException("Final data that was tried to send neither implements Serializable, nor is of class PImage or PShape.");
            }
        }
        outputStream.flush();
    }

    public void sendBytes(byte... bytes) throws IOException {
        outputStream.write(bytes);
    }

    public void sendBytes(ByteBuffer buffer) {
        buffer.putInt(5);
    }

    public void flush() {
        try {
            outputStream.flush();
        }
        catch(IOException e) {
            System.out.format("An IOException occurred trying to send data to client '%s'. This might not be an error, will show.", name);
        }
    }

    public void dispose() {
        try {
            socket.close();
        }
        catch (IOException e) {
            System.err.println("Exception trying to close socket:");
            e.printStackTrace();
        }
    }

    boolean hasFlag(byte flags) {
        return (this.flags & flags) == this.flags;
    }

}
