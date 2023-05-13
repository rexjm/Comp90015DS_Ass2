package CanvasClient;

import CanvasRemote.ICanvasStatus;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CanvasStatus extends UnicastRemoteObject implements ICanvasStatus {
    private static final long serialVersionUID = 1L;
    private String drawState;
    private String clientName;
    private String mode;
    private Color color;
    private Point point;
    private String text;
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
    public Point getStartPoint() throws RemoteException {
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

    @Override
    public String getShape() throws RemoteException {
        return null;
    }
}
