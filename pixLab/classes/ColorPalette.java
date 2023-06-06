import java.awt.Color;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ListIterator;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Contains information for color schemes using lists of Colors (palettes)
 *
 * @author Peter Olson
 * @version 2/19/23
 */
public class ColorPalette {
    //Default Palette
    private final String DEFAULT_PALETTE_FILE_NAME = "PALETTE_RESURRECT.txt";
    
    //Master Color List File
    private static final String MASTER_COLOR_LIST_FILE_NAME = "MASTER_COLOR_LIST.txt";
   
    //Palette Studio - Contains the list of color palettes
    private ArrayList<StrColor[][]> studio = new ArrayList<StrColor[][]>();
    
    //Selected colors, schemes, and palettes
    private StrColor     currentColor;
    private StrColor[]   currentScheme;
    private StrColor[][] currentPalette;
    
    /**
     * Create a default ColorPalette, which uses the DEFAULT_PALETTE_FILE_NAME palette of
     * Colors
     * 
     * @see getPaletteFromFile( String fileName )
     * @see setup()
     */
    public ColorPalette() {
        this.currentPalette = getPaletteFromFile( DEFAULT_PALETTE_FILE_NAME );
        setup();
    }
    
    /**
     * Create a ColorPalette from the given palette. A palette is a list of color schemes, and
     * a color scheme is a list of Colors. Thus, a palette is a 2D list of colors.
     * 
     * Color schemes are categories of colors that are grouped together within the palette.
     * Eg. Shades of blue, warm colors, dark colors, etc
     * 
     * @param palette The custom palette to use
     * @see setup()
     */
    public ColorPalette( StrColor[][] palette ) {
        this.currentPalette = palette;
        setup();
    }
    
    /**
     * Create a ColorPalette from a text file. A palette is a list of color schemes, and
     * a color scheme is a list of Colors. Thus, a palette is a 2D list of colors.
     * 
     * Color schemes are categories of colors that are grouped together within the palette.
     * Eg. Shades of blue, warm colors, dark colors, etc
     * 
     * The text file format for creating a palette should look like the following:
     * 
     *      SchemeName      //First line of file       Eg. RED_HUES
     *      ColorName R G B                                DARK_RED 139 0 0
     *      ColorName R G B                                CHERRY 255 87 51
     *      ...                                            ...
     *                      //Blank line separating schemes
     *      SchemeName                                     ORANGE_HUES
     *      ColorName R G B                                GINGER_ORANGE 176 101 0 
     *      ColorName R G B                                HONEY 255 189 49
     *      ...                                            ...
     *      
     *      etc                                            etc
     * 
     * The name of the color palette text file must have the following form:
     *      
     *      PALETTE_[PALETTE_NAME].txt          eg. PALETTE_RAINBOW.txt
     * 
     * @param fileName The text file that has the color palette information
     * @see getPaletteFromFile( String fileName )
     * @see setup()
     */
    public ColorPalette( String fileName ) {
        this.currentPalette = getPaletteFromFile( fileName );
        setup();
    }
    
    /**
     * Initialize common properties and data for this ColorPalette
     * 
     * @see getRandomScheme()
     * @see getRandomColor()
     * @see setMasterList()
     */
    private void setup() {
        this.currentScheme  = getRandomScheme();
        this.currentColor   = getRandomColor();
        studio.add( currentPalette );
        setMasterList();
    }
    
    /**
     * Set the master color list text file by adding any 'new' colors. A 'new' color is any
     * StrColor whose RGB has not been seen before. If there is a repeated RGB, or a repeated
     * StrColor name that has been seen before, the user is notified which Colors have been
     * seen before, including their name and rgb value.
     * 
     * @see sortStrColorArrayByName( StrColor[] colorList )
     * @see addToMasterList( LinkedList<StrColor> masterList, StrColor[] colorList )
     * @see convertMasterListToText( LinkedList<StrColor masterList )
     */
    private void setMasterList() {
        LinkedList<StrColor> masterList = getMasterList();
        StrColor[] colorList = getAllColors();
        sortStrColorArrayByName( colorList );
        
        String issues = addToMasterList( masterList, colorList );
        if( !issues.equals("") )
            SOPln("Found the following Color conflicts:\n\n" + issues );
            
        String text = convertMasterListToText( masterList );
        writeToFile( MASTER_COLOR_LIST_FILE_NAME, text );
    }
    
