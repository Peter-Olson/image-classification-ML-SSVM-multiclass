import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.text.DecimalFormat;

/**
 * This class is used to gather information and data about a Picture in a single O(n^2) sweep
 * 
 * PictureData collects data on the following components of a Picture:
 * 1. List of Unique Colors
 * 2. List of Color Categories found
 * 3. List of Color Categories by Percentage
 * 4. Average Color
 * 5. Range of Colors
 * 6. Contrast Coefficient
 * 7. Saturation Coefficient
 * 8. Saturation Categories
 * 9. Saturation Categories by Percentage
 * 10. Brightness Coefficient
 * 11. Brightness Locators
 * - Darkness Locators
 * - Busyness Coefficient
 * - Busyness Locators
 * - Gradient Directionality
 *
 * @author Peter Olson
 * @version 2/27/23
 */
public class PictureData {
    private DecimalFormat df = new DecimalFormat("0.00");
    
    /* ---------- DATA CATEGORIES ---------- */
    //1. Finding a list of Colors
    public Color[] UNIQUE_COLORS;
    //2. Color Categories and Count
    public LinkedHashMap<Color, Integer> CATEGORY_MAP;
    //3. Color Categories and Count by Percentage
    public LinkedHashMap<Color, Double> CATEGORY_MAP_PERCENT;
    //4. Average Color
    public Color AVERAGE_COLOR;
    //5. Range of Colors - array with 2 elements: [0] -> darkest Color, [1] -> lightest Color
    public Color[] COLOR_RANGE;
    //6. Contrast Coefficient - 0.0 - no contrast, 1.0 high contrast... 0.5 average contrast
    public double CONTRAST_COEFFICIENT;
    private int TOTAL_COLOR_BINS = 16;
    /*The weighting of dark vs light colors for the contrast coefficient. The higher the
     * weight, the more important dark vs light separation is. The lower, the more important
     * dark vs light balance is */
    private int DARK_LIGHT_WEIGHT = 4;
    //7. Saturation Coefficient
    public double SATURATION_COEFFICIENT;
    //8. Saturation Coefficients by Color Category
    public LinkedHashMap<Color, Integer> SATURATION_MAP;
    //9. Saturation Coefficients by Percent
    public LinkedHashMap<Color, Double> SATURATION_MAP_PERCENT;
    //10. Brightness Coefficient
    public double BRIGHTNESS_COEFFICIENT;
    //11. Brightness Locators
    public ArrayList<Point> BRIGHTNESS_LOCATORS;
    public ArrayList<Point> BRIGHTNESS_LOCATORS_INDIV;
    private final int TOTAL_LOCATOR_ROWS, TOTAL_LOCATOR_COLS;
    private final int MIN_BRIGHTNESS_AVG_FOR_LOCATOR = 225;
    
    /**
     * Create the PictureData object by finding all data on the Picture
     * 
     * For the types of data found and set, see the javadoc comment for this class, the field
     * variables, or the README
     * 
     * @param pic The Picture to find data on
     */
    public PictureData( Picture pic ) {
        //Use general color scheme, including DARK_BLUE, PURPLE, and BROWN
        this( pic, new ColorPalette("PALETTE_STANDARD.txt").getAllColors(), 8, 8 );
    }
    
    /**
     * Create the PictureData object by finding all data on the Picture
     * 
     * For the types of data found and set, see the javadoc comment for this class, the field
     * variables, or the README
     * 
     * @param pic The Picture to find data on
     * @param compareScheme The scheme of Colors used for categorical comparisons
     */
    public PictureData( Picture pic, Color[] compareScheme ) {
        this( pic, compareScheme, 8, 8 );
    }
    
