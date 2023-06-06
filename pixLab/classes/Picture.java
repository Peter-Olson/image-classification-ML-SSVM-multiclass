import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.List; //Resolves problem with java.awt.List and java.util.List

/**
 * A class that represents a picture. This class inherits from 
 * SimplePicture and has different image processing methods, including advanced edge
 * detection methods. See below for a list of the current image processing functionality:
 * 
 * - addGrid(...) -> Add gridlines to the pic
 * - allToColor(...) -> All pixels changed to a single color in a given section
 * - clearIslands...(...) -> [BW] See the Islands class
 * - copy(...) -> Copy pixels from a different picture to this one
 * - copyTo(...) -> Copy pixels from this Picture to a different Picture
 * - defuzz(...) -> [BW] Remove 'fuzziness' in the black and white Picture
 * - drawPixels(...) -> Draw a new section of Pixels onto the Picture
 * - drawStars(...) -> Draw stars onto the Picture
 * - drawString(...) -> [SimplePicture] Draw a String on the Picture
 * - edgeDetection(...) -> Find the edges of an image
 * - fillIslands(...) -> [BW] Convert all non-islands to islands [W -> B]
 * - filterRed(...) -> Highlight the red pixels in the Picture
 * - filterGreen(...) -> Highlight the green pixels in the Picture
 * - filterBlue(...) -> Highlight the blue pixels in the Picture
 * - findDifferences(...) -> Create a new Picture showing the differences between
 *                           two images
 * - gaussianBlur(...) -> Produce a blurring effect on the Picture
 * - grayscale(...) -> Convert the Picture into a black and white image (spectrum)
 * - linearize(...) -> [BW] Edge detection method using edge detection and
 *                     post-processing
 * - makeOpaque(...) -> Make the given image opaque
 * - minimizeColors(...) -> Filter the picture to follow a color scheme
 * - minimizeColorsAndSaturate(...) -> Filter the picture to follow a color scheme
 *                                     and modulate for narrow color schemed pictures
 *                                     or narrow color schemes
 * - mirrorHorizontal(...) -> Flip the picture horizontally (mirrored)
 * - mirrorVertical(...) -> Flip the picture vertically (mirrored)
 * - outlineSubject(...) -> Outline the subject of a Picture and crop the image
 * - pixelate(...) -> Pixelate the picture
 * - scale(...) -> [SimplePicture] Scale the picture in the x and y direction
 * - splitIntoGrid(...) -> Split the picture into multiple pictures based on a grid
 * - subPicture(...) -> Save a smaller region of the picture as a new picture
 * - toBW(...) -> Convert the picture to be only black pixels or white pixels
 * - zeroRed(...) -> Removes all red hues from the picture
 * - zeroGreen(...) -> Removes all green hues from the picture
 * - zeroBlue(...) -> Removes all blue hues from the picture
 * 
 * Methods to implement (not including submethods of the outlineSubject(...) method)
 * - colorCatToCat( Color fromColor, Color toColor ) -> color-shift all colors of the
 *      given Color category to the new Color category, on a weighted basis
 * - gradient(...) -> apply a gradient hue color-shift on a vector-based direction.
 *      Can also have gradients applied in circular directions
 * - fade(...) -> apply a transparency fade based on a rectangular or circular
 *      direction
 * - backgroundFade(...) -> fade out a background using transparency, relying on the
 *      outlineSubject(...) method
 * - groupColors(...) -> color-match pixels based on neighboring pixel color averages
 * - desaturate(...) -> desaturate the picture by a given amount
 * - saturate(...) -> saturate the picture by a given amount
 * - upcontrast(...) -> increase the contrast of the image
 * - decontrast(...) -> decrease the contrast of the image
 * - *for multiple methods* -> add overloaded methods that support applications to
 *      arrays of points (Pixels) in the image
 * - getObjectSubsets(...) -> find objects in the image based on outlines and edge
 *      detection techniques and return them in a list of list of Pixels (which have
 *      coordinates)
 * - hardenEdges(...) -> harden edges (perceived) of an image
 * - softenEdges(...) -> soften edges (perceived) of an image
 * - getTextFromImage(...) -> [ML] Using machine learning to find text in an image
 *      using alphabetic weights, generated from the non-binary image classification
 *      ML program, and returns the text as a list of Strings
 * 
 * @author Peter Olson mrpeterfolson@gmail.com, Barbara Ericson ericson@cc.gatech.edu
 */
public class Picture extends SimplePicture {
    /* @@@@@@@@@@@@@@@ CONSTRUCTORS @@@@@@@@@@@@@@@ */

    /**
     * Constructor that takes a file name and creates the picture
     * 
     * @param fileName The name of the file to create the picture from
     */
    public Picture( String fileName ) {
        super( fileName );
    }

    /**
     * Constructor that takes the width and height
     * 
     * @param width The width of the desired picture
     * @param height The height of the desired picture
     */
    public Picture( int width, int height ) {
        super( width, height );
    }

    /**
     * Constructor that takes a picture and creates a 
     * copy of that picture
     * 
     * @param copyPicture The picture to copy
     */
    public Picture( Picture copyPicture ) {
        super( copyPicture );
    }

    /**
     * Constructor that takes a BufferedImage
     * 
     * @param image The BufferedImage to use
     */
    public Picture( BufferedImage image ) {
        super( image );
    }

    /* @@@@@@@@@@@@@@@ ENUMS @@@@@@@@@@@@@@@ */
    public enum Blur { MILD, MEDIUM, STRONG }
    
    public enum Direction { UP, LEFT, DOWN, RIGHT }
    
    /* @@@@@@@@@@@@@@@ METHODS @@@@@@@@@@@@@@@ */

    /**
     * Method to return a String with information about this picture.
     * 
     * @return String A String with information about the picture such as fileName,
     *         width and height
     */
    public String toString() {
        return "File name: " + getFileName() + 
               ", width: " + getWidth() + ", height: " + getHeight();
    }
    
    /**
     * Method to add a grid to the image
     * 
     * @param interval Grid line separation, in pixels
     * @param color The color of the lines
     */
    public void addGrid( int interval, Color color ) {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = interval; row < pixels.length; row += interval )
            for( int col = 0; col < pixels[0].length; col++ )
                pixels[row][col].setColor( color );
        
