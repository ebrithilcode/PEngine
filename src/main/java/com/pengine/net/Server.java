package com.pengine.net;

import com.pengine.PEngine;
import com.pengine.UserAPI;
import com.pengine.rendering.IRenderer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pengine.net.NetConstants.*;


public class Server {

    private static final AtomicInteger ID_POOL;
    private static final NetworkMessageFactory REMOVAL_MESSAGE_FACTORY = (o, m) -> ByteBuffer.allocate(3).put(DESTROY).putShort(o.getID()).array();
    private static final NetworkMessageFactory CREATION_MESSAGE_FACTORY = (o, m) -> ByteBuffer.allocate(4+m.length).put(CREATE).putShort(o.getID()).put(getClassID(o)).put(m).array();
    private static final NetworkMessageFactory UPDATE_MESSAGE_FACTORY = (o, m) -> c.sendBytes(UPDATE, r.getID(), m);

    //Queue<ClientSpecificRendererActionImpl> actionQueue;

    static {
        ID_POOL = new AtomicInteger(0);
    }

    ServerSocket serverSocket;
    ExecutorService clientListenExecutor;
    Set<ClientInstance> clients;
    Map<Class<? extends NetworkApplicable>, Byte> networkClassIdMap;
    private AtomicInteger scheduledClients = new AtomicInteger(0);

    private Server(int port) {
        clients = new CopyOnWriteArraySet<>(); //TODO reconsider, probably good
        clientListenExecutor = Executors.newSingleThreadExecutor();
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(180000); //TODO reconsider
        }
        catch (IOException e) {
            e.printStackTrace(); //TODO: documentation
            PEngine.APPLET.exit();
        }
    }

    public static short getNewID() {
        int resultingID = ID_POOL.getAndIncrement();
        if(resultingID > Short.MAX_VALUE) {
            System.err.format("The maximum of %d unique IDs for networking objects (mainly renderers) were assigned. The Sketch is now being shut down. If you think that this limit is not enough please create an issue on Github.", Short.MAX_VALUE);
            PEngine.APPLET.exit();
        }
        return (short) resultingID;
    }

    public void listenForClient() {
        listenForClients(1);
    }

    private void listenForClients(int numClients) {
        int totalClientsForListen = scheduledClients.addAndGet(numClients);
        System.out.format("Added %d expected clients. Now waiting for %d clients to connect.", numClients, totalClientsForListen);
        if(clientListenExecutor.isTerminated() || clientListenExecutor.isShutdown()) {
            clientListenExecutor = Executors.newSingleThreadExecutor();
            clientListenExecutor.submit(this::acceptClient);
        }
    }

    private void acceptClient() {
        if(scheduledClients.get() <= 0) return; //double check
        try {
            ClientInstance newClient = ClientInstance.fromSocket(serverSocket.accept());
            clients.add(newClient);
            if(scheduledClients.decrementAndGet() > 0) {
                acceptClient();
            }
            else {
                clientListenExecutor.shutdown();
            }
        }
        catch(SocketTimeoutException e) {
            System.err.format("No client tried to connect after %d milliseconds, stopped listening for clients.", 180000);
            scheduledClients.set(0);
            clientListenExecutor.shutdown();
        }
        catch(IOException e) {
            System.err.println("Connection to client failed, retrying:");
            e.printStackTrace();
            acceptClient();
        }
    }

    private void rendererUpdateLoop() {
        for(ClientInstance client : clients) {
            for(IRenderer rendererToRemove : removalQueue) {
            }
            for(IRenderer renderer : renderers) {
            }
        }
    }

    @SuppressWarnings("unused") //used by processing shutdown algorithm
    public void dispose() {
        clients.forEach(ClientInstance::dispose); //dispose all clients
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            //TODO important?
        }
        clientListenExecutor.shutdownNow();
    }

    @UserAPI
    public void createRendererForClient(IRenderer renderer, ClientInstance client, byte Message) {
        Objects.requireNonNull(renderer); //TODO reconsider
        Objects.requireNonNull(client); //TODO reconsider
        actionQueue.add(new ClientSpecificRendererActionImpl(client, renderer, CREATION_ACTION));
    }

    @UserAPI
    public void createRendererForClients(IRenderer renderer, byte flag) {
        Objects.requireNonNull(renderer); //TODO reconsider
        /*clients.stream()
                .filter(c -> c.hasFlag(flag))
                .forEach(c -> removeRendererFromClient(renderer, c));*/
        for(ClientInstance client : clients) {
            if(client.hasFlag(flag)) removeRendererFromClient(renderer, client);
        }
    }

    @UserAPI
    public void createRendererForClient(IRenderer renderer, String clientName) {
        //omit clientName String null check, since it will cause no harm
        Objects.requireNonNull(renderer); //TODO reconsider
        for(ClientInstance client : clients) {
            if(client.getName().equals(clientName)) {
                removeRendererFromClient(renderer, client);
                return;
            }
        }
    }

    @UserAPI
    public void removeRendererFromClient(IRenderer renderer, ClientInstance client) {
        Objects.requireNonNull(renderer); //TODO reconsider
        Objects.requireNonNull(client); //TODO reconsider
        actionQueue.add(new ClientSpecificRendererActionImpl(client, renderer, REMOVAL_ACTION));
    }

    @UserAPI
    public void removeRendererFromClients(IRenderer renderer, byte flag) {
        Objects.requireNonNull(renderer); //TODO reconsider
        /*clients.stream()
                .filter(c -> c.hasFlag(flag))
                .forEach(c -> removeRendererFromClient(renderer, c));*/
        for(ClientInstance client : clients) {
            if(client.hasFlag(flag)) removeRendererFromClient(renderer, client);
        }
    }

    @UserAPI
    public void removeRendererFromClient(IRenderer renderer, String clientName) {
            //omit clientName String null check, since it will cause no harm
            Objects.requireNonNull(renderer); //TODO reconsider
            for(ClientInstance client : clients) {
            if(client.getName().equals(clientName)) {
                removeRendererFromClient(renderer, client);
                return;
            }
        }
    }

    private void getClassID(NetworkApplicable networkObject) {
        Byte classIDWrapper = networkClassIdMap.get(networkObject.getClass());
        if(classIDWrapper == null) {
            System.err.format("Networking Object Class %s was not registered but still used in networking. Register it via Server::registerObjectClass before")
        }
    }
    @FunctionalInterface
    private interface NetworkMessageFactory {

        byte[] getMessage(NetworkApplicable networkObject, byte[] messageContent) throws IOException;

    }

    private static final class SavedClientSpecificRendererAction {

        private ClientInstance client;
        private NetworkApplicable networkObject;
        private byte[] message;
        private NetworkMessageFactory factory;

        private SavedClientSpecificRendererAction(ClientInstance client, NetworkApplicable networkObject, byte[] message, NetworkMessageFactory factory) {
            this.client = client;
            this.networkObject = networkObject;
            this.message = message;
            this.factory = factory;
        }

        private void sendAction() throws IOException {
            client.sendBytes(factory.getMessage(networkObject, message));
        }

    }

}