    /**
     * Create the PictureData object by finding all data on the Picture
     * 
     * For the types of data found and set, see the javadoc comment for this class, the field
     * variables, or the README
     * 
     * @param pic The Picture to find data on
     * @param compareScheme The scheme of Colors used for categorical comparisons
     * @param locatorRows The total number of rows used for locators
     * @param locatorCols The total number of columns used for locators
     */
    public PictureData( Picture pic, Color[] compareScheme, int locatorRows, int locatorCols ) {
        Pixel[][] pixels = pic.getPixels2D();
        this.TOTAL_LOCATOR_ROWS = locatorRows;
        this.TOTAL_LOCATOR_COLS = locatorCols;
        
        /*----------Data Initialization--------------*/
        //1. Finding a list of Colors
        HashSet<Color> colorList = new HashSet<Color>();
        //2. Finding a list of Color categories and their count
        CATEGORY_MAP = new LinkedHashMap<Color, Integer>();
        //3. Color Categories and Count by Percentage
        CATEGORY_MAP_PERCENT = new LinkedHashMap<Color, Double>();
        //4. Average Color
        int totalRed = 0, totalGreen = 0, totalBlue = 0;
        final int TOTAL_PIXELS = pixels.length * pixels[0].length;
        //5. Range of Colors - array w/ 2 elements: [0] -> darkest Color, [1] -> lightest Color
        COLOR_RANGE = new Color[2];
        Color darkestColor = pixels[0][0].getColor();
        Color lightestColor = pixels[0][0].getColor();
        //6. Contrast Coefficient - see getContrastCoefficient(...) for details
        int[] colorBins = new int[TOTAL_COLOR_BINS];
        //7. Saturation Coefficient
        int totalSaturatedPixels = 0;
        //8. Saturation by Color Category
        SATURATION_MAP = new LinkedHashMap<Color, Integer>();
        //9. Saturation category map by percent
        SATURATION_MAP_PERCENT = new LinkedHashMap<Color, Double>();
        //10. Brightness Coefficient
        double totalAverageBrightness = 0.0;
        //11. Brightness Locators
        BRIGHTNESS_LOCATORS = new ArrayList<Point>();
        BRIGHTNESS_LOCATORS_INDIV = new ArrayList<Point>();
        ArrayList<ArrayList<Point>> cellLocators = new ArrayList<ArrayList<Point>>();
        double[][] cellWeights = new double[ TOTAL_LOCATOR_ROWS ][ TOTAL_LOCATOR_COLS ];
        final int CELL_ROW_HEIGHT = pixels.length / TOTAL_LOCATOR_ROWS;
        final int CELL_COL_WIDTH  = pixels[0].length / TOTAL_LOCATOR_COLS;
        
        //Compute all picture data within a single nested for loop
        for( int row = 0; row < pixels.length; row++ ) {
            //11. Brightness Locators
            int cellX = row / CELL_ROW_HEIGHT;
            if( cellX >= TOTAL_LOCATOR_ROWS )
                cellX = TOTAL_LOCATOR_ROWS - 1;
            else if( row % CELL_ROW_HEIGHT == 0 )
                cellLocators.add( new ArrayList<Point>() );
            for( int col = 0; col < pixels[0].length; col++ ) {
                //Data Available
                Pixel pix = pixels[row][col];
                Color color = pix.getColor();
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int avgRGB = (r + g + b) / 3;
                
                //1. Finding a list of Colors
                colorList.add( color );
                
                //2. Finding a list of Color categories and their count
                updateColorCategories( pix, compareScheme );
                
                //4. Average Color
                totalRed   += r;
                totalGreen += g;
                totalBlue  += b;
                
                //5. Range of Colors
                darkestColor = getDarkerColor( darkestColor, color );
                lightestColor = getLighterColor( lightestColor, color );
                
                //6. Contrast Coefficient
                addToBin( colorBins, avgRGB );
                
                //7. Saturation Coefficient
                if( r == 255 || g == 255 || b == 255 ) {
                    totalSaturatedPixels++;
                //8. Saturation Coefficient by Color Category
                    updateSaturationCategories( r, g, b );
                }
                
                //10. Brightness Coefficient
                totalAverageBrightness += (double)avgRGB / 255;
                
                //11. Brightness Locators
                if( row % CELL_ROW_HEIGHT == 0 && col % CELL_COL_WIDTH == 0 )
                    cellLocators.get(cellX).add( new Point( 0, 0 ) );
                if( avgRGB >= MIN_BRIGHTNESS_AVG_FOR_LOCATOR )
                    updateCellPointBrightnessAverages(
                        row, col, avgRGB, cellLocators, cellWeights,
                        CELL_ROW_HEIGHT, CELL_COL_WIDTH );
            }
        }
        
        /* ----------------- Set Fields ---------------- */
        //1. Finding a list of Colors
        UNIQUE_COLORS = colorList.toArray( new Color[ colorList.size() ] );
        //2. Finding a list of Color categories and their count
        sortColorMapByInt( CATEGORY_MAP );
        //3. Color Categories and Count by Percentage
        setPercentageMap( CATEGORY_MAP, CATEGORY_MAP_PERCENT );
        //4. Average Color
        AVERAGE_COLOR = new Color( totalRed   / TOTAL_PIXELS,
                                   totalGreen / TOTAL_PIXELS,
                                   totalBlue  / TOTAL_PIXELS );
        //5. Range of Colors
        COLOR_RANGE[0] = darkestColor;
        COLOR_RANGE[1] = lightestColor;
        //6. Contrast Coefficient
        CONTRAST_COEFFICIENT = getContrastCoefficient( colorBins, TOTAL_PIXELS );
        //7. Saturation Coefficient
        SATURATION_COEFFICIENT = getSaturationCoefficient( totalSaturatedPixels, TOTAL_PIXELS );
        //8. Saturation by Color Category
        sortColorMapByInt( SATURATION_MAP );
        //9. Saturation map by percent
        setPercentageMap( SATURATION_MAP, SATURATION_MAP_PERCENT );
        //10. Brightness Coefficient
        BRIGHTNESS_COEFFICIENT = totalAverageBrightness / TOTAL_PIXELS;
        //11. Brightness Locators
        setBrightnessLocators( cellLocators, cellWeights, CELL_ROW_HEIGHT, CELL_COL_WIDTH );
    }
    
    /**
     * Print the list of unique colors
     */
    public void printUniqueColorList() {
        for( Color c: UNIQUE_COLORS )
            SOPln( toStringForColor(c) );
    }
    
    /**
     * Print the category map of Color Categories and how many Colors fall into each Category
     */
    public void printCategoryMap() {
        for( Color c : CATEGORY_MAP.keySet() ) {
            int count = CATEGORY_MAP.get(c);
            String name = ColorPalette.findColorName(c);
            if( name.equals("") )
                name = "UNKNOWN Category";
            SOPln( name + ": " + toStringForColor(c) + " - Total pixels: " + count );
        }
    }
    
