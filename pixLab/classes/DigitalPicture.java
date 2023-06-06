import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Interface to describe a digital picture.  A digital picture can have an 
 * associated file name.  It can have a title.  It has pixels 
 * associated with it and you can get and set the pixels.  You 
 * can get an Image from a picture or a BufferedImage.  You can load
 * it from a file name or image.  You can show a picture.  You can 
 * explore a picture.  You can create a new image for it.
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public interface DigitalPicture {
    public String getFileName(); // Get the file name that the picture came from
    public String getTitle(); // Get the title of the picture
    public void setTitle( String title ); // Set the title of the picture
    public int getWidth(); // Get the width of the picture in pixels
    public int getHeight(); // Get the height of the picture in pixels
    public Image getImage(); // Get the image from the picture
    public BufferedImage getBufferedImage(); // Get the buffered image
    public int getBasicPixel( int x, int y ); // Get the pixel information as an int   
    public void setBasicPixel( int x, int y, int rgb ); // Set the pixel information
    public Pixel getPixel( int x, int y ); // Get the pixel information as an object
    public Pixel[] getPixels(); // Get all pixels in row-major order
    public Pixel[][] getPixels2D(); // Get 2-D array of pixels in row-major order
    public void load( Image image ); // Load the image into the picture
    public boolean load( String fileName ); // Load the picture from a file
    public void show(); // Show the picture 
    public JFrame explore(); // Explore the picture
    public boolean write( String fileName ); // Write out a file
}