    /**
     * Gets the colors from the master color list text file and converts it to a LinkedList
     * 
     * @return LinkedList<StrColor> The master list of StrColors
     */
    private LinkedList<StrColor> getMasterList() {
        LinkedList<StrColor> masterList = new LinkedList<StrColor>();
        Scanner scanner = getScanner( MASTER_COLOR_LIST_FILE_NAME );
        while( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            StrColor color;
            if( tokens.length == 2 )
                color = new StrColor( Color.decode( tokens[1] ), tokens[0] );
            else
                color = new StrColor( Integer.parseInt( tokens[1] ), //r
                                      Integer.parseInt( tokens[2] ), //g
                                      Integer.parseInt( tokens[3] ), //b
                                      tokens[0] );                   //NAME
            masterList.add( color );
        }
        scanner.close();
        
        return masterList;
    }
    
    /**
     * Add the alphabetized array of StrColors to the master list
     * 
     * This method guarantees that the resulting master list will still remain in alphabetical
     * order according to the StrColor's NAME.
     * 
     * If a StrColor that is trying to be added has the same NAME or RGB values as another
     * StrColor that is already in the master list, the StrColor is not added and it is noted
     * to the user the list of StrColors that share either the same NAME as another StrColor
     * that is already on the master list, or another StrColor that shares the same RGB values
     * 
     * @param masterList The list of StrColors that belong to the master list
     * @param colorList The array of StrColors to add to the master list of Colors
     * @return String The list of Color conflicts as they relate to the master list.
     */
    private String addToMasterList( LinkedList<StrColor> masterList, StrColor[] colorList ) {
        String issuesList = "";
        int totalConflicts = 0;
        ListIterator iter = masterList.listIterator(0);
        boolean checkNewElement = false;
        HashSet<String> colorRGBList = getColorRGBList( masterList );
        
        for( int addIndex = 0; iter.hasNext() && addIndex < colorList.length; ) {
            StrColor masterListColor;
            if( checkNewElement ) //Get the newly added StrColor to check against
                masterListColor = (StrColor)iter.previous();
            else
                masterListColor = (StrColor)iter.next();
            StrColor addListColor = colorList[ addIndex ];
            
            //Check for existing Colors with the same RGB
            String addListColorRGB = "" +  addListColor.getRed() +
                                     " " + addListColor.getGreen() +
                                     " " + addListColor.getBlue();
            
            int compareResult = masterListColor.NAME.compareToIgnoreCase( addListColor.NAME );
            
            //Check if the two Colors have the same RGB values or same names
            if( colorRGBList.contains( addListColorRGB ) || compareResult == 0 ) {
                String conflictNote;
                if( compareResult == 0 ) {
                    conflictNote =
                    "Master List Color: " + masterListColor.toString() + "\n and new Color: " +
                    addListColor.toString() + " share the same name.\n" +
                    addListColor.NAME + " was not added to the Master Color List.\n\n";
                } else {
                    StrColor masterColor = findColorInList( masterList, addListColorRGB );
                    conflictNote =
                    "Master List Color: " + masterColor.toString() + "\nand new Color: " +
                    addListColor.toString() + " share the same RGB values.\n" +
                    addListColor.NAME + " was not added to the Master Color List.\n\n";
                }
                issuesList += conflictNote;
                addIndex++;
                totalConflicts++;
                checkNewElement = false;
                continue;
            }
            
            //Check if the new Color should be inserted into the master list here
            if( compareResult > 0 ) {
                //addListColor.NAME is closer to 'A' or 'a'
                iter.previous();
                iter.add( addListColor );
                colorRGBList.add( addListColorRGB );
                addIndex++;
                checkNewElement = true;      
            } else {
                checkNewElement = false;
            }
        }
        
        /*Check if the total number of conflicts is equal to the size of the color list.
          If so, this means that a scheme is trying to be added again that has already been
          added, so don't print the conflict issues. The only conflicts we care to see are new
          ones, and it is much more likely for the same color scheme to be added when the
          ColorPalette is set, than for a partially known set of Colors to be added */
        if( totalConflicts >= colorList.length - 1 )
            issuesList = "";
        
        return issuesList;
    }
    
    public static void printColorList( Color[] colors ) {
        for( Color c : colors )
            System.out.println( ((StrColor)c).toString() );
    }
    
