package CanvasClient;

import CanvasRemote.ICanvasClient;
import CanvasRemote.ICanvasServer;
import CanvasRemote.ICanvasStatus;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Set;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.CENTER;

public class CanvasClient extends UnicastRemoteObject implements ICanvasClient {

    // The stub of the canvas server.
    static ICanvasServer CanvasServer;
    static JFrame frame;
    private CanvasWhiteboard canvasWhiteboard;
    private JScrollPane msgArea;
    private JList<String> chatInputBox;
    private JButton cleanButton, lineButton, circleButton, ovalButton, rectangleButton,textButton, eraserButton;
    private JButton newButton, openButton, saveButton, saveAsButton,kickUserButton;
    private JButton orangeBtn, yellowBtn, cyanBtn, blackBtn, brownBtn, pinkBtn, greyBtn, blueBtn, greenBtn, redBtn;
    private JButton purpleBtn, darkBlueBtn, darkgreyBtn, magentaBtn,  lightGrayBtn, whiteBtn;
    private DefaultListModel<String> clientList,chatList;
    private String clientName;
    private String fileName;
    private String filePath;
    private boolean isManager;
    private boolean allowed;

    private Hashtable<String, Point> startPoints = new Hashtable<String, Point>();


    protected CanvasClient() throws RemoteException {
        this.clientList = new DefaultListModel<>();
        this.chatList = new DefaultListModel<>();
        this.isManager = false;
        this.allowed = false;
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            LineBorder noChosen = new LineBorder(new Color(238, 238, 238), 1);
            LineBorder border = new LineBorder(Color.BLUE, 2);

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
            }else if (source.equals(kickUserButton)) {

            } else if (source.equals(lineButton)) {
                canvasWhiteboard.setLineMode();
                lineButton.setBorder(border);
            } else if (source.equals(circleButton)) {
                canvasWhiteboard.setCircleMode();
                circleButton.setBorder(border);
            } else if (source.equals(ovalButton)) {
                canvasWhiteboard.setOvalMode();
                ovalButton.setBorder(border);
            }  else if (source.equals(eraserButton)) {
                canvasWhiteboard.setEraserMode();
                eraserButton.setBorder(border);
            } else if (source.equals(rectangleButton)) {
                canvasWhiteboard.setRectMode();
                eraserButton.setBorder(border);
            }
        }
    };

    private void saveAsFile() throws IOException{
        FileDialog saveAsDialog = new FileDialog(frame, "Save an image.", FileDialog.SAVE);
        saveAsDialog.setVisible(true);
        if (saveAsDialog.getFile() != null) {
            this.fileName = saveAsDialog.getName();
            this.fileName = saveAsDialog.getDirectory();
            ImageIO.write(canvasWhiteboard.saveCanvas(), "png", new File(filePath + fileName));

        }

    }

    private void saveFile() throws IOException{
        if (fileName != null && filePath != null) {
            ImageIO.write(canvasWhiteboard.saveCanvas(), "png", new File(filePath + fileName));
        } else {
            saveAsFile();
        }
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

    public void initialize(ICanvasServer canvasServer) {
        JFrame frame = new JFrame("Login account: " + clientName);
        frame.setTitle(clientName + " Have fun !");
        Container content = frame.getContentPane();

        // Create the canvas for drawing
        canvasWhiteboard = new CanvasWhiteboard(clientName, isManager, canvasServer);

        //Set up the colour buttons
        blueBtn = new JButton();
        blueBtn.setBackground(Color.blue);
        blueBtn.setBorderPainted(false);
        blueBtn.setOpaque(true);
        blueBtn.addActionListener(actionListener);
        cyanBtn = new JButton();
        cyanBtn.setBackground(Color.cyan);
        cyanBtn.setBorderPainted(false);
        cyanBtn.setOpaque(true);
        cyanBtn.addActionListener(actionListener);
        magentaBtn = new JButton();
        magentaBtn.setBackground(Color.magenta);
        magentaBtn.setBorderPainted(false);
        magentaBtn.setOpaque(true);
        magentaBtn.addActionListener(actionListener);
        greenBtn = new JButton();
        greenBtn.setBackground(Color.green);
        greenBtn.setBorderPainted(false);
        greenBtn.setOpaque(true);
        greenBtn.addActionListener(actionListener);
        yellowBtn = new JButton();
        yellowBtn.setBackground(Color.yellow);
        yellowBtn.setBorderPainted(false);
        yellowBtn.setOpaque(true);
        yellowBtn.addActionListener(actionListener);
        orangeBtn = new JButton();
        orangeBtn.setBackground(Color.orange);
        orangeBtn.setBorderPainted(false);
        orangeBtn.setOpaque(true);
        orangeBtn.addActionListener(actionListener);
        pinkBtn = new JButton();
        pinkBtn.setBackground(Color.pink);
        pinkBtn.setBorderPainted(false);
        pinkBtn.setOpaque(true);
        pinkBtn.addActionListener(actionListener);
        redBtn = new JButton();
        redBtn.setBackground(Color.red);
        redBtn.setBorderPainted(false);
        redBtn.setOpaque(true);
        redBtn.addActionListener(actionListener);
        blackBtn = new JButton();
        blackBtn.setBackground(Color.black);
        blackBtn.setBorderPainted(false);
        blackBtn.setOpaque(true);
        blackBtn.addActionListener(actionListener);
        darkgreyBtn = new JButton();
        darkgreyBtn.setBackground(Color.darkGray);
        darkgreyBtn.setBorderPainted(false);
        darkgreyBtn.setOpaque(true);
        darkgreyBtn.addActionListener(actionListener);
        greyBtn = new JButton();
        greyBtn.setBackground(Color.gray);
        greyBtn.setBorderPainted(false);
        greyBtn.setOpaque(true);
        greyBtn.addActionListener(actionListener);
        lightGrayBtn = new JButton();
        lightGrayBtn.setBackground(Color.lightGray);
        lightGrayBtn.setBorderPainted(false);
        lightGrayBtn.setOpaque(true);
        lightGrayBtn.addActionListener(actionListener);
        whiteBtn = new JButton();
        whiteBtn.setBackground(Color.white);
        whiteBtn.setBorderPainted(false);
        whiteBtn.setOpaque(true);
        whiteBtn.addActionListener(actionListener);
        brownBtn = new JButton();
        brownBtn.setBackground(new Color(153,76,0));
        brownBtn.setBorderPainted(false);
        brownBtn.setOpaque(true);
        brownBtn.addActionListener(actionListener);
        purpleBtn = new JButton();
        purpleBtn.setBackground(new Color(128, 0, 128));
        purpleBtn.setBorderPainted(false);
        purpleBtn.setOpaque(true);
        purpleBtn.addActionListener(actionListener);
        darkBlueBtn = new JButton();
        darkBlueBtn.setBackground(new Color(0, 0, 139));
        darkBlueBtn.setBorderPainted(false);
        darkBlueBtn.setOpaque(true);
        darkBlueBtn.addActionListener(actionListener);

        // Create colorButtonsPanel and set the color button size
        JPanel colorButtonsPanel = new JPanel();
        colorButtonsPanel.setLayout(new GridLayout(2, 8));
        colorButtonsPanel.setMaximumSize(new Dimension(180, 50));
        Dimension colorBtnDim = new Dimension(5, 5);
        greenBtn.setPreferredSize(colorBtnDim);
        redBtn.setPreferredSize(colorBtnDim);
        orangeBtn.setPreferredSize(colorBtnDim);
        pinkBtn.setPreferredSize(colorBtnDim);
        darkgreyBtn.setPreferredSize(colorBtnDim);
        yellowBtn.setPreferredSize(colorBtnDim);
        blackBtn.setPreferredSize(colorBtnDim);
        whiteBtn.setPreferredSize(colorBtnDim);
        blueBtn.setPreferredSize(colorBtnDim);
        greyBtn.setPreferredSize(colorBtnDim);
        purpleBtn.setPreferredSize(colorBtnDim);
        darkBlueBtn.setPreferredSize(colorBtnDim);
        cyanBtn.setPreferredSize(colorBtnDim);
        brownBtn.setPreferredSize(colorBtnDim);
        magentaBtn.setPreferredSize(colorBtnDim);
        lightGrayBtn.setPreferredSize(colorBtnDim);

        colorButtonsPanel.add(orangeBtn);
        colorButtonsPanel.add(redBtn);
        colorButtonsPanel.add(greenBtn);
        colorButtonsPanel.add(pinkBtn);
        colorButtonsPanel.add(blackBtn);
        colorButtonsPanel.add(whiteBtn);
        colorButtonsPanel.add(yellowBtn);
        colorButtonsPanel.add(cyanBtn);
        colorButtonsPanel.add(brownBtn);
        colorButtonsPanel.add(greyBtn);
        colorButtonsPanel.add(purpleBtn);
        colorButtonsPanel.add(darkBlueBtn);
        colorButtonsPanel.add(darkgreyBtn);
        colorButtonsPanel.add(blueBtn);
        colorButtonsPanel.add(magentaBtn);
        colorButtonsPanel.add(lightGrayBtn);

        LineBorder border = new LineBorder(Color.black, 2);
        Icon icon = new ImageIcon("./icon/1.png");
//        drawBtn = new JButton(icon);
//        drawBtn.setToolTipText("Pencil draw");
//        drawBtn.setBorder(border);
//        drawBtn.addActionListener(actionListener);
        border = new LineBorder(new Color(238,238,238), 2);

        ImageIcon cleanIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/clean.png");
        cleanButton = new JButton(cleanIcon);
        cleanButton.setIcon(resizeIcon(cleanIcon, 60, 60));
        cleanButton.setToolTipText("CLean the whiteboard");
        cleanButton.setBorder(border);
        cleanButton.addActionListener(actionListener);
        ImageIcon lineIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/line.png");
        lineButton = new JButton(lineIcon);
        lineButton.setIcon(resizeIcon(lineIcon, 60, 60));
        lineButton.setToolTipText("Draw line");
        lineButton.setBorder(border);
        lineButton.addActionListener(actionListener);
        ImageIcon recIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/rectangle.png");
        rectangleButton = new JButton(recIcon);
        rectangleButton.setIcon(resizeIcon(recIcon, 60, 60));
        rectangleButton.setToolTipText("Draw rectangle");
        rectangleButton.setBorder(border);
        rectangleButton.addActionListener(actionListener);
        ImageIcon cirIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/circle.png");
        circleButton = new JButton(cirIcon);
        circleButton.setIcon(resizeIcon(cirIcon, 60, 60));
        circleButton.setToolTipText("Draw circle");
        circleButton.setBorder(border);
        circleButton.addActionListener(actionListener);
        ImageIcon ovalIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/oval.png");
        ovalButton = new JButton(ovalIcon);
        ovalButton.setIcon(resizeIcon(ovalIcon, 60, 60));
        ovalButton.setToolTipText("Draw oval");
        ovalButton.setBorder(border);
        ovalButton.addActionListener(actionListener);
        ImageIcon TBIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/Text-Box.png");
        textButton = new JButton(TBIcon);
        textButton.setIcon(resizeIcon(TBIcon, 60, 60));
        textButton.setToolTipText("Text box");
        textButton.setBorder(border);
        textButton.addActionListener(actionListener);
        ImageIcon eraserIcon = new ImageIcon("/Users/rex/Desktop/Comp90015 DS/Ass2/Ass2_Canvas/src/Icon/eraser.png");
        eraserButton = new JButton(eraserIcon);
        eraserButton.setIcon(resizeIcon(eraserIcon, 60, 60));
        eraserButton.setToolTipText("Eraser");
        eraserButton.setBorder(border);
        eraserButton.addActionListener(actionListener);

        newButton = new JButton("New Board");
        newButton.setToolTipText("Create a new board");
        newButton.addActionListener(actionListener);
        openButton = new JButton("Open Image");
        openButton.setToolTipText("Open an image file");
        openButton.addActionListener(actionListener);
        saveButton = new JButton("Save Image");
        saveButton.setToolTipText("Save as image file");
        saveButton.addActionListener(actionListener);
        saveAsButton = new JButton("Save as");
        saveAsButton.setToolTipText("Save image file");
        saveAsButton.addActionListener(actionListener);
        kickUserButton = new JButton("Remove User");
        kickUserButton.setToolTipText("Kick out the selected user");
        kickUserButton.addActionListener(actionListener);

        JList<String> clientJlist = new JList<>(clientList);
        JScrollPane currUsers = new JScrollPane(clientJlist);
        currUsers.setMinimumSize(new Dimension(100, 150));

        // if the client is the manager, he can remove client
        if (isManager) {
            clientJlist.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    @SuppressWarnings("unchecked")
                    JList<String> list = (JList<String>)evt.getSource();
                    if (evt.getClickCount() == 2) {
                        int index = list.locationToIndex(evt.getPoint());
                        String selectedName = list.getModel().getElementAt(index);
                        try {
                            //manager can't remove him/herself
                            if(! getClientName().equals(selectedName)) {
                                int dialogResult = JOptionPane.showConfirmDialog (frame, "Are you sure to remove " + selectedName + "?",
                                        "Warning", JOptionPane.YES_NO_OPTION);
                                if(dialogResult == JOptionPane.YES_OPTION) {
                                    try {
                                        canvasServer.kickUser(selectedName);
                                        updateUserList(canvasServer.updateUserList());
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        System.err.println("There is an IO error.");
                                    }
                                }
                            }
                        } catch (HeadlessException e) {
                            // TODO Auto-generated catch block
                            System.err.println("There is an headless error.");
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            System.err.println("There is an IO error.");
                        }
                    }
                }
            });
        }

        // create a chatbox with a send button
        chatInputBox = new JList<>(chatList);
        msgArea = new JScrollPane(chatInputBox);
        msgArea.setMaximumSize(new Dimension(950, 300));
        // Create a TextField for inputting text
        JTextField msgText = new JTextField();
        msgText.setMaximumSize(new Dimension(870, 140));

        JButton sendBtn = new JButton("Send"); //addMouseListener here 直接call server 去broadcast message
        sendBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if(!msgText.getText().equals("")) {
                    try {
                        canvasServer.addChat(clientName + ": "+ msgText.getText());
                        // Set the scrollpane to show the updated chat message
                        SwingUtilities.invokeLater(() -> {
                            JScrollBar vertical = msgArea.getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        });
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        JOptionPane.showMessageDialog(null, "WhiteBoard server is down, please save and exit.");
                    }
                    msgText.setText("");
                }
            }
        });


        // set the layout
        GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // The Horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(CENTER)
                        .addComponent(lineButton)
                        .addComponent(rectangleButton)
                        .addComponent(circleButton)
                        .addComponent(ovalButton)
                        .addComponent(textButton)
                        .addComponent(eraserButton)
                        .addComponent(cleanButton)
                        .addComponent(currUsers)
                        .addComponent(kickUserButton)
                )
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(openButton)
                                .addComponent(saveButton)
                                .addComponent(saveAsButton)
                                .addComponent(colorButtonsPanel)
                        )
                        .addGroup(layout.createParallelGroup()
                                .addComponent(canvasWhiteboard)
                                .addComponent(msgArea)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(msgText)
                                        .addComponent(sendBtn)
                                )
                        )
                )
        );

        // The vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lineButton)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(openButton)
                                        .addComponent(saveButton)
                                        .addComponent(saveAsButton)
                                        .addComponent(colorButtonsPanel)
                                )
                        )
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(rectangleButton)
                                .addComponent(circleButton)
                                .addComponent(ovalButton)
                                .addComponent(textButton)
                                .addComponent(eraserButton)
                                .addComponent(cleanButton)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(canvasWhiteboard)
                        )
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(currUsers)
                                .addComponent(kickUserButton)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(msgArea)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(msgText)
                                        .addComponent(sendBtn)
                                )
                        )
                )
        );
        //format to same size
