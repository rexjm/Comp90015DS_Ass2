/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description : The ICanvasStatus interface defines a set of methods for a remote object in a client-server
 * system, which is utilized to manage the status and attributes of a shared canvas, such as color, endpoint, text,
 * name, state, and mode, using RMI.
 */

package CanvasRemote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICanvasStatus extends Remote {

    Paint getColor() throws RemoteException;
    Point getEndPoint() throws RemoteException;
    String getText() throws RemoteException;
    String getName() throws RemoteException;

    Object getState() throws RemoteException;

    Object getMode() throws RemoteException;
}