    /**
     * Find a Color's name using the master list, based on it's RGB values. If the Color is
     * not found in the master list, then an empty String is returned
     * 
     * @param c The Color whose name is being searched for
     * @return String The name of this Color. An empty String is returned if a name is not found
     */
    public static String findColorName( Color c ) {
        if( c instanceof StrColor )
            return ((StrColor)c).NAME;
        return findColorName( c.getRed(), c.getGreen(), c.getBlue() );
    }
    /**
     * Find a Color's name using the master list, based on it's RGB values. If the Color is
     * not found in the master list, then an empty String is returned
     * 
     * @param r The red component of the Color
     * @param g The green component of the Color
     * @param b The blue component of the Color
     */
    private static String findColorName( int r, int g, int b ) {
        String name = "";
        String searchToken = "" + r + " " + g + " " + b;
        Scanner scanner = getScanner( MASTER_COLOR_LIST_FILE_NAME );
        
        while( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            if( line.contains( searchToken ) )
                name = line.substring( 0, line.indexOf(" ") );
        }
        
        return name;
    }
    
    /**
     * Find a Color in a LinkedList based on the String RGB value
     * 
     * @param list The list of StrColors
     * @param colorRGB The String RGB form of a Color
     * @return StrColor The StrColor that has the same RGB values as the interpreted String RGB
     */
    private StrColor findColorInList( LinkedList<StrColor> list, String colorRGB ) {
        ListIterator iter = list.listIterator(0);
        while( iter.hasNext() ) {
            StrColor color = (StrColor)iter.next();
            if( color.hasEqualRGB( colorRGB ) )
                return color;
        }
        
        return null;
    }
    
    /**
     * Creates a HashSet of Strings from a Linked List of StrColors' RGB values (as a String)
     * 
     * @param list The list of StrColors
     * @return HashSet<String> A set of all of the RGB values (as Strings) in the list
     */
    private HashSet<String> getColorRGBList( LinkedList<StrColor> list ) {
        ListIterator iter = list.listIterator(0);
        HashSet<String> colorRGBList = new HashSet<String>();
        while( iter.hasNext() ) {
            StrColor color = (StrColor)iter.next();
            String colorRGB = "" +  color.getRed() +
                              " " + color.getGreen() +
                              " " + color.getBlue();
            colorRGBList.add( colorRGB );
        }
        
        return colorRGBList;
    }
    
    /**
     * Convert the master list of StrColors to text, which is used to overwrite the
     * MASTER_COLOR_LIST text file
     * 
     * @param masterList The master list of StrColors
     * @return String The text representing the list of StrColors in the master list, which
     *                will be used to write to the MASTER_COLOR_LIST text file
     */
    private String convertMasterListToText( LinkedList<StrColor> masterList ) {
        String text = "";
        ListIterator<StrColor> iter = masterList.listIterator(0);
        while( iter.hasNext() ) {
            StrColor color = iter.next();
            String line = color.NAME + " " +
                          color.getRed() + " " +
                          color.getGreen() + " " +
                          color.getBlue();
            text += line;
            if( iter.hasNext() )
                text += "\n";
        }
        return text;
    }
    
    /**
     * Create a palette from a text file
     * 
     * @param fileName The name of the text file containing the information about the color
     *                 palette
     * @return StrColor[][] The palette of Colors
     * @see ColorPalette( String fileName ) for the requirements for file formatting
     */
    private StrColor[][] getPaletteFromFile( String fileName ) {
        ArrayList<StrColor[]> paletteList = new ArrayList<StrColor[]>();
        ArrayList<StrColor> schemeList = new ArrayList<StrColor>();
        
        Scanner scanner = getScanner( fileName );
        while( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            if( tokens.length <= 1 ) {
                tokens[0] = tokens[0].trim();
                if( tokens[0].length() == 0 ) {
                    StrColor[] arrSchemeList = schemeList.toArray(
                                                   new StrColor[ schemeList.size() ] );
                    paletteList.add( arrSchemeList );
                    schemeList.clear();
                }
            } else if( tokens.length > 1 ) {
                StrColor color;
                if( tokens.length == 2 )
                    color = new StrColor( Color.decode( tokens[1] ), tokens[0] );
                else
                    color = new StrColor( Integer.parseInt( tokens[1] ),        //r
                                          Integer.parseInt( tokens[2] ),        //g
                                          Integer.parseInt( tokens[3].trim() ), //b
                                          tokens[0].trim()                      //Color Name
                                        );
                schemeList.add( color );
            }
        }
        scanner.close();
        
        StrColor[] arrSchemeList = schemeList.toArray(
                                        new StrColor[ schemeList.size() ] );
        paletteList.add( arrSchemeList );
        
        StrColor[][] palette = paletteList.toArray( new StrColor[ paletteList.size() ][] );
        return palette;
    }
    
