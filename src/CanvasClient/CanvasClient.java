package CanvasClient;

import CanvasRemote.ICanvasClient;
import CanvasRemote.ICanvasServer;
import CanvasRemote.ICanvasStatus;

import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class CanvasClient extends UnicastRemoteObject implements ICanvasClient {

    // The stub of the canvas server.
    static ICanvasServer CanvasServer;
    static JFrame frame;
    private CanvasWhiteboard canvasWhiteboard;
    private JScrollPane msgArea;
    private JList<String> chatInputBox;
    private JButton cleanButton, lineButton, circleButton, ovalButton, rectangleButton, eraserButton;
    private JButton newButton, openButton, saveButton, saveAsButton;
    private JButton selectColourButton;

    private DefaultListModel<String> clientList,chatList;
    private String clientName;
    private String fileName;
    private String filePath;
    private boolean isManager;
    private boolean allowed;

    protected CanvasClient() throws RemoteException {
        this.clientList = new DefaultListModel<>();
        this.chatList = new DefaultListModel<>();
        this.isManager = false;
        this.allowed = false;
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            LineBorder noChosen = new LineBorder(new Color(238, 238,238), 1);
            LineBorder chosen = new LineBorder(Color.BLUE, 1);

            Object source = e.getSource();
            if (source.equals(newButton)) {
                if (isManager) {
                    canvasWhiteboard.cleanAll();
                }
            } else if (source.equals(openButton)) {
                try {
                    openNewFile();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else if (source.equals(saveButton)) {
                try {
                    saveFile();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else if (source.equals(saveAsButton)) {
                try {
                    saveAsFile();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else if (source.equals(selectColourButton)) {

            } else if (source.equals(lineButton)) {
                canvasWhiteboard.line();
                lineButton.setBorder(chosen);
            } else if (source.equals(circleButton)) {
                canvasWhiteboard.circle();
                circleButton.setBorder(chosen);
            } else if (source.equals(ovalButton)) {
                canvasWhiteboard.oval();
                ovalButton.setBorder(chosen);
            } else if (source.equals(rectangleButton)) {
                canvasWhiteboard.rectangle();
                rectangleButton.setBorder(chosen);
            } else if (source.equals(eraserButton)) {
                canvasWhiteboard.eraser();
                eraserButton.setBorder(chosen);
            }
        }
    };

    private void saveAsFile() throws IOException{
        FileDialog saveAsDialog = new FileDialog(frame, "Save an image.", FileDialog.SAVE);
        saveAsDialog.setVisible(true);
        if (saveAsDialog.getFile() != null) {
            this.fileName = saveAsDialog.getName();
            this.fileName = saveAsDialog.getDirectory();
            ImageIO.write(canvasWhiteboard.saveCavas(), "png", new File(filePath + fileName));

        }

    }

    private void saveFile() throws IOException{
        if (fileName != null && filePath != null) {
            ImageIO.write(canvasWhiteboard.saveCavas(), "png", new File(filePath + fileName));
        } else {
            saveAsFile();
        }
    }

    public void initialize(ICanvasServer canvasServer) {
        JFrame frame = new JFrame("Login account: " + clientName);
        frame.setTitle("Good Luck !");
    }


    private void openNewFile() throws IOException{
        FileDialog fileDialog = new FileDialog(frame, "Open an exist image.", FileDialog.LOAD);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            this.fileName = fileDialog.getName();
            this.filePath = fileDialog.getDirectory();
            BufferedImage openedImage = ImageIO.read(new File(filePath + filePath));
            canvasWhiteboard.showImage(openedImage);
            ByteArrayOutputStream openedImageByte = new ByteArrayOutputStream();
            ImageIO.write(openedImage, "png", openedImageByte);
            CanvasServer.updateImage(openedImageByte.toByteArray());
        }
    }

    @Override
    public void updateCanvas(ICanvasStatus CanvasStatus) throws RemoteException {
    }

    @Override
    public void updateUserList(Set<ICanvasClient> usernames) throws RemoteException {

    }

    @Override
    public void notifyKickedOut() throws RemoteException {

    }

    @Override
    public void updateChatBox(String chatMsg) throws RemoteException {

    }

    @Override
    public void clearCanvas() throws RemoteException {

    }

    @Override
    public void loadNewImage(byte[] imageData) throws RemoteException {

    }

    @Override
    public String getClientName() throws RemoteException {
        return null;
    }

    @Override
    public void setClientName() throws RemoteException {

    }

    @Override
    public String getClientManager() throws RemoteException {
        return null;
    }

    @Override
    public void setClientManager(String managerName) throws RemoteException {

    }

    @Override
    public byte[] synCanvas() throws RemoteException, IOException {
        return new byte[0];
    }

    @Override
    public void terminateApp() throws RemoteException {

    }

    @Override
    public boolean allowJoin() throws RemoteException {
        return false;
    }

    @Override
    public void cleanCanvas() throws RemoteException {

    }

    @Override
    public byte[] sendImage() {
        return new byte[0];
    }

    @Override
    public void shutDownUI() throws RemoteException {

    }
}
