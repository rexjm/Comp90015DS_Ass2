/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description : The CanvasStatus class encapsulates the status of a drawing action on the whiteboard.
 */

package CanvasClient;

import CanvasRemote.ICanvasStatus;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CanvasStatus extends UnicastRemoteObject implements ICanvasStatus {
    private static final long serialVersionUID = 1L;
    private final String drawState;
    private final String clientName;
    private final String mode;
    private final Color color;
    private final Point point;
    private final String text;
    public CanvasStatus (String state, String name, String mode, Color color, Point pt, String text) throws RemoteException {
        this.drawState = state;
        this.clientName = name;
        this.mode = mode;
        this.color = color;
        this.point = pt;
        this.text = text;
    }

    @Override
    public Paint getColor() throws RemoteException {
        return this.color;
    }

    @Override
    public Point getEndPoint() throws RemoteException {
        return this.point;
    }

    @Override
    public String getText() throws RemoteException {
        return this.text;
    }

    @Override
    public String getName() throws RemoteException {
        return this.clientName;
    }

    @Override
    public Object getState() throws RemoteException {
        return this.drawState;
    }

    @Override
    public Object getMode() {
        return this.mode;
    }

}
