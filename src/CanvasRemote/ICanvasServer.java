package CanvasRemote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * RMI Remote interface - must be shared between client and server.
 * All methods will throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 *
 * The methods can be used by CanvasClient
 */
public interface ICanvasServer extends Remote {

    // Add the new clients connecting to server
    void addUser(ICanvasClient CanvasClient) throws RemoteException;

    // Sharing the canvas within users
    void UpdateCanvas(ICanvasStatus canvasStatus) throws RemoteException;

    // Check the validity of the new user added and set it as the manager if there is no client.
    void checkUserValidity(ICanvasClient CanvasClient) throws RemoteException;

    // when manager kicks out a user
    void kickUser(String clientName) throws RemoteException;

    // when user self-exit, or t
    void removeUser(String clientName) throws RemoteException;

    // getter function for clients
    Set<ICanvasClient> getUsers() throws RemoteException;

    // show the canvas when a new client join
    byte[] loadExistCanvas() throws RemoteException;

    // when the manager of the whiteboard creates a new whiteboard
    void openNewCanvas() throws RemoteException;

    // when the manager of the whiteboard opens a new image

    void openNewImage(byte[] image) throws IOException;

    // update the chat box when a new user join
    void updateChatBox(String chatMsg) throws RemoteException;

    // when the manager quits, application terminate
    void terminateApp() throws IOException;

    byte[] updateImage(byte[] toByteArray) throws IOException;
}
