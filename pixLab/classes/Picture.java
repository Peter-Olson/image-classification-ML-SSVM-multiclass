import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.List; // resolves problem with java.awt.List and java.util.List

/**
 * A class that represents a picture.  This class inherits from 
 * SimplePicture and allows the student to add functionality to
 * the Picture class.  
 * 
 * @author Barbara Ericson ericson@cc.gatech.edu, Peter Olson mrpeterfolson@gmail.com
 */
public class Picture extends SimplePicture 
{
    ///////////////////// constructors //////////////////////////////////

    /**
     * Constructor that takes no arguments 
     */
    public Picture ()
    {
        /* not needed but use it to show students the implicit call to super()
         * child constructors always call a parent constructor 
         */
        super();  
    }

    /**
     * Constructor that takes a file name and creates the picture 
     * @param fileName the name of the file to create the picture from
     */
    public Picture(String fileName)
    {
        // let the parent class handle this fileName
        super(fileName);
    }

    /**
     * Constructor that takes the width and height
     * @param height the height of the desired picture
     * @param width the width of the desired picture
     */
    public Picture(int height, int width)
    {
        // let the parent class handle this width and height
        super(width,height);
    }

    /**
     * Constructor that takes a picture and creates a 
     * copy of that picture
     * @param copyPicture the picture to copy
     */
    public Picture(Picture copyPicture)
    {
        // let the parent class do the copy
        super(copyPicture);
    }

    /**
     * Constructor that takes a buffered image
     * @param image the buffered image to use
     */
    public Picture(BufferedImage image)
    {
        super(image);
    }

    ////////////////////// methods ///////////////////////////////////////

    /**
     * Method to return a string with information about this picture.
     * @return a string with information about the picture such as fileName,
     * height and width.
     */
    public String toString()
    {
        String output = "Picture, filename " + getFileName() + 
            " height " + getHeight() 
            + " width " + getWidth();
        return output;
    }
    