//        layout.linkSize(SwingConstants.HORIZONTAL, cleanButton, saveButton, saveAsButton, openButton);

        // set the minimum framesize
//        if (isManager) frame.setMinimumSize(new Dimension(1200, 800));
//        else
        frame.setMinimumSize(new Dimension(1200, 800));

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        //if the manager close the window, all other client are removed and the all clients' window are force closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (isManager) {
                    if (JOptionPane.showConfirmDialog(frame,
                            "You are the manager? Are you sure close the application?", "Close Application?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        try {
                            canvasServer.terminateApp();

                        } catch (IOException e) {
                            System.err.println("There is an IO error");
                        } finally {
                            System.exit(0);
                        }
                    }
                } else {
                    if (JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to quit?", "Close Paint Board?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        try {
                            canvasServer.removeUser(clientName);
                            updateUserList(canvasServer.updateUserList());
                        } catch (RemoteException e) {
                            JOptionPane.showMessageDialog(null, "Canvas server is down, please save and exit.");
                        } finally {
                            System.exit(0);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean allowJoin() throws RemoteException {
        return this.allowed;
    }

    @Override
    public void setAllowed(boolean permission) throws RemoteException {
        this.allowed = permission;
    }


    public Shape makeLine(Shape shape,Point start, Point end) {
        shape = new Line2D.Double(start.x, start.y, end.x, end.y);
        return shape;
    }

    //draw Rectangle
    public Shape makeRect(Shape shape,Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y= Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        shape = new Rectangle2D.Double(x, y, width, height);
        return shape;
    }

    //draw circle
    public Shape makeCircle(Shape shape,Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y= Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        shape = new Ellipse2D.Double(x, y, Math.max(width, height), Math.max(width, height));
        return shape;
    }

    //draw oval
    public Shape makeOval(Shape shape, Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        shape = new Ellipse2D.Double(x, y, width, height);
        return shape;
    }


    //Make text
    public Shape makeText(Shape shape,Point start) {
        int x = start.x - 5;
        int y= start.y - 20;
        int width = 130;
        int height = 25;
        shape = new RoundRectangle2D.Double(x, y, width, height, 15, 15);
        return shape;
    }




    @Override
    public void updateCanvas(ICanvasStatus CanvasStatus) throws RemoteException {
        // skip msg from itself
        if (CanvasStatus.getName().compareTo(clientName) == 0) {
            return;
        }
        Shape shape = null;

        if (CanvasStatus.getState().equals("start")) {
            //Let hashmap startPoint stores the start point of a client and wait for the next draw action
            startPoints.put(CanvasStatus.getName(), CanvasStatus.getStartPoint());
            return;
        }

        //start from the start point of client x
        Point startPtName = (Point)startPoints.get(CanvasStatus.getName());

        //set canvas painting color (Sets the Paint attribute for the Graphics2D context.)
        canvasWhiteboard.getGraphic().setPaint(CanvasStatus.getColor());

        if (CanvasStatus.getState().equals("drawing")) {
            if (CanvasStatus.getMode().equals("eraser")) {
                // Constructs a solid BasicStroke with the specified line width and with default values for the cap
                canvasWhiteboard.getGraphic().setStroke(new BasicStroke(15.0f));// The width
            }
            shape = makeLine(shape,startPtName, CanvasStatus.getStartPoint());
            startPoints.put(CanvasStatus.getName(), CanvasStatus.getStartPoint());
            canvasWhiteboard.getGraphic().draw(shape);
            canvasWhiteboard.repaint();
            return;
        }

        //the mouse is released, so we draw from start point to the broadcast point
        if (CanvasStatus.getState().equals("end")) {
            if (CanvasStatus.getMode().equals("draw") || CanvasStatus.getMode().equals("line")) {
                shape = makeLine(shape,startPtName, CanvasStatus.getStartPoint());
            } else if (CanvasStatus.getMode().equals("eraser")) {
                canvasWhiteboard.getGraphic().setStroke(new BasicStroke(1.0f));
            } else if (CanvasStatus.getMode().equals("rect")) {
                shape = makeRect(shape,startPtName, CanvasStatus.getStartPoint());
            } else if (CanvasStatus.getMode().equals("circle")) {
                shape = makeCircle(shape,startPtName, CanvasStatus.getStartPoint());
            }  else if (CanvasStatus.getMode().equals("oval")) {
                shape = makeOval(shape,startPtName, CanvasStatus.getStartPoint());
            } else if (CanvasStatus.getMode().equals("text")) {
                canvasWhiteboard.getGraphic().setFont(new Font("TimesRoman", Font.PLAIN, 24));
                canvasWhiteboard.getGraphic().drawString(CanvasStatus.getText(), CanvasStatus.getStartPoint().x, CanvasStatus.getStartPoint().y);
            }
            //draw shape if in shape mode: triangle, circle, rectangle
            if (!CanvasStatus.getMode().equals("text")) {
                try {
                    canvasWhiteboard.getGraphic().draw(shape);
                }catch(Exception e) {

                }
            }
            // If this component is a lightweight component, this method causes a call to
            // this component's paint method as soon as possible.
            canvasWhiteboard.repaint();
            //once finished drawing remove the start point of client x
            startPoints.remove(CanvasStatus.getName());
//            return;
        }
        return;
    }

    @Override
    public void updateUserList(List<ICanvasClient> usernames) throws RemoteException {
        this.clientList.removeAllElements();
        for(ICanvasClient c: usernames) {
            try {
                clientList.addElement(c.getClientName());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notifyKickedOut() throws RemoteException {

    }

    @Override
    public void updateChatBox(String chatMsg) throws RemoteException {
        this.chatList.addElement(chatMsg);
    }

    @Override
    public void clearCanvas() throws RemoteException {
        if (this.isManager == false)
            this.canvasWhiteboard.cleanAll();
    }

    @Override
    public void loadNewImage(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        this.canvasWhiteboard.showImage(image);
    }

    @Override
    public String getClientName() throws RemoteException {
        return this.clientName;
    }


    @Override
    public void setClientName(String name) throws RemoteException {
        this.clientName = name;
    }

    @Override
    public boolean isClientManager() throws RemoteException {
        return this.isManager;
    }

    @Override
    public void setClientManager(String managerName) throws RemoteException {
        this.isManager = true;
        this.clientName = managerName;
    }

    @Override
    public byte[] synCanvas() throws RemoteException, IOException {
        ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
        ImageIO.write(this.canvasWhiteboard.saveCanvas(), "png", imageArray);
        return imageArray.toByteArray();
    }

    @Override
    public void terminateApp() throws RemoteException {

    }


    @Override
    public boolean askManagerPermission(String name) throws RemoteException {
        if (JOptionPane.showConfirmDialog(frame,
                name + " wants to join. Do you approve?", "Grant permission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            return true;
        }else {
            return false;
        }
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
        //if manager does not give permission
        if(!this.allowed) {
            Thread t = new Thread(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null, "Sorry, You were not grant access to the shared whiteboard." + "\n",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                }
            });
            t.start();
            return;
        }
        //if kicked out or manager quit
        Thread t = new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(frame, "The manager has quit.\n or you have been removed.\n" +
                                "Your application will be closed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        t.start();
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, ServerNotActiveException {
        if(args.length != 2) {
            throw new IllegalArgumentException("Need exactly two arguments.");
        }
        try {
            if(!(args[0].equals("localhost") || !args[0].equals("127.0.0.1"))) {
                System.err.println("Please enter localhost or 127.0.0.1");
                return;
            }

            String serverAddress = "//" + args[0]+":"+args[1] + "/CanvasServer";
            //Look up the Canvas Server from the RMI name registry
            ICanvasServer canvasServer = (ICanvasServer) Naming.lookup(serverAddress);
            System.out.println("Connected to the server!");
            ICanvasClient client =  new CanvasClient();
            //show user register GUI and register the user name to server
            boolean validName = false;
            String client_name = "";
            while(!validName) {
                client_name = JOptionPane.showInputDialog("Please type in your name:");
                if(client_name.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a name!");
                }else {
                    validName = true;
                }
                // If the user is the first to be added
                List<ICanvasClient> users = canvasServer.getUsers();
                if (users != null) {
                    for (ICanvasClient c : users) {
                        if (c != null) {
                            if (client_name.equals(c.getClientName()) || c.getClientName().equals("[Manager] " + client_name)) {
                                validName = false;
                                JOptionPane.showMessageDialog(null, "The name is taken, think a different name!");
                            }
                        }
                    }
                }
            }
            // if the name is valid, try to add it, need to get manager's permission
            client.setClientName(client_name);
            // add the user to the client list in server
            try {
                canvasServer.addUser(client);
            } catch(RemoteException e) {
                System.err.println("Error registering with remote server");
            }
            //launch the White Board GUI and start drawing
            client.initialize(canvasServer);
            //do not get the permission from manager
            if(!client.allowJoin()) {
                canvasServer.kickUser(client.getClientName());
            }
        } catch(ConnectException e) {
            System.err.println("Server is down or wrong IP address or  Port number.");
            e.printStackTrace();
        } catch(Exception e) {
            System.err.println("Please enter Valid IP and Port number.");
            e.printStackTrace();
        }
    }


}