    /**
     * Print the category map of Color Categories and the percentage that each Color is
     * represented in the Picture
     */
    public void printCategoryMapPercent() {
        for( Color c : CATEGORY_MAP_PERCENT.keySet() ) {
            double percent = CATEGORY_MAP_PERCENT.get(c) * 100.0;
            DecimalFormat df = new DecimalFormat("0.0");
            String name = ColorPalette.findColorName(c);
            if( name.equals("") )
                name = "UNKNOWN Category";
            SOPln( name + ": " + toStringForColor(c) + " - " + df.format( percent ) + "%" );
        }
    }
    
    /**
     * Print the average Color of this Picture
     */
    public void printAverageColor() {
        String name = ColorPalette.findColorName( AVERAGE_COLOR );
        if( name.equals("") )
            name = "UNKNOWN Color";
        SOPln( "The average Color of the Picture is:\n\t" + name +
               ": " + toStringForColor( AVERAGE_COLOR ) );
    }
    
    /**
     * Print the range of Colors of this Picture by printing the darkest Color and the
     * lightest Color
     */
    public void printColorRange() {
        SOPln( "Darkest Color: "    + toStringForColor( COLOR_RANGE[0] ) +
               "\nLightest Color: " + toStringForColor( COLOR_RANGE[1] ) );
    }
    
    /**
     * Print the contrast coefficient for this Picture
     */
    public void printContrastCoefficient() {
        SOPln( "Contrast Coefficient: " + df.format( CONTRAST_COEFFICIENT ) );
    }
    
    /**
     * Print the saturation coefficient for this Picture
     */
    public void printSaturationCoefficient() {
        SOPln( "Saturation Coefficient: " + df.format( SATURATION_COEFFICIENT ) );
    }
    
    /**
     * Print the saturation map of Color Categories and how many Colors are saturated in each
     * Color category
     */
    public void printSaturationMap() {
        for( Color c : SATURATION_MAP.keySet() ) {
            int count = SATURATION_MAP.get(c);
            String name = ColorPalette.findColorName(c);
            if( name.equals("") )
                name = "UNKNOWN Category";
            SOPln( name + ": " + toStringForColor(c) + " - Total pixels: " + count );
        }
    }
    
    /**
     * Print the saturation map of Color categories and the percentage saturation of each
     * Color category in the picture
     */
    public void printSaturationMapPercent() {
        for( Color c : SATURATION_MAP_PERCENT.keySet() ) {
            double percent = SATURATION_MAP_PERCENT.get(c) * 100.0;
            DecimalFormat df = new DecimalFormat("0.0");
            String name = ColorPalette.findColorName(c);
            if( name.equals("") )
                name = "UNKNOWN Category";
            SOPln( name + ": " + toStringForColor(c) + " - " + df.format( percent ) + "%" );
        }
    }
    
    /**
     * Print the brightness coefficient for this Picture
     * 
     * The brightness coefficient is a ratio that describes how bright or dark the image is.
     * A coefficient of 0.0 is absolute darkness (black). A coefficient of 1.0 is absolute
     * brightness (white)
     */
    public void printBrightnessCoefficient() {
        SOPln( "Brightness Coefficient: " + df.format( BRIGHTNESS_COEFFICIENT ) );
    }
    
    /**
     * Print the bright locators Points for this Picture
     * 
     * These Points are the approximated centerpoints of brightness that were found throughout
     * cells (whose sizes are defined by the user) and aggregated using weight averages. It is
     * possible for the exact location of a brightness Point to not be centered directly on a
     * Pixel that is bright, because of these averages
     */
    public void printBrightnessLocators() {
        SOPln("Brightness locators:");
        for( Point p : BRIGHTNESS_LOCATORS ) {
            SOPln("(" + p.x + ", " + p.y + ")");
        }
    }
    
    /**
     * Print the bright locators individual Points for this Picture
     * 
     * These Points are the locations of brightness that were found throughout the Picture
     */
    public void printIndividualBrightnessLocators() {
        SOPln("Brightness locators:");
        for( Point p : BRIGHTNESS_LOCATORS_INDIV ) {
            SOPln("(" + p.x + ", " + p.y + ")");
        }
    }

