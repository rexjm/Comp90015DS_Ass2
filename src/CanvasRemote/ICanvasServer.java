/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description : RMI Remote interface - must be shared between client and server.
 * All methods will throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 * The methods can be used by CanvasClient
 */

package CanvasRemote;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public interface ICanvasServer extends Remote, Serializable {

    // Add the new clients connecting to server
    void addUser(ICanvasClient CanvasClient) throws RemoteException;

    // Sharing the canvas within users
    void UpdateCanvas(ICanvasStatus canvasStatus) throws RemoteException;

    // Check the validity of the new user added and set it as the manager if there is no client.
    void checkUserValidity(ICanvasClient CanvasClient) throws RemoteException;

    // when manager kicks out a user
    void kickUser(String clientName) throws RemoteException;


    // getter function for clients
    List<ICanvasClient> getUsers() throws RemoteException;


    // when the manager of the whiteboard creates a new whiteboard
    void cleanAllCanvas() throws RemoteException;

    // update the chat box when a new user join

    void updateServerChatBox(String chatMsg) throws RemoteException;

    // when the manager quits, application terminate
    void ManagerQuit() throws IOException;

    void updateImage(byte[] toByteArray) throws IOException;

    byte[] getManagerImage() throws IOException;

    ArrayList<String> getChatHistory() throws RemoteException;
}
