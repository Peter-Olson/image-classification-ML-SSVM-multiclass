import java.awt.Color;

/**
 * A wrapper class for java.awt.Color
 * 
 * @author Peter Olson
 * @version 2/7/23
 */
public class StrColor extends Color {
    public final String NAME;
    
    /**
     * Create a StrColor object with the given Color and a String name
     * 
     * @param color The Color with RGB values
     * @param NAME The name of the Color
     */
    public StrColor( Color color, String NAME ) {
        super( color.getRed(), color.getGreen(), color.getBlue() );
        this.NAME = NAME;
    }
    /**
     * Create a StrColor object with RGB values and a String name
     * 
     * @param r The red component of this Color
     * @param g The green component of this Color
     * @param b The blue component of this Color
     * @param NAME The String name of this Color
     */
    public StrColor( int r, int g, int b, String NAME ) {
        super(r, g, b);
        this.NAME = NAME;
    }
    
    /**
     * Determines whether this StrColor and the given StrColor are equal
     * 
     * @param c The other StrColor to compare to
     * @return boolean True if the StrColors are equal, false otherwise
     * @see Color.equals( Object obj )
     */
    public boolean equals( StrColor c ) {
        if( !super.equals( c ) )
            return false;
        else if( !this.NAME.equals( c.NAME ) )
            return false;
        
        return true;
    }
    
    /**
     * Determines whether this StrColor and another StrColor have equal RGB values
     * 
     * @param other The StrColor being compared to this one
     * @return boolean True if the two StrColors have the same RGB values, false otherwise
     */
    public boolean hasEqualRGB( StrColor other ) {
        if( this.getRed() == other.getRed() &&
            this.getGreen() == other.getGreen() &&
            this.getBlue() == other.getBlue() )
            return true;
        return false;
    }
    /**
     * Determines whether this StrColor has RGB values equivalent to the String RGB passed in,
     * or not
     * 
     * @param strRGB The RGB value, in String form. Either in the format of # # # or #,#,#
     * @return boolean True if this StrColor shares its RGB values with the String RGB, false
     *                 otherwise
     */
    public boolean hasEqualRGB( String strRGB ) {
        String[] rgbParts = strRGB.split("[\\s,]+"); //Split by spaces or comma
        if( this.getRed() == Integer.parseInt( rgbParts[0] ) &&
            this.getGreen() == Integer.parseInt( rgbParts[1] ) &&
            this.getBlue() == Integer.parseInt( rgbParts[2] ) )
            return true;
        
        return false;
    }
    
    /**
     * Get a Color object representing this StrColor
     * 
     * @return Color The Color representing this StrColor
     */
    public Color getColor() {
        return new Color( getRed(), getGreen(), getBlue() );
    }
    
    /**
     * Give a description of this StrColor object
     * 
     * @return String The description of this StrColor object
     */
    public String toString() {
        return NAME + " (" + getRed() + ", " + getGreen() + ", " + getBlue() + ")";
    }
}