    /**
     * Set the brightness locators based on the cell locators and their weights
     * 
     * In order to detect brightness areas that stretch beyond cell dimensions, a radial-based
     * algorithm is applied based on the cell width and height. Any cell locators that are
     * within the greater of the cell width or height in range are grouped to a single
     * brightness spot. The average of these groups becomes a single brightness locator for a
     * region. There may be multiple brightness locators per picture, or just 1, or none
     * 
     * @param cellLocators The 2D list of aggregate cell locator weighted coordinates. Each
     *                     coordinate corresponds with average brightness of a cell that has
     *                     one or more pixels exceeding the minimum brightness threshhold.
     *                     Note that all cells have a Point, but any cells whose
     *                     weighted average Point is (0, 0) is a cell that does not have any
     *                     pixels whose minimum brightness exceeds the minimum brightness
     *                     threshhold. These are discarded and are not used within this method.
     *                     Also, it is worth noting that these aggregate cell locator weighted
     *                     coordinates have not been averaged yet. This occurs within this
     *                     method when these Points are divided by the total cell weights,
     *                     producing a weighted mean cell brightness locator Point.
     * @param cellWeights The 2D array of aggregate cell pixel brightness weights. Each cell's
     *                    pixel brightness weights are added together and saved at each cell
     *                    coordinate location. These are used to produce the weighted mean cell
     *                    brightness locator Points by dividing the cell locator weighted
     *                    coordinates by the aggregate brightness weights
     *                    
     *                    cellCoordX = Summation[n=0,k=CELL_ROW_HEIGHTxCELL_COL_WIDTH]
     *                                      {px[n]*p_weight[n]}
     *                    cellCoordY = Summation[n=0,k=CELL_ROW_HEIGHTxCELL_COL_WIDTH]
     *                                      {py[n]*p_weight[n]}
     * @param CELL_ROW_HEIGHT The height of each row, in pixels. The smaller of this variable or
     *                        CELL_COL_WIDTH is used as the radius for grouping cell locator
     *                        Points
     * @param CELL_COL_WIDTH The width of each column, in pixels. See notes on CELL_ROW_HEIGHT
     */
    private void setBrightnessLocators( ArrayList<ArrayList<Point>> cellLocators,
                                        double[][] cellWeights,
                                        int CELL_ROW_HEIGHT, int CELL_COL_WIDTH ) {
        int currentTotalRows = cellLocators.size();
        int currentTotalCols = cellLocators.get(0).size();
        
        //Get brightness spots
        LinkedList<Point> brightnessSpots = new LinkedList<Point>();
        for( int row = 0; row < currentTotalRows; row++ ) {
            for( int col = 0; col < currentTotalCols; col++ ) {
                Point p = cellLocators.get(row).get(col);
                if( p.x == 0 && p.y == 0 )
                    continue;
                p.x = (int)(p.x / cellWeights[row][col]);
                p.y = (int)(p.y / cellWeights[row][col]);
                brightnessSpots.add( p );
                BRIGHTNESS_LOCATORS_INDIV.add( p );
            }
        }
        
        int locatorRadius = CELL_ROW_HEIGHT > CELL_COL_WIDTH ? CELL_COL_WIDTH : CELL_ROW_HEIGHT;
        
        //Find average brightness spots, grouped by radial proximity equal to the locatorRadius
        while( !brightnessSpots.isEmpty() ) {
            Point head = brightnessSpots.peek();
            int avgX = head.x;
            int avgY = head.y;
            int size = brightnessSpots.size();
            int groupTotal = 1;
            //Compare head Point against all others. If a Point is within the locatorRadius,
            //it is added to the final list of brightness Points
            for( int rep = 1; rep < size; rep++ ) {
                Point next = brightnessSpots.get(rep);
                if( distance( head, next ) <= locatorRadius ) {
                    avgX += next.x;
                    avgY += next.y;
                    brightnessSpots.remove( rep );
                    --rep;
                    --size;
                    groupTotal++;
                }
            }
            if( avgX == head.x && avgY == head.y ) { //If the Point isn't close to any others
                BRIGHTNESS_LOCATORS.add( new Point( avgX, avgY ) );
                brightnessSpots.pop();
                continue;
            } else //Pop the head Point off
                brightnessSpots.pop();
            avgX /= groupTotal;
            avgY /= groupTotal;
            BRIGHTNESS_LOCATORS.add( new Point( avgX, avgY ) );
        }
    }
    
    /**
     * Get the distance between two Points
     * 
     * @param p1 The first Point
     * @param p2 The second Point
     * @return double The distance between the two Points
     */
    private double distance( Point p1, Point p2 ) {
        return Math.sqrt( Math.pow( p2.x - p1.x, 2 ) + Math.pow( p2.y - p1.y, 2 ) );
    }
    
    /**
     * Find the correct cell as designated by the positions and update the weighted average
     * pixel coordinate based on...
     *  1) meeting the minimum brightness threshhold
     *  2) the brightness of the pixel coordinate, weighted from 0.0 to 1.0, where 1.0 is equal
     *     to the pixel with RGB (255, 255, 255) and 0.0 is equal to the pixel with RGB
     *     (MIN_BRIGHTNESS.., MIN_BRIGHTNESS.., MIN_BRIGHTNESS)
     * This produces the weighted coordinate mean, which is used to determine the centerpoint
     * of brightness for the given cell
     * 
     * @param row The row of the Pixel
     * @param col The col of the Pixel
     * @param avgRGB The average RGB value of the pixel
     * @param cellLocators A nested ArrayList of Points, where each point holds the total
     *                     average weighted coordinates of the pixels that meet the brightness
     *                     minimum requirements
     * @param cellWeights A 2D array of each cell's total weights of the pixels that meet the
     *                    minimum brightness requirements. Each pixel weight is based on how
     *                    close the pixel's weight is to MIN_BRIGHTNESS.. and 255
     * @param CELL_ROW_HEIGHT The height of each row, in pixels
     * @param CELL_COL_WIDTH The weidth of each column, in pixels
     */
    private void updateCellPointBrightnessAverages(
            int row, int col, int avgRGB,
            ArrayList<ArrayList<Point>> cellLocators, double[][] cellWeights,
            int CELL_ROW_HEIGHT, int CELL_COL_WIDTH ) {
        double weight = getPixelBrightnessWeight( avgRGB );
        Point brightPoint = new Point( row, col );
        brightPoint.x *= weight;
        brightPoint.y *= weight;
        int cellX = row / CELL_ROW_HEIGHT;
        if( cellX >= TOTAL_LOCATOR_ROWS )
            cellX = TOTAL_LOCATOR_ROWS - 1;
        int cellY = col / CELL_COL_WIDTH;
        if( cellY >= TOTAL_LOCATOR_COLS )
            cellY = TOTAL_LOCATOR_COLS - 1;
        Point totalCoordPixelForCell =
            new Point( cellLocators.get(cellX).get(cellY).x + brightPoint.x,
                       cellLocators.get(cellX).get(cellY).y + brightPoint.y );
        cellLocators.get( cellX ).set( cellY, totalCoordPixelForCell );
        cellWeights[cellX][cellY] += weight;
    }
    
