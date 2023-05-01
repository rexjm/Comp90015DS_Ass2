package CanvasServer;

import CanvasRemote.ICanvasServer;

import javax.swing.*;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CanvasServer {
    public static void main(String[] args) {

        try {
            ICanvasServer CanvasServer = (ICanvasServer) new CanvasServerServant();

            //Publish the remote object's stub in the registry under the name "Compute"
//            Registry registry = LocateRegistry.getRegistry();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt("8888")); //args[0]
            registry.bind("CanvasServer", CanvasServer);

            System.out.println("CanvasServer ready!");
            JOptionPane.showMessageDialog(null,"CanvasServer ready!");

        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