        for( int col = interval; col < pixels[0].length; col += interval )
            for( int row = 0; row < pixels.length; row++ )
                pixels[row][col].setColor( color );
    }
    
    /**Method that paints this Picture to be all one color*/
    public void allToColor( Color color ) { allToColor( this.getPixels2D(), color ); }
    /**
     * Method that paints the pixel section to be all one color
     * 
     * @param color The color to paint this Picture
     */
    public void allToColor( Pixel[][] section, Color color ) {
        for( int row = 0; row < section.length; row++ )
            for( int col = 0; col < section[0].length; col++ )
                section[row][col].setColor( color );
    }
    
    /**
     * Method that gets whether this pixel has a black neighbor in their upper-left diagonal
     * 
     * @@NOTE: This method is ONLY for looking at subsections of a larger image, where the Pixel
     *         data does NOT match the subsection coordinates! See example in the linearize(...)
     *         method, where subsections are pathed, and thus each subsection has different
     *         coordinates that the larger Picture
     * 
     * @param xOffset The x offset from this pixel's location
     * @param yOffset The y offset from this pixel's location
     * @param row The row of the Pixel
     * @param col The col of the Pixel
     * @param pixels The image of pixels
     * @param origColor The color a pixel has that belongs to an island
     * @return boolean True if this neighbor exists, false otherwise
     */
    private boolean getNeighbor( int xOffset, int yOffset, int row, int col,
                                 Pixel[][] pixels, Color origColor ) {
        int xNewPix = row + xOffset;
        int yNewPix = col + yOffset;
        
        if( xNewPix < 0 || xNewPix >= pixels.length )
            return false;
        if( yNewPix < 0 || yNewPix >= pixels[0].length )
            return false;
        
        if( pixels[xNewPix][yNewPix].getColor().equals( origColor ) )
            return true;
        
        return false;
    }
    
    private class Neighbor {
        private int x, y;
        private boolean visited;
        
        public Neighbor( int x, int y, boolean visited ) {
            this.x = x;
            this.y = y;
            this.visited = visited;
        }
        
        public boolean isEqual( Neighbor o ) {
            if( o == null )                       return false;
            if( o.getClass() != this.getClass() ) return false;
            if( o.getX() != this.getX() )         return false;
            if( o.getY() != this.getY() )         return false;
            
            return true;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public boolean isVisited() { return visited; }
        public void setVisited( boolean visited ) { this.visited = visited; }
    }
    
    private class ColorNeighbor extends Neighbor {
        private Color c;
        
        public ColorNeighbor( int x, int y, boolean visited, Color c ) {
            super(x, y, visited);
            this.c = c;
        }
        
        public Color getColor() { return c; }
        public void setColor( Color c ) { this.c = c; }
    }
    
    private class Pair {
        public Neighbor a, b;
        
        public Pair( Neighbor a, Neighbor b ) {
            this.a = a;
            this.b = b;
        }
    }
    
    /**
     * Method to copy pixels from the given Picture
     * to this Picture at the location specified
     * 
     * @param fromPic the picture to copy from
     * @param startRow the start row to copy to
     * @param startCol the start col to copy to
     */
    public void copy( Picture fromPic, int startRow, int startCol )
    { copy( fromPic.getPixels2D(), startRow, startCol ); }
    /**
     * Method to copy pixels from a 2D array of Pixels to the current Picture
     * 
     * @param pixels The pixels to copy
     * @param startRow The row to start copying at within this picture
     * @param startcol The col to start copying at within this picture
     */
    public void copy( Pixel[][] fromPixels, int startRow, int startCol ) {
        Pixel fromPixel = null;
        Pixel toPixel = null;
        Pixel[][] toPixels = this.getPixels2D();
        for( int fromRow = 0, toRow = startRow; 
             fromRow < fromPixels.length && toRow < toPixels.length; 
             fromRow++, toRow++ ) {
            for( int fromCol = 0, toCol = startCol; 
                 fromCol < fromPixels[0].length && toCol < toPixels[0].length;  
                 fromCol++, toCol++ ) {
                fromPixel = fromPixels[fromRow][fromCol];
                toPixel = toPixels[toRow][toCol];
                //if( fromPixel.getAlpha() != 0 )
                toPixel.setColorKeepAlpha(fromPixel.getColor()); //Used to be setColor(...)
            }
        }
    }

    /**
     * Method to copy pixels from this Picture to another
     * 
     * This method will copy pixels from this Picture starting from
     * the startingRow and startingCol position, and go until the
     * new Picture is filled or until the bounds of this Picture is
     * reached
     * 
     * @param toPicture The picture being copied to
     * @param startRow The row being started at
     * @param startCol The column being started at
     */
    public void copyTo( Picture toPicture, int startRow, int startCol ) {
        Pixel[][] pixels = this.getPixels2D();
        Pixel[][] toPixels = toPicture.getPixels2D();
        int width = pixels.length > toPixels.length ? toPixels.length : pixels.length;
        int height = pixels[0].length > toPixels[0].length ? toPixels[0].length
                                                           : pixels[0].length;
        copyTo( subarray( pixels, startRow, startCol, width, height ), toPicture );
    }
    /**
     * Method to copy pixels from this Picture to another
     * 
     * This method works best when pixels and toPicture are the
     * same dimensions. It also works if toPicture is larger than
     * pixels
     * 
     * @param pixels The grid of this Picture's pixels
     * @param toPicture The Picture to copy to
     */
    public void copyTo( Pixel[][] pixels, Picture toPicture ) {
        Pixel thisPixel = null;
        Pixel toPixel = null;
        Pixel[][] toPixels = toPicture.getPixels2D();
        
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                thisPixel = pixels[row][col];
                toPixel = toPixels[row][col];
                toPixel.setColor( thisPixel.getColor() );
            }
        }
    }
    
    /**
     * Method to create a collage of several pictures
     */
    private void createCollage() {
        Picture flower1 = new Picture("flower1.jpg");
        Picture flower2 = new Picture("flower2.jpg");
        this.copy( flower1, 0, 0 );
        this.copy( flower2, 100, 0 );
        this.copy( flower1, 200, 0 );
        Picture flowerNoBlue = new Picture( flower2 );
        flowerNoBlue.zeroBlue();
        this.copy( flowerNoBlue, 300, 0 );
        this.copy( flower1, 400, 0 );
        this.copy( flower2, 500, 0);
        this.mirrorVertical();
        this.write("collage.jpg");
    }

    /**@@For B/W pictures:@@*/
    public void defuzz( int sectionWidth )                { defuzz( sectionWidth, 0, 0, 1 ); }
    public void defuzz( int sectionWidth, int neighbors ) { defuzz( sectionWidth, 0, 0, neighbors ); }
    /**
     * [B/W] - Method to remove 'fuzz' from pixels. Fuzz is defined as any
     * black squares that have x or less neighbors, where x is the parameter neighbors
     * 
     * @param sectionWidth The width of the section being defuzzed
     * @param offsetX The starting offset in the x direction
     * @param offsetY The starting offset in the y direction
     * @param neighbors Pixels with this many or less neighbors that are Color.BLACK
     *                  will be changed to be Color.WHITE
     */
    public void defuzz( int sectionWidth, int offsetX, int offsetY, int neighbors ) {
        Pixel[][] pixels = this.getPixels2D();
        boolean[][] coordsToRemove = new boolean[ pixels.length ][ pixels[0].length ];
        for( int row = 0 + offsetX; row < pixels.length; row += sectionWidth ) {
            for( int col = 0 + offsetY; col < pixels[0].length; col += sectionWidth ) {
                //Last scopes can be smaller that edgeDistance x edgeDistance
                int rowLimit = row + sectionWidth;
                int colLimit = col + sectionWidth;
                if( row + sectionWidth > pixels.length )
                    rowLimit = pixels.length;
                if( col + sectionWidth > pixels[0].length )
                    colLimit = pixels[0].length;
                
                for( int rowScope = row; rowScope < rowLimit; rowScope++ ) {
                    for( int colScope = col; colScope < colLimit; colScope++ ) {
                        Pixel pix = pixels[rowScope][colScope];
                        if( rowScope > 0 && colScope > 0 &&
                            rowScope < rowLimit - 1 && colScope < colLimit - 1 &&
                            !pix.getColor().equals( Color.WHITE ) &&
                            totalNeighbors( rowScope, colScope, pixels, Color.BLACK ) <= neighbors ) {
                            coordsToRemove[rowScope][colScope] = true;
                        }
                    }
                }
            }
        }
        
        //Set fuzz pixels to white
        for( int row = 0; row < pixels.length; row++ )
            for( int col = 0; col < pixels[0].length; col++ )
                if( coordsToRemove[row][col] )
                    pixels[row][col].setColor( Color.WHITE );
    }
    
    /** @@For B/W Pictures:@@*/
    public void superDefuzz( int sectionWidth ) { superDefuzz( sectionWidth, 1 ); }
    /**
     * [B/W] - Method that calls the defuzz three different times,
     * in order to cover all edge all edges of scope passes
     * 
     * @param sectionWidth The width of the section being defuzzed
     * @param neighbors Pixels with this many or less neighbors that are Color.BLACK
     *                  will be changed to be Color.WHITE
     */
    public void superDefuzz( int sectionWidth, int neighbors ) {
        int offset = sectionWidth / 2;
        defuzz( sectionWidth, offset, 0, neighbors );
        defuzz( sectionWidth, 0, offset, neighbors );
        defuzz( sectionWidth, offset, offset, neighbors );
    }
    
    /**
     * [B/W] - Method that determines the total neighbors of a pixel
     * 
     * @param x The x coordinate of the pixel
     * @param y The y coordinate of the pixel
     * @param pixels The 2D array of pixels of the picture
     * @param origColor The color of the neighbors
     * @return int The total neighbors of this pixel
     */
    private int totalNeighbors( int x, int y, Pixel[][] pixels, Color origColor ) {
        int neighborCount = 0;
        
        int marginX = 0;
        int marginY = 0;
        int j = 0;
        int k = 0;
        if( x == 0 ) {
            ++x;
            ++j;
        } else if( x == pixels.length - 1 )
            marginX = 1;
        
        if( y == 0 ) {
            ++y;
            ++k;
        } else if( y == pixels[0].length - 1 )
            marginY = 1;

        int kReset = k;
        
        for( int startX = x - 1; startX < x - 1 + 3 - marginX && j < 3; startX++ ) {
            for( int startY = y - 1; startY < y - 1 + 3 - marginY && k < 3; startY++ ) {
                if( j == 1 && k == 1 ) {
                    ++k;
                    continue;
                }
                
                Pixel pix = pixels[startX][startY];
                if( pix.getColor().equals( origColor ) )
                    neighborCount++;
                
                if( startY + 1 == y - 1 + 3 - marginY )
                    k = 0;
                else
                    ++k;
            }
            
            k = kReset;
            if( startX + 1 == x - 1 + 3 - marginX )
                j = 0;
            else
                ++j;
        }
        
        return neighborCount;
    }
    
    /** Method to draw a list of pixels on this image */
    public void drawPixels( ArrayList<ColorNeighbor> pixelList )
    { drawPixels( this.getPixels2D(), pixelList, 1 ); }
    /** Method to draw a list of pixels on this image */
    public void drawPixels( ArrayList<ColorNeighbor> pixelList, int lineThickness )
    { drawPixels( this.getPixels2D(), pixelList, lineThickness ); }
    /** Method to draw a list of pixels on a section of an image */
    public void drawPixels( Pixel[][] section, ArrayList<ColorNeighbor> pixelList )
    { drawPixels( section, pixelList, 1 ); }
    /**
     * Method to draw a list of pixels on a section of an image
     * 
     * @param section The section to draw the pixels to
     * @param pixelList The list of pixels to draw
     * @param lineThickness The thickness of the line 
     */
    public void drawPixels( Pixel[][] section, ArrayList<ColorNeighbor> pixelList,
                            int lineThickness ) {
        int size = pixelList.size();
        for( int rep = 0; rep < size; rep++ ) {
            int row = pixelList.get(rep).getX();
            int col = pixelList.get(rep).getY();
            Color c = pixelList.get(rep).getColor();
            int counter = 0;
            boolean isHorizontal = isHorizontalPath( pixelList );
            while( counter++ < lineThickness ) {
                if( isHorizontal )
                    section[row++][col].setColor( c );
                else
                    section[row][col++].setColor( c );
            }
        }
    }
    
    /**
     * Method that determines which direction the path is moving
     * 
     * @param path The path of pixels
     * @return boolean True if the path is horizontal, false otherwise
     */
    private boolean isHorizontalPath( ArrayList<ColorNeighbor> path ) {
        int size = path.size();
        return path.get( size - 1 ).getY() - path.get(0).getY()
               >
               path.get( size - 1 ).getX() - path.get(0).getX()
               ? true : false;
    }
    
    /**
     * Method that draws a series of star images on top of this Picture at all of the Points
     * within a List
     * 
     * @param list The list of Point coordinates to draw the images at
     * @see little_star.png
     * @see copy( Picture fromPicture, int startRow, int startCol )
     */
    public void drawStars( List<java.awt.Point> list ) {
        Picture star = new Picture( "little_star.png" );
        for( java.awt.Point p : list )
            copy( star, p.x, p.y );
    }
    
    /**
     * Method to detect edges in images by converting pixels to black or white. This method
     * finds edges by looking for large changes in color
     * 
     * @param edgeDist the distance for finding edges
     */
    public void edgeDetection( int edgeDist ) {
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        Pixel[][] pixels = this.getPixels2D();
        Color rightColor = null;
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length - 1; col++ ) {
                leftPixel = pixels[row][col];
                rightPixel = pixels[row][col+1];
                rightColor = rightPixel.getColor();
                if( leftPixel.colorDistance( rightColor ) > edgeDist )
                    leftPixel.setColor( Color.BLACK );
                else
                    leftPixel.setColor( Color.WHITE );
            }
        }
    }
    
    /**
     * Method to bring out the red colors and reduce the green and blue colors
     */
    public void filterRed() {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                Pixel pixelObj = pixels[row][col];

                int red = pixelObj.getRed();
                int green = pixelObj.getGreen();
                int blue = pixelObj.getBlue();

                pixelObj.setRed( green | blue );
                pixelObj.setGreen( green & red );
                pixelObj.setBlue( blue & red );
            }
        }
    }

    /**
     * Method to bring out the green colors and reduce the red and blue colors
     */
    public void filterGreen() {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                Pixel pixelObj = pixels[row][col];

                int red = pixelObj.getRed();
                int green = pixelObj.getGreen();
                int blue = pixelObj.getBlue();

                pixelObj.setRed( red & green );
                pixelObj.setGreen( red | blue );
                pixelObj.setBlue( blue & green );
            }
        }
    }

    /**
     * Method to bring out the blue colors and reduce the red and green colors
     */
    public void filterBlue() {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                Pixel pixelObj = pixels[row][col];

                int red = pixelObj.getRed();
                int green = pixelObj.getGreen();
                int blue = pixelObj.getBlue();

                pixelObj.setRed( red & blue );
                pixelObj.setGreen( green & blue );
                pixelObj.setBlue( red | green );
            }
        }
    }

    /**
     * [B/W] - Method that compares two pictures and saves an image of their differences
     * 
     * @param second The second pic
     */
    public void findDifferences( Picture second ) {
        Picture finalPic = new Picture( this.getHeight(), this.getWidth() );
        
        Pixel[][] firstPixels = this.getPixels2D();
        Pixel[][] secondPixels = second.getPixels2D();
        Pixel[][] finalPixels = finalPic.getPixels2D();
        for( int row = 0; row < firstPixels.length; row++ ) {
            for( int col = 0; col < firstPixels[0].length; col++ ) {
                Pixel firstPix = firstPixels[row][col];
                Pixel secondPix = secondPixels[row][col];
                if( !firstPix.getColor().equals( secondPix.getColor() ) ) {
                    double firstPixelDifference = firstPix.colorDistanceAdvanced( Color.BLACK );
                    double secondPixelDifference = secondPix.colorDistanceAdvanced( Color.BLACK );
                    if( firstPixelDifference > secondPixelDifference ) //second is darker
                        finalPixels[row][col].setColor( secondPix.getColor() );
                    else
                        finalPixels[row][col].setColor( firstPix.getColor() );
                }
            }
        }
        
        String name = this.getFileName();
        int extensionIndex = name.indexOf(".");
        finalPic.write(
            name.substring( 0, extensionIndex ) +
            "_difference" +
            name.substring( extensionIndex )
        );
    }
    
    /**
     * Method that applies a Gaussian blur effect to the image.
     * The effect can apply mild, medium, or strong blur effects.
     * 
     * @param strength The strength of the blur. Either Blur.MILD, Blur.MEDIUM, or
     *                 Blur.STRONG
     */
    public void gaussianBlur( Blur strength ) {
        int[][] kernel;
        int[] kReductionEdgeOffset;
        int KREDUCTION = 16;
        if( strength == Blur.MILD ) {
            kernel = new int[][]{{1,2,1},//4
                                 {2,4,2},//8
                                 {1,2,1}};//4
            kReductionEdgeOffset = new int[]{0, 8, (16-1)};
        } else if( strength == Blur.MEDIUM ) {
            kernel = new int[][]{{1,2,4,2,1},//10
                                 {2,4,6,4,2},//18
                                 {4,6,8,6,4},//28
                                 {2,4,6,4,2},//18
                                 {1,2,4,2,1}};//10
            kReductionEdgeOffset = new int[]{10, 20, 41, 62, (84-1)};
            KREDUCTION = 84;
        } else {
            kernel = new int[][]{{1, 2, 4, 6, 8, 6, 4, 2,1},//34
                                 {2, 4, 6, 8,10, 8, 6, 4,2},//50
                                 {4, 6, 8,10,12,10, 8, 6,4},//68
                                 {6, 8,10,12,14,12,10, 8,6},//86
                                 {8,10,12,14,16,14,12,10,8},//104
                                 {6, 8,10,12,14,12,10, 8,6},//86
                                 {4, 6, 8,10,12,10, 8, 6,4},//68
                                 {2, 4, 6, 8,10, 8, 6, 4,2},//50
                                 {1, 2, 4, 6, 8, 6, 4, 2,1}};//34
            kReductionEdgeOffset = new int[]{15, 60, 120, 195, 270, 355, 430, 505, (580-1)};
            KREDUCTION = 580;
        }
        int KSIZE = kernel.length;
        
        Pixel[][] pixels = this.getPixels2D();
        
        final int WIDTH = pixels.length;
        final int HEIGHT = pixels[0].length;
        
        for ( int row = 0; row < WIDTH; row++ ) {
            for ( int col = 0; col < HEIGHT; col++ ) {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;
                for( int kRow = 0; kRow < KSIZE; kRow++ ) {
                    for( int kCol = 0; kCol < KSIZE; kCol++ ) {
                        int rowLimit = row + kRow /*- 1*/;
                        int colLimit = col + kCol /*- 1*/;
                        if( rowLimit >= 0 &&
                            colLimit >= 0 &&
                            rowLimit < WIDTH &&
                            colLimit < HEIGHT ) {            
                            int red   = pixels[ rowLimit ][ colLimit ].getRed();
                            int green = pixels[ rowLimit ][ colLimit ].getGreen();
                            int blue  = pixels[ rowLimit ][ colLimit ].getBlue();
                            totalRed   += kernel[ kRow ][ kCol ] * red;
                            totalGreen += kernel[ kRow ][ kCol ] * green;
                            totalBlue  += kernel[ kRow ][ kCol ] * blue;
                        }
                    }
                }
                
                int kReductionEdgeOffsetRowValue = 0,
                    kReductionEdgeOffsetColValue = 0,
                    kReductionOffsetValue = 0;
                if( WIDTH - row < kReductionEdgeOffset.length )
                    kReductionEdgeOffsetRowValue =
                        kReductionEdgeOffset[ kReductionEdgeOffset.length - (WIDTH - row + 1) ];
                if( HEIGHT - col < kReductionEdgeOffset.length )
                    kReductionEdgeOffsetColValue =
                        kReductionEdgeOffset[ kReductionEdgeOffset.length - (HEIGHT - col + 1) ];
                
                kReductionOffsetValue =
                    Math.abs(kReductionEdgeOffsetRowValue)
                    >
                    Math.abs(kReductionEdgeOffsetColValue) ? kReductionEdgeOffsetRowValue
                                                           : kReductionEdgeOffsetColValue;
                
               
                totalRed   /= (KREDUCTION - kReductionOffsetValue);
                totalGreen /= (KREDUCTION - kReductionOffsetValue);
                totalBlue  /= (KREDUCTION - kReductionOffsetValue);
                
                pixels[ row ][ col ].updatePicture(
                    pixels[row][col].getAlpha(), totalRed, totalGreen, totalBlue );
            }
        }
    }
    
    /**
     * Method that grayscales an image
     */
    public void grayscale() {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                Pixel pix = pixels[row][col];
                int rgb = pix.getRGBValue();
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                //Normalize and gamma correct
                float rr = (float)Math.pow( r / 255.0, 2.2 );
                float gg = (float)Math.pow( g / 255.0, 2.2 );
                float bb = (float)Math.pow( b / 255.0, 2.2 );

                //Calculate luminance
                float lum = (float)( 0.2126 * rr + 0.7152 * gg + 0.0722 * bb );

                //Gamma compand and rescale to byte range
                int grayLevel = (int)( 255.0 * Math.pow( lum, 1.0 / 2.2 ) );
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
                pix.setRGBValue( gray );
            }
        }   
    }

    /**
     * [B/W] - Method that converts sections of pixels into their linear direction.
     * 
     * It is recommended that you use the following methods on your B/W image
     * before running this function, in the following order:
     *    1) edgeDetection(...)
     *    2) superDefuzz(...)
     *    3) Islands.clearIslands...(...)
     *    4) fillIslands(...)
     * 
     * After this method, it is recommended that you run the followings method(s):
     *    1) thin(...) (I haven't written this yet. It would remove all black sections
     *                 (if they exist--see setPath method) and then connect existing paths
     *                 to one another, the goal of being that the end result would resemble
     *                 a better edge detection image than just running edgeDetection alone
     *                 
     * @param pathThickness The thickness of the path, in pixels
     * @param sectionThickness The thickness of the section for pathfinding, in pixels
     */
    public void linearize( int pathThickness, int sectionThickness ) {
        final int SECTION_WIDTH = pathThickness * sectionThickness;
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row += SECTION_WIDTH ) {
            for( int col = 0; col < pixels[0].length; col += SECTION_WIDTH ) {
                //Last scopes can be smaller that SECTION_WIDTH x SECTION_WIDTH
                int rowLimit = SECTION_WIDTH;
                int colLimit = SECTION_WIDTH;
                if( row + SECTION_WIDTH > pixels.length )
                    rowLimit = pixels.length - row;
                if( col + SECTION_WIDTH > pixels[0].length )
                    colLimit = pixels[0].length - col;
                
                Pixel[][] section = subarray( pixels, row, col, rowLimit, colLimit );
                
                setPath( section, pathThickness );
                
                this.copy( section, row, col );
            }
        }
    }
    
    /**
     * [B/W] - Method to test whether a small picture (the size of a section) works correctly
     * using the setPath(...) method
     */
    private void testSetPath() {
        Pixel[][] pixels = this.getPixels2D();
        ArrayList<ColorNeighbor> path = getPath( pixels );
        printPath( path, pixels );
        //setPath( pixels, 1 );
    }
    
    /**
     * Method used to print a path of Neighbors
     * 
     * @param path The path of neighbor objects
     * @param pixels The pixel array of this picture
     */
    private void printPath( ArrayList<ColorNeighbor> path, Pixel[][] pixels ) {
        boolean isValidPath      = true;
        boolean isHorizontalPath = false;
        boolean isVerticalPath   = false;
        
        if( path == null ) {
            System.out.println("There are both horizontal and vertical paths. This section will be filled.");
            return;
        }
        int size = path.size();
        if( size == 0 ) {
            System.out.println("There are no valid paths. This section will be cleared.");
            return;
        }
        
        System.out.println("Path:\n");
        
        for( int rep = 0; rep < size; rep++ ) {
            if( rep == 0 && size > 1 ) {
                if( path.get(0).getY() == 0 &&
                    path.get( size - 1 ).getY() == pixels[0].length - 1 )
                    isHorizontalPath = true;
                else if( path.get(0).getX() == 0 &&
                         path.get( size - 1 ).getX() == pixels.length - 1 )
                    isVerticalPath = true;
            }
            
            if( isHorizontalPath &&
                rep != size - 1 &&
                Math.abs( path.get(rep).getX() - path.get(rep+1).getX() ) > 1 )
                isValidPath = false;
            else if( isVerticalPath &&
                     rep != size - 1 &&
                     Math.abs( path.get(rep).getY() - path.get(rep+1).getY() ) > 1 )
                isValidPath = false;

            System.out.println("(" + path.get(rep).getX() + "," + path.get(rep).getY() + ")");
        }
        
        if(      isHorizontalPath ) System.out.println("\nThe path is horizontal.");
        else if( isVerticalPath   ) System.out.println("\nThe path is vertical.");
        else                        System.out.println("\nThe path is diagonal.");
        
        if( !isValidPath )          System.out.println("\nThe path is not valid.");
    }
    
    /**
     * Method to get a smaller 2D array from a 2D array
     * 
     * @param pixels The pixels of the given Picture
     * @param startingRow The starting row index for the subarray
     * @param startingCol The starting column index for the subarray
     * @param numRows The total number of rows of the subarray
     * @param numCols The total number of cols of the subarray
     */
    private Pixel[][] subarray( Pixel[][] pixels, int startingRow, int startingCol,
            int numRows, int numCols ) {
        Pixel[][] a = new Pixel[numRows][numCols];
        for( int rep = startingRow, row = 0; rep < startingRow + numRows; rep++, row++ )
            a[row] = Arrays.copyOfRange( pixels[rep], startingCol, startingCol + numCols );
        
        return a;
    }
    
    /**
     * [B/W] - Method that sets the path within a given sector.
     * This method will also expand the width of the path by the value
     * 'lineThickness'. For details on the algorithm, see 'getPath(...)'
     * 
     * @param section The 2D array of pixels
     * @param lineThickness The path thickness to be set
     */
    private void setPath( Pixel[][] section, int lineThickness ) {
        ArrayList<ColorNeighbor> path = getPath( section );
        
        if( path != null )
            allToColor( section, Color.WHITE );
        /*@@CHANGE: Style preference. vv Experiment with changing this to
         *          Color.BLACK or Color.WHITE */
        else {
            allToColor( section, Color.BLACK );
            return;
        }
        
        if( path.size() != 0 )
            drawPixels( section, path, lineThickness );
    }

    /**
     * [B/W] - Method that returns a path of neighbors within a given sector.
     * This method tries to cross the width of the section over one nested loop, and the height
     * for the next. If there exists a path, note the center-most, smallest-ycoord black neighbors as
     * a path, or the center-most, smallest-xcoord black neighbors as a path.
     * 
     * Directionality is determined by the longest path, but a section having all four
     * edges pathed returns null, as does having no paths. Otherwise, there can be a
     * horizontal path, vertical path, or any of the four possible diagonal paths that
     * exist between adjacent edges. A path must have a greater distance than
     * MIN_PATH_LENGTH in order to be considered a path (paths can't simply clip
     * corners, otherwise they are considered not a path. Other sections should
     * pick up this path, so scrub this section of paths).
     * 
     * Examples:
     * 
     *    Starting Section        After method call (for setter, line width 1)
     * 
     *    oXXXo                   oooXo
     *    XXXoX           --->    XXXoX           Horizontal Path found
     *    XXXXX                   ooooo
     *    ooooo                   ooooo
     *    ooooo                   ooooo
     *    
     *    
     *    XXXXo                   ooXoo
     *    oooXo           --->    oooXo           Vertical Path found
     *    oXXXo                   ooXoo
     *    XXXoo                   oXooo
     *    ooXoo                   ooXoo
     *    
     *    
     *    ooooo                   ooooo
     *    ooooo           --->    ooooo           Diagonal (Down, Right) Path found
     *    oooXX                   ooooX           Valid path since length > (MIN_PATH_LENGTH = 3)
     *    ooXXX                   ooXXo
     *    oXXXo                   oXooo
     *    
     *    
     *    oXXXo                   oXXoo                   XXXXX
     *    XXoXo           --->    XooXo           --->    XXXXX      Horizontal and Vertical path found.
     *    XooXX                   oooXX                   XXXXX      Null is returned; Section set to all black
     *    XXooX                   ooooX                   XXXXX
     *    oooXo                   oooXo                   XXXXX
     *    
     *    
     *    oooXX                   ooooo
     *    ooXoo           --->    ooooo           No horizontal, vertical, or diagonal paths found
     *    ooooX                   ooooo           Empty list returned; Section set to all white
     *    XXooX                   ooooo
     *    oXXoo                   ooooo
     * 
     * @@NOTE: This algorithm does not work for particular scenarios. Any section that is 'zigzaggy' has a change
     *         of failing. For instance, the following example will not find a valid path, despite there being one:
     *    
     *    ooooXoo                 ooooooo
     *    oooXooo                 ooooooo         No matter whether the left, middle, or right directions are
     *    ooXXooo         --->    ooooooo         prioritized, the algorithm will regardless get 'stuck' on
     *    ooXoXoo                 ooooooo         either nub-like branch on the left and right of the main
     *    ooooXXo                 ooooooo         path. Thus, the current algorithm can't be foolproof, unless
     *    oooXoXo                 ooooooo         a recursive type search is taken, such as 'Djikstra's' algo type
     *    ooXoooo                 ooooooo         solution. However, with such large amounts of data to process,
     *                                            a stack overflow error is almost certain
     * 
     * @param section The 2D array of pixels
     * @return ArrayList<Neighbor> The path of Neighbors that connect from one edge of the section to
     *                             another. Returns null if there is no path, or there are 
     */
    private ArrayList<ColorNeighbor> getPath( Pixel[][] section ) {
        boolean hasHorizontalPath         = false;
        boolean hasVerticalPath           = false;
        boolean hasDiagonalPathHorizontal = false;
        boolean hasDiagonalPathVertical   = false;

        ArrayList<ColorNeighbor> horizontalPathUpPriority     = new ArrayList<ColorNeighbor>();
        ArrayList<ColorNeighbor> horizontalPathMiddlePriority = new ArrayList<ColorNeighbor>();
        ArrayList<ColorNeighbor> horizontalPathDownPriority   = new ArrayList<ColorNeighbor>();
        ArrayList<ColorNeighbor> verticalPathLeftPriority     = new ArrayList<ColorNeighbor>();
        ArrayList<ColorNeighbor> verticalPathMiddlePriority   = new ArrayList<ColorNeighbor>();
        ArrayList<ColorNeighbor> verticalPathRightPriority    = new ArrayList<ColorNeighbor>();

        /* @@@@@ Get largest Horizontal path @@@@@ */
        int pathDecode1 = getHorizontalPath( section, horizontalPathUpPriority,    -1,  0,  1 );
        int pathDecode2 = getHorizontalPath( section, horizontalPathMiddlePriority, 0, -1,  1 );
        int pathDecode3 = getHorizontalPath( section, horizontalPathDownPriority,   1,  0, -1 );

        int upSize     = horizontalPathUpPriority.size();
        int middleSize = horizontalPathMiddlePriority.size();
        int downSize   = horizontalPathDownPriority.size();
        
        //Find biggest path
        ArrayList<ColorNeighbor> horizontalPath =
            upSize >= middleSize
            ? (upSize >= downSize ? horizontalPathUpPriority
                                  : (middleSize >= downSize ? horizontalPathMiddlePriority
                                                            : horizontalPathDownPriority))
            : (middleSize >= downSize ? horizontalPathMiddlePriority
                                      : (upSize >= downSize ? horizontalPathUpPriority
                                                            : horizontalPathDownPriority));
        
        //Find correct path booleans
        int pathSize = horizontalPath.size();
        if( pathSize == upSize ) {
            if(      pathDecode1 == 0 ) hasHorizontalPath         = true;
            else if( pathDecode1 == 1 ) hasDiagonalPathHorizontal = true;
        } else if( pathSize == middleSize ) {
            if(      pathDecode2 == 0 ) hasHorizontalPath         = true;
            else if( pathDecode2 == 1 ) hasDiagonalPathHorizontal = true;
        }
        // pathSize == downSize
        else {
            if(      pathDecode3 == 0 ) hasHorizontalPath         = true;
            else if( pathDecode3 == 1 ) hasDiagonalPathHorizontal = true;
        }
        
        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
        
        /* @@@@@ Get largest Vertical path @@@@@ */
        pathDecode1 = getVerticalPath( section, verticalPathLeftPriority,  -1,  0,  1 );
        pathDecode2 = getVerticalPath( section, verticalPathMiddlePriority, 0, -1,  1 );
        pathDecode3 = getVerticalPath( section, verticalPathRightPriority,  1,  0, -1 );

        int leftSize   = verticalPathLeftPriority.size();
            middleSize = verticalPathMiddlePriority.size();
        int rightSize  = verticalPathRightPriority.size();
        
        //Find biggest path
        ArrayList<ColorNeighbor> verticalPath =
            leftSize >= middleSize ? (leftSize >= rightSize ? verticalPathLeftPriority
                                                            : (middleSize >= rightSize ? verticalPathMiddlePriority
                                                                                       : verticalPathRightPriority))
                                   : (middleSize >= rightSize ? verticalPathMiddlePriority
                                                              : (leftSize >= rightSize ? verticalPathLeftPriority
                                                                                       : verticalPathRightPriority));
        
        //Find correct path booleans
        pathSize = verticalPath.size();
        if(      pathSize == leftSize ) {
            if(      pathDecode1 == 0 ) hasVerticalPath         = true;
            else if( pathDecode1 == 1 ) hasDiagonalPathVertical = true;
        } else if( pathSize == middleSize ) {
            if(      pathDecode2 == 0 ) hasVerticalPath         = true;
            else if( pathDecode2 == 1 ) hasDiagonalPathVertical = true;
        }
        // pathSize == rightSize
        else {
            if(      pathDecode3 == 0 ) hasVerticalPath         = true;
            else if( pathDecode3 == 1 ) hasDiagonalPathVertical = true;
        }
        
        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
        
        /*@@DEBUG
            printPath( horizontalPath, section );
            printPath( verticalPath,   section );
        //*/
        
        //Check for dominant path
        if( hasHorizontalPath && hasVerticalPath )
            return null; //null designates that this section should be painted black
        if( hasHorizontalPath )
            return horizontalPath;
        if( hasVerticalPath )
            return verticalPath;
        if( hasDiagonalPathHorizontal && hasDiagonalPathVertical )
            return horizontalPath.size() > verticalPath.size() ? horizontalPath
                                                               : verticalPath;
        if( hasDiagonalPathHorizontal )
            return horizontalPath;
        if( hasDiagonalPathVertical )
            return verticalPath;
        
        horizontalPath.clear();
        
        return horizontalPath; //no path exists
    }
    
    /**
     * [B/W] - Method that gets the horizontal path of a section
     * 
     * @param section The section of the Picture (composed of Pixels in a 2D array)
     * @param horizontalPath The list of Neighbors in this horizontal path
     * @param FIRST This determines which direction the path checks for first (either UP-RIGHT
     *              neighbor (-1), MIDDLE-RIGHT neighbor (0), or DOWN_RIGHT neighbor (1))
     * @param SECOND Which checks for second
     * @param THIRD Which checks for third
     * @return int 0 --> There is a horizontal path
     *             1 --> There is a diagonal horizontal path
     *             2 --> There are no horizontal or diagonal paths
     */
    private int getHorizontalPath(
            Pixel[][] section, ArrayList<ColorNeighbor> horizontalPath,
            int FIRST, int SECOND, int THIRD ) {
        int row = 0;
        int col = 0;
        
        boolean hasHorizontalPath = false;
        boolean hasDiagonalPathHorizontal = false;
        
        int sectionStartX = 0;
        final int MIN_PATH_LENGTH = 3;
        
        //Check for horizontal path at each row starting pixel
        for( ; row < section.length; row++ ) {
            int pathRow = row;
            boolean foundPath    = false;
            boolean downPathDead = false;
            
            for( col = 0; col < section[0].length ; col++ ) {
                if( !section[pathRow][col].getColor().equals( Color.BLACK ) )
                    break;
                
                foundPath = true;
                
                horizontalPath.add( new ColorNeighbor( pathRow, col, true, Color.BLACK ) );
                
                if( col == section[0].length - 1 )
                    break;
                
                //Set the x position of the first available neighbor
                if( getNeighbor( FIRST,  1, pathRow, col, section, Color.BLACK ) ) {
                    if(      FIRST == -1 ) pathRow--;
                    else if( FIRST ==  1 ) pathRow++;
                } else if( getNeighbor( SECOND, 1, pathRow, col, section, Color.BLACK ) ) {
                    if(      SECOND == -1 ) pathRow--;
                    else if( SECOND ==  1 ) pathRow++;
                } else if( getNeighbor( THIRD,  1, pathRow, col, section, Color.BLACK ) ) {
                    if(      THIRD == -1 ) pathRow--;
                    else if( THIRD ==  1 ) pathRow++;
                }
                //traverse down
                else if( !downPathDead &&
                           getNeighbor(  1, 0, pathRow, col, section, Color.BLACK ) ) {
                    pathRow++;
                    --col;
                }
                //traverse up
                else if( getNeighbor( -1, 0, pathRow, col, section, Color.BLACK ) ) {
                    downPathDead = true;
                    pathRow--;
                    --col;
                }
                //No paths on this column
                else
                    break;
                
                //See if a diagonal path exists
                if( !hasDiagonalPathHorizontal &&
                    (pathRow == sectionStartX || pathRow == sectionStartX + section.length - 1) &&
                    horizontalPath.size() > MIN_PATH_LENGTH )
                    hasDiagonalPathHorizontal = true;
            }
            
            if( !foundPath )
                continue;
            
            //Check if there is a valid horizontal path. If true, stop searching
            if( col == section[0].length - 1 ) {
                hasHorizontalPath = true;
                break;
            }
            else if( !hasDiagonalPathHorizontal ) {
                horizontalPath.clear();
            }
            else if( horizontalPath.size() > MIN_PATH_LENGTH ) {
                break;
            }
        }
        
        if( hasHorizontalPath )         return 0;
        if( hasDiagonalPathHorizontal ) return 1;
        
        return 2;
    }
    
    /**
     * [B/W] - Method that gets the vertical path of a section
     * 
     * @param section The section of the Picture (composed of Pixels in a 2D array)
     * @param verticalPath The list of Neighbors in this vertical path
     * @param FIRST This determines which direction the path checks for first
     *              (either DOWN-LEFT neighbor (-1), DOWN-MIDDLE neighbor (0),
     *              or DOWN_RIGHT neighbor (1))
     * @param SECOND Which checks for second
     * @param THIRD Which checks for third
     * @return int 0 --> There is a vertical path
     *             1 --> There is a diagonal vertical path
     *             2 --> There are no vertical or diagonal paths
     */
    private int getVerticalPath(
            Pixel[][] section, ArrayList<ColorNeighbor> verticalPath,
            int FIRST, int SECOND, int THIRD ) {
        int row = 0;
        int col = 0;
        
        boolean hasVerticalPath = false;
        boolean hasDiagonalPathVertical = false;
        
        int sectionStartY = 0;
        final int MIN_PATH_LENGTH = 3;
        
        //Check for vertical path at each col starting pixel
        for( ; col < section[0].length; col++ ) {
            int pathCol = col;
            boolean foundPath     = false;
            boolean rightPathDead = false;
            
            for( row = 0; row < section.length ; row++ ) {
                if( !section[row][pathCol].getColor().equals( Color.BLACK ) )
                    break;
                
                foundPath = true;
                
                verticalPath.add( new ColorNeighbor( row, pathCol, true, Color.BLACK ) );
                
                if( row == section.length - 1 )
                    break;
                
                //Set the x position of the first available neighbor
                if( getNeighbor( 1, FIRST,  row, pathCol, section, Color.BLACK ) ) {
                    if(      FIRST == -1 ) pathCol--;
                    else if( FIRST ==  1 ) pathCol++;
                } else if( getNeighbor( 1, SECOND, row, pathCol, section, Color.BLACK ) ) {
                    if(      SECOND == -1 ) pathCol--;
                    else if( SECOND ==  1 ) pathCol++;
                } else if( getNeighbor( 1, THIRD,  row, pathCol, section, Color.BLACK ) ) {
                    if(      THIRD == -1 ) pathCol--;
                    else if( THIRD ==  1 ) pathCol++;
                }
                //traverse right
                else if( !rightPathDead &&
                         getNeighbor(  0, 1, row, pathCol, section, Color.BLACK ) ) {
                    pathCol++;
                    --row;
                }
                //traverse left
                else if( getNeighbor( 0, -1, row, pathCol, section, Color.BLACK ) ) {
                    rightPathDead = true;
                    pathCol--;
                    --row;
                }
                //No paths on this column
                else
                    break;
                
                //See if a diagonal path exists
                if( !hasDiagonalPathVertical &&
                    (pathCol == sectionStartY || pathCol == sectionStartY + section[0].length - 1) &&
                    verticalPath.size() > MIN_PATH_LENGTH )
                    hasDiagonalPathVertical = true;
            }
            
            if( !foundPath )
                continue;
            
            //Check if there is a valid vertical path. If true, stop searching
            if( row == section.length - 1 ) {
                hasVerticalPath = true;
                break;
            } else if( !hasDiagonalPathVertical ) {
                verticalPath.clear();
            } else if( verticalPath.size() > MIN_PATH_LENGTH ) {
                break;
            }
        }
        
        if( hasVerticalPath )         return 0;
        if( hasDiagonalPathVertical ) return 1;
        
        return 2;
    }
    
    /**
     * Method that makes an image completely opaque
     */
    public void makeOpaque() {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ )
            for( int col = 0; col < pixels[0].length; col++ )
                pixels[row][col].setAlpha(255);
    }

    /**
     * Method that reduces color scheme to just Color.RED, Color.ORANGE, Color.YELLOW,
     * Color.GREEN, Color.CYAN, Color.BLUE, DARK_BLUE=(0,0,139), Color.MAGENTA,
     * PURPLE=(128,0,128), Color.PINK, BROWN=(102,51,0), Color.LIGHT_GRAY,
     * Color.GRAY, Color.DARK_GRAY, Color.BLACK, and Color.WHITE
     * 
     * Each pixel will be converted to a color in the list that it is 'closest to', which
     * is determined by a distance-based algorithm for the rgb values
     */
    public void minimizeColors() {
        final Color DARK_BLUE = new Color( 0,   0, 139 );
        final Color PURPLE    = new Color( 128, 0, 128 );
        final Color BROWN     = new Color( 102, 51,  0 );
        //The color gray tends to win over most blue colors,
        //so I have found it is better to remove these as options
        Color[] colors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
                           Color.CYAN, DARK_BLUE, Color.MAGENTA, PURPLE,
                           Color.PINK, BROWN, /*Color.LIGHT_GRAY, Color.GRAY,
                           Color.DARK_GRAY,*/ Color.BLACK, Color.WHITE };
        minimizeColors( colors );
    }
    /**
     * Minimizes the total color scheme used by converting colors to their closest neighboring
     * colors, in terms of RGB distance.
     * 
     * Each pixel will be converted to a color in the list that it is 'closest to', which
     * is determined by a distance-based algorithm for the rgb values
     * 
     * @param colors The list of colors to reduce down to, averaging other colors to colors
     *               that only exist in this list
     */
    public void minimizeColors( Color[] colors ) {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                int smallestDistanceIndex = 0;
                double smallestDistance = 255.0 * 3.0;
                Pixel pix = pixels[row][col];
                for( int rep = 0; rep < colors.length; rep++ ) {
                    double distance = pix.colorDistanceAdvanced( colors[rep] );
                    if( distance < smallestDistance ) {
                        smallestDistance = distance;
                        smallestDistanceIndex = rep;
                    }
                }
                    
                pix.setColorKeepAlpha( colors[ smallestDistanceIndex ] );
            }
        }
    }
    /**
     * Minimizes the total color scheme used by converting colors to their closest neighboring
     * colors, in terms of RGB distance. This method uses modulation for color differentiating,
     * which allows for more narrow color schemes to have a broader, more accurate color
     * conversion.
     * 
     * In essence, Pictures that have a much lighter set of Colors would find their closest
     * neighbor to the lighter colors in the palette being converted to. Under the
     * minimizeColors(...) method, this would lead to none of the darker hues being used, and
     * the differentiation across the Color scheme would be minimal. This method adjusts by
     * insuring a deeper gradient of Colors, saturating lights and darks more than the
     * original image, comparatively speaking, using the new Color scheme
     * 
     * Each pixel will be converted to a color in the list that it is 'closest to', which
     * is determined by a distance-based algorithm for the rgb values
     * 
     * @param colors The list of colors to reduce down to, averaging other colors to colors
     *               that only exist in this list
     */
    public void minimizeColorsAndSaturate( Color[] colors ) {
        //Sort colors from darkest to lightest
        List<Color> colorList = new ArrayList<>( Arrays.asList(colors) );
        sortColorsByLuminescence( colorList );
        colors = colorList.toArray( new Color[ colorList.size() ] );
        
        Color[] colorSpread = getColorSpread( colors );
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                int smallestDistanceIndex = 0;
                double smallestDistance = 255.0 * 3.0;
                Pixel pix = pixels[row][col];
                for( int rep = 0; rep < colors.length; rep++ ) {
                    double distance = pix.colorDistanceAdvanced( colorSpread[rep] );
                    if( distance < smallestDistance ) {
                        smallestDistance = distance;
                        smallestDistanceIndex = rep;
                    }
                }
                
                pix.setColorKeepAlpha( colors[ smallestDistanceIndex ] );
            }
        }
    }
    
    /**
     * Sort a Color list from darkest to lightest
     * 
     * @param colors The list of colors to sort
     */
    private void sortColors( List<Color> colors ) {
        Collections.sort(colors, new Comparator<Color>() {
            @Override
            public int compare(Color c1, Color c2) {
                return Integer.compare( c1.getRed() + c1.getGreen() + c1.getBlue(),
                                        c2.getRed() + c2.getGreen() + c2.getBlue());
            }
        });
    }
    
    /**
     * Sort a Color list from darkest to lightest by luminescence
     * 
     * @param colors The list of colors to sort
     */
    private void sortColorsByLuminescence( List<Color> colors ) {
        Collections.sort(colors, new Comparator<Color>() {
            @Override
            public int compare(Color c1, Color c2) {
                return Float.compare(
                    ((float) c1.getRed() * 0.299f + (float) c1.getGreen() * 0.587f
                    + (float) c1.getBlue() * 0.114f) / 256f,
                    ((float) c2.getRed() * 0.299f + (float) c2.getGreen() * 0.587f
                    + (float) c2.getBlue() * 0.114f) / 256f);
            }
        });
    }
    
    /**
     * Use the list of Colors to create an artificial spread of Colors depending on the total
     * number of Colors in the original list. The new list's Colors will not be drawn onto
     * the Picture, but will be used for the Color distances instead of the original Colors.
     * Then the original Colors will be used for color correction based on the indices of the
     * new Color list
     * 
     * @param colors The list of Colors used to set the list of interval Colors
     * @return Color[] The list of interval Colors, which are used for color distance instead of
     *                 the original colors in the minimizeColorAndSaturate(...) method
     */
    private Color[] getColorSpread( Color[] colors ) {
        final int MAX_INT = 255;
        Color[] colorSpread = new Color[ colors.length ];
        int colorInterval = MAX_INT / colors.length;
        int startValue = colorInterval / 2; //So that the Colors are centered and not skewed
        for( int rep = 0, colorValue = startValue;
             rep < colors.length;
             rep++, colorValue += colorInterval )
            colorSpread[ rep ] = new Color( colorValue, colorValue, colorValue );
        
        return colorSpread;
    }
    
    /**
     * Method that mirrors the picture around a horizontal mirror in the center
     * of the picture from left to right
    */
    public void mirrorHorizontal() {
        Pixel[][] pixels = this.getPixels2D();
        Pixel topPixel = null;
        Pixel bottomPixel = null;
        int height = pixels.length;
        for( int col = 0; col < pixels[0].length; col++ ) {
            for( int row = 0; row < height / 2; row++ ) {
                topPixel = pixels[row][col];
                bottomPixel = pixels[height - 1 - row][col];
                bottomPixel.setColor(topPixel.getColor());
            }
        } 
    }

    /**
     * Mirror just part of a picture of a temple
    */
    private void mirrorTemple() {
        int mirrorPoint = 276;
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int count = 0;
        Pixel[][] pixels = this.getPixels2D();

        // loop through the rows
        for( int row = 27; row < 97; row++ ) {
            // loop from 13 to just before the mirror point
            for( int col = 13; col < mirrorPoint; col++ ) {
                leftPixel = pixels[row][col];      
                rightPixel = pixels[row]                       
                [mirrorPoint - col + mirrorPoint];
                rightPixel.setColor(leftPixel.getColor());
            }
        }
    }

    /**
     * Method that mirrors the picture around a vertical mirror in the center
     * of the picture from left to right
    */
    public void mirrorVertical() {
        Pixel[][] pixels = this.getPixels2D();
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int width = pixels[0].length;
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < width / 2; col++ ) {
                leftPixel = pixels[row][col];
                rightPixel = pixels[row][width - 1 - col];
                rightPixel.setColor( leftPixel.getColor() );
            }
        } 
    }
    
    /**
     * Outline the subject (the largest object) in an image by converting the image
     * to black and white and drawing a black line around the subject. This method
     * uses the combination of the following methods:
     * 
     * 1) edgeDetection(...)
     * 2) Islands.clearIslandsOfSizeXAndBelow(...) -> clearing black islands
     * 3) Islands.clearIslandsOfSizeXAndBelow(...) -> filling white islands in black
     *                                                islands
     * 4) defuzz(...)
     * 5) linearize(...)
     * 6) thin(...) TBD
     * 7) connectLines(...) TBD
     * 8) removeForeground(...) TBD
     */
    public void outlineSubject(
        Picture pic, Pixel[][] pixels, 
        int edgeDistance,
        Islands clearBlack, Islands fillWhite, int islandMaxSize, boolean includeDiagonals,
        int sectionWidth, int neighbors,
        int pathThickness, int sectionThickness ) {
        // Method order matters
        pic.edgeDetection( edgeDistance );
        clearBlack.clearIslandsOfSizeXAndBelow( pixels, islandMaxSize, includeDiagonals );
        fillWhite.clearIslandsOfSizeXAndBelow( pixels, islandMaxSize, includeDiagonals );
        pic.defuzz( sectionWidth, neighbors );
        pic.linearize( pathThickness, sectionThickness );
    }
    
    /**
     * Pixelate an image based on new pixel sizes for the image
     * 
     * This method supports pixelation growth, but not shrinkage
     */
    public void pixelate() {
        final int PIXEL_WIDTH = 10; //in pixels
        pixelate( PIXEL_WIDTH );
    }
    /**
     * Pixelate an image by specifying new pixel sizes for the image.
     * 
     * Averages based on the pixelWidth parameter are used to set new areas of pixels to the
     * same color
     * 
     * This method supports pixelation growth, but not shrinkage
     * 
     * @param pixelWidth The new pixel width sizes (in pixels)
     */
    public void pixelate( int pixelWidth ) {
        Pixel[][] pixels = this.getPixels2D();
        Color[][] avgPixColor =
            new Color[pixels.length / pixelWidth + 1][pixels[0].length / pixelWidth + 1];
        //Get the average Color of each megapixel
        for( int startRow = 0, colorRowIndex = 0;
             startRow < pixels.length;
             startRow += pixelWidth, colorRowIndex++ ) {
            for( int startCol = 0, colorColIndex = 0;
                     startCol < pixels[0].length;
                     startCol += pixelWidth, colorColIndex++ ) {
                int sumR, sumG, sumB;
                sumR = sumG = sumB = 0;
                int row = startRow, col = startCol;
                for( ; row < startRow + pixelWidth &&
                       row < pixels.length;
                       row++ ) {
                    for( ; col < startCol + pixelWidth &&
                           col < pixels[0].length;
                           col++ ) {
                        sumR += pixels[row][col].getRed();
                        sumG += pixels[row][col].getGreen();
                        sumB += pixels[row][col].getBlue();
                    }
                    col = startCol;
                }
                int megapixelWidth  = row - startRow;
                int megapixelHeight = col - startCol;
                int totalPixelsWidth  = megapixelWidth  > 0 ? megapixelWidth  : pixelWidth;
                int totalPixelsHeight = megapixelHeight > 0 ? megapixelHeight : pixelWidth;
                int rVal = sumR / (totalPixelsWidth * totalPixelsHeight);
                int gVal = sumG / (totalPixelsWidth * totalPixelsHeight);
                int bVal = sumB / (totalPixelsWidth * totalPixelsHeight);
                avgPixColor[ colorRowIndex ][ colorColIndex ] = new Color( rVal, gVal, bVal );
            }
        }
        
        //Set the color of each megapixel
        for( int startRow = 0, colorRowIndex = 0;
             startRow < pixels.length;
             startRow += pixelWidth, colorRowIndex++ )
            for( int startCol = 0, colorColIndex = 0;
                 startCol < pixels[0].length;
                 startCol += pixelWidth, colorColIndex++ )
                for( int row = startRow;
                     row < startRow + pixelWidth && row < pixels.length;
                     row++ )
                    for( int col = startCol;
                         col < startCol + pixelWidth && col < pixels[0].length;
                         col++ )
                        pixels[row][col].setColorKeepAlpha(
                            avgPixColor[ colorRowIndex ][ colorColIndex ] );
    }
    
    /**
     * Method to divide a picture into multiple pictures and save those pictures
     * 
     * @param totalRows The total number of pictures each column will be split into
     * @param totalColumns The total number of pictures each row will be split into
     * @see subPicture(...)
     */
    public void splitIntoGrid( int totalRows, int totalColumns ) {
        Pixel[][] pixels = this.getPixels2D();
        int widthInterval = pixels.length / totalRows;
        int heightInterval = pixels[0].length / totalColumns;
        for( int pictureNumRow = 0; pictureNumRow < totalRows; pictureNumRow++ ) {
            for( int pictureNumCol = 0; pictureNumCol < totalColumns; pictureNumCol++ ) {
                int row = pictureNumRow * widthInterval;
                int col = pictureNumCol * heightInterval;
                subPicture( row, col, widthInterval, heightInterval,
                            "pic_num_" + pictureNumRow + "x" + pictureNumCol );
            }
        }
    }
    
    /**
     * Method to take a piece of a Picture to create a new Picture
     * 
     * @param startingRow The top left x coordinate of the new Picture
     * @param startingCol The top left y coordinate of the new Picture
     * @param width The width of the new Picture
     * @param height The height of the new Picture
     * @param extension The String extension name to attach to the end of the picture name
     */
    public void subPicture( int startingRow, int startingCol, int width, int height,
                            String extension ) {
        Pixel[][] pixels = this.getPixels2D();
        Pixel[][] subPixels = subarray( pixels, startingRow, startingCol, width, height );
        
        Picture finalPic = new Picture( width, height );
        copyTo( subPixels, finalPic );
        
        String name = this.getFileName();
        int extensionIndex = name.indexOf(".");
        finalPic.write(
            name.substring( 0, extensionIndex ) + "_" + width + "x" + height + "_" +
                            extension + name.substring( extensionIndex ) );
    }
    public void subPicture( int startingRow, int startingCol, int width, int height ) {
        subPicture( startingRow, startingCol, width, height, "" );
    }
        
    /**
     * Method to convert a picture to Color.BLACK and Color.WHITE pixels only
     */
    public void toBW() {
        final int COLOR_SPLIT = 110;
        
        final int BW_LINE = COLOR_SPLIT*3 - 1;
        
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[0].length; col++ ) {
                int totalColor = pixels[row][col].getRed() +
                                 pixels[row][col].getGreen() +
                                 pixels[row][col].getBlue();
                if( totalColor < BW_LINE )
                    pixels[row][col].setColor( Color.BLACK );
                else
                    pixels[row][col].setColor( Color.WHITE );
            }
        }
    }
    
    /**
     * Method that sets all red hues to zero
    */
    public void zeroRed() {
        Pixel[][] pixels = this.getPixels2D();
        for( Pixel[] rowArray : pixels )
            for( Pixel pixelObj : rowArray )
                pixelObj.setRed(0);
    }

    /**
     * Method that sets all green hues to zero
    */
    public void zeroGreen() {
        Pixel[][] pixels = this.getPixels2D();
        for( Pixel[] rowArray : pixels )
            for( Pixel pixelObj : rowArray )
                pixelObj.setGreen(0);
    }

    /**
     * Method that sets all blue hues to zero
    */
    public void zeroBlue() {
        Pixel[][] pixels = this.getPixels2D();
        for( Pixel[] rowArray : pixels )
            for( Pixel pixelObj : rowArray )
                pixelObj.setBlue(0);
    }

    /* Image names in folder --
     *      arch.jpg                jenny-red.jpg       swan.jpg
     *      barbaraS.jpg            KatieFancy.jpg      temple.jpg
     *      beach.jpg               kitten2.jpg         thruDoor.jpg
     *      blue-mark.jpg           koala.jpg           wall.jpg
     *      blueMotorcycle.jpg      moon-surface.jpg    water.jpg
     *      butterfly.jpg           *** nike.png ***    whiteFlower.jpg
     *      caterpillar.jpg         rainbow.jpg         rainbowCoat.jpg
     *      CumberlandIsland.jpg    redMotorcycle.jpg   msg.jpg
     *      femaleLionAndHall.jpg   robot.jpg
     *      flower1.jpg             seagull.jpg
     *      flower2.jpg             snowman.jpg
     */
    public static void main( String[] args ) {
        /* @@@@@@@@@@@@@@@@@@@@ Creating Pictures @@@@@@@@@@@@@@@@@@@@ */
        Picture pic = new Picture("umbrella.jpg");
        //Picture pic2 = new Picture("flower1_linearized_sectionW_5.jpg");
        //Picture picDif = new Picture("rainbowCoat_defuzz_difference.png");
        
        /* @@@@@@@@@@@@@@@@@@@@ Setting Color Palette @@@@@@@@@@@@@@@@ */
        ColorPalette palette = new ColorPalette("PALETTE_STANDARD.txt");
        Color[] colors = palette.getAllColors();
        //ColorPalette.printColorList( colors );
        //ColorPalette palette2 = new ColorPalette("PALETTE_STANDARD.txt");
        //Color[] colors2 = palette2.getAllColors();
        
        /* @@@@@@@@@@@@@@@@@@@@ Using Islands Class @@@@@@@@@@@@@@@@@@@@@@@ */
        //pic.toBW();
        Pixel emptyValuePixel = new Pixel( pic, 0, 0 );
        emptyValuePixel.setColor( new Color( 255, 255, 255 ) ); //White pixels are 'empty' pixels
        Pixel islandValuePixel = new Pixel( pic, 0, 0 );
        islandValuePixel.setColor( new Color( 0, 0, 0 ) ); //Black pixels are 'island' pixels
        Islands islandClearer = new Islands( emptyValuePixel );
        Islands islandFiller = new Islands( islandValuePixel );
        Pixel[][] pixels = pic.getPixels2D();
        int smallestDimension = pixels.length > pixels[0].length
                                    ? pixels[0].length : pixels.length;
        int largestDimension = pixels.length > pixels[0].length
                                    ? pixels.length : pixels[0].length;
        /* @@@@@@@@@@@@@@@@@@@@ B/W ONLY @@@@@@@@@@@@@@@@@@@@ */
        //islandClearer.clearIslandsAboveRow( pixels, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsAboveRow(
        //    pixels, pixels.length / 2/*above this row*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsBelowRow( pixels, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsBelowRow(
        //    pixels, pixels.length / 2/*below this row*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsInRegion(
        //    pixels, pixels.length / 4/*topLeftCoordRow*/, pixels.length / 4/*topLeftCoordCol*/,
        //    pixels.length - 1/*bottomLeftCoordRow*/, pixels.length - 1/*bottomLeftCoordCol*/,
        //    true/*includeDiagonals*/ );
        //islandClearer.clearIslandsInRegionOfSizeBetweenXAndY(
        //    pixels, pixels.length / 4/*topLeftCoordRow*/, pixels.length / 4/*topLeftCoordCol*/,
        //    pixels.length - 1/*bottomLeftCoordRow*/, pixels.length - 1/*bottomLeftCoordCol*/,
        //    2/*minSize*/, 5/*maxSize*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsInRegionOfSizeXAndAbove(
        //    pixels, pixels.length / 4/*topLeftCoordRow*/, pixels.length / 4/*topLeftCoordCol*/,
        //    pixels.length - 1/*bottomLeftCoordRow*/, pixels.length - 1/*bottomLeftCoordCol*/,
        //    2/*minSize*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsInRegionOfSizeXAndBelow(
        //    pixels, pixels.length / 4/*topLeftCoordRow*/, pixels.length / 4/*topLeftCoordCol*/,
        //    pixels.length - 1/*bottomLeftCoordRow*/, pixels.length - 1/*bottomLeftCoordCol*/,
        //    5/*maxSize*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsLeftCol( pixels, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsLeftCol(
        //    pixels, pixels[0].length / 2/*left of this col*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsRightCol( pixels, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsRightCol(
        //    pixels, pixels[0].length / 2/*right of this col*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsOfSizeBetweenXAndY(
        //    pixels, 2/*minSize*/, 5/*maxSize*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsOfSizeXAndAbove(
        //    pixels, 2/*minSize*/, true/*includeDiagonals*/ );
        //islandClearer.clearIslandsOfSizeXAndBelow(
        //    pixels, 10/*maxSize*/, true/*includeDiagonals*/ );
        
        /* @@@@@@@@@@@@@@@@@@@@@ Applying Effects @@@@@@@@@@@@@@@@@@@@@@ */
        //pic.addGrid( 5/*interval*/, Color.RED );
        //pic.allToColor( Color.WHITE );
        //pic.defuzz/*BW*/( 20/*sectionWidth=20*/, 1/*neighbors=1*/ );
        //pic.copy( robot/*Picture to copy and paste into this image*/, 100/*startRow*/, 100/*startCol*/ );
        //pic.copyTo( robot/*Picture to copy to from this image, 200/*startRow*/, 200/*startCol*/ );
        //pic.drawPixels( /*ArrayList<ColorNeighbor> pixelList*/ );
        //pic.edgeDetection( 20/*edge distance=20*/ );
        //pic.defuzz/*BW*/( 20/*sectionWidth=20*/, 1/*neighbors=1*/ );
        //pic.filterRed();
        //pic.filterGreen();
        //pic.filterBlue();
        //pic.findDifferences( pic2 ); //@@NOTE: This method automatically saves the result in a new image
        //pic.gaussianBlur( Blur.MEDIUM/*Blur.MILD, Blur.MEDIUM, or Blur.STRONG*/ );
        //pic.grayscale();
        //pic.linearize/*BW*/( 1/*pathThickness=1*/, 5/*sectionThickness=5*/ );
        //pic.makeOpaque();
        //pic.minimizeColors();
        //pic.pixelate( 10/*pixelWidth=10*/ );
        pic.minimizeColors( colors/*Array of Color or StrColor objects*/ );
        //pic.minimizeColorsAndSaturate( colors/*Array of Color or StrColor objects*/ );
        //pic.mirrorVertical();
        //pic.mirrorHorizontal();
        //pic.outlineSubject(
        //    pic/*The Picture to outline*/, pixels/*The 2D array of Pixels*/,
        //    /*(int)(smallestDimension*0.1)*/20/*edgeDistance=20*/,
        //    islandClearer, islandFiller, 10/*maxSize=10*/, true/*includeDiagonals*/,
        //    (int)(smallestDimension*0.05)/*sectionWidth=20, or sD*0.1*/, 1/*totalNeighbors=1*/,
        //    1/*pathThickness=1*/, 3/*sectionThickness=5*/ );
        //pic.pixelate( 5/*pixelWidth=10*/ );
        //pic.scale( 2/*xFactor*/, 2/*yFactor*/ );
        //pic.splitIntoGrid( 11/*totalRows*/, 1/*totalColumns*/ );
        //pic.subPicture( 100/*startingRow*/, 100/*startingCol*/, 100/*width*/, 100/*height*/ ); //@@NOTE: Autosaves new image
        //pic.superDefuzz/*BW*/( 20/*sectionWidth=20*/, 1/*neighbors=1*/ );
        //pic.toBW();
        //pic.zeroRed();
        pic.zeroGreen();
        //pic.zeroBlue();

        /* @@@@@@@@@@@@@@@@@@@@ Gathering Picture Data @@@@@@@@@@@@@@@@@@@@ */
        //PictureData picData = new PictureData( pic, colors );
        //PictureData picData2 = new PictureData( pic, colors2 );
        //picData.printUniqueColorList();
        //picData.printCategoryMap();
        //picData.printCategoryMapPercent();
        //picData2.printCategoryMapPercent();
        //picData.printAverageColor();
        //picData.printColorRange();
        //picData.printContrastCoefficient();
        //picData.printSaturationCoefficient();
        //picData.printSaturationMap();
        //picData.printSaturationMapPercent();
        //picData.printBrightnessCoefficient();
        //picData.printBrightnessLocators();
        //picData.printIndividualBrightnessLocators();
        //pic.drawStars( picData.BRIGHTNESS_LOCATORS );
        
        //@@DEBUG methods
        //pic.testSetPath();
        
        /* @@@@@@@@@@@@@@@@@@@@@@@ Viewing Picture @@@@@@@@@@@@@@@@@@@@ */
        pic.explore();
        
        /* @@@@@@@@@@@@@@@@@@@@@@@ Saving Picture @@@@@@@@@@@@@@@@@@@@@ */
        pic.write("butterfly_crazy.jpg");
    }
}