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
    private boolean isModified = false;

   public CanvasWhiteboard(String name, boolean isManager, ICanvasServer RemoteInterface){
        this.server = RemoteInterface;
        this.clientName = name;
        this.isManager = isManager;
        this.color = Color.black;
        this.mode = "draw"; //default mode
        this.text = "";

        // Disabling dual buffering may provide a performance advantage
        setDoubleBuffered(false);
        //When listens a mouse click, store the start location and send it to the server
        addMouseListener(new MouseAdapter() {
             public void mousePressed(MouseEvent e) {
                 setModified(true);
                 startPt = e.getPoint();
                 saveCanvas();
                 try {
                     CanvasStatus status = new CanvasStatus("start", clientName, mode, color, startPt, text);
                     server.UpdateCanvas(status);
                 } catch (RemoteException ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(null, "Canvas server is down.");
                 }
             }
         });
        //Listen to the action on the canvas, draw the shape on local client, then send the shape to server
        addMouseMotionListener (new MouseMotionAdapter() {
            public void mouseDragged (MouseEvent e) {
                //get the end point
                setModified(true);
                endPt = e.getPoint ();
                Shape shape = null;
                if (graphics != null) {
                    if (mode.equals("draw")) {
                        //newShape. makeLine (startPt, endPt);
                        graphics.setPaint(color);
                        shape = makeLine(shape, startPt, endPt);
                        startPt = endPt;
                        try {
                            CanvasStatus item = new CanvasStatus("drawing", clientName, mode, color, endPt, "");
                            server.UpdateCanvas(item);
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(null, "Canvas server is down.");
                        }
                    } else if (mode.equals("eraser")) {
                        shape = makeLine(shape, startPt, endPt);
                        startPt = endPt;
                        graphics.setPaint(Color.white);
                        graphics.setStroke(new BasicStroke(30.0f));
                        try {
                            CanvasStatus message = new CanvasStatus("drawing", clientName, mode, Color.white, endPt, "");
                            server.UpdateCanvas(message);
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(null, "Canvas server is down.");
                        }
                    } else if (mode.equals("line")) {
                        //when drawing, draw the previous image then add to it
                        graphics.setPaint(color);
                        drawPreviousCanvas();
                        shape = makeLine(shape, startPt, endPt);
                    } else if (mode.equals("rect")) {
                        graphics.setPaint(color);
                        drawPreviousCanvas();
                        shape = makeRect(shape, startPt, endPt);
                    } else if (mode.equals("circle")) {
                        graphics.setPaint(color);
                        drawPreviousCanvas();
                        shape = makeCircle(shape, startPt, endPt);
                    } else if (mode.equals("oval")) {
                        graphics.setPaint(color);
                        drawPreviousCanvas();
                        shape = makeOval(shape, startPt, endPt);
                    } else if (mode.equals("star")) {
                        graphics.setPaint(color);
                        drawPreviousCanvas();
                        shape = makeStar(shape, startPt, calculateDistance(startPt, endPt));
                    } else if (mode.equals("text")) {
                        graphics.setPaint(color);
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
               setModified(true);
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
                   } else if (mode.equals("oval")) {
                       shape = makeOval(shape, startPt, endPt);
                   } else if (mode.equals("star")) {
                       shape = makeStar(shape, startPt, calculateDistance(startPt,endPt));
                   }else if (mode.equals("text")) {
                       text = JOptionPane.showInputDialog("What text you want to add?");
                       if (text == null) {
                           text = "";
                       }
                       drawPreviousCanvas();
                       graphics.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                       graphics.drawString(text, endPt.x, endPt.y);
                       graphics.setStroke(new BasicStroke(1.8f));
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
                           CanvasStatus message = new CanvasStatus("end", clientName, mode, Color.white, endPt, text);
                           server.UpdateCanvas(message);
                       } catch (RemoteException el) {
                           JOptionPane.showMessageDialog(null, "Canvas server is down.");
                       }
                       graphics.setPaint(color);
                       graphics.setStroke(new BasicStroke(1.0f));
                   } else {
                       try {
                           CanvasStatus message = new CanvasStatus("end", clientName, mode, color, endPt, text);
                           server.UpdateCanvas(message);
                       } catch (RemoteException e1) {
                           JOptionPane.showMessageDialog(null, "Canvas server is down.");
                       }
                   }
               }
           }
       });
   }

    public int calculateDistance(Point startPt, Point endPt) {
        int xDifference = endPt.x - startPt.x;
        int yDifference = endPt.y - startPt.y;

        return (int) Math.sqrt(xDifference * xDifference + yDifference * yDifference);
    }

    //The method for painting the shape on the white board.
    // initialize the white board to synchronize with the manager's image when the client join the shared white board

    // When call the repaint() method, Swing automatically calls the paintComponent
    // method to repaint the component
    protected void paintComponent (Graphics g) {
        if (image == null) {
            if (isManager) {
                image = new BufferedImage(950, 550, BufferedImage.TYPE_INT_RGB);
                graphics = (Graphics2D) image.getGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                reset();
            } else {
                try {
                    byte[] rawImage = server.getManagerImage();
                    image = ImageIO.read(new ByteArrayInputStream(rawImage));
                    graphics = (Graphics2D) image.getGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setPaint(getCurrColor());
                } catch (IOException e) {
                    System.err.println("Fail receiving image!");
                }
            }
        }
        g.drawImage(image, 0, 0, null);
    }
    public Color getCurrColor() {
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
    public RenderedImage
    saveCanvas () {
        ColorModel cm = image.getColorModel();
        WritableRaster raster = image.copyData(null);
        //Copy to previousCanvas so user can continue editing after saving the image without affecting the saved image
        previousCanvas = new BufferedImage(cm, raster, false, null);
        return previousCanvas;
    }
    //cover the current canvas with previous canvas states
    public void drawPreviousCanvas () {
        drawImage(previousCanvas) ;
    }
    public void drawImage (BufferedImage img) {
        graphics.drawImage(img, null, 0, 0);
        repaint();
    }
    public void setBlue() {
        this.color = Color.blue;
        graphics.setPaint(color);
    }
    public void setCyan() {
        this.color = Color.cyan;
        graphics.setPaint (color);
    }
    public void setMagenta() {
        this. color = Color.magenta;
        graphics.setPaint (color);
    }
    public void setGreen() {
        color = Color.green;
        graphics.setPaint(color);
    }
    public void setYellow() {
        color = Color.yellow;
        graphics.setPaint(color);
    }
    public void setOrange() {
        color = Color.orange;
        graphics.setPaint(color);
    }
    public void setPink () {
        color = Color.pink;
        graphics.setPaint (color);
    }
    public void setRed() {
        color = Color.red;
        graphics.setPaint(color);
    }
    public void setBlack() {
        color = Color.black;
        graphics.setPaint(color);
    }
    public void setDarkGray() {
        color = Color.darkGray;
        graphics.setPaint(color);
    }
    public void setWhite() {
        color = Color.white;
        graphics.setPaint (color);
    }
    public void setGray() {
        color = Color.gray;
        graphics.setPaint (color);
    }
    public void setLightGray() {
        color = Color.lightGray;
        graphics.setPaint (color);
    }
    public void setBrown() {
        color = new Color(153,76,0);
        graphics.setPaint(color);
    }
    public void setPurple() {
        color = new Color(128, 0, 128);
        graphics.setPaint(color);
    }
    public void setDarkBlue() {
        color = new Color(0, 0, 139);
        graphics.setPaint(color);
    }
    public void draw() {
        mode = "draw";
    }
    public void setLineMode() {
        mode = "line";
    }
    public void setRectMode() {
        mode = "rect";
    }
    public void setCircleMode() {
        mode = "circle";
    }
    public void setOvalMode() {
        mode = "oval";
    }

    public void setStarMode() {
       mode = "star";
    }

    public void setTextMode() {
        mode = "text";
    }

    public void setEraserMode() {
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
        shape = new Rectangle2D.Double(x, y, width, height);
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
    //draw star
    public Shape makeStar(Shape shape, Point center, int radius) {
        // The number of points of the star
        int numPoints = 5;

        // The angle between each point in radians
        double angle = Math.PI * 2 / numPoints;

        // The coordinates of the points
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];

        // Calculate the coordinates of each point
        for (int i = 0; i < numPoints; i++) {
            xPoints[i] = center.x + (int) (Math.sin(i * angle) * radius);
            yPoints[i] = center.y - (int) (Math.cos(i * angle) * radius);
        }

        // Connect every second point
        int[] xStarPoints = new int[numPoints];
        int[] yStarPoints = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            xStarPoints[i] = xPoints[(2 * i) % numPoints];
            yStarPoints[i] = yPoints[(2 * i) % numPoints];
        }

        shape = new Polygon(xStarPoints, yStarPoints, numPoints);
        return shape;
    }

    //draw oval
    public Shape makeOval(Shape shape, Point startPt, Point endPt) {
        int x = Math.min(startPt.x, endPt.x);
        int y = Math.min(startPt.y, endPt.y);
        int width = Math.abs(startPt.x - endPt.x);
        int height = Math.abs(startPt.y - endPt.y);
        shape = new Ellipse2D.Double(x, y, width, height);
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
        // After opening the image, the graphics object does not reinitialize when the image is not null
        // due to a condition in paintComponent method. So, Initialize the graphics object

        this.image = openedImage;
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setPaint(color);

        // revalidate the component to indicate that the preferred size might have changed
        revalidate();

        // When calling the repaint() method, Swing automatically calls the paintComponent
        // method to repaint the component
        repaint();
    }




    public void cleanAll() {

       reset();
    }


    public boolean askClean() {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to open a new " +
                "canvas? The previous canvas will be cleared. Please save first if you modified!", "Confirmation"
                , JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }
}

