package com.pengine.net.server;

import com.pengine.net.ReflectiveSerializer;
import com.pengine.rendering.IRenderer;
import com.pengine.PEngine;
import processing.core.PImage;
import processing.core.PShape;
import processing.event.Event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ServerHandler {

    private final List<ClientInstance> clients;
    private PEngine parent;
    private int port;
    private int numThreads;

    private volatile boolean listeningForEvents;

    private Object[] finalData;

    private Method clientEventMethod;

    public ServerHandler(PEngine parent, int port, Object[] finalData, String clientEventListener) {
        this.port = port;
        this.parent = parent;
        this.finalData = finalData;
        clients = new LinkedList<>();
        numThreads = 0;
        try {
            clientEventMethod = PEngine.APPLET.getClass().getDeclaredMethod(clientEventListener, Event.class);
        } catch (NoSuchMethodException e) {
            System.err.format("Method %s which is intended to listen for client events does not exist. Exited sketch.", clientEventListener);
            PEngine.APPLET.exit();
        }
        startClientEventListenThread();
    }

    private void startClientEventListenThread() {
        listeningForEvents = true;
        new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY); //rendering over event reading
                while (listeningForEvents) {
                    for (Iterator<ClientInstance> clientIterator = clients.iterator(); clientIterator.hasNext(); ) {
                        synchronized (clients) {
                            ClientInstance client = clientIterator.next();
                            try {
                                if (client.eventAvailable()) {
                                    try {
                                        clientEventMethod.invoke(PEngine.APPLET, client.readEvent());
                                    } catch (IllegalAccessException e) {
                                        System.err.format("Can't access method '%s' which is supposed to listen for client side events, try changing its access modifier. Exiting sketch...", clientEventMethod.getName());
                                        PEngine.APPLET.exit();
                                    } catch (InvocationTargetException e) {
                                        System.err.format("Method %s could not be called by the PApplet instance of the sketch. Might have to do with the sketch being shut down, trying to exit it if not...", clientEventMethod.getName());
                                        e.printStackTrace();
                                        if (!PEngine.APPLET.finished) {
                                            PEngine.APPLET.exit();
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                System.out.format("An IOException occurred during communication with client '%s', connection ended:", client.getName());
                                e.printStackTrace();
                                client.dispose();
                                clientIterator.remove();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    public void listenForClientConnection() {
        if (numThreads >= 3) {
            System.err.format("Already %d threads are listening for clients. Invocation of listenForClient() method was aborted.", numThreads);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                numThreads++;
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(180000);
                    Socket clientSocket = serverSocket.accept();
                    addClient(clientSocket);
                } catch (SocketTimeoutException e) {
                    System.err.format("Thread reached time limit of 3 minutes to connect. Stopped listening for clients. There are still %d threads actively listening", numThreads - 1);
                } catch (IOException e) {
                    System.err.println("An IOException occurred trying to connect to clients or while creating a server socket, resumed without connecting a client:");
                    e.printStackTrace();
                }
                numThreads--;
            }
        }.start();
    }

    private void addClient(Socket clientSocket) {
        try {
            ClientInstance client = new ClientInstance(clientSocket, finalData);
            clients.add(client);
        } catch (IOException e) {
            System.err.println("An IOException occurred trying to get the OutputStream of a client's socket:");
            e.printStackTrace();
        }
    }

    public void disableClientEventListener() {
        listeningForEvents = false;
    }

    public void sendRenderTemplates() {
        synchronized (clients) {
            for (Iterator<ClientInstance> clientIterator = clients.iterator(); clientIterator.hasNext(); ) {
                ClientInstance client = clientIterator.next();
                for (Iterator<IRenderer> renderableIterator = parent.getRenderables().iterator(); renderableIterator.hasNext(); ) {
                    IRenderer renderable = renderableIterator.next();
                    if (renderable.isDead()) {
                        renderableIterator.remove();
                        continue;
                    }
                    try {
                        client.sendRenderTemplate(renderable.getRenderTemplate(client));
                    } catch (IOException e) {
                        System.err.format("An IOException occurred trying send data to client '%s'. Disconnected...", client.getName());
                        client.dispose();
                        clientIterator.remove();
                        break;
                    }
                }
                client.flush();
            }
        }
    }

    protected void sendFinalDataToClient(ClientInstance client) {
        outputStream.writeInt(finalData.length); //write number of objects to expect
}

    //fallback method
    public int getFinalDataIndex(Object obj) {
        for (int i = 0; i < finalData.length; i++) {
            if (finalData[i].equals(obj)) return i;
        }
        return -1;
    }

    public void dispose() {
        listeningForEvents = false;
        for (ClientInstance client : clients) {
            client.dispose();
        }
    }

    /*------------------------------------- net wrappers -------------------------------------*/

    public static class PImageNetWrapper implements Externalizable {

        private transient PImage image;

        public PImageNetWrapper(PImage image) {
            this.image = image;
        }

        public PImageNetWrapper() {}

        public PImage getImage() {
            return image;
        }

        @Override
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            image.loadPixels(); //general contract of using PImage
            objectOutput.writeInt(image.pixelWidth);
            objectOutput.writeInt(image.pixelHeight);
            objectOutput.writeInt(image.format);
            objectOutput.writeInt(image.pixelDensity);
            objectOutput.writeObject(image.pixels);
        }

        @Override
        public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
            int pixelWidth = objectInput.readInt();
            int pixelHeight = objectInput.readInt();
            int format = objectInput.readInt();
            int pixelDensity = objectInput.readInt();
            int[] pixels = (int[]) objectInput.readObject();

            image = new PImage(pixelWidth / pixelDensity, pixelHeight / pixelDensity, format, pixelDensity);
            image.parent = PEngine.APPLET;
            image.pixels = pixels;
            image.updatePixels(); //general contract of using PImage
        }

    }

    public static class PShapeNetWrapper implements Externalizable {

        protected transient PShape shape;

        public PShapeNetWrapper(PShape shape) {
            this.shape = shape;
        }

        public PShapeNetWrapper() {}

        public PShape getShape() {
            return shape;
        }

        @Override
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            ReflectiveSerializer.serializeReflectively(objectOutput, shape, "g", "parent");
        }

        @Override
        public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
            shape = ReflectiveSerializer.deserializeReflectively(objectInput, PShape.class);
            try {
                Field pgrahicsField = PShape.class.getDeclaredField("g");
                pgrahicsField.setAccessible(true);
                pgrahicsField.set(shape, PEngine.APPLET.g);
                if(shape.getFamily() == PShape.GROUP) setGroupParents(shape);
            }
            catch(NoSuchFieldException e) {
                System.err.println("A field in a Processing class might have been updated without the knowledge of the developers of this library, please try to contact them over this issue:");
                e.printStackTrace();
            }
            catch(IllegalAccessException ignore) {}
        }

        public void setGroupParents(PShape parent) throws IllegalAccessException, NoSuchFieldException {
            Field parentField = PShape.class.getDeclaredField("parent");
            Field graphicsField = PShape.class.getDeclaredField("g");
            parentField.setAccessible(true);
            graphicsField.setAccessible(true);
            for (PShape child : parent.getChildren()) {
                parentField.set(child, parent);
                graphicsField.set(child, PEngine.APPLET.g);
                if (child.getFamily() == PShape.GROUP) {
                    setGroupParents(child);
                }
            }
        }
    }

}