    /**
     * Get the weight of this pixel's brightness based on its average RGB value. A pixel whose
     * Color's average RGB is equal to MIN_BRIGHTNESS.. receives a weight of 0.0. The maximum
     * weight is 1.0 which would be the color (255, 255, 255)
     * 
     * @param avgRGB The average RGB value of the pixel's Color
     * @return double A value from 0.0 to 1.0 based on how close the avgRGB value is to the
     *                maximum and minimum brightness values
     */
    private double getPixelBrightnessWeight( int avgRGB ) {
        final int MAX_BRIGHTNESS_AFTER_MIN_CORRECTION = 255 - MIN_BRIGHTNESS_AVG_FOR_LOCATOR;
        avgRGB -= MIN_BRIGHTNESS_AVG_FOR_LOCATOR;
        return (double)avgRGB / MAX_BRIGHTNESS_AFTER_MIN_CORRECTION;
    }
    
    /**
     * Get the saturation coefficient by finding the percentage of pixels that are saturated
     * 
     * @param totalSaturatedPixels The total number of pixels that are saturated in the Picture.
     *                             A saturated Pixel is any pixel that has one or more
     *                             components at its maximum value
     * @param TOTAL_PIXELS The total number of pixels in the Picture
     * @return double The saturation coefficient, where 0.0 represents 0% saturated pixels,
     *                and 1.0 represents 100% saturated pixels
     */
    private double getSaturationCoefficient( int totalSaturatedPixels, int TOTAL_PIXELS ) {
        return (double)totalSaturatedPixels / TOTAL_PIXELS;
    }
    
