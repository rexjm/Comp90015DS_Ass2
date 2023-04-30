package CanvasServer;

import CanvasRemote.ICanvasClient;
import CanvasRemote.ICanvasServer;
import CanvasRemote.ICanvasStatus;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
    }


    @Override
    public void UpdateCanvas(ICanvasStatus canvasStatus) throws RemoteException {
        for (ICanvasClient canvasClient: clientSet) {
            canvasClient.updateCanvas(canvasStatus);
        }
    }

    // Check the validity of the new user added and set it as the manager if there is no client.
    public void checkUserValidity(ICanvasClient CanvasClient) throws RemoteException {
        boolean allowed = true;
        for (ICanvasClient canvasClient : clientSet) {
            if (canvasClient.getClientManager() != null) {
                try {
                    allowed = canvasClient.allowJoin();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        addClient(CanvasClient);
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
    public Set<ICanvasClient> getUsers() throws RemoteException {
        return clientSet;
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

    public byte[] UpdateImage() throws IOException {
        byte[] image = null;
        for (ICanvasClient client : clientSet) {
            if (client.getClientManager() != null) {
                image = client.sendImage();
            }
        }
        return image;
    }
    @Override
    public void openNewImage(byte[] image) throws IOException {
        for (ICanvasClient client : clientSet) {
            if (client.getClientManager() == null) {
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
    public void addClient(ICanvasClient client) {
        this.clientSet.add(client);
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
