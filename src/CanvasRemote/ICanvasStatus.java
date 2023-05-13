package CanvasRemote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICanvasStatus extends Remote {

    String getShape() throws RemoteException;
    Paint getColor() throws RemoteException;
    Point getStartPoint() throws RemoteException;
    String getText() throws RemoteException;
    String getName() throws RemoteException;

    Object getState() throws RemoteException;

    Object getMode() throws RemoteException;
}
