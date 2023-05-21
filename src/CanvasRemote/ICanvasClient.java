/**
 * Name:Ruixiang
 * Surname:TANG
 * Student ID:1298221
 * Description: RMI Remote interface - must be shared between client and server.
 * All methods will throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 * The methods can be used by CanvasServer, and the canvas client interface provides methods for
 * updating the canvas,user list, and chat box, as well as managing the canvas state and handling
 * manager actions such as kicking users out and terminating the application.
 */

package CanvasRemote;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICanvasClient extends Remote, Serializable {

    // Receive an updated shape drawn on the canvas by a user
    void updateCanvas(ICanvasStatus CanvasStatus) throws RemoteException;

    // Update the client's user list when a user is added or removed
    void updateUserList(List<ICanvasClient> usernames) throws RemoteException;

    // Update the client's chat box when a new message is received
    void updateChatBox(String chatMsg) throws RemoteException;

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


    // Terminate the client application when the manager quits

    boolean askManagerPermission(String name) throws RemoteException;

    byte[] getImage() throws IOException;

    void shutDownUI(String kick) throws RemoteException;

    void initialize(ICanvasServer canvasServer) throws RemoteException;

    boolean allowJoin() throws RemoteException;

    void setAllowed(boolean permission) throws RemoteException;

    void clearCanvas() throws RemoteException;

    void syncImage(byte[] image) throws RemoteException;

}