    /**
     * Gets a random color scheme from the current pallete
     * The scheme is stored in an array of StrColors
     * 
     * @return StrColor[] The color scheme, chosen randomly from the current palette
     */
    public StrColor[] getRandomScheme() {
        return getRandomScheme( currentPalette );
    }
    /**
     * Gets a random color scheme from the given palette
     * The scheme is stored in an array of StrColors
     * 
     * @param palette The palette to get a random scheme from
     * @return StrColor[] The color scheme, chosen randomly from the palette
     */
    public StrColor[] getRandomScheme( StrColor[][] palette ) {
        return palette[ (int)(Math.random() * palette.length) ];
    }
    
    /** Gets a random StrColor from a random scheme within the current palette */
    public StrColor getRandomColor() {
        return getRandomColor( getRandomScheme() );
    }
    /**
     * Gets a random StrColor from the given scheme
     * A StrColor scheme is an array of StrColors
     * 
     * @param scheme An array of StrColors
     * @return StrColor The random StrColor from the given scheme
     */
    public StrColor getRandomColor( StrColor[] scheme ) {
        return scheme[ (int)(Math.random() * scheme.length) ];
    }
    
    /**
     * Determines if the two StrColors are equal
     * 
     * @param first The first StrColor
     * @param second The second StrColor
     * @return boolean True if the StrColors have the same rgba values and names,
     *                 false otherwise
     */
    public static boolean areEqual( StrColor first, StrColor second ) {
        return first.equals( second );
    }
    /**
     * Determines if the two StrColor schemes are equal
     * 
     * @param first The first StrColor scheme, an array of StrColors
     * @param second The second StrColor scheme, an array of StrColors
     * @return boolean True if the StrColor schemes have the same StrColors, false otherwise
     */
    public static boolean areEqual( StrColor[] first, StrColor[] second ) {
        if( first.length != second.length )
            return false;
        
        //Use HashSets to fix insertion order
        HashSet<StrColor> firstSet = new HashSet<StrColor>();
        HashSet<StrColor> secondSet = new HashSet<StrColor>();
        for( int rep = 0; rep < first.length; rep++ ) {
            firstSet.add( first[rep] );
            secondSet.add( second[rep] );
        }
        
        if( !firstSet.equals( secondSet ) )
            return false;
        
        return true;
    }
    /**
     * Determine if the two StrColor palettes are equal
     * 
     * @param first The first StrColor palette
     * @param second The second StrColor palette
     * @return boolean True if the two palettes have the same StrColor schemes, false otherwise
     */
    public static boolean areEqual( StrColor[][] first, StrColor[][] second ) {
        if( first.length != second.length )
            return false;
        
        for( int rep = 0; rep < first.length; rep++ )
            if( !areEqual( first[rep], second[rep] ) )
                return false;
        
        //Use HashSets to fix insertion order
        HashSet<StrColor[]> firstSet = new HashSet<StrColor[]>();
        HashSet<StrColor[]> secondSet = new HashSet<StrColor[]>();
        for( int rep = 0; rep < first.length; rep++ ) {
            firstSet.add( first[rep] );
            secondSet.add( second[rep] );
        }
        
        if( !firstSet.equals( secondSet ) )
            return false;
                
        return true;
    }
    
    /**
     * Get the studio of StrColors
     * 
     * @return ArrayList<StrColor[][]> The list of palettes
     */
    public ArrayList<StrColor[][]> getStudio() {
        return studio;
    }
    /**
     * Get the current pallete of StrColors
     * 
     * @return StrColor[][] The current palette
     */
    public StrColor[][] getPalette() {
        return currentPalette;
    }
    /**
     * Get the current scheme of StrColors
     * 
     * @return StrColor[] The current scheme
     */
    public StrColor[] getScheme() {
        return currentScheme;
    }
    /**
     * Get the current color
     * 
     * @return StrColor The current StrColor
     */
    public StrColor getColor() {
        return currentColor;
    }
    
