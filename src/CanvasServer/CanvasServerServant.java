/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description :The class creates a server-side remote object that manages the clients of a remote drawing canvas service,
 * handling operations such as updating the canvas, adding and removing users, cleaning all client canvases,
 * and managing chat functionality.
 */

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
    private Set<ICanvasClient> clientSet;
    private boolean hasManager;
    private ArrayList<String> chatHistory = new ArrayList<>();

    protected CanvasServerServant() throws RemoteException {
        this.clientSet = new HashSet<>();
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
                CanvasClient.setAllowed(true);
            } else {
                CanvasClient.setAllowed(false);
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
            this.hasManager = true;
            // The first person is allowed to join
            CanvasClient.setAllowed(true);
        }
        // Ask the manager whether the new client is allowed to join
        checkUserValidity(CanvasClient);
    }


    @Override
    public void kickUser(String clientName) throws RemoteException {
        // If traverse clientSet while trying to delete elements from it, it will throw
        // ConcurrentModificationException. so it should add the element to remove to another list during the traversal,
        //  then remove it when the traversal is over.
        List<ICanvasClient> clientsToBeRemoved = new ArrayList<>();
        for (ICanvasClient client : clientSet) {
            if (client.getClientName().equals(clientName)) {
                clientsToBeRemoved.add(client);
                System.out.println("User" + clientName + " left!");
                client.shutDownUI("kick");
            }
        }
        clientSet.removeAll(clientsToBeRemoved);
        for (ICanvasClient client : clientSet) {
            client.updateUserList(getUsers());
        }
    }

    @Override
    public List<ICanvasClient> getUsers() throws RemoteException {
        return new ArrayList<>(this.clientSet);
    }


    // Once the manager creates a new whiteboard, all clients will clear their canvas
    @Override
    public void cleanAllCanvas() throws RemoteException {
        for (ICanvasClient client : clientSet) {
            client.clearCanvas();
        }
    }

    public void updateImage(byte[] toByteArray) throws IOException {
        for (ICanvasClient client : clientSet) {
            if (!client.isClientManager()) {
                client.syncImage(toByteArray);
            }
        }
    }

    @Override
    public byte[] getManagerImage() throws IOException {
        byte[] image = null;
        for (ICanvasClient client : clientSet) {
            if (client.isClientManager()) {
                image = client.getImage();
                break;  // Find the manager client and jump out of the loop
            }
        }
        return image;
    }

    @Override
    public void updateServerChatBox(String chatMsg) throws RemoteException {
        chatHistory.add(chatMsg);
        for (ICanvasClient client : clientSet) {
            try {
                client.updateChatBox(chatMsg);
            } catch (RemoteException e) {
                System.out.println(new RuntimeException(e));
            }
        }
    }

    public ArrayList<String> getChatHistory() throws RemoteException {
        return chatHistory;
    }

    // If the manager closes the application
    @Override

    public void ManagerQuit() throws IOException {
        // Create a copy of clientSet to prevent a ConcurrentModificationException
        Set<ICanvasClient> clientSetCopy = new HashSet<>(clientSet);
        for (ICanvasClient client : clientSetCopy) {
            client.shutDownUI("managerQuit");
        }
        // Clear the clientSet
        // When a client disconnects, it is necessary for the server to clean up any references and resources
        // associated with that client. If this is not done, it could cause issues with future connections
        clientSet.clear();
        // reset the manager to avoid the next client cannot join
        hasManager = false;
        // Re-initialize the chat history, if set to null, a NullPointerException could be thrown
        chatHistory = new ArrayList<String>();
        System.out.println("The manager closes the application!");
    }


    // Add a new client to the client set
    public void addClientToList(ICanvasClient client) {
        this.clientSet.add(client);
        System.out.println("Updated client set: " + clientSet);
    }

    // Check if the canvas has a manager
    public boolean hasManager() {
        return hasManager;
    }

}
