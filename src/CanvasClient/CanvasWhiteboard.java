package CanvasClient;

import CanvasRemote.ICanvasServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class CanvasWhiteboard extends JComponent {
    private static final long serialVersionUID = 1L;
    private String clientName;
    private boolean isManager;
    private Point startPt, endPt;
    private Color color;
    private String mode;
    private String text;

    private BufferedImage image; //store the dimension and data for canvas to save
    private BufferedImage previousCanvas;
    private Graphics2D graphics;//save the state of current/previous canvas
    private ICanvasServer server;
   public CanvasWhiteboard(String name, boolean isManager, ICanvasServer RemoteInterface){
        this.server = RemoteInterface;
        this.clientName = name;
        this.isManager = isManager;
        this.color = Color.black;
        this.mode = "draw"; //default mode
        this.text = "";

        setDoubleBuffered(false);
        //When listens a mouse click, store the start location and send it to the server
        addMouseListener(new MouseAdapter() {
             public void mousePressed(MouseEvent e) {

                 startPt = e.getPoint();
                 saveCanvas();
                 try {
                     ChatBox message = new ChatBox("start", clientName, mode, color, startPt, text);
                     server.UpdateCanvas(message);
                 } catch (RemoteException ex) {
                     JOptionPane.showMessageDialog(null, "Canvas server is down.");
                 }
             }
         });
        //Listen to the action on the canvas, draw the shape on local client, then send the shape to server
        addMouseMotionListener (new MouseMotionAdapter() {
            public void mouseDragged (MouseEvent e) {
                //get the end point
                endPt = e.getPoint ();
                Shape shape = null;
                if (graphics != null) {
                    if (mode.equals("draw")) {
                        //newShape. makeLine (startPt, endPt);
                        shape = makeLine(shape, startPt, endPt);
                        startPt = endPt;
                        try {
                            ChatBox item = new ChatBox("drawing", clientName, mode, color, endPt, "");
                            server.UpdateCanvas(item);
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(null, "Canvas server is down.");
                        }
                    } else if (mode.equals("eraser")) {
                            shape = makeLine(shape, startPt, endPt);
                            startPt = endPt;
                            graphics.setPaint (Color .white);
                            graphics.setStroke(new BasicStroke (15.0f));
                            try {
                                ChatBox message = new ChatBox("drawing", clientName, mode, Color.white, endPt, "");
                                        server. UpdateCanvas(message);
                            } catch (RemoteException ex) {
                                    JOptionPane.showMessageDialog(null,"Canvas server is down.");
                            }
                    } else if (mode.equals("line")) {
                    //when drawing, draw the previous image then add to it
                        drawPreviousCanvas();
                        shape = makeLine(shape, startPt, endPt);
                    } else if (mode.equals("rect")) {
                        drawPreviousCanvas ();
                        shape = makeRect (shape, startPt, endPt);
                    } else if (mode.equals("circle")) {
                        drawPreviousCanvas();
                        shape = makeCircle(shape, startPt, endPt);
                    } else if (mode.equals("text")) {
                        drawPreviousCanvas();
                        graphics.setFont (new Font ("TimesRoman", Font.PLAIN, 20));
                        graphics.drawString("Enter text here", endPt.x, endPt.y) ;
                        shape = makeText(shape, startPt);
                        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float []{3}, 0);
                        graphics.setStroke (dashed);
                    }
                    //this shows the shape while dragging in local clients and does not send to server
                    graphics.draw(shape);
                    repaint();
                }
            }
        });
        //when mouse is released we can draw shape
       addMouseListener (new MouseAdapter () {
           public void mouseReleased(MouseEvent e) {
               //Once the mouse is released
               endPt = e.getPoint();
               Shape shape = null;
               if (graphics != null) {
                   if (mode.equals("line")) {
                       shape = makeLine(shape, startPt, endPt);
                   } else if (mode.equals("draw")) {
                       shape = makeLine(shape, startPt, endPt);
                   } else if (mode.equals("rect")) {
                       shape = makeRect(shape, startPt, endPt);
                   } else if (mode.equals("circle")) {
                       shape = makeCircle(shape, startPt, endPt);
                   } else if (mode.equals("text")) {
                       text = JOptionPane.showInputDialog("What text you want to add?");
                       if (text == null) {
                           text = "";
                       }
                       drawPreviousCanvas();
                       graphics.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                       graphics.drawString(text, endPt.x, endPt.y);
                       graphics.setStroke(new BasicStroke(1.0f));
                   }
                   // if in shape modes
                   if (!mode.equals("text")) {
                       try {
                           graphics.draw(shape);
                       } catch (NullPointerException ex) {
                           //do nothing, this is caused by draw mode, where the end result need not be send
                       }
                   }
                   repaint();
                   //eraser
                   if (mode.equals("eraser")) {
                       try {
                           ChatBox message = new ChatBox("end", clientName, mode, Color.white, endPt, text);
                           server.UpdateCanvas(message);
                       } catch (RemoteException el) {
                           JOptionPane.showMessageDialog(null, "Canvas server is down.");
                       }
                       graphics.setPaint(color);
                       graphics.setStroke(new BasicStroke(1.0f));
                   } else {
                       try {
                           ChatBox message = new ChatBox("end", clientName, mode, color, endPt, text);
                           server.UpdateCanvas(message);
                       } catch (RemoteException e1) {
                           JOptionPane.showMessageDialog(null, "Canvas server is down.");
                       }
                   }
               }
           }
       });
   }
    //The method for painting the shape on the white board.
    // initialize the white board to synchronize with the manager's image when the client join the shared white board
    protected void paintComponent (Graphics g) {
        if (image == null) {
            if (isManager) {
                image = new BufferedImage(950, 550, BufferedImage.TYPE_INT_RGB);
                graphics = (Graphics2D) image.getGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                reset();
            } else {
                try {
                    byte[] rawImage = server.sendImage();
                    image = ImageIO.read(new ByteArrayInputStream(rawImage));
                    graphics = (Graphics2D) image.getGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setPaint(color);
                } catch (IOException e) {
                    System.err.println("Fail receiving image!");
                }
            }
        }
        g.drawImage(image, 0, 0, null);
    }
    public Color gerCurrColor() {
        return color;
    }
    public String gerCurrMode() {
        return mode;
    }
    public Graphics2D getGraphic() {
        return graphics;
    }
        public BufferedImage getCanvas () {
            saveCanvas() ;
            return previousCanvas;
        }
        public void reset(){
        graphics.setPaint (Color.white);
        graphics. fillRect(0, 0, 950, 550);
        graphics.setPaint (color);
        repaint();
    }

    //save the image
    public RenderedImage saveCanvas () {
        ColorModel cm = image.getColorModel();
        WritableRaster raster = image.copyData(null);
        previousCanvas = new BufferedImage(cm, raster, false, null);
        return null;
    }
    //cover the current canvas with previous canvas states
    public void drawPreviousCanvas () {
        drawImage(previousCanvas) ;
    }
    public void drawImage (BufferedImage img) {
        graphics.drawImage(img, null, 0, 0);
        repaint();
    }
    public void brown() {
        this.color = new Color(153, 76, 0);
        graphics.setPaint(color);
    }
    public void red() {
        this.color = Color.red;
        graphics.setPaint (color);
    }
    public void pink() {
        this. color = new Color (255, 153,204);
        graphics.setPaint (color);
    }
    public void black() {
        color = Color.black;
        graphics.setPaint(color);
    }
    public void green() {
        color = Color.green;
        graphics.setPaint(color);
    }
    public void blue() {
        color = Color.blue;
        graphics.setPaint(color);
    }
    public void orange () {
        color = Color.orange;
        graphics.setPaint (color);
    }
    public void grey() {
        color = Color.gray;
        graphics.setPaint(color);
    }
    public void purple() {
        color = new Color (102, 0, 204);
        graphics.setPaint(color);
    }
    public void lime() {
        color = new Color(102, 102, 0);
        graphics.setPaint(color);
    }
    public void darkgrey() {
        color = Color.darkGray;
        graphics.setPaint (color);
    }
    public void magenta() {
        color = Color .magenta;
        graphics.setPaint (color);
    }
    public void a01() {
        color = new Color (0, 102, 102);
        graphics.setPaint (color);
    }
    public void sky() {
        color = new Color(0, 128, 255);
        graphics.setPaint(color);
    }
    public void yellow() {
        color = Color.yellow;
        graphics.setPaint(color);
    }
    public void cyan() {
        color = Color.cyan;
        graphics.setPaint(color);
    }
    public void draw() {
        mode = "draw";
    }
    public void line() {
        mode = "line";
    }
    public void rect() {
        mode = "rect";
    }
    public void circle() {
        mode = "circle";
    }
    public void oval() {
        mode = "oval";
    }
    public void triangle() {
        mode = "triangle";
    }
    public void text() {
        mode = "text";
    }

    public void eraser() {
        mode = "eraser";
    }
    // draw line or wiggles
    public Shape makeLine(Shape shape, Point start, Point end) {
        shape = new Line2D.Double(start.x, start.y, end.x, end.y);
        return shape;
    }
    //draw Rectangle
    public Shape makeRect(Shape shape, Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y= Math.min(start.y, end.y) ;
        int width = Math.abs (start.x - end.x);
        int height = Math.abs(start.y - end.y);
        shape = new Rectangle2D. Double(x, y, width, height);
        return shape;
    }
    //draw circle
    public Shape makeCircle (Shape shape, Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        shape = new Ellipse2D.Double(x, y, Math.max(width, height), Math.max(width, height));
        return shape;
    }
    //Make text
    public Shape makeText (Shape shape, Point start) {
        int x = start.x - 5;
        int y = start.y - 20;
        int width = 130;
        int height = 25;
        shape = new RoundRectangle2D.Double(x, y, width, height, 15, 15);
        return shape;
    }

    public void showImage(BufferedImage openedImage) {
    }

    public void cleanAll() {
    }
}