    /**
     * Set the current palette to a new palette
     * 
     * @param newPalette The new palette of StrColors to set the current palette to
     */
    public void setPalette( StrColor[][] newPalette ) {
        currentPalette = newPalette;
    }
    /**
     * Set the current scheme to a new scheme
     * 
     * @param newScheme The new scheme of StrColors to set the current scheme to
     */
    public void setScheme( StrColor[] newScheme ) {
        currentScheme = newScheme;
    }
    /**
     * Set the current StrColor to a new StrColor
     * 
     * @param newColor The new StrColor to set the current StrColor to
     */
    public void setColor( StrColor newColor ) {
        currentColor = newColor;
    }
    
    /**
     * Find the StrColor associated with the color name. If the color is not found,
     * null is returned
     * 
     * @param colorName The name of the StrColor
     * @return StrColor The StrColor associated with this name, or null if the color
     *                  is not found
     */
    public StrColor getColor( String colorName ) {
        for( StrColor[][] palette : studio ) {
            StrColor color = getColor( palette, colorName );
            if( color != null )
                return color;
        }
        
        return null;
    }
    /**
     * Find the StrColor associated with the color name that is within the palette.
     * If the StrColor is not in the palette, then null is returned
     * 
     * @param palette The palette of StrColors to search through
     * @param colorName The name of the StrColor that is being searched for
     * @return StrColor The StrColor that has the given name, or null if it is not found
     */
    public StrColor getColor( StrColor[][] palette, String colorName ) {
        for( StrColor[] scheme : palette ) {
            StrColor color = getColor( scheme, colorName );
            if( color != null )
                return color;
        }
        
        return null;
    }
    /**
     * Find the StrColor associated with the color name that is within the scheme.
     * If the StrColor is not in the scheme, then null is returned
     * 
     * @param scheme The scheme of StrColors to search through
     * @param colorName The name of the StrColor that is being searched for
     * @return StrColor The StrColor that has the given name, or null if it is not found
     */
    public StrColor getColor( StrColor[] scheme, String colorName ) {
        for( StrColor color : scheme )
            if( color.NAME.equals( colorName ) )
                return color;
        
        return null;
    }
    /**
     * Find the StrColor associated with the Color. If the color is not found,
     * then null is returned
     * 
     * @param color The color being searched for
     * @return StrColor The StrColor associated with the RGB values of Color
     */
    public StrColor getColor( Color color ) {
        return getColor( color.getRed(), color.getGreen(), color.getBlue() );
    }
    /**
     * Find the StrColor associated with the RGB values. If the color is not found,
     * then null is returned
     * 
     * @param r The red component of the Color
     * @param g The green component of the Color
     * @param b The blue component of the Color
     * @return StrColor The StrColor associated with the RGB values, or null if not found
     */
    public StrColor getColor( int r, int g, int b ) {
        for( StrColor[][] palette : studio ) {
            StrColor color = getColor( palette, r, g, b );
            if( color != null )
                return color;
        }
        
        return null;
    }
    /**
     * Find the StrColor in the palette that has the given RGB values. If the color is not
     * found, then null is returned
     * 
     * @param palette The palette of StrColors being searched
     * @param r The red component of the Color
     * @param g The green component of the Color
     * @param b The blue component of the Color
     * @return StrColor The StrColor that was found in the palette that has the RGB values
     */
    public StrColor getColor( StrColor[][] palette, int r, int g, int b ) {
        for( StrColor[] scheme : palette ) {
            StrColor color = getColor( scheme, r, g, b );
            if( color != null )
                return color;
        }
        
        return null;
    }
    /**
     * Find the StrColor in the scheme that has the given RGB values. If the color is not found,
     * then null is returned
     * 
     * @param scheme The scheme of StrColors being searched
     * @param r The red component of the Color
     * @param g The green component of the Color
     * @param b The blue component of the Color
     * @return StrColor The StrColor that was found in the scheme that has the RGB values
     */
    public StrColor getColor( StrColor[] scheme, int r, int g, int b ) {
        for( StrColor color : scheme )
            if( color.getRed() == r && color.getGreen() == g && color.getBlue() == b )
                return color;
        
        return null;
    }
    