    /**
     * Get the contrast coefficient by using color bin counts, BIN_GROUP_SIZE averages, and
     * weights based on the bins. See PictureData constructor for details
     * 
     * The contrast coefficient is a decimal value between 0.0 and 1.0 (inclusive for both),
     * where 0.0 represents no contrast, and 1.0 represents absolute contrast (black and
     * white), and anything inbetween is a spectrum of contrast
     * 
     * In order to determine this coefficient, a series of steps are followed to compute
     * an algorithm:
     * 1. Find the average r, g, or b component of each Color
     * 2. Create TOTAL_COLOR_BINS 'bins', ranging from 0 to 255. Each bin spans
     *    TOTAL_PIXELS / TOTAL_COLOR_BINS pixels. Some of these bins will designate dark
     *    Colors, the others will designate light Colors. This will depend on the midpoint
     *    of the weights across all of the bins
     * 3. Make counts of where each Color would go, keeping track of the total Colors that
     *    would be in each bin. The actual Color objects don't need to be kept track of,
     *    just the counts
     * 4. After all Colors are counted in the nested for loop, find the percentage of each
     *    bin count compared to the total count
     * 5. Find the fulcrum point that separates the designated dark Colors from the light
     *    Colors. The fulcrum is the midpoint where the weights in bins left of the fulcrum
     *    are equivalent in weight (or approximately equivalent) to the weights in bins
     *    right of the fulcrum. This point is an index, not a margin, where the index is
     *    exclusive to the dark Color bins, and inclusive to the right Color bins.
     *    Eg. The weights across the bins finds index 5 (out of 16) to be the midpoint.
     *        This means that indices 0 to 4 (inclusive) specify the dark bins, and 5 to
     *        15 (inclusive) specify the light bins
     *    As an added note, the fulcrum point cannot be set any more left than the
     *    BIN_GROUP_SIZE, or any more right than the TOTAL_COLOR_BINS - BIN_GROUP_SIZE
     * 6. Find the the largest average percentage count among sets of BIN_GROUP_SIZE
     *    adjacent bins.
     *    This becomes db = the average weight for the largest weight set for dark colors
     * 7. Do the same for the light bins. lb = the avg weight for the largest weight set for
     *    light colors
     * 8. Get the dark weight by determining how far from the left-most bin set the bins are
     *    dw = 1.0 for the far-left bin set; 0.9 for second from left, 0.8 for third, etc
     * 9. Get the light weight in the same manner. lw = 1.0 for the far-right bin set, 0.9
     *    for the second from right set, etc
     * 10. In order to get a decimal number for the bin coefficient, find the smaller of the
     *     two values between db and lb:
     *         dl_coefficient = lb > db ? db/lb : lb/db;
     * 11. Thus, dl_coefficient is a measurement of the proportion of total dark pixels
     *     compared to total light pixels, where the constant must be less than 1.0 and
     *     greater than 0.0.
     *     However, it was found that the dl_coefficient is much less important to
     *     determining contrast as opposed to the highest average bin weight location for
     *     both the dark and light bins. A picture whose average dark bin weight is closer
     *     to (0, 0, 0) and whose average light bin weight is closer to (255, 255, 255) is
     *     a more highly contrasted image. A picture that is much more dark than light is
     *     counter-balanced by the fulcrum movement and the dl_coefficient.
     *     Lastly, in order to stress the importance of the dw and lw values, the field
     *     DARK_LIGHT_WEIGHT gives weight to these values. The higher this values, the
     *     less important the dl_coefficient is
     * 12. Contrast Coefficient = (dl_coefficient * DARK_LIGHT_WEIGHT * (0.5)(dw + lw));
     * 
     * I will attempt to draw this here as a chart (Note that I didn't bother adding up
     * the percentages, but they should add up to 100%):
     * 
     * Example
     * 
     * Bin Weight       Dark Bins         Light Bins         ________
     * 50%|       _____              |                      /        \
     * 45%|      /     \             |                       lb = 0.2
     * 40%|     db = 0.25            |                               X
     * 35%|                          |                               X
     * 30%|      X                   |                               X
     * 25%|      X     X             |                               X
     * 20%|      X  X  X             |                   X           X
     * 15%|      X  X  X             |               X   X   X       X
     * 10%|   X  X  X  X             |               X   X   X       X
     * 5% |X  X  X  X  X  X      X   |   X   X       X   X   X   X   X
     * 0% |X  X  X  X  X  X  X   X   |   X   X   X   X   X   X   X   X
     * ------------------------------------------------------------------
     *    0 16 32 48 64 80 96 112 127 128 144 160 176 192 208 224 240 255
     *                          Bin Counts
     *           |_____|                                     |_______|
     *              > dw = 0.8                                  > lw = 1.0
     * 
     * dl_coefficient = lb > db ? db / lb : lb / db; // --> evals to 0.2 / 0.25 = 0.8
     * contrast_coeff = (dl_coefficient + (1.0 / 2.0) * (dw + lw) * DARK_LIGHT_WEIGHT)
     *                  / (DARK_LIGHT_WEIGHT + 1);
     * 
     * --> contrast_coeff = (0.8 + 0.5 * (1.8) * 4) / 5 = 0.88
     * 
     * @param colorBins The array of color counts falling into TOTAL_COLOR_BINS different bins
     * @param TOTAL_PIXELS The total number of pixels in the Picture
     * @return double The contrast coefficient, a decimal between 0.0 and 1.0, inclusive, where
     *                0.0 represents no contrast, and 1.0 represents absolute contrast (black
     *                and white)
     */
    private double getContrastCoefficient( int[] colorBins, int TOTAL_PIXELS ) {
        final int BIN_GROUP_SIZE = 3;
        
        //Check against total color counts
        if( UNIQUE_COLORS.length == 2 )
            return 1.0;
        else if( UNIQUE_COLORS.length == 1 )
            return 0.0;
        
        //Adjust contrastCoefficient if a small palette is used
        final int MIN_COLOR_COUNT_FOR_FULL_PALETTE = 6;
        final int MIN_BINS = 2;
        if( UNIQUE_COLORS.length < MIN_COLOR_COUNT_FOR_FULL_PALETTE )
            TOTAL_COLOR_BINS = MIN_BINS;
            
        //Get bin percentage weights
        double[] binPercentages = new double[ TOTAL_COLOR_BINS ];
        for( int rep = 0; rep < binPercentages.length; rep++ )
            binPercentages[ rep ] = (double)colorBins[ rep ] / (double)TOTAL_PIXELS;
        /*Find the fulcrum point, so that the weight of dark colors and light colors is
          evenly distributed. This should only be changed if the total color count is greater
          than the MIN_COLOR_COUNT_FOR_FULL_PALETTE*/
        int darkBoundBinIndex = BIN_GROUP_SIZE;
        int lightBoundBinIndex = TOTAL_COLOR_BINS - BIN_GROUP_SIZE;
        int dl_fulcrum = TOTAL_COLOR_BINS / 2; //Marks the index where dark bins and light bins
        double darkBinTotalWeight = 0.0;       //are evenly separated by weight
        double lightBinTotalWeight = 0.0;
        if( UNIQUE_COLORS.length >= MIN_COLOR_COUNT_FOR_FULL_PALETTE ) {
            darkBinTotalWeight += binPercentages[ darkBoundBinIndex ];
            lightBinTotalWeight += binPercentages[ lightBoundBinIndex ];
            while( darkBoundBinIndex + 1 < lightBoundBinIndex ) {
                if( darkBinTotalWeight <= lightBinTotalWeight ) {
                    darkBoundBinIndex++;
                    darkBinTotalWeight += binPercentages[ darkBoundBinIndex ];
                } else {
                    lightBoundBinIndex--;
                    lightBinTotalWeight += binPercentages[ lightBoundBinIndex ];
                }
            }
            dl_fulcrum = lightBoundBinIndex;
        }
            
        /*Find highest percentage BIN_GROUP_SIZE-bin average weight, or single-bin for small
          palettes*/
        //Dark bin highest average percent weight
        double db = 0.0;
        int dbBinIndex = 0;
        if( TOTAL_COLOR_BINS >= MIN_COLOR_COUNT_FOR_FULL_PALETTE )
            for( int rep = 0; rep < dl_fulcrum - BIN_GROUP_SIZE + 1; rep++ ) {
                double avgBinPercent =
                    (binPercentages[rep] + binPercentages[rep+1] + binPercentages[rep+2])
                    / BIN_GROUP_SIZE;
                if( avgBinPercent > db ) {
                    db = avgBinPercent;
                    dbBinIndex = rep;
                }
            }
        else { //Bin size less than BIN_GROUP_SIZE for darks
            double totalDarkBinPercent = 0.0;
            for( int rep = 0; rep < dl_fulcrum; rep++ )
                totalDarkBinPercent += binPercentages[rep];
            db = totalDarkBinPercent / dl_fulcrum;
        }
        
        //Light bin highest average percent weight
        double lb = 0.0;
        int lbBinIndex = 0;
        if( TOTAL_COLOR_BINS >= MIN_COLOR_COUNT_FOR_FULL_PALETTE )
            for( int rep = dl_fulcrum;
                 rep < binPercentages.length - BIN_GROUP_SIZE + 1;
                 rep++ ) {
                double avgBinPercent =
                    (binPercentages[rep] + binPercentages[rep+1] + binPercentages[rep+2])
                    / BIN_GROUP_SIZE;
                if( avgBinPercent > lb ) {
                    lb = avgBinPercent;
                    lbBinIndex = rep;
                }
            }
        else { //Bin size less than BIN_GROUP_SIZE for lights
            double totalLightBinPercent = 0.0;
            for( int rep = dl_fulcrum; rep < binPercentages.length; rep++ )
                totalLightBinPercent += binPercentages[rep];
            lb = totalLightBinPercent / dl_fulcrum;
        }
        
        /*Scale bin indices - Bins can only range from 0 to half, or half or last index,
          but bin indices are used to get a weight range between 1.0 and 0.0, so scale
          ranges to fit the full 0.0 to 1.0 possibility instead of 0.5 to 1.0
          If the only option is a single bin group, the lw or dw gets set to 0.0*/
        //Get bin weights
        double lw = 0.0;
        if( dl_fulcrum != TOTAL_COLOR_BINS - BIN_GROUP_SIZE )
            lw = ((double)((double)(lbBinIndex - dl_fulcrum)
                                 / (TOTAL_COLOR_BINS - dl_fulcrum - BIN_GROUP_SIZE)));
        double dw = 0.0;
        if( dl_fulcrum != BIN_GROUP_SIZE )
            dw = 1.0 - ((double)(dbBinIndex) / (dl_fulcrum - BIN_GROUP_SIZE) );
        
        //Get dark:light coefficient (or light:dark)
        double dl_coeff = lb > db ? db / lb : lb / db;
        
        //Get contrast coefficient - This is assigned to the global var elsewhere
        double contrastCoefficient =
            (dl_coeff + DARK_LIGHT_WEIGHT * (0.5) * (dw + lw)) / (DARK_LIGHT_WEIGHT + 1);
        
        return contrastCoefficient;
    }
    
