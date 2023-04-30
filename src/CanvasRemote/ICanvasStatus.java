package CanvasRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICanvasStatus extends Remote {

    String getShape() throws RemoteException;
    String getColor() throws RemoteException;
    String getStartPoint() throws RemoteException;
    String getText() throws RemoteException;
    String getName() throws RemoteException;

}
