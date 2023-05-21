/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description :The CanvasServer class in Java sets up a server that provides a remote canvas service, registering
 * an instance of CanvasServerServant as a remote object in a registry service bound to the port of arg[0], making it
 * available for clients to interact with using RMI system.
 */
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
            ICanvasServer CanvasServer = new CanvasServerServant();

            //Publish the remote object's stub in the registry under the name "Compute"
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0])); //args[0]
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
