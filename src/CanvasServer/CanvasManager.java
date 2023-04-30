//package CanvasServer;
//
//import CanvasRemote.ICanvasClient;
//
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class CanvasManager implements Iterable<ICanvasClient> {
//    private Set<ICanvasClient> clientSet;
//    private ICanvasClient manager;
//
//    public CanvasManager(CanvasServerServant canvasServerServant) {
//        this.clientSet = Collections.newSetFromMap(new ConcurrentHashMap<ICanvasClient, Boolean>());
//    }
//
//    // Add a new client to the client set
//    public void addClient(ICanvasClient client) {
//        this.clientSet.add(client);
//    }
//
//    // Get the set of clients
//    public Set<ICanvasClient> getClientSet() {
//        return this.clientSet;
//    }
//
//    // Set the manager of the canvas
//    public void setManager(ICanvasClient manager) {
//        this.manager = manager;
//    }
//
//    // Kick out a client from the client list
//    public void kickClient(ICanvasClient client) {
//        this.clientSet.remove(client);
//    }
//
//    // Check if the canvas has a manager
//    public boolean hasManager() {
//        return manager != null;
//    }
//
//    @Override
//    public Iterator<ICanvasClient> iterator() {
//        return clientSet.iterator();
//    }
//}
