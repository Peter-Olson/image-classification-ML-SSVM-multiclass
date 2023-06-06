
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.swing.border.*;
/**
 * Displays a picture and lets you explore the picture by displaying the row, column, red,
 * green, and blue values of the pixel at the cursor when you click a mouse button or
 * press and hold a mouse button while moving the cursor.  It also lets you zoom in or
 * out.  You can also type in a row and column value to see the color at that location.
 * 
 * Originally created for the Jython Environment for Students (JES). 
 * Modified to work with DrJava by Barbara Ericson
 * Also modified to show row and columns by Barbara Ericson
 * 
 * @author Keith McDermottt, gte047w@cc.gatech.edu
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class PictureExplorer implements MouseMotionListener, ActionListener, MouseListener {
 
    // Current indicies
    /** Row index */
    private int rowIndex = 0; 
    /** Column index */
    private int colIndex = 0;
    
    // Main GUI
    /** Window to hold GUI */
    private JFrame pictureFrame;
    /** Window that allows the user to scroll to see a large picture */
    private JScrollPane scrollPane;
    
    // GUI components
    /** Column label */
    private JLabel colLabel;
    /** Column previous button */
    private JButton colPrevButton;
    /** Row previous button */
    private JButton rowPrevButton;
    /** Column next button */
    private JButton colNextButton;
    /** Row next button */
    private JButton rowNextButton;
    /** Row label */
    private JLabel rowLabel;
    /** Text field to show column index */
    private JTextField colValue;
    /** Text field to show row index */
    private JTextField rowValue;
    /** Red value label */
    private JLabel rValue;
    /** Green value label */
    private JLabel gValue;
    /** Blue value label */
    private JLabel bValue;
    /** Color swatch label */
    private JLabel colorLabel;
    /** Panel to show the color swatch */
    private JPanel colorPanel;
    
    // Menu components
    /** Menu bar */
    private JMenuBar menuBar;
    /** Zoom menu */
    private JMenu zoomMenu;
    /** 25% zoom level */
    private JMenuItem twentyFive;
    /** 50% zoom level */
    private JMenuItem fifty;
    /** 75% zoom level */
    private JMenuItem seventyFive;
    /** 100% zoom level */
    private JMenuItem hundred;
    /** 150% zoom level */
    private JMenuItem hundredFifty;
    /** 200% zoom level */
    private JMenuItem twoHundred;
    /** 500% zoom level */
    private JMenuItem fiveHundred;
    
    /** The picture being explored */
    private DigitalPicture picture;
    
    /** The image icon used to display the picture */
    private ImageIcon scrollImageIcon;
    
    /** The image display */
    private ImageDisplay imageDisplay;
    
    /** The zoom factor (amount to zoom) */
    private double zoomFactor;
    
    /** The number system to use, 0 means starting at 0, 1 means starting at 1 */
    private int numberBase = 0;

    /**
     * Public constructor
     * 
     * @param picture the picture to explore
     */
    public PictureExplorer( DigitalPicture picture ) {
        // Set the fields
        this.picture = picture;
        zoomFactor = 1;
    
        // Create the window and set things up
        createWindow();
    }
  
    /**
     * Gets access to this PictureFrame's JFrame
     *
     * Should I be doing this? No. But I want to be able to close these windows
     * for ML applications
     * 
     * @return JFrame The JFrame of the window
     * @author Peter Olson
     */
    public JFrame getWindow() {
        return pictureFrame;
    }
  
    /**
     * Changes the number system to start at one
     */
    public void changeToBaseOne() {
        numberBase = 1;
    }
  
    /**
     * Set the title of the frame
     * 
     *@param title The title to use in the JFrame
     */
    public void setTitle( String title ) {
        pictureFrame.setTitle( title );
    }
  
    /**
     * Method to create and initialize the picture frame
     */
    private void createAndInitPictureFrame() {
        pictureFrame = new JFrame(); // Create the JFrame
        pictureFrame.setResizable( true );  // Allow the user to resize it
        pictureFrame.getContentPane().setLayout( new BorderLayout() ); // Use border layout
        pictureFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE ); // When close stop
        pictureFrame.setTitle( picture.getTitle() );
        PictureExplorerFocusTraversalPolicy newPolicy =
            new PictureExplorerFocusTraversalPolicy();
        pictureFrame.setFocusTraversalPolicy( newPolicy );
    }
  
    /**
     * Method to create the menu bar, menus, and menu items
     */
    private void setUpMenuBar() {
        // Create menu
        menuBar = new JMenuBar();
        zoomMenu = new JMenu("Zoom");
        twentyFive = new JMenuItem("25%");
        fifty = new JMenuItem("50%");
        seventyFive = new JMenuItem("75%");
        hundred = new JMenuItem("100%");
        hundred.setEnabled( false );
        hundredFifty = new JMenuItem("150%");
        twoHundred = new JMenuItem("200%");
        fiveHundred = new JMenuItem("500%");
    
        // Add the action listeners
        twentyFive.addActionListener( this );
        fifty.addActionListener( this );
        seventyFive.addActionListener( this );
        hundred.addActionListener( this );
        hundredFifty.addActionListener( this );
        twoHundred.addActionListener( this );
        fiveHundred.addActionListener( this );
    
        // Add the menu items to the menus
        zoomMenu.add( twentyFive );
        zoomMenu.add( fifty );
        zoomMenu.add( seventyFive );
        zoomMenu.add( hundred );
        zoomMenu.add( hundredFifty );
        zoomMenu.add( twoHundred );
        zoomMenu.add( fiveHundred );
        menuBar.add( zoomMenu );
    
        // Set the menu bar to this menu
        pictureFrame.setJMenuBar( menuBar );
    }
  
    /**
     * Create and initialize the scrolling image
     */
    private void createAndInitScrollingImage() {
        scrollPane = new JScrollPane();
        
        BufferedImage bimg = picture.getBufferedImage();
        imageDisplay = new ImageDisplay( bimg );
        imageDisplay.addMouseMotionListener( this );
        imageDisplay.addMouseListener( this );
        imageDisplay.setToolTipText(
            "Click a mouse button on a pixel to see the pixel information");
        scrollPane.setViewportView( imageDisplay );
        pictureFrame.getContentPane().add( scrollPane, BorderLayout.CENTER );
    }
  
    /**
     * Creates the JFrame and sets everything up
     */
    private void createWindow() {
        // Create the picture frame and initialize it
        createAndInitPictureFrame();
        
        // Set up the menu bar
        setUpMenuBar();
        
        // Create the information panel
        createInfoPanel();
        
        // Creates the scrollpane for the picture
        createAndInitScrollingImage();
        
        // Show the picture in the frame at the size it needs to be
        pictureFrame.pack();
        pictureFrame.setVisible( true );
    }
  
    /**
     * Method to set up the next and previous buttons for the
     * pixel location information
     */
    private void setUpNextAndPreviousButtons() {
        // Create the image icons for the buttons
        Icon prevIcon = new ImageIcon( DigitalPicture.class.getResource("leftArrow.gif"), 
                                      "previous index" );
        Icon nextIcon = new ImageIcon( DigitalPicture.class.getResource("rightArrow.gif"), 
                                      "next index" );
        // Create the arrow buttons
        colPrevButton = new JButton( prevIcon );
        colNextButton = new JButton( nextIcon );
        rowPrevButton = new JButton( prevIcon );
        rowNextButton = new JButton( nextIcon );
    
        // Set the tool tip text
        colNextButton.setToolTipText("Click to go to the next column value");
        colPrevButton.setToolTipText("Click to go to the previous column value");
        rowNextButton.setToolTipText("Click to go to the next row value");
        rowPrevButton.setToolTipText("Click to go to the previous row value");
        
        // Set the sizes of the buttons
        int prevWidth = prevIcon.getIconWidth() + 2;
        int nextWidth = nextIcon.getIconWidth() + 2;
        int prevHeight = prevIcon.getIconHeight() + 2;
        int nextHeight = nextIcon.getIconHeight() + 2;
        Dimension prevDimension = new Dimension( prevWidth, prevHeight );
        Dimension nextDimension = new Dimension( nextWidth, nextHeight );
        colPrevButton.setPreferredSize( prevDimension );
        rowPrevButton.setPreferredSize( prevDimension );
        colNextButton.setPreferredSize( nextDimension );
        rowNextButton.setPreferredSize( nextDimension );
    
        // Handle previous column button press
        colPrevButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                colIndex--;
                if( colIndex < 0 )
                    colIndex = 0;
                displayPixelInformation( colIndex, rowIndex );
            }
        });
    
        // handle previous row button press
        rowPrevButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                rowIndex--;
                if( rowIndex < 0 )
                    rowIndex = 0;
                displayPixelInformation( colIndex, rowIndex );
            }
        });
    
        // Handle next column button press
        colNextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                colIndex++;
                if( colIndex >= picture.getWidth() )
                    colIndex = picture.getWidth() - 1;
                displayPixelInformation( colIndex, rowIndex );
            }
        });
    
        // Handle next row button press
        rowNextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                rowIndex++;
                if( rowIndex >= picture.getHeight() )
                    rowIndex = picture.getHeight() - 1;
                displayPixelInformation( colIndex, rowIndex );
            }
        });
    }
  
    /**
     * Create the pixel location panel
     * 
     * @param labelFont the font for the labels
     * @return JPanel the location panel
     */
    public JPanel createLocationPanel( Font labelFont ) {
    
        // Create a location panel
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout( new FlowLayout() );
        Box hBox = Box.createHorizontalBox();
    
        // Create the labels
        rowLabel = new JLabel("Row:");
        colLabel = new JLabel("Column:");
    
        // Create the text fields
        colValue = new JTextField( Integer.toString( colIndex + numberBase ), 6 );
        colValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                displayPixelInformation( colValue.getText(), rowValue.getText() );
            }
        });
        rowValue = new JTextField( Integer.toString( rowIndex + numberBase ), 6 );
        rowValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                displayPixelInformation( colValue.getText(), rowValue.getText() );
            }
        });
    
        // Set up the next and previous buttons
        setUpNextAndPreviousButtons();
        
        // Set up the font for the labels
        colLabel.setFont( labelFont );
        rowLabel.setFont( labelFont );
        colValue.setFont( labelFont );
        rowValue.setFont( labelFont );
    
        // Add the items to the vertical box and the box to the panel
        hBox.add( Box.createHorizontalGlue() );
        hBox.add( rowLabel );
        hBox.add( rowPrevButton );
        hBox.add( rowValue );
        hBox.add( rowNextButton );
        hBox.add( Box.createHorizontalStrut( 10 ) );
        hBox.add( colLabel );
        hBox.add( colPrevButton );
        hBox.add( colValue );
        hBox.add( colNextButton );
        locationPanel.add( hBox );
        hBox.add( Box.createHorizontalGlue() );
    
        return locationPanel;
    }
  
    /**
     * Create the color information panel
     * 
     * @param labelFont the font to use for labels
     * @return the color information panel
     */
    private JPanel createColorInfoPanel( Font labelFont ) {
        // Create a color info panel
        JPanel colorInfoPanel = new JPanel();
        colorInfoPanel.setLayout( new FlowLayout() );
    
        // Get the pixel at the x and y
        Pixel pixel = new Pixel( picture, colIndex, rowIndex );
        
        // Create the labels
        rValue = new JLabel( "R: " + pixel.getRed()   );
        gValue = new JLabel( "G: " + pixel.getGreen() );
        bValue = new JLabel( "B: " + pixel.getBlue()  );
        
        // Create the sample color panel and label
        colorLabel = new JLabel("Color at location: ");
        colorPanel = new JPanel();
        colorPanel.setBorder(new LineBorder(Color.black,1));
    
        // Set the color sample to the pixel color
        colorPanel.setBackground( pixel.getColor() );
        
        // Set the font
        rValue.setFont( labelFont );
        gValue.setFont( labelFont );
        bValue.setFont( labelFont );
        colorLabel.setFont( labelFont );
        colorPanel.setPreferredSize( new Dimension( 25, 25 ) );
    
        // Add items to the color information panel
        colorInfoPanel.add( rValue );
        colorInfoPanel.add( gValue );
        colorInfoPanel.add( bValue );
        colorInfoPanel.add( colorLabel );
        colorInfoPanel.add( colorPanel );
        
        return colorInfoPanel; 
    }
  
    /**
     * Creates the North JPanel with all the pixel location
     * and color information
     */
    private void createInfoPanel() {
        // Create the info panel and set the layout
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout( new BorderLayout() );
        
        // Create the font
        Font largerFont = new Font( infoPanel.getFont().getName(),
                                    infoPanel.getFont().getStyle(), 14 );
        
        // Create the pixel location panel
        JPanel locationPanel = createLocationPanel( largerFont );
        
        // Create the color information panel
        JPanel colorInfoPanel = createColorInfoPanel( largerFont );
    
        // Add the panels to the info panel
        infoPanel.add( BorderLayout.NORTH, locationPanel );
        infoPanel.add( BorderLayout.SOUTH, colorInfoPanel ); 
        
        // Add the info panel
        pictureFrame.getContentPane().add( BorderLayout.NORTH, infoPanel );
    }
  
    /**
     * Method to check that the current position is in the viewing area and if
     * not scroll to center the current position if possible
     */
    public void checkScroll() {
        // Get the x and y position in pixels
        int xPos = (int)(colIndex * zoomFactor); 
        int yPos = (int)(rowIndex * zoomFactor); 
        
        // Only do this if the image is larger than normal
        if( zoomFactor > 1 ) {
      
            // Get the rectangle that defines the current view
            JViewport viewport = scrollPane.getViewport();
            Rectangle rect = viewport.getViewRect();
            int rectMinX = (int)rect.getX();
            int rectWidth = (int)rect.getWidth();
            int rectMaxX = rectMinX + rectWidth - 1;
            int rectMinY = (int)rect.getY();
            int rectHeight = (int)rect.getHeight();
            int rectMaxY = rectMinY + rectHeight - 1;
            
            // Get the maximum possible x and y index
            int macolIndexX = (int)(picture.getWidth() * zoomFactor) - rectWidth - 1;
            int macolIndexY = (int)(picture.getHeight() * zoomFactor) - rectHeight - 1;
      
            // Calculate how to position the current position in the middle of the viewing
            // area
            int viewX = xPos - (int)(rectWidth / 2);
            int viewY = yPos - (int)(rectHeight / 2);
      
            // Reposition the viewX and viewY if outside allowed values
            if( viewX < 0 )
                viewX = 0;
            else if( viewX > macolIndexX )
                viewX = macolIndexX;
            if( viewY < 0 )
                viewY = 0;
            else if( viewY > macolIndexY )
                viewY = macolIndexY;
      
            // Move the viewport upper left point
            viewport.scrollRectToVisible(
                new Rectangle( viewX, viewY, rectWidth, rectHeight ) );
        }
    }
  
    /**
     * Zooms in the on picture by scaling the image.  
     * It is extremely memory intensive
     * 
     * @param factor the amount to zoom by
     */
    public void zoom( double factor ) {
        // Save the current zoom factor
        zoomFactor = factor;
    
        // Calculate the new width and height and get an image that size
        int width = (int)( picture.getWidth() * zoomFactor );
        int height = (int)( picture.getHeight() * zoomFactor );
        BufferedImage bimg = picture.getBufferedImage();
        
        // Set the scroll image icon to the new image
        imageDisplay.setImage( bimg.getScaledInstance( width, height, Image.SCALE_DEFAULT ) );
        imageDisplay.setCurrentX( (int)( colIndex * zoomFactor ) );
        imageDisplay.setCurrentY( (int)( rowIndex * zoomFactor ) );
        imageDisplay.revalidate();
        checkScroll();  // Check if need to reposition scroll
    }
  
    /**
     * Repaints the image on the scrollpane.  
     */
    public void repaint() {
        pictureFrame.repaint();
    }
  
    //****************************************//
    //               Event Listeners          //
    //****************************************//
  
    /**
     * Called when the mouse is dragged (button held down and moved)
     * 
     * @param e the mouse event
     */
    public void mouseDragged( MouseEvent e ) {
        displayPixelInformation(e);
    }
  
    /**
    * Method to check if the given x and y are in the picture
    * 
    * @param column the horizontal value
    * @param row the vertical value
    * @return boolean True if the row and column are in the picture 
    *                 and false otherwise
    */
    private boolean isLocationInPicture( int column, int row ) {
        boolean result = false; // the default is false
        if( column >= 0 && column < picture.getWidth() &&
            row >= 0 && row < picture.getHeight() )
            result = true;
        
        return result;
    }
  
    /**
     * Method to display the pixel information from the passed x and y but
     * also converts x and y from strings
     * 
     * @param xString The x value as a string from the user
     * @param yString The y value as a string from the user
     */
    public void displayPixelInformation( String xString, String yString ) {
        int x = -1;
        int y = -1;
        try {
          x = Integer.parseInt( xString );
          x = x - numberBase;
          y = Integer.parseInt( yString );
          y = y - numberBase;
        } catch( Exception e ) {}
        
        if( x >= 0 && y >= 0 )
            displayPixelInformation( x, y );
    }
  
    /**
     * Method to display pixel information for the passed x and y
     * 
     * @param pictureX The x value in the picture
     * @param pictureY The y value in the picture
     */
    private void displayPixelInformation( int pictureX, int pictureY ) {
        // Check that this x and y are in range
        if( isLocationInPicture( pictureX, pictureY ) ) {
            // Save the current x and y index
            colIndex = pictureX;
            rowIndex = pictureY;
            
            // Get the pixel at the x and y
            Pixel pixel = new Pixel( picture, colIndex, rowIndex );
            
            // Set the values based on the pixel
            colValue.setText( Integer.toString( colIndex  + numberBase ) );
            rowValue.setText( Integer.toString( rowIndex + numberBase ) );
            rValue.setText("R: " + pixel.getRed() );
            gValue.setText("G: " + pixel.getGreen() );
            bValue.setText("B: " + pixel.getBlue() );
            colorPanel.setBackground(
                new Color( pixel.getRed(), pixel.getGreen(), pixel.getBlue() ) );
        } else {
            clearInformation();
        }
        
        // Notify the image display of the current x and y
        imageDisplay.setCurrentX( (int)(colIndex * zoomFactor ) );
        imageDisplay.setCurrentY( (int)(rowIndex * zoomFactor ) );
    }
  
    /**
     * Method to display pixel information based on a mouse event
     * 
     * @param e A mouse event
     */
    private void displayPixelInformation( MouseEvent e ) {
        // Get the cursor x and y
        int cursorX = e.getX();
        int cursorY = e.getY();
        
        // Get the x and y in the original (not scaled image)
        int pictureX = (int)( cursorX / zoomFactor + numberBase );
        int pictureY = (int)( cursorY / zoomFactor + numberBase );
        
        // Display the information for this x and y
        displayPixelInformation( pictureX, pictureY );
    }
  
    /**
     * Method to clear the labels and current color and reset the 
     * current index to -1
     */
    private void clearInformation() {
        colValue.setText("N/A");
        rowValue.setText("N/A");
        rValue.setText("R: N/A");
        gValue.setText("G: N/A");
        bValue.setText("B: N/A");
        colorPanel.setBackground( Color.black );
        colIndex = -1;
        rowIndex = -1;
    }
  
    /**
     * Method called when the mouse is moved with no buttons down
     * 
     * @param e The mouse event
     */
    public void mouseMoved( MouseEvent e ) {}
  
    /**
     * Method called when the mouse is clicked
     * 
     * @param e The mouse event
     */
    public void mouseClicked( MouseEvent e ) {
        displayPixelInformation(e);
    }
  
    /**
     * Method called when the mouse button is pushed down
     * 
     * @param e The mouse event
     */ 
    public void mousePressed( MouseEvent e ) {
        displayPixelInformation(e);
    }
  
    /**
     * Method called when the mouse button is released
     * 
     * @param e The mouse event
     */
    public void mouseReleased( MouseEvent e ) {}
  
    /**
     * Method called when the component is entered (mouse moves over it)
     * 
     * @param e The mouse event
     */
    public void mouseEntered( MouseEvent e ) {}
  
    /**
     * Method called when the mouse moves over the component
     * 
     * @param e The mouse event
     */
    public void mouseExited( MouseEvent e ) {}
  
    /**
     * Method to enable all menu commands
     */
    private void enableZoomItems() {
        twentyFive.setEnabled( true );
        fifty.setEnabled( true );
        seventyFive.setEnabled( true );
        hundred.setEnabled( true );
        hundredFifty.setEnabled( true );
        twoHundred.setEnabled( true );
        fiveHundred.setEnabled( true );
    }
  
    /**
     * Controls the zoom menu bar
     *
     * @param a The ActionEvent 
     */
    public void actionPerformed( ActionEvent a ) {
        if( a.getActionCommand().equals("Update") )
            this.repaint();
        
        if( a.getActionCommand().equals("25%") ) {
            this.zoom( 0.25 );
            enableZoomItems();
            twentyFive.setEnabled( false );
        }
    
        if( a.getActionCommand().equals("50%") ) {
            this.zoom( 0.50 );
            enableZoomItems();
            fifty.setEnabled( false );
        }
        
        if( a.getActionCommand().equals("75%") ) {
            this.zoom( 0.75 );
            enableZoomItems();
            seventyFive.setEnabled( false );
        }
        
        if( a.getActionCommand().equals("100%") ) {
            this.zoom( 1.0 );
            enableZoomItems();
            hundred.setEnabled( false );
        }
        
        if( a.getActionCommand().equals("150%") ) {
            this.zoom( 1.5 );
            enableZoomItems();
            hundredFifty.setEnabled( false );
        }
        
        if( a.getActionCommand().equals("200%") ) {
          this.zoom( 2.0 );
          enableZoomItems();
          twoHundred.setEnabled( false );
        }
        
        if( a.getActionCommand().equals("500%") ) {
            this.zoom( 5.0 );
            enableZoomItems();
            fiveHundred.setEnabled( false );
        }
    }
  
    /**
     * Class for establishing the focus for the textfields
     */
    private class PictureExplorerFocusTraversalPolicy extends FocusTraversalPolicy {
        /**
         * Method to get the next component for focus
         */
        public Component getComponentAfter( Container focusCycleRoot, Component aComponent) {
            if( aComponent.equals( colValue ) )
                return rowValue;
            else 
                return colValue;
        }
    
        /**
         * Method to get the previous component for focus
         */
        public Component getComponentBefore( Container focusCycleRoot, Component aComponent) {
            if( aComponent.equals( colValue ) )
                return rowValue;
            else 
                return colValue;
        }
    
        public Component getDefaultComponent( Container focusCycleRoot ) {
            return colValue;
        }
    
        public Component getLastComponent( Container focusCycleRoot ) {
            return rowValue;
        }
    
        public Component getFirstComponent( Container focusCycleRoot ) {
            return colValue;
        }
    }
  
    /**
     * Test Main.  It will explore the beach 
     */
    public static void main( String args[] ) {
        Picture pix = new Picture("beach.jpg");
        pix.explore();
    }
}