    /**
     * Method to add a grid to the image
     * @param interval Grid line separation, in pixels
     * @param color The color of the lines
     */
    public void addGrid( int interval, Color color )
    {
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
     * @param color The color to paint this Picture
     */
    public void allToColor( Pixel[][] section, Color color )
    {
        for( int row = 0; row < section.length; row++ )
            for( int col = 0; col < section[0].length; col++ )
                section[row][col].setColor( color );
    }
    
    /**@@For B/W Pictures:@@*/
    public void clearIslands( int pixelIslandLimit, boolean includeDiagonals )
    { clearIslands( pixelIslandLimit, includeDiagonals, Color.BLACK, Color.WHITE ); }
    /**
     * @@For B/W Pictures:@@
     * 
     * Method that removes islands of black pixels whose total pixel count
     * is less than the parameter 'pixelIslandLimit'
     * @param pixelIslandLimit Any islands of black pixels whose total count is less than
     *                         this limit will be converted to Color.WHITE pixels
     * @param includeDiagonals True if diagonals are included in islands, false otherwise
     * @param origColor The original color of the pixel
     * @param newColor The new color of the pixel
     */
    public void clearIslands( int pixelIslandLimit, boolean includeDiagonals, Color origColor, Color newColor )
    {
        Pixel[][] pixels = this.getPixels2D();
        Neighbor[][] imageNeighbors = new Neighbor[ pixels.length ][ pixels[0].length ];
        for( int row = 0; row < pixels.length; row++ )
        {
            for( int col = 0; col < pixels[0].length; col++ )
            {
                Pixel pix = pixels[row][col];
                if( pix.getColor().equals( origColor ) &&
                    (imageNeighbors[row][col] == null ||
                    !imageNeighbors[row][col].isVisited() ) )
                {
                    Neighbor thisPixel = new Neighbor( row, col, true );
                    imageNeighbors[row][col] = thisPixel;
                    ArrayList<Neighbor> island = new ArrayList<Neighbor>();
                    island.add( thisPixel );
                    ArrayList<Neighbor> islandNeighbors =
                        getIsland( thisPixel, island, pixels, imageNeighbors, includeDiagonals,
                                   pixelIslandLimit, origColor );
                    if( islandNeighbors != null && islandNeighbors.size() < pixelIslandLimit )
                        removeIsland( islandNeighbors, imageNeighbors, pixels, newColor );
                }
            }
        }
    }
    
    public enum Direction
    {
        UP, LEFT, DOWN, RIGHT;
    }
    
    /**
     * Method to find all of the neighbors of a black pixel. The parameter 'includeDiagonals'
     * controls whether diagonal neighbors should be included or not
     * 
     * This method works by finding a path of Color.WHITE pixels that goes around the island
     * containing the pixel. Then, every black pixel within the perimeter is added to the list and
     * then returned
     * 
     * Note: This method returns null if the island is greater than the pixelIslandLimit. If this
     *       limit + 2 rows are found that have black pixels, null is returned right away, saving
     *       processing time (and making this method actually usable)
     * 
     * @param thisPixel The neighbor being observed
     * @param island The list of neighbors that make up this island
     * @param pixels The pixels of the picture
     * @param imageNeighbors The entire picture of Neighbor objects, which keep track of whether each has been visited or not
     * @param includeDiagonals True if diagonal black pixels should be included as neighbors, false otherwise
     * @param pixelIslandLimit The limit to the size of an island. Note that this method returns null if any number of rows
     *                         are found greater than this limit plus two
     * @param origColor The original color of the pixel. These pixels make up the island
     * @return ArrayList<Neighbor> The list of Neighbors that belong to this island, or null if the island size must be greater
     *                             than the pixelIslandLimit
     */
    private ArrayList<Neighbor> getIsland(
        Neighbor thisPixel, ArrayList<Neighbor> island, Pixel[][] pixels,
        Neighbor[][] imageNeighbors, boolean includeDiagonals,
        int pixelIslandLimit, Color origColor )
    {
        int x = thisPixel.getX();
        int y = thisPixel.getY();
        int iterX = x;
        int iterY = y;
        Neighbor lastPix     = new Neighbor(  0,  0, false );
        Neighbor lastRemoved = new Neighbor( -1, -1, false );
        
        HashMap<Integer, Integer> rowList = new HashMap<Integer, Integer>();
        ArrayList<Integer> rowX  = new ArrayList<Integer>();
        ArrayList<Integer> colY1 = new ArrayList<Integer>();
        ArrayList<Integer> colY2 = new ArrayList<Integer>();
        
        Direction dir;
        Direction originalDir = Direction.UP;

        //Island of 1 pixel
        if( totalNeighbors( x, y, pixels, origColor ) == 0 ) return island;
        
        //Determine starting pixel whose color is Color.WHITE, and the direction
        if(      iterX != 0 ) { iterX--; dir = Direction.LEFT;  } //x is not on top row
        else if( iterY != 0 ) { iterY--; dir = Direction.DOWN;  } //x is on top row
        else                  { iterX++; dir = Direction.RIGHT; } //x and y is the (0, 0) pixel
        
        int startX = iterX;
        int startY = iterY;
        
        //If there is no starting WHITE pixel or if there are 8 surrounding black neighbor pixels
        if( pixels[startX][startY].getColor().equals( origColor ) )              return null;
        if( totalNeighbors( startX, startY, pixels, origColor ) == 8 ) return null;
        
        //Keep going around island until find original start position
        do
        {
            Pixel pix = pixels[iterX][iterY];
            int xPix = pix.getY();
            int yPix = pix.getX();
            Neighbor thisPix = new Neighbor( xPix, yPix, false );
            
            //Add pixel x and y
            if( !rowList.containsKey( xPix ) )
            {
                rowList.put( xPix, yPix );
                originalDir = dir;
                lastPix = new Neighbor( xPix, yPix, false );
                
                /*@@NOTE: Comment out this if statement to prevent this method from short-circuiting and
                          returning null instead of the full island size 
                */
                if( rowList.size() >= pixelIslandLimit + 2 ) return null;
            }

            //Traverse one step around island
            if( dir == Direction.UP )
            {
                if( pix.getColor().equals( origColor ) ) //Hit bounds of image
                {
                    if( hasDir( dir, pix, pixels ) )
                        iterX--;
                    else
                    {
                        dir = Direction.LEFT;
                        if( hasDir( dir, pix, pixels ) )
                            iterY--;
                        else
                        {
                            dir = Direction.DOWN;
                            iterX++;
                        }
                    }
                }
                else 
                {
                    if( iterX == 0 ) //Reach top row, go left
                    {
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    //On outer edge and black pixel is above
                    else if( iterY == pixels[0].length - 1 &&
                             getNeighbor( -1, 0, pix, pixels, origColor ) &&
                             getNeighbor( 0, -1, pix, pixels, origColor ) )
                    {
                        iterX--;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( -1, -1, pix, pixels, origColor ) ) //cut through up-left diagonal
                    {
                        iterX--;
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    else if( !getNeighbor( 0, -1, pix, pixels, origColor ) ) //empty space to left
                    {
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    else if( getNeighbor( 0, 1, pix, pixels, origColor ) &&
                             getNeighbor( -1, 0, pix, pixels, origColor ) ) //neighbor left, up, and right, so turn around
                    {
                        dir = Direction.DOWN;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( -1, 1, pix, pixels, origColor ) ) //cut through up-right diagonal
                    {
                        iterX--;
                        iterY++;
                        dir = Direction.RIGHT;
                    }
                    else if( getNeighbor( -1, 0, pix, pixels, origColor ) ) //neighbor left and above, so go right
                    {
                        iterY++;
                        dir = Direction.RIGHT;
                    }
                    else if( !getNeighbor( -1, -1, pix, pixels, origColor ) ) //cut to left diagonal
                    {
                        iterX--;
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    else //neighbor to left and diag left, go up
                    {
                        iterX--;
                    }
                }
            }
            else if( dir == Direction.LEFT )
            {
                if( pix.getColor().equals( origColor ) ) //Hit bounds of image
                {
                    if( hasDir( dir, pix, pixels ) )
                        iterY--;
                    else
                    {
                        dir = Direction.DOWN;
                        if( hasDir( dir, pix, pixels ) )
                            iterX++;
                        else
                        {
                            dir = Direction.RIGHT;
                            iterY++;
                        }
                    }
                }
                else 
                {
                    if( iterY == 0 ) //reached left column, go down
                    {
                        iterX++;
                        dir = Direction.DOWN;
                    } //on top edge and black pixel is left
                    else if( iterX == 0 &&
                             getNeighbor( 0, -1, pix, pixels, origColor ) &&
                             getNeighbor( 1, 0, pix, pixels, origColor ) )
                    {
                        iterY--;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( 1, -1, pix, pixels, origColor ) ) //cut through down-left diagonal
                    {
                        iterX++;
                        iterY--;
                        dir = Direction.DOWN;
                    }
                    else if( !getNeighbor( 1, 0, pix, pixels, origColor ) ) //empty space down
                    {
                        iterX++;
                        dir = Direction.DOWN;
                    }
                    else if( getNeighbor( -1, 0, pix, pixels, origColor ) &&
                             getNeighbor( 0, -1, pix, pixels, origColor ) ) //neighbor left, up, and down, so turn around
                    {
                        dir = Direction.RIGHT;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( -1, -1, pix, pixels, origColor ) ) //cut through up-left diagonal
                    {
                        iterX--;
                        iterY--;
                        dir = Direction.UP;
                    }
                    else if( getNeighbor( 0, -1, pix, pixels, origColor ) ) //neighbor down and left, so go up
                    {
                        iterX--;
                        dir = Direction.UP;
                    }
                    else if( !getNeighbor( 1, -1, pix, pixels, origColor ) ) //cut to down diagonal
                    {
                        iterX++;
                        iterY--;
                        dir = Direction.DOWN;
                    }
                    else //neighbor down and diag down, go left
                    {
                        iterY--;
                    }
                }
            }
            else if( dir == Direction.DOWN )
            {
                if( pix.getColor().equals( origColor ) ) //Hit bounds of image
                {
                    if( hasDir( dir, pix, pixels ) )
                        iterX++;
                    else
                    {
                        dir = Direction.RIGHT;
                        if( hasDir( dir, pix, pixels ) )
                            iterY++;
                        else
                        {
                            dir = Direction.UP;
                            iterX--;
                        }
                    }
                }
                else 
                {
                    if( iterX == pixels.length - 1 ) //reached bottom row, go right
                    {
                        iterY++;
                        dir = Direction.RIGHT;
                    } //On outer edge and black pixel is down
                    else if( iterY == 0 &&
                             getNeighbor( 1, 0, pix, pixels, origColor ) &&
                             getNeighbor( 0, 1, pix, pixels, origColor ) )
                    {
                        iterX++;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( 1, 1, pix, pixels, origColor ) ) //cut through down-right diagonal
                    {
                        iterX++;
                        iterY++;
                        dir = Direction.RIGHT;
                    }
                    else if( !getNeighbor( 0, 1, pix, pixels, origColor ) ) //empty space right
                    {
                        iterY++;
                        dir = Direction.RIGHT;
                    }
                    else if( getNeighbor( 0, -1, pix, pixels, origColor ) &&
                             getNeighbor( 1, 0, pix, pixels, origColor ) ) //neighbor left, down, and right, so turn around
                    {
                        dir = Direction.UP;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( 1, -1, pix, pixels, origColor ) ) //cut through down-left diagonal
                    {
                        iterX++;
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    else if( getNeighbor( 1, 0, pix, pixels, origColor ) ) //neighbor down and right, so go left
                    {
                        iterY--;
                        dir = Direction.LEFT;
                    }
                    else if( !getNeighbor( 1, 1, pix, pixels, origColor ) ) //cut to down diagonal
                    {
                        iterX++;
                        iterY++;
                        dir = Direction.RIGHT;
                    }
                    else //neighbor right and diag right, go down
                    {
                        iterX++;
                    }
                }
            }
            else // dir == Direction.RIGHT
            {
                if( pix.getColor().equals( origColor ) ) //Hit bounds of image
                {
                    if( hasDir( dir, pix, pixels ) )
                        iterY++;
                    else
                    {
                        dir = Direction.UP;
                        if( hasDir( dir, pix, pixels ) )
                            iterX--;
                        else
                        {
                            dir = Direction.LEFT;
                            iterY--;
                        }
                    }
                }
                else 
                {
                    if( iterY == pixels[0].length - 1 ) //reach rightmost column, go up
                    {
                        iterX--;
                        dir = Direction.UP;
                    } //on edge and black pixel is right
                    else if( iterX == pixels.length - 1 &&
                             getNeighbor( 0, 1, pix, pixels, origColor ) &&
                             getNeighbor( -1, 0, pix, pixels, origColor ) )
                    {
                        iterY++;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( -1, 1, pix, pixels, origColor ) ) //cut through up-right diagonal
                    {
                        iterX--;
                        iterY++;
                        dir = Direction.UP;
                    }
                    else if( !getNeighbor( -1, 0, pix, pixels, origColor ) ) //empty space up
                    {
                        iterX--;
                        dir = Direction.UP;
                    }
                    else if( getNeighbor( 1, 0, pix, pixels, origColor ) &&
                             getNeighbor( 0, 1, pix, pixels, origColor ) ) //neighbor right, up, and down, so turn around
                    {
                        dir = Direction.LEFT;
                    }
                    else if( !includeDiagonals &&
                             !getNeighbor( 1, 1, pix, pixels, origColor ) ) //cut through down-right diagonal
                    {
                        iterX++;
                        iterY++;
                        dir = Direction.DOWN;
                    }
                    else if( getNeighbor( 0, 1, pix, pixels, origColor ) ) //neighbor up and right, so go down
                    {
                        iterX++;
                        dir = Direction.DOWN;
                    }
                    else if( !getNeighbor( -1, 1, pix, pixels, origColor ) ) //cut to up diagonal
                    {
                        iterX--;
                        iterY++;
                        dir = Direction.UP;
                    }
                    else //neighbor up and diag up, go right
                    {
                        iterY++;
                    }
                }
            }
            
            //If HashMap has a Pixel of this row and the direction has changed,
            //pop out and add y values to parallel lists
            if( rowList.containsKey( iterX ) )
            {
                Neighbor oldPix = new Neighbor( iterX, rowList.get(iterX), false );
                Neighbor newPix = new Neighbor( iterX, iterY, false );
                
                if( oldPix.getY() < newPix.getY() &&         //first pixel found in row must be left of second pixel
                    (dir != originalDir || xPix != iterX) && //must have different directions or different x coords
                    !oldPix.isEqual( lastPix ) &&            //can't be the same pixel
                    !isNextTo( oldPix, newPix , includeDiagonals ) )    //pixels can't be next to one another
                {
                    rowX.add( newPix.getX() );               //parallel lists for tracking coordinates of rows
                    colY1.add( oldPix.getY() );
                    colY2.add( newPix.getY() );

                    rowList.remove( newPix.getX() );         //allow for new rows to be found at this x coord
                }
            }
        } while( iterX != startX || iterY != startY );
        
        //Add neighbors to island list and update neighbor list
        int size = rowX.size();
        boolean addedPixel = false;
        for( int row = 0; row < size; row++ )
        {
            int totalInRow = colY2.get(row) - colY1.get(row);
            for( int col = 0; col < totalInRow; col++ )
            {
                int rowVal = rowX.get(row);
                int colVal = colY1.get(row) + col;
                if( pixels[rowVal][colVal].getColor().equals( origColor ) &&
                    imageNeighbors[rowVal][colVal] == null )
                {
                    Neighbor n = new Neighbor( rowVal, colVal, true );
                    imageNeighbors[ rowVal ][ colVal ] = n;
                    island.add(n);
                    addedPixel = true;
                }
            }
        }
        
        //If no pixels need to be removed (turned to Color.WHITE)
        if( rowX.size() <= 0 || !addedPixel ) return null;
        
        return island;
        /*@@NOTE: This reference shouldn't need to be returned, but it has
         *        been left as a return type in order to match the recursive
         *        version of this method, which does need to return the
         *        reference in order to work correctly
         */
    }
    
    /**
     * Method that tests whether the current pixel is on the top row or not
     * @param dir The current direction of iteration
     * @param pix The pixel to check
     * @param pixels The image of pixels
     * @return boolean True if this direction can be iterated, false otherwise
     */
    private boolean hasDir( Direction dir, Pixel pix, Pixel[][] pixels )
    {
        if( dir == Direction.UP )
        {
            if( pix.getY() != 0 ) return true;
            else                  return false;
        }
        else if( dir == Direction.DOWN )
        {
            if( pix.getY() != pixels.length - 1 ) return true;
            else                                  return false;
        }
        else if( dir == Direction.LEFT )
        {
            if( pix.getX() != 0 ) return true;
            else                  return false;
        }
        else  // dir == Direction.RIGHT
        {
            if( pix.getX() != pixels[0].length - 1 ) return true;
            else                                     return false;
        }
    }
    
    /**
     * Method that gets whether two pixels are neighbors or not.
     * @param a First pixel
     * @param b Second pixel
     * @param includeDiagonals True if diagonals are considered neighbors, false otherwise
     * @return boolean True if they are neighbors, false otherwise
     */
    private boolean isNextTo( Neighbor a, Neighbor b, boolean includeDiagonals )
    {
        int aX = a.getX(); int bX = b.getX();
        int aY = a.getY(); int bY = b.getY();
        if( aX == bX && aY == bY ) return false;
        
        double delta = 0.0;
        if( aX == bX || aY == bY ) delta += 0.01;
        
        if( includeDiagonals )
        {
            if( Math.abs(aX - bX) + Math.abs(aY - bY) > 2.0 - delta ) return false;
            else                                                      return true;
        }
        else
        {
            if( Math.abs(aX - bX) + Math.abs(aY - bY) > 1.0 ) return false;
            else                                              return true;
        }
    }
    
    /**
     * Method that gets whether this pixel has a black neighbor in their upper-left diagonal
     * 
     * @@NOTE: If you use 'thisPixel' and are use subsections, 'thisPixel' will still contain
     *         the original coordinates from the larger Picture! You should instead use the
     *         other getNeighbor(...) function that uses fixed row and col coordinates
     * 
     * @param xOffset The x offset from this pixel's location
     * @param yOffset The y offset from this pixel's location
     * @param pix The pixel to check
     * @param pixels The image of pixels
     * @param origColor The color a pixel has that belongs to an island
     * @return boolean True if this neighbor exists, false otherwise
     */
    private boolean getNeighbor( int xOffset, int yOffset, Pixel thisPixel,
                                 Pixel[][] pixels, Color origColor )
    {
        int xNewPix = thisPixel.getY() + xOffset;
        int yNewPix = thisPixel.getX() + yOffset;
        
        if( xNewPix < 0 || xNewPix >= pixels.length )    return false;
        if( yNewPix < 0 || yNewPix >= pixels[0].length ) return false;
        
        if( pixels[xNewPix][yNewPix].getColor().equals( origColor ) ) return true;
        
        return false;
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
                                 Pixel[][] pixels, Color origColor )
    {
        int xNewPix = row + xOffset;
        int yNewPix = col + yOffset;
        
        if( xNewPix < 0 || xNewPix >= pixels.length )    return false;
        if( yNewPix < 0 || yNewPix >= pixels[0].length ) return false;
        
        if( pixels[xNewPix][yNewPix].getColor().equals( origColor ) ) return true;
        
        return false;
    }
    
    /**
     * @@NOTE: This method uses recursion. Any medium to large images (100x100px or greater)
     *         will likely result in a StackOverflow. It is not recommended that you use this
     *         method, except for educational purposes (it does work though)
     * 
     * Method to find all of the neighbors of a black pixel. The parameter 'includeDiagonals'
     * controls whether diagonal neighbors should be included or not
     * 
     * This method works by recursively finding all neighbors of a black pixel, then finding all neighbors
     * of each neighbor, etc. Once a pixel has no more non-visited black pixel neighbors, the method is returned,
     * and the stack resumes from that last method call
     * 
     * @param thisPixel The neighbor being observed
     * @param island The list of neighbors that make up this island
     * @param pixels The pixels of the picture
     * @param imageNeighbors The entire picture of Neighbor objects, which keep track of whether each has been visited or not
     * @param includeDiagonals True if diagonal black pixels should be included as neighbors, false otherwise
     * @return ArrayList<Neighbor> The list of Neighbors that belong to this island
     */
    private ArrayList<Neighbor> getIslandRecursive(
        Neighbor thisPixel, ArrayList<Neighbor> island, Pixel[][] pixels,
        Neighbor[][] imageNeighbors, boolean includeDiagonals )
    {
        int x = thisPixel.getX();
        int y = thisPixel.getY();
        
        int marginX = 0;
        int marginY = 0;
        int j = 0;
        int k = 0;
        if( x == 0 ) { ++x; ++j; }
        else if( x == pixels.length - 1 )    marginX = 1;
        
        if( y == 0 ) { ++y; ++k; }
        else if( y == pixels[0].length - 1 ) marginY = 1;
        
        for( int startX = x - 1; startX < x - 1 + 3 - marginX && j < 3; startX++ )
        {
            for( int startY = y - 1; startY < y - 1 + 3 - marginY && k < 3; startY++ )
            {
                Pixel pix = pixels[startX][startY];
                if( pix.getColor().equals( Color.BLACK ) && imageNeighbors[startX][startY] == null &&
                    (includeDiagonals || (!includeDiagonals && (j % 2 != 0 || k % 2 != 0) ) ) )
                {
                    Neighbor neighbor = new Neighbor( startX, startY, true );
                    imageNeighbors[startX][startY] = neighbor;
                    island.add( neighbor );
                    getIslandRecursive( neighbor, island, pixels, imageNeighbors, includeDiagonals );
                }
                
                if( startY + 1 == y - 1 + 3 - marginY ) k = 0;
                else                                    ++k;
            }
            
            k = 0;
            if( startX + 1 == x - 1 + 3 - marginX ) j = 0;
            else                                    ++j;
        }
        
        return island;
    }
    
    /**
     * Method to remove all of the neighbors in the list by changing their colors to Color.WHITE
     * @param islandNeighbors The neighbor pixels that compose the island of black pixels
     * @param imageNeighbors The entire picture of Neighbor objects, which keep track of whether each has been visited or not
     * @param pixels The pixels of the picture
     * @param newColor The new color of the island pixel that is removed
     */
    private void removeIsland( ArrayList<Neighbor> islandNeighbors, Neighbor[][] imageNeighbors,
                               Pixel[][] pixels, Color newColor )
    {
        for( int rep = 0; rep < islandNeighbors.size(); rep++ )
        {
            int x = islandNeighbors.get( rep ).getX();
            int y = islandNeighbors.get( rep ).getY();
            pixels[x][y].setColor( newColor );
            imageNeighbors[x][y].setVisited( true );
        }
    }
    
    private class Neighbor
    {
        private int x, y;
        private boolean visited;
        
        public Neighbor( int x, int y, boolean visited )
        {
            this.x = x;
            this.y = y;
            this.visited = visited;
        }
        
        public boolean isEqual( Neighbor o )
        {
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
    
    private class ColorNeighbor extends Neighbor
    {
        private Color c;
        
        public ColorNeighbor( int x, int y, boolean visited, Color c )
        {
            super(x, y, visited);
            this.c = c;
        }
        
        public Color getColor() { return c; }
        public void setColor( Color c ) { this.c = c; }
    }
    
    private class Pair
    {
        public Neighbor a, b;
        
        public Pair( Neighbor a, Neighbor b )
        {
            this.a = a;
            this.b = b;
        }
    }
    
    /**
     * Method to copy pixels from the given Picture
     * to this Picture at the location specified
     * @param fromPic the picture to copy from
     * @param startRow the start row to copy to
     * @param startCol the start col to copy to
     */
    public void copy( Picture fromPic, int startRow, int startCol )
    { copy( fromPic.getPixels2D(), startRow, startCol ); }
    /**
     * Method to copy pixels from a 2D array of Pixels to the current Picture
     * @param pixels The pixels to copy
     * @param startRow The row to start copying at within this picture
     * @param startcol The col to start copying at within this picture
     */
    public void copy( Pixel[][] fromPixels, int startRow, int startCol )
    {
        Pixel fromPixel = null;
        Pixel toPixel = null;
        Pixel[][] toPixels = this.getPixels2D();
        for (int fromRow = 0, toRow = startRow; 
             fromRow < fromPixels.length &&
             toRow < toPixels.length; 
             fromRow++, toRow++)
        {
            for (int fromCol = 0, toCol = startCol; 
                 fromCol < fromPixels[0].length &&
                 toCol < toPixels[0].length;  
                 fromCol++, toCol++)
            {
                fromPixel = fromPixels[fromRow][fromCol];
                toPixel = toPixels[toRow][toCol];
                toPixel.setColor(fromPixel.getColor());
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
     */
    public void copyTo( Picture toPicture, int startRow, int startCol )
    {
        Pixel[][] pixels = this.getPixels2D();
        Pixel[][] toPixels = toPicture.getPixels2D();
        int width = pixels.length > toPixels.length ? toPixels.length : pixels.length;
        int height = pixels[0].length > toPixels[0].length ? toPixels[0].length : pixels[0].length;
        copyTo( subarray( pixels, startRow, startCol, width, height ), toPicture );
    }
    /**
     * Method to copy pixels from this Picture to another
     * 
     * This method works best when pixels and toPicture are the
     * same dimensions. It also works if toPicture is larger than
     * pixels
     * @param toPicture The picture to copy to
     */
    public void copyTo( Pixel[][] pixels, Picture toPicture )
    {
        Pixel thisPixel = null;
        Pixel toPixel = null;
        Pixel[][] toPixels = toPicture.getPixels2D();
        
        for( int row = 0; row < pixels.length; row++ )
        {
            for( int col = 0; col < pixels[0].length; col++ )
            {
                thisPixel = pixels[row][col];
                toPixel = toPixels[row][col];
                toPixel.setColor( thisPixel.getColor() );
            }
        }
    }
    
    /** Method to create a collage of several pictures */
    public void createCollage()
    {
        Picture flower1 = new Picture("flower1.jpg");
        Picture flower2 = new Picture("flower2.jpg");
        this.copy(flower1,0,0);
        this.copy(flower2,100,0);
        this.copy(flower1,200,0);
        Picture flowerNoBlue = new Picture(flower2);
        flowerNoBlue.zeroBlue();
        this.copy(flowerNoBlue,300,0);
        this.copy(flower1,400,0);
        this.copy(flower2,500,0);
        this.mirrorVertical();
        this.write("collage.jpg");
    }

    /**@@For B/W pictures:@@*/
    public void defuzz( int sectionWidth )                { defuzz( sectionWidth, 0, 0, 1 ); }
    public void defuzz( int sectionWidth, int neighbors ) { defuzz( sectionWidth, 0, 0, neighbors ); }
    /**
     * @@For B/W pictures:@@
     * 
     * Method to remove 'fuzz' from pixels. Fuzz is defined as any
     * black squares that have x or less neighbors, where x is the parameter neighbors
     * @param sectionWidth The width of the section being defuzzed
     * @param offsetX The starting offset in the x direction
     * @param offsetY The starting offset in the y direction
     * @param neighbors Pixels with this many or less neighbors that are Color.BLACK
     *                  will be changed to be Color.WHITE
     */
    public void defuzz( int sectionWidth, int offsetX, int offsetY, int neighbors )
    {
        Pixel[][] pixels = this.getPixels2D();
        boolean[][] coordsToRemove = new boolean[ pixels.length ][ pixels[0].length ];
        for( int row = 0 + offsetX; row < pixels.length; row += sectionWidth )
        {
            for( int col = 0 + offsetY; col < pixels[0].length; col += sectionWidth )
            {
                //Last scopes can be smaller that edgeDistance x edgeDistance
                int rowLimit = row + sectionWidth;
                int colLimit = col + sectionWidth;
                if( row + sectionWidth > pixels.length )    rowLimit = pixels.length;
                if( col + sectionWidth > pixels[0].length ) colLimit = pixels[0].length;
                
                for( int rowScope = row; rowScope < rowLimit; rowScope++ )
                {
                    for( int colScope = col; colScope < colLimit; colScope++ )
                    {
                        Pixel pix = pixels[rowScope][colScope];
                        if( rowScope > 0 && colScope > 0 &&
                            rowScope < rowLimit - 1 && colScope < colLimit - 1 &&
                            !pix.getColor().equals( Color.WHITE ) &&
                            totalNeighbors( rowScope, colScope, pixels, Color.BLACK ) <= neighbors )
                        {
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
     * @@For B/W Pictures:@@
     * 
     * Method that calls the defuzz three different times,
     * in order to cover all edge all edges of scope passes
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
     * @@For B/W Pictures:@@
     * 
     * Method that determines the total neighbors of a pixel
     * @param x The x coordinate of the pixel
     * @param y The y coordinate of the pixel
     * @param pixels The 2D array of pixels of the picture
     * @param origColor The color of the neighbors
     * @return int The total neighbors of this pixel
     */
    private int totalNeighbors( int x, int y, Pixel[][] pixels, Color origColor )
    {
        int neighborCount = 0;
        
        int marginX = 0;
        int marginY = 0;
        int j = 0;
        int k = 0;
        if( x == 0 ) { ++x; ++j; }
        else if( x == pixels.length - 1 )    marginX = 1;
        
        if( y == 0 ) { ++y; ++k; }
        else if( y == pixels[0].length - 1 ) marginY = 1;

        int kReset = k;
        
        for( int startX = x - 1; startX < x - 1 + 3 - marginX && j < 3; startX++ )
        {
            for( int startY = y - 1; startY < y - 1 + 3 - marginY && k < 3; startY++ )
            {
                if( j == 1 && k == 1 ) 
                {
                    ++k;
                    continue;
                }
                
                Pixel pix = pixels[startX][startY];
                if( pix.getColor().equals( origColor ) )
                    neighborCount++;
                
                if( startY + 1 == y - 1 + 3 - marginY ) k = 0;
                else                                    ++k;
            }
            
            k = kReset;
            if( startX + 1 == x - 1 + 3 - marginX ) j = 0;
            else                                    ++j;
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
     * @param section The section to draw the pixels to
     * @param pixelList The list of pixels to draw
     * @param lineThickness The thickness of the line 
     */
    public void drawPixels( Pixel[][] section, ArrayList<ColorNeighbor> pixelList, int lineThickness )
    {
        int size = pixelList.size();
        for( int rep = 0; rep < size; rep++ )
        {
            int row = pixelList.get(rep).getX();
            int col = pixelList.get(rep).getY();
            Color c = pixelList.get(rep).getColor();
            int counter = 0;
            boolean isHorizontal = isHorizontalPath( pixelList );
            while( counter++ < lineThickness )
            {
                if( isHorizontal )
                    section[row++][col].setColor( c );
                else
                    section[row][col++].setColor( c );
            }
        }
    }
    
    /**
     * Method that determines which direction the path is moving
     * @param path The path of pixels
     * @return boolean True if the path is horizontal, false otherwise
     */
    private boolean isHorizontalPath( ArrayList<ColorNeighbor> path )
    {
        int size = path.size();
        return path.get( size - 1 ).getY() - path.get(0).getY()
               >
               path.get( size - 1 ).getX() - path.get(0).getX()
               ? true : false;
    }
    
    /**
     * Method to show large changes in color 
     * @param edgeDist the distance for finding edges
     */
    public void edgeDetection(int edgeDist)
    {
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        Pixel[][] pixels = this.getPixels2D();
        Color rightColor = null;
        for (int row = 0; row < pixels.length; row++)
        {
            for (int col = 0; col < pixels[0].length - 1; col++)
            {
                leftPixel = pixels[row][col];
                rightPixel = pixels[row][col+1];
                rightColor = rightPixel.getColor();
                if (leftPixel.colorDistance(rightColor) > edgeDist)
                    leftPixel.setColor(Color.BLACK);
                else
                    leftPixel.setColor(Color.WHITE);
            }
        }
    }

    /**@@For B/W Pictures only:@@*/
    public void fillIslands( int pixelIslandLimit, boolean includeDiagonals )
    { fillIslands( pixelIslandLimit, includeDiagonals, Color.WHITE, Color.BLACK ); }
    /**
     * @@For B/W Pictures only:@@
     * 
     * Method that fills all gaps of the specified color with a different color
     * 
     * Note: This is the same as clearingIslands of a given size, but with the colors switched
     * 
     * @param pixelIslandLimit Islands that have this limit or less are changed to 'newColor'
     * @param includeDiagonals If true, counts diagonal pixels as belonging to the given gap
     * @param origColor The color of the gaps
     * @param newColor The color to change the gaps to
     */
    public void fillIslands( int pixelIslandLimit, boolean includeDiagonals, Color origColor, Color newColor )
    {
        clearIslands( pixelIslandLimit, includeDiagonals, origColor, newColor );
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
     * @@For B/W pictures:@@
     * 
     * Method that compares two pictures and saves an image of their differences
     * @param second The second pic
     */
    public void findDifferences( Picture second )
    {
        Picture finalPic = new Picture( this.getHeight(), this.getWidth() );
        
        Pixel[][] firstPixels = this.getPixels2D();
        Pixel[][] secondPixels = second.getPixels2D();
        Pixel[][] finalPixels = finalPic.getPixels2D();
        for( int row = 0; row < firstPixels.length; row++ )
        {
            for( int col = 0; col < firstPixels[0].length; col++ )
            {
                Pixel firstPix = firstPixels[row][col];
                Pixel secondPix = secondPixels[row][col];
                if( !firstPix.getColor().equals( secondPix.getColor() ) ) {
                    double firstPixelDifference  = firstPix.colorDistanceAdvanced(  Color.BLACK );
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
        finalPic.write( name.substring( 0, extensionIndex ) + "_difference" + name.substring( extensionIndex ) );
    }
    
    public enum Blur { MILD, MEDIUM, STRONG };
    
    /**
     * Method that applies a Gaussian blur effect to the image.
     * The effect can apply mild, medium, or strong blur effects.
     * @param strength The strength of the blur. Either Blur.MILD, Blur.MEDIUM, or
     *                 Blur.STRONG
     */
    public void gaussianBlur( Blur strength )
    {
        int[][] kernel;
        int[] kReductionEdgeOffset;
        int KREDUCTION = 16;
        if( strength == Blur.MILD )
        {
            kernel = new int[][]{{1,2,1},//4
                                 {2,4,2},//8
                                 {1,2,1}};//4
            kReductionEdgeOffset = new int[]{0, 8, (16-1)};
        }
        else if( strength == Blur.MEDIUM )
        {
            kernel = new int[][]{{1,2,4,2,1},//10
                                 {2,4,6,4,2},//18
                                 {4,6,8,6,4},//28
                                 {2,4,6,4,2},//18
                                 {1,2,4,2,1}};//10
            kReductionEdgeOffset = new int[]{10, 20, 41, 62, (84-1)};
            KREDUCTION = 84;
        }
        else
        {
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
        
        for ( int row = 0; row < WIDTH; row++ )
        {
            for ( int col = 0; col < HEIGHT; col++ )
            {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;
                for( int kRow = 0; kRow < KSIZE; kRow++ )
                {
                    for( int kCol = 0; kCol < KSIZE; kCol++ )
                    {
                        int rowLimit = row + kRow /*- 1*/;
                        int colLimit = col + kCol /*- 1*/;
                        if( rowLimit >= 0 && colLimit >= 0 && rowLimit < WIDTH && colLimit < HEIGHT )
                        {            
                            int red   = pixels[ rowLimit ][ colLimit ].getRed();
                            int green = pixels[ rowLimit ][ colLimit ].getGreen();
                            int blue  = pixels[ rowLimit ][ colLimit ].getBlue();
                            totalRed   += kernel[ kRow ][ kCol ] * red;
                            totalGreen += kernel[ kRow ][ kCol ] * green;
                            totalBlue  += kernel[ kRow ][ kCol ] * blue;
                        }
                    }
                }
                
                int kReductionEdgeOffsetRowValue = 0, kReductionEdgeOffsetColValue = 0, kReductionOffsetValue = 0;
                if( WIDTH - row < kReductionEdgeOffset.length )
                    kReductionEdgeOffsetRowValue = kReductionEdgeOffset[ kReductionEdgeOffset.length - (WIDTH - row + 1) ];
                if( HEIGHT - col < kReductionEdgeOffset.length )
                    kReductionEdgeOffsetColValue = kReductionEdgeOffset[ kReductionEdgeOffset.length - (HEIGHT - col + 1) ];
                
                kReductionOffsetValue = Math.abs(kReductionEdgeOffsetRowValue) > Math.abs(kReductionEdgeOffsetColValue) ?
                                        kReductionEdgeOffsetRowValue : kReductionEdgeOffsetColValue;
                
               
                totalRed   /= (KREDUCTION - kReductionOffsetValue);
                totalGreen /= (KREDUCTION - kReductionOffsetValue);
                totalBlue  /= (KREDUCTION - kReductionOffsetValue);
                
                pixels[ row ][ col ].updatePicture( pixels[row][col].getAlpha(), totalRed, totalGreen, totalBlue );
            }
        }
    }
    
    /**
     * Method that grayscales an image
     */
    public void grayscale()
    {
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ )
        {
            for( int col = 0; col < pixels[0].length; col++ )
            {
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
     * @@For B/W Pictures:@@
     * 
     * Method that converts sections of pixels into their linear direction.
     * 
     * It is recommended that you use the following methods on your B/W image
     * before running this function, in the following order:
     *    1) edgeDetection(...)
     *    2) superDefuzz(...)
     *    3) clearIslands(...)
     *    4) fillIslands(...)
     * 
     * After this method, it is recommended that you run the followings method(s):
     *    1) thin(...) (I haven't written this yet. It would remove all black sections
     *                 (if they exist--see setPath method) and then connect existing paths
     *                 to one another, the goal of being that the end result would resemble
     *                 a better edge detection image than just running edgeDetection alone
     * @param lineThickness The thickness of the lines, in pixels
     */
    public void linearize( int lineThickness )
    {
        final int SECTION_WIDTH = lineThickness * 5;
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row += SECTION_WIDTH )
        {
            for( int col = 0; col < pixels[0].length; col += SECTION_WIDTH )
            {
                //Last scopes can be smaller that SECTION_WIDTH x SECTION_WIDTH
                int rowLimit = SECTION_WIDTH;
                int colLimit = SECTION_WIDTH;
                if( row + SECTION_WIDTH > pixels.length )    rowLimit = pixels.length - row;
                if( col + SECTION_WIDTH > pixels[0].length ) colLimit = pixels[0].length - col;
                
                Pixel[][] section = subarray( pixels, row, col, rowLimit, colLimit );
                
                setPath( section, lineThickness );
                
                this.copy( section, row, col );
            }
        }
    }
    
    /**
     * @@For B/W pictures:@@
     * 
     * Method to test whether a small picture (the size of a section) works correctly
     * using the setPath(...) method
     */
    private void testSetPath()
    {
        Pixel[][] pixels = this.getPixels2D();
        ArrayList<ColorNeighbor> path = getPath( pixels );
        printPath( path, pixels );
        //setPath( pixels, 1 );
    }
    
    /**
     * Method used to print a path of Neighbors
     * @param path The path of neighbor objects
     * @param pixels The pixel array of this picture
     */
    private void printPath( ArrayList<ColorNeighbor> path, Pixel[][] pixels )
    {
        boolean isValidPath      = true;
        boolean isHorizontalPath = false;
        boolean isVerticalPath   = false;
        
        if( path == null )
        {
            System.out.println("There are both horizontal and vertical paths. This section will be filled.");
            return;
        }
        int size = path.size();
        if( size == 0 )
        {
            System.out.println("There are no valid paths. This section will be cleared.");
            return;
        }
        
        System.out.println("Path:\n");
        
        for( int rep = 0; rep < size; rep++ )
        {
            if( rep == 0 && size > 1 )
            {
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
     */
    private Pixel[][] subarray( Pixel[][] pixels, int startingRow, int startingCol, int numRows, int numCols )
    {
        Pixel[][] a = new Pixel[numRows][numCols];
        for( int rep = startingRow, row = 0; rep < startingRow + numRows; rep++, row++ )
            a[row] = Arrays.copyOfRange( pixels[rep], startingCol, startingCol + numCols );
        
        return a;
    }
    
    /**
     * @@For B/W Pictures:@@
     * 
     * Method that sets the path within a given sector.
     * This method will also expand the width of the path by the value
     * 'lineThickness'. For details on the algorithm, see 'getPath(...)'
     * 
     * @param section The 2D array of pixels
     * @param lineThickness The path thickness to be set
     */
    private void setPath( Pixel[][] section, int lineThickness )
    {
        ArrayList<ColorNeighbor> path = getPath( section );
        
        if( path != null )
            allToColor( section, Color.WHITE );
        else
        {   //@@CHANGE: Style preference. vv Experiment with changing this to Color.BLACK or Color.WHITE
            allToColor( section, Color.WHITE );
            return;
        }
        
        if( path.size() != 0 )
            drawPixels( section, path, lineThickness );
    }

    /**
     * @@For B/W Pictures:@@
     * 
     * Method that returns a path of neighbors within a given sector.
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
    private ArrayList<ColorNeighbor> getPath( Pixel[][] section )
    {
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
        ArrayList<ColorNeighbor> horizontalPath = upSize >= middleSize ? (upSize >= downSize ? horizontalPathUpPriority : (middleSize >= downSize ? horizontalPathMiddlePriority
                                                                                                                                                      : horizontalPathDownPriority))
                                                                       : (middleSize >= downSize ? horizontalPathMiddlePriority : (upSize >= downSize ? horizontalPathUpPriority
                                                                                                                                                        : horizontalPathDownPriority));
        
        //Find correct path booleans
        int pathSize = horizontalPath.size();
        if(      pathSize == upSize )
        {
            if(      pathDecode1 == 0 ) hasHorizontalPath         = true;
            else if( pathDecode1 == 1 ) hasDiagonalPathHorizontal = true;
        }
        else if( pathSize == middleSize )
        {
            if(      pathDecode2 == 0 ) hasHorizontalPath         = true;
            else if( pathDecode2 == 1 ) hasDiagonalPathHorizontal = true;
        }
        else  // pathSize == downSize
        {
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
        ArrayList<ColorNeighbor> verticalPath = leftSize >= middleSize ? (leftSize >= rightSize ? verticalPathLeftPriority : (middleSize >= rightSize ? verticalPathMiddlePriority
                                                                                                                                                      : verticalPathRightPriority))
                                                                       : (middleSize >= rightSize ? verticalPathMiddlePriority : (leftSize >= rightSize ? verticalPathLeftPriority
                                                                                                                                                        : verticalPathRightPriority));
        
        //Find correct path booleans
        pathSize = verticalPath.size();
        if(      pathSize == leftSize )
        {
            if(      pathDecode1 == 0 ) hasVerticalPath         = true;
            else if( pathDecode1 == 1 ) hasDiagonalPathVertical = true;
        }
        else if( pathSize == middleSize )
        {
            if(      pathDecode2 == 0 ) hasVerticalPath         = true;
            else if( pathDecode2 == 1 ) hasDiagonalPathVertical = true;
        }
        else  // pathSize == rightSize
        {
            if(      pathDecode3 == 0 ) hasVerticalPath         = true;
            else if( pathDecode3 == 1 ) hasDiagonalPathVertical = true;
        }
        
        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
        
        /*@@DEBUG
            printPath( horizontalPath, section );
            printPath( verticalPath,   section );
        //*/
        
        //Check for dominant path
        if( hasHorizontalPath && hasVerticalPath ) return null; //null designates that this section should be painted black
        if( hasHorizontalPath )                    return horizontalPath;
        if( hasVerticalPath )                      return verticalPath;
        if( hasDiagonalPathHorizontal && hasDiagonalPathVertical )
            return horizontalPath.size() > verticalPath.size() ? horizontalPath : verticalPath;
        if( hasDiagonalPathHorizontal )            return horizontalPath;
        if( hasDiagonalPathVertical )              return verticalPath;
        
        horizontalPath.clear();
        
        return horizontalPath; //no path exists
    }
    
    /**
     * @@For B/W only:@@
     * 
     * Method that gets the horizontal path of a section
     * @param section The section of the Picture (composed of Pixels in a 2D array)
     * @param horizontalPath The list of Neighbors in this horizontal path
     * @param FIRST This determines which direction the path checks for first (either UP-RIGHT neighbor (-1), MIDDLE-RIGHT neighbor (0), or DOWN_RIGHT neighbor (1))
     * @param SECOND Which checks for second
     * @param THIRD Which checks for third
     * @return int 0 --> There is a horizontal path
     *             1 --> There is a diagonal horizontal path
     *             2 --> There are no horizontal or diagonal paths
     */
    private int getHorizontalPath( Pixel[][] section, ArrayList<ColorNeighbor> horizontalPath, int FIRST, int SECOND, int THIRD )
    {
        int row = 0;
        int col = 0;
        
        boolean hasHorizontalPath = false;
        boolean hasDiagonalPathHorizontal = false;
        
        int sectionStartX = 0;
        final int MIN_PATH_LENGTH = 3;
        
        //Check for horizontal path at each row starting pixel
        for( ; row < section.length; row++ )
        {
            int pathRow = row;
            boolean foundPath    = false;
            boolean downPathDead = false;
            
            for( col = 0; col < section[0].length ; col++ )
            {
                if( !section[pathRow][col].getColor().equals( Color.BLACK ) )
                    break;
                
                foundPath = true;
                
                horizontalPath.add( new ColorNeighbor( pathRow, col, true, Color.BLACK ) );
                
                if( col == section[0].length - 1 ) break;
                
                //Set the x position of the first available neighbor
                if(      getNeighbor( FIRST,  1, pathRow, col, section, Color.BLACK ) )
                {
                    if(      FIRST == -1 ) pathRow--;
                    else if( FIRST ==  1 ) pathRow++;
                }
                else if( getNeighbor( SECOND, 1, pathRow, col, section, Color.BLACK ) )
                {
                    if(      SECOND == -1 ) pathRow--;
                    else if( SECOND ==  1 ) pathRow++;
                }
                else if( getNeighbor( THIRD,  1, pathRow, col, section, Color.BLACK ) )
                {
                    if(      THIRD == -1 ) pathRow--;
                    else if( THIRD ==  1 ) pathRow++;
                }
                else if( !downPathDead &&
                         getNeighbor(  1, 0, pathRow, col, section, Color.BLACK ) ) //traverse down
                {
                    pathRow++;
                    --col;
                }
                else if( getNeighbor( -1, 0, pathRow, col, section, Color.BLACK ) ) //traverse up
                {
                    downPathDead = true;
                    pathRow--;
                    --col;
                }
                else break; //No paths on this column
                
                //See if a diagonal path exists
                if( !hasDiagonalPathHorizontal &&
                    (pathRow == sectionStartX || pathRow == sectionStartX + section.length - 1) &&
                    horizontalPath.size() > MIN_PATH_LENGTH )
                    hasDiagonalPathHorizontal = true;
            }
            
            if( !foundPath ) continue;
            
            //Check if there is a valid horizontal path. If true, stop searching
            if( col == section[0].length - 1 )
            {
                hasHorizontalPath = true;
                break;
            }
            else if( !hasDiagonalPathHorizontal )
            {
                horizontalPath.clear();
            }
            else if( horizontalPath.size() > MIN_PATH_LENGTH )
            {
                break;
            }
        }
        
        if( hasHorizontalPath )         return 0;
        if( hasDiagonalPathHorizontal ) return 1;
        
        return 2;
    }
    
    /**
     * @@For B/W only:@@
     * 
     * Method that gets the vertical path of a section
     * @param section The section of the Picture (composed of Pixels in a 2D array)
     * @param verticalPath The list of Neighbors in this vertical path
     * @param FIRST This determines which direction the path checks for first (either DOWN-LEFT neighbor (-1), DOWN-MIDDLE neighbor (0), or DOWN_RIGHT neighbor (1))
     * @param SECOND Which checks for second
     * @param THIRD Which checks for third
     * @return int 0 --> There is a vertical path
     *             1 --> There is a diagonal vertical path
     *             2 --> There are no vertical or diagonal paths
     */
    private int getVerticalPath( Pixel[][] section, ArrayList<ColorNeighbor> verticalPath, int FIRST, int SECOND, int THIRD )
    {
        int row = 0;
        int col = 0;
        
        boolean hasVerticalPath = false;
        boolean hasDiagonalPathVertical = false;
        
        int sectionStartY = 0;
        final int MIN_PATH_LENGTH = 3;
        
        //Check for vertical path at each col starting pixel
        for( ; col < section[0].length; col++ )
        {
            int pathCol = col;
            boolean foundPath     = false;
            boolean rightPathDead = false;
            
            for( row = 0; row < section.length ; row++ )
            {
                if( !section[row][pathCol].getColor().equals( Color.BLACK ) )
                    break;
                
                foundPath = true;
                
                verticalPath.add( new ColorNeighbor( row, pathCol, true, Color.BLACK ) );
                
                if( row == section.length - 1 ) break;
                
                //Set the x position of the first available neighbor
                if(      getNeighbor( 1, FIRST,  row, pathCol, section, Color.BLACK ) )
                {
                    if(      FIRST == -1 ) pathCol--;
                    else if( FIRST ==  1 ) pathCol++;
                }
                else if( getNeighbor( 1, SECOND, row, pathCol, section, Color.BLACK ) )
                {
                    if(      SECOND == -1 ) pathCol--;
                    else if( SECOND ==  1 ) pathCol++;
                }
                else if( getNeighbor( 1, THIRD,  row, pathCol, section, Color.BLACK ) )
                {
                    if(      THIRD == -1 ) pathCol--;
                    else if( THIRD ==  1 ) pathCol++;
                }
                else if( !rightPathDead &&
                         getNeighbor(  0, 1, row, pathCol, section, Color.BLACK ) ) //traverse right
                {
                    pathCol++;
                    --row;
                }
                else if( getNeighbor( 0, -1, row, pathCol, section, Color.BLACK ) ) //traverse left
                {
                    rightPathDead = true;
                    pathCol--;
                    --row;
                }
                else break; //No paths on this column
                
                //See if a diagonal path exists
                if( !hasDiagonalPathVertical &&
                    (pathCol == sectionStartY || pathCol == sectionStartY + section[0].length - 1) &&
                    verticalPath.size() > MIN_PATH_LENGTH )
                    hasDiagonalPathVertical = true;
            }
            
            if( !foundPath ) continue;
            
            //Check if there is a valid vertical path. If true, stop searching
            if( row == section.length - 1 )
            {
                hasVerticalPath = true;
                break;
            }
            else if( !hasDiagonalPathVertical )
            {
                verticalPath.clear();
            }
            else if( verticalPath.size() > MIN_PATH_LENGTH )
            {
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
    public void makeOpaque()
    {
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
     */
    public void minimizeColors()
    {
        final Color DARK_BLUE = new Color( 0,   0, 139 );
        final Color PURPLE    = new Color( 128, 0, 128 );
        final Color BROWN     = new Color( 102, 51,  0 );
        //The color gray tends to win over most blue colors, so I have found it is better to remove these as options
        Color[] colors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
                           Color.CYAN, DARK_BLUE, Color.MAGENTA, PURPLE,
                           Color.PINK, BROWN, /*Color.LIGHT_GRAY, Color.GRAY,
                           Color.DARK_GRAY,*/ Color.BLACK, Color.WHITE };
        
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ )
        {
            for( int col = 0; col < pixels[0].length; col++ )
            {
                int smallestDistanceIndex = 0;
                double smallestDistance = 255.0 * 3.0;
                Pixel pix = pixels[row][col];
                for( int rep = 0; rep < colors.length; rep++ )
                {
                    double distance = pix.colorDistanceAdvanced( colors[rep] );
                    if( distance < smallestDistance )
                    {
                        smallestDistance = distance;
                        smallestDistanceIndex = rep;
                    }
                }
                
                pix.setColor( colors[ smallestDistanceIndex ] );
            }
        }
    }
    
    /** Method that mirrors the picture around a 
     * horizontal mirror in the center of the picture
     * from left to right */
    public void mirrorHorizontal()
    {
        Pixel[][] pixels = this.getPixels2D();
        Pixel topPixel = null;
        Pixel bottomPixel = null;
        int height = pixels.length;
        for (int col = 0; col < pixels[0].length; col++)
        {
            for (int row = 0; row < height / 2; row++)
            {
                topPixel = pixels[row][col];
                bottomPixel = pixels[height - 1 - row][col];
                bottomPixel.setColor(topPixel.getColor());
            }
        } 
    }

    /** Mirror just part of a picture of a temple */
    public void mirrorTemple()
    {
        int mirrorPoint = 276;
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int count = 0;
        Pixel[][] pixels = this.getPixels2D();

        // loop through the rows
        for (int row = 27; row < 97; row++)
        {
            // loop from 13 to just before the mirror point
            for (int col = 13; col < mirrorPoint; col++)
            {
                leftPixel = pixels[row][col];      
                rightPixel = pixels[row]                       
                [mirrorPoint - col + mirrorPoint];
                rightPixel.setColor(leftPixel.getColor());
            }
        }
    }

    /** Method that mirrors the picture around a 
     * vertical mirror in the center of the picture
     * from left to right */
    public void mirrorVertical()
    {
        Pixel[][] pixels = this.getPixels2D();
        Pixel leftPixel = null;
        Pixel rightPixel = null;
        int width = pixels[0].length;
        for (int row = 0; row < pixels.length; row++)
        {
            for (int col = 0; col < width / 2; col++)
            {
                leftPixel = pixels[row][col];
                rightPixel = pixels[row][width - 1 - col];
                rightPixel.setColor(leftPixel.getColor());
            }
        } 
    }
    
    /**
     * Method to take a piece of a Picture to create a new Picture
     * @param startingRow The top left x coordinate of the new Picture
     * @param startingCol The top left y coordinate of the new Picture
     * @param width The width of the new Picture
     * @param height The height of the new Picture
     */
    public void subPicture( int startingRow, int startingCol, int width, int height )
    {
        Pixel[][] pixels = this.getPixels2D();
        Pixel[][] subPixels = subarray( pixels, startingRow, startingCol, width, height );
        
        Picture finalPic = new Picture( width, height );
        copyTo( subPixels, finalPic );
        
        String name = this.getFileName();
        int extensionIndex = name.indexOf(".");
        finalPic.write(
            name.substring( 0, extensionIndex ) + "_" + width + "x" + height + name.substring( extensionIndex ) );
    }
    
    /**
     * Method to convert a picture to Color.BLACK and Color.WHITE pixels only
     */
    public void toBW()
    {
        final int COLOR_SPLIT = 110;
        
        final int BW_LINE = COLOR_SPLIT*3 - 1;
        
        Pixel[][] pixels = this.getPixels2D();
        for( int row = 0; row < pixels.length; row++ )
        {
            for( int col = 0; col < pixels[0].length; col++ )
            {
                int totalColor = pixels[row][col].getRed() + pixels[row][col].getGreen() + pixels[row][col].getBlue();
                if( totalColor < BW_LINE )
                    pixels[row][col].setColor( Color.BLACK );
                else
                    pixels[row][col].setColor( Color.WHITE );
            }
        }
    }
    
    /** Method to set the red to 0 */
    public void zeroRed()
    {
        Pixel[][] pixels = this.getPixels2D();
        for (Pixel[] rowArray : pixels)
        {
            for (Pixel pixelObj : rowArray)
            {
                pixelObj.setRed(0);
            }
        }
    }

    /** Method to set the green to 0 */
    public void zeroGreen()
    {
        Pixel[][] pixels = this.getPixels2D();
        for (Pixel[] rowArray : pixels)
        {
            for (Pixel pixelObj : rowArray)
            {
                pixelObj.setGreen(0);
            }
        }
    }

    /** Method to set the blue to 0 */
    public void zeroBlue()
    {
        Pixel[][] pixels = this.getPixels2D();
        for (Pixel[] rowArray : pixels)
        {
            for (Pixel pixelObj : rowArray)
            {
                pixelObj.setBlue(0);
            }
        }
    }

    /* Main method for testing - each class in Java can have a main 
     * method
     * 
     * Image names in folder --
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
    public static void main(String[] args)
    {
        Picture flutter = new Picture("rainbowCoat.png");
        //Picture flutter2 = new Picture("rainbowCoat_blur_strong_normal_test.png");
        //Picture flutterDif = new Picture("rainbowCoat_defuzz_difference.png");
        //Picture robot = new Picture("robot.jpg");
        
        //flutter.addGrid( 5/*interval*/, Color.RED );
        //flutter.allToColor( Color.WHITE );
        //flutter.defuzz/*BW*/( 20/*sectionWidth*/, 1/*neighbors*/ );
        //flutter.clearIslands/*BW*/( 100/*islandLowerLimit*/, true/*includeDiagonals*/ );
        //flutter.copy( robot/*Picture to copy and paste into this image*/, 100/*startRow*/, 100/*startCol*/ );
        //flutter.copyTo( robot/*Picture to copy to from this image, 200/*startRow*/, 200/*startCol*/ );
        //flutter.drawPixels( /*ArrayList<ColorNeighbor> pixelList*/ );
        //flutter.edgeDetection( 20/*edge distance*/ );
        //flutter.fillIslands/*BW*/( 10/*gapLowerLimit*/, true/*includeDiagonals*/ );
        //flutter.filterRed();
        //flutter.filterGreen();
        //flutter.filterBlue();
        //flutter.findDifferences( flutter2 ); //@@NOTE: This method automatically saves the result in a new image
        flutter.gaussianBlur( Blur.MILD/*Blur.MILD, Blur.MEDIUM, or Blur.STRONG*/ );
        //flutter.grayscale();
        //flutter.linearize/*BW*/( 1/*lineThickness*/);
        //flutter.makeOpaque();
        //flutter.minimizeColors();
        //flutter.mirrorVertical();
        //flutter.mirrorHorizontal();
        //flutter.subPicture( 100/*startingRow*/, 100/*startingCol*/, 100/*width*/, 100/*height*/ ); //@@NOTE: Autosaves new image
        //flutter.superDefuzz/*BW*/( 20/*sectionWidth*/, 1/*neighbors*/ );
        //flutter.toBW();
        //flutter.zeroRed();
        //flutter.zeroGreen();
        //flutter.zeroBlue();

        //@@DEBUG methods
        //flutter.testSetPath();
        
        //Keep this line -- opens the picture
        flutter.explore();
        
        //flutter.write("rainbowCoat_blur_mild.png");
    }

} // this } is the end of class Picture, put all new methods before this
