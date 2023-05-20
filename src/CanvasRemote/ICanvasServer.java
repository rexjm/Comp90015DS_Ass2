package CanvasRemote;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * RMI Remote interface - must be shared between client and server.
 * All methods will throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 *
 * The methods can be used by CanvasClient
 */
public interface ICanvasServer extends Remote, Serializable {

    // Add the new clients connecting to server
    void addUser(ICanvasClient CanvasClient) throws RemoteException;

    // Sharing the canvas within users
    void UpdateCanvas(ICanvasStatus canvasStatus) throws RemoteException;

    // Check the validity of the new user added and set it as the manager if there is no client.
    void checkUserValidity(ICanvasClient CanvasClient) throws RemoteException;

    // when manager kicks out a user
    void kickUser(String clientName) throws RemoteException;

    // when user self-exit, or t
//    void removeUser(String clientName) throws RemoteException;

    // getter function for clients
    List<ICanvasClient> getUsers() throws RemoteException;

    // show the canvas when a new client join
    byte[] loadExistCanvas() throws RemoteException;

    // when the manager of the whiteboard creates a new whiteboard
    void cleanAllCanvas() throws RemoteException;

    // when the manager of the whiteboard opens a new image


    // update the chat box when a new user join

    void updateServerChatBox(String chatMsg) throws RemoteException;

    // when the manager quits, application terminate
    void ManagerQuit() throws IOException;

    void updateImage(byte[] toByteArray) throws IOException;

    List<ICanvasClient> updateUserList() throws RemoteException;

//    void addChat(String s) throws RemoteException;

    byte[] getManagerImage() throws IOException;

    ArrayList<String> getChatHistory() throws RemoteException;
}