    /**
     * Find the bin that the Color belongs in based on its averageRGB value
     * 
     * @param colorBins The bins that contain the total counts of colors in those bins
     * @param avgRGB The average RGB value of the Color to be placed in a bin
     */
    private void addToBin( int[] colorBins, int avgRGB ) {
        final int MAX = 255;
        final int MIN = 0;
        int BIN_SIZE_INC = (MAX - MIN + 1) / colorBins.length;
        for( int binNumber = 0, BIN_SIZE = BIN_SIZE_INC;
             binNumber < colorBins.length;
             binNumber++, BIN_SIZE += BIN_SIZE_INC ) {
            if( avgRGB < BIN_SIZE ) {
                colorBins[ binNumber ]++;
                break;
            }
        }
    }
    
    /**
     * Finds and returns the lighter of the two Colors
     * 
     * Color lightness is determined by the highest red, green, or blue value of the Color.
     * If two Colors are tied with the highest component, then the next component value is
     * looked at, and so on
     * 
     * @param first The first Color to compare
     * @param second The second Color to compare
     * @return Color The lighter of the two Colors
     */
    private Color getLighterColor( Color first, Color second ) {
        int[] firstOrder = getOrderedRGB( first );
        int[] secondOrder = getOrderedRGB( second );
        if( firstOrder[0] > secondOrder[0] )
            return first;
        else if( firstOrder[0] < secondOrder[0] )
            return second;
        else if( firstOrder[1] > secondOrder[1] )
            return first;
        else if( firstOrder[1] < secondOrder[1] )
            return second;
        else if( firstOrder[2] > secondOrder[2] )
            return first;
        else
            return second;
    }
    
    /**
     * Finds and returns the darker of the two Colors
     * 
     * Color darkness is determined by the lowest red, green, or blue value of the Color.
     * If two Colors are tied with the lowest component, then the next component value is
     * looked at, and so on
     * 
     * @param first The first Color to compare
     * @param second The second Color to compare
     * @return Color The darker of the two Colors
     */
    private Color getDarkerColor( Color first, Color second ) {
        int[] firstOrder = getOrderedRGB( first );
        int[] secondOrder = getOrderedRGB( second );
        if( firstOrder[2] > secondOrder[2] )
            return second;
        else if( firstOrder[2] < secondOrder[2] )
            return first;
        else if( firstOrder[1] > secondOrder[1] )
            return second;
        else if( firstOrder[1] < secondOrder[1] )
            return first;
        else if( firstOrder[0] > secondOrder[0] )
            return second;
        else
            return first;
    }
    
