package CanvasServer;

import CanvasRemote.ICanvasClient;
import CanvasRemote.ICanvasServer;
import CanvasRemote.ICanvasStatus;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the remote object (skeleton) of Server then implements the remote interface.
 */
public class CanvasServerServant extends UnicastRemoteObject implements ICanvasServer {
//    private CanvasManager canvasManager;
    private Set<ICanvasClient> clientSet;
    private ICanvasClient manager;

    protected CanvasServerServant() throws RemoteException {
//        this.canvasManager = new CanvasManager(this);
        this.clientSet = new HashSet<ICanvasClient>();
    }


    @Override
    public void UpdateCanvas(ICanvasStatus canvasStatus) throws RemoteException {
        for (ICanvasClient canvasClient: clientSet) {
            canvasClient.updateCanvas(canvasStatus);
        }
    }

    // Check the validity of the new user added and set it as the manager if there is no client.
    public void checkUserValidity(ICanvasClient CanvasClient) throws RemoteException {
        //If the client is the first to join, add directly
        if (clientSet.size() == 0) {
            addClientToList(CanvasClient);
        } else {
            boolean allowed = false;
            // Locating the manager then ask his permission
            for (ICanvasClient canvasClient : clientSet) {
                if (canvasClient.isClientManager()) {
                    try {
                        allowed = canvasClient.askManagerPermission(CanvasClient.getClientName());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            // if the new client get the permission from manager
            if (allowed) {
                addClientToList(CanvasClient);
            }
        }
        // Update the client lists for all clients
        for (ICanvasClient client : clientSet) {
            client.updateUserList(getUsers());
        }
    }

    // Check the validity of the new user added and set it as the manager if there is no client.
    @Override
    public void addUser(ICanvasClient CanvasClient) throws RemoteException {
        if  (!hasManager()) {
            CanvasClient.setClientManager("[Manager] " + CanvasClient.getClientName());
        }
        // Ask the manager whether the new client is allowed to join
        checkUserValidity(CanvasClient);
    }



    @Override
    public void kickUser(String clientName) throws RemoteException {
        for (ICanvasClient client : clientSet) {
            if (client.getClientName().equals(clientName)) {
                kickClient(clientName);
                System.out.println("User" + clientName + "has been removed!");
                client.shutDownUI();
            }
        }
        for (ICanvasClient client : clientSet) {
            client.updateUserList(getUsers());
        }
    }

    // if the user quits
    @Override
    public void removeUser(String clientName) throws RemoteException {
        for (ICanvasClient client : clientSet) {
            if (client.getClientName().equals(clientName)) {
                kickClient(clientName);
                System.out.println("User" + clientName + "quits!");
            }
        }
        for (ICanvasClient client : clientSet) {
            client.updateUserList(getUsers());
        }
    }

    @Override
    public List<ICanvasClient> getUsers() throws RemoteException {
        List<ICanvasClient> clientList = new ArrayList<>(this.clientSet);
        return clientList;
    }

    @Override
    public byte[] loadExistCanvas() throws RemoteException {
        return new byte[0];
    }

    // Once the manager creates a new whiteboard, all clients will clear their canvas
    @Override
    public void openNewCanvas() throws RemoteException {
        for (ICanvasClient client : clientSet) {
            client.cleanCanvas();
        }
    }

    public byte[] updateImage(byte[] toByteArray) throws IOException {
        byte[] image = null;
        for (ICanvasClient client : clientSet) {
            if (client.isClientManager()) {
                image = client.sendImage();
            }
        }
        return image;
    }

    @Override
    public List<ICanvasClient> updateUserList() {
        return null;
    }

    @Override
    public void addChat(String s) throws RemoteException {

    }

    @Override
    public byte[] sendImage() throws RemoteException {
        byte[] image = null;
        for (ICanvasClient client : clientSet) {
            if (client.isClientManager()) {
                image = client.sendImage();
            }
        }
        return image;
    }

    @Override
    public void openNewImage(byte[] image) throws IOException {
        for (ICanvasClient client : clientSet) {
            if (!client.isClientManager()) {
                client.loadNewImage(image);
            }
        }
    }

    @Override
    public void updateChatBox(String chatMsg) throws RemoteException {
        for (ICanvasClient client : clientSet) {
            try {
                client.updateChatBox(chatMsg);
            } catch (RemoteException e) {
                System.out.println(new RuntimeException(e));
            }
        }
    }

    // If the manager closes the application
    @Override
    public void terminateApp() throws IOException {
        for (ICanvasClient client : clientSet) {
            clientSet.remove(client);
            client.shutDownUI();
        }
        System.out.println("The manager closes the application!");
    }


    // Add a new client to the client set
    public void addClientToList(ICanvasClient client) {
        this.clientSet.add(client);
        System.out.println("Updated client set: " + clientSet);
    }

    // Get the set of clients
    public Set<ICanvasClient> getClientSet() {
        return this.clientSet;
    }

    // Set the manager of the canvas
    public void setManager(ICanvasClient manager) {
        this. manager = manager;
    }

    // Kick out a client from the client list
    public void kickClient(String client) {
        this.clientSet.remove(client);
    }

    // Check if the canvas has a manager
    public boolean hasManager() {
        return manager != null;
    }

}
