package CanvasRemote;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * RMI Remote interface - must be shared between client and server.
 * All methods will throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 *
 * The methods can be used by CanvasServer, and the canvas client interface provides methods for
 * updating the canvas,user list, and chat box, as well as managing the canvas state and handling
 * manager actions such as kicking users out and terminating the application.
 */

public interface ICanvasClient extends Remote, Serializable {

    // Receive an updated shape drawn on the canvas by a user
    void updateCanvas(ICanvasStatus CanvasStatus) throws RemoteException;

    // Update the client's user list when a user is added or removed
    void updateUserList(List<ICanvasClient> usernames) throws RemoteException;

    // Notify the client when they are kicked out by the manager
    void notifyKickedOut() throws RemoteException;

    // Update the client's chat box when a new message is received
    void updateChatBox(String chatMsg) throws RemoteException;

//    // Load the existing canvas for a new client joining the whiteboard
//    void loadExistingCanvas(byte[] canvasData) throws RemoteException;

    // Clear the client's canvas when the manager creates a new whiteboard
    void clearCanvas() throws RemoteException;

    // Load a new image on the client's canvas when the manager opens a new image
    void loadNewImage(byte[] imageData) throws IOException;

    // Get the client's name (username)
    String getClientName() throws RemoteException;

    // Set the client's name (username)
    void setClientName(String name) throws RemoteException;

    // Get the client's manager
    boolean isClientManager() throws RemoteException;

    // Set the client's manager
    void setClientManager(String managerName) throws RemoteException;

    // Send the canvas status to server
    byte[] synCanvas() throws RemoteException, IOException;

    ///////

    // Terminate the client application when the manager quits
    void terminateApp() throws RemoteException;

    boolean askManagerPermission(String name) throws RemoteException;

    void cleanCanvas() throws RemoteException;

    byte[] sendImage() throws RemoteException;

    void shutDownUI() throws RemoteException;

    void initialize(ICanvasServer canvasServer) throws RemoteException;

    boolean allowJoin() throws RemoteException;

    void setAllowed(boolean permission) throws RemoteException;
}