    /**
     * Get an ordered array of the Colors RGB values from greatest to least in value
     * 
     * @param color The Color to get an ordered RGB array from
     * @return orderedRGB The ordered array of RGB components (3 total) from greatest to least
     *                    in value
     */
    private int[] getOrderedRGB( Color color ) {
        int[] orderedRGB = new int[3];
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        orderedRGB[0] = r >= g && r >= b ? r : (g >= b && g >= r ? g : b);
        orderedRGB[1] = r<g&&r>=b?r:(r<b&&r>=g?r:(g<r&&g>=b?g:(g<b&&g>=r?g:b)));
        orderedRGB[2] = r < g && r < b ? r : (g < r && g < b ? g : b);
        return orderedRGB;
    }
    
    /**
     * Convert a Color to a String format
     */
    private String toStringForColor( Color c ) {
        return "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
    }
    
    /**
     * Set the entries for colorMapByPercent based on the counts in colorMap
     * 
     * @param colorMap The map of Color keys and their total counts found
     * @param colorMapByPercent The map of Color keys and their percentage makeup in the Picture
     */
    private void setPercentageMap( LinkedHashMap<Color, Integer> colorMap,
                                   LinkedHashMap<Color, Double> colorMapByPercent ) {
        int totalColorCount = 0;
        ArrayList<Integer> colorCountList = new ArrayList<Integer>();
        //Find total color count
        for( Map.Entry entry : colorMap.entrySet() ) {
            int count = (Integer)entry.getValue();
            totalColorCount += count;
            colorCountList.add( count );
        }
        //Get percentages and update map
        int index = 0;
        for( Map.Entry entry : colorMap.entrySet() ) {
            double count = (double)((Integer)entry.getValue());
            colorMapByPercent.put( (Color)entry.getKey(),
                                   count / (double)totalColorCount );
        }
    }
    
    /**
     * Update the CATEGORY_MAP with the new Color
     * 
     * @param pix The Pixel whose Color is being looked at
     * @param compareScheme The list of Colors being compared
     * @see getClosestColor(...)
     */
    private void updateColorCategories( Pixel pix, Color[] compareScheme ) {
        Color color = getClosestColor( pix, compareScheme );
        if( CATEGORY_MAP.containsKey( color ) ) {
            int count = CATEGORY_MAP.get( color );
            CATEGORY_MAP.replace( color, count, count + 1 );
        } else {
            CATEGORY_MAP.put( color, 1 );
        }
    }
    
    /**
     * Update the SATURATION_MAP with the new Color and increase its count
     * 
     * @param r The red component of the Color
     * @param g The green component of the Color
     * @param b The blue component of the Color
     */
    private void updateSaturationCategories( int r, int g, int b ) {
        //Adjust for saturation category
        if( r != 255 ) r = 0;
        if( g != 255 ) g = 0;
        if( b != 255 ) b = 0;
        Color color = new Color(r,g,b);
        
        //Update map
        if( SATURATION_MAP.containsKey( color ) ) {
            int count = SATURATION_MAP.get( color );
            SATURATION_MAP.replace( color, count, count + 1 );
        } else {
            SATURATION_MAP.put( color, 1 );
        }
    }
    
    /**
     * Method that finds the closest Color to this Pixel's Color within the list of Colors
     * This is a color difference algorithm that uses Pixel's colorDistanceAdvanced(...)
     * method
     * 
     * @param pix The Pixel to compare and changes colors
     * @param colors A list of Colors, which is being compared against
     * @return Color The Color that this Pixel's Color is closest to (in rgb distance)
     */
    private Color getClosestColor( Pixel pix, Color[] colors ) {
        int smallestDistanceIndex = 0;
        double smallestDistance = 255.0 * 3.0;
        for( int rep = 0; rep < colors.length; rep++ )
        {
            double distance = pix.colorDistanceAdvanced( colors[rep] );
            if( distance < smallestDistance )
            {
                smallestDistance = distance;
                smallestDistanceIndex = rep;
            }
        }
        
        return colors[ smallestDistanceIndex ];
    }
    
    /**
     * Sort a LinkedHashMap by its value (for Integers)
     * 
     * @param map A LinkedHashMap of Color keys, and Integer values
     */
    private void sortColorMapByInt( LinkedHashMap<Color, Integer> map ) {
        Set<Map.Entry<Color, Integer>> entries = map.entrySet();
        Object[] a = entries.toArray();
        Arrays.sort( a, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                return ((Map.Entry<Color, Integer>) o2).getValue()
                           .compareTo(((Map.Entry<Color, Integer>) o1).getValue());
            }
        });
        map.clear();
        for( Object entry : a )
            map.put( (Color)(((Map.Entry)entry).getKey()),
                     (Integer)(((Map.Entry)entry).getValue()) );
    }
    
    private void SOPln( String str ) {
        System.out.println( str );
    }
    private void SOPln() {
        System.out.println("");
    }
    private void SOP( String str ) {
        System.out.print( str );
    }
}