    /**
     * Get an array of all the Colors within the studio
     * 
     * @return StrColor[] The list of all the colors in the studio
     */
    public StrColor[] getAllColors() {
        HashSet<StrColor> colorList = new HashSet<StrColor>();
        for( StrColor[][] palette : studio )
            colorList.addAll( getAllColorsSet( palette ) );
        return colorList.toArray(new StrColor[colorList.size()]);
    }
    /**
     * Get an array of all the Colors within the palette
     * 
     * @return StrColor[] The list of all colors in the palette
     */
    public StrColor[] getAllColors( StrColor[][] palette ) {
        HashSet<StrColor> colorList = getAllColorsSet( palette );
        return colorList.toArray(new StrColor[colorList.size()]);
    }
    /**
     * Get a HashSet of all the Colors within the palette
     * 
     * @return HashSet<StrColor> The set of all the colors in the palette
     */
    private HashSet<StrColor> getAllColorsSet( StrColor[][] palette ) {
        HashSet<StrColor> colorList = new HashSet<StrColor>();
        for( StrColor[] scheme : palette )
            colorList.addAll( getAllColorsSet( scheme ) );
        return colorList;
    }
    /**
     * Get a HashSet of all the Colors within the scheme
     * 
     * @return HashSet<StrColor> The set of all the colors in the scheme
     */
    private HashSet<StrColor> getAllColorsSet( StrColor[] scheme ) {
        HashSet<StrColor> colorList = new HashSet<StrColor>();
        for( StrColor color : scheme )
            colorList.add( color );
        return colorList;
    }
    
    /**
     * Get a list of all the schemes
     * 
     * @return ArrayList<StrColor[]> The list of schemes that are within the studio
     */
    public ArrayList<StrColor[]> getSchemes() {
        ArrayList<StrColor[]> schemeList = new ArrayList<StrColor[]>();
        for( StrColor[][] palette : studio )
            for( StrColor[] scheme : palette )
                schemeList.add( scheme );
        
        return schemeList;
    }
    /**
     * Find the list of schemes that contains the given StrColor
     * 
     * @param color The StrColor being searched for
     * @return ArrayList<StrColor[]> The list of schemes that contain the given StrColor
     */
    public ArrayList<StrColor[]> getSchemes( StrColor color ) {
        ArrayList<StrColor[]> schemeList = new ArrayList<StrColor[]>();
        for( StrColor[][] palette : studio )
            for( StrColor[] scheme : palette )
                for( StrColor strColor : scheme )
                    if( strColor.equals( color ) ) {
                        schemeList.add( scheme );
                        break;
                    }
        
        return schemeList;
    }
    /**
     * Find the list of palettes that contains the given StrColor
     * 
     * @param color The StrColor being searched for
     * @return ArrayList<StrColor[][]> paletteList = new ArrayList<StrColor[][]>();
     */
    public ArrayList<StrColor[][]> getPalettes( StrColor color ) {
        ArrayList<StrColor[][]> paletteList = new ArrayList<StrColor[][]>();
        for( StrColor[][] palette : studio ) {
            for( StrColor[] scheme : palette ) {
                boolean foundPalette = false;
                for( StrColor strColor : scheme ) {
                    if( strColor.equals( color ) ) {
                        paletteList.add( palette );
                        foundPalette = true;
                        break;
                    }
                }
                if( foundPalette )
                    break;
            }
        }
        
        return paletteList;
    }
    
    /**
     * Sorts a StrColor array alphabetically by its NAME
     * 
     * @param colorList The array of StrColors to sort alphabetically
     */
    private void sortStrColorArrayByName( StrColor[] colorList ) {
        Arrays.sort( colorList, new Comparator<StrColor>(){  
            @Override
            public int compare( StrColor first, StrColor second ){  
                 return first.NAME.compareTo( second.NAME );  
            }  
        }); 
    }
    
    /** Get a Scanner object that is reading a File based on a File name */
    private static Scanner getScanner( String fileName )
    { return getScanner( new File( fileName ) ); }
    /**
        Get a Scanner object that is reading a File
      
        @param file The file to scan
        @return Scanner The scanner object that is reading the given file
    */
    private static Scanner getScanner( File file ) {
        Scanner sc = null;
        try {
            sc = new Scanner( file );
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        
        return sc;
    }
    
    /**
     * Overwrite an existing File
     * 
     * @param fileLoc The location of the File to write to
     * @param text The text to write to the File
     */
    private void writeToFile( String fileLoc, String text ) {
        FileWriter fw = null;
        try {
            fw = new FileWriter( fileLoc );
            fw.write( text );
            fw.close();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Add to an existing File
     * 
     * @param fileLoc The location of the File to write to
     * @param text The text to write to the File
     */
    private void addToFile( String fileLoc, String text ) {
        FileWriter fw = null;
        try {
            fw = new FileWriter( fileLoc, true );
            fw.write( text );
            fw.close();
        } catch( IOException e ) {
            e.printStackTrace();
        }
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
