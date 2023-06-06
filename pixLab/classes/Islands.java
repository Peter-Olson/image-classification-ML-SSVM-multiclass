import java.util.*;

/**
   Islands.java
   
   A class containing a set of functions that work to finding islands of objects, as separated by some other
   value denoting empty space. This class allows for islands to be classified by using diagonals nodes or by
   not allowing diagonal connections.
      
   There are two main functionalities within this program: Clearing islands (5 different options to choose from) and getting
   islands. Islands can be cleared based on size (eg. clearIslandsOfSizeXAndBelow(...)) and islands can be cleared based on region
   (eg. clearIslandsInRegion(...)), and the two can be combined (eg. clearIslandsInRegionOfSizeXAndBelow(...)). Islands can
   be retrieved as arrays of Points based on a Point location (eg. getIsland(...)), or islands can be retrieved based on a region
   (eg. getIslandsInRegion(...)) or size (eg. getIslandsOfSizeBetweenXAndY(...))
   
   ----------------------------------------------------------------------------------------------------
   
   Examples: [USING EMPTY SPACE '0' (Integer Wrapper class)]
   
   Clearing Islands within Region
   
   REGION - Within perimeter, but not touching
   
   Before (Diagonals)      After (Diagonals)                Before (No Diagonals)   After (No Diagonals)
   0 0 0 0 0               0 0 0 0 0                        0 0 0 0 0               0 0 0 0 0
   0 1 0 0 1               0 0 0 0 1                        1 1 0 0 1               1 1 0 0 1
   0 0 1 0 1               0 0 0 0 1                        0 0 1 0 1               0 0 0 0 1
   0 1 1 0 0               0 0 0 0 0                        0 1 1 0 0               0 0 0 0 0
   0 0 0 0 0               0 0 0 0 0                        0 0 0 0 0               0 0 0 0 0
   
   Clearing Islands of Size X and Below
   
   SIZE - Islands of size 3 or less are cleared
   
   Before (Diagonals)      After (Diagonals)                Before (No Diagonals)   After (No Diagonals)
   0 0 0 0 0               0 0 0 0 0                        0 0 0 0 0               0 0 0 0 0
   0 1 0 0 1               0 1 0 0 0                        1 1 0 0 1               0 0 0 0 0
   0 0 1 0 1               0 0 1 0 0                        0 0 1 0 1               0 0 0 0 0
   0 1 1 0 0               0 1 1 0 0                        0 1 1 0 0               0 0 0 0 0
   0 0 0 0 0               0 0 0 0 0                        0 0 0 0 0               0 0 0 0 0
   
   -----------------------------------------------------------------------------------------------------
   
   Public Methods
   * clearIslandsAboveRow(...) -> Clear islands above a row
   * clearIslandsBelowRow(...) -> Clear islands below a row
   * clearIslandsInRegion(...) -> Clear islands in region
   * clearIslandsInRegionOfSizeBetweenXAndY(...) -> Clear islands in region, whose size is within a range
   * clearIslandsInRegionOfSizeXAndAbove(...) -> Clear islands in region above a certain size
   * clearIslandsInRegionOfSizeXAndBelow(...) -> Clear islands in region below a certain size
   * clearIslandsLeftCol(...) -> Clear islands left of a column
   * clearIslandsRightCol(...) -> Clear islands right of a column
   * clearIslandsOfSizeBetweenXAndY(...) -> Clear islands whose size is within a range
   * clearIslandsOfSizeXAndAbove(...) -> Clear islands above a certain size
   * clearIslandsOfSizeXAndBelow(...) -> Clear islands below a certain size
   * getEmptyValue(...) -> Get the empty value which defines the space between islands
   * getIsland(...) -> Get an island at a specified Point
   * getIslandsInRegion(...) -> Get the islands in a region
   * getIslandsInRegionOfSizeBetweenXAndY(...) -> Get the islands in a region whose size is within a range
   * getIslandsInRegionOfSizeXAndAbove(...) -> Get the islands in a region above a certain size
   * getIslandsInRegionOfSizeXAndBelow(...) -> Get the islands in a region below a certain size
   * getIslandsOfSizeBetweenXAndY(...) -> Get the islands whose size is within a range
   * getIslandsOfSizeXAndAbove(...) -> Get the islands above a certain size
   * getIslandsOfSizeXAndBelow(...) -> Get the islands below a certain size
   * getNeighbors(...) -> Get the Points that are neighbor to a given Point
   * hasNeighbors(...) -> Determine if a Point has (non-empty) neighbors
   * isEmpty(...) -> Determines if a Point is empty
   * isInBounds(...) -> Determines if a Point is in-bounds
   * isNeighbor(...) -> Determines if two Points are neighbors
   * isOnEdge(...) -> Determines if a Point is on the edge of the grid
   * toIntArr(...) -> [@NOTE: Make private] Convert an ArrayList<Integer> to an int array
   * toPointArr(...) -> [@NOTE: Make private] Convert an ArrayList<Point> to a Point array
   * totalNeighbors(...) -> Gets the number of neighbors that a Point has
   
   -----------------------------------------------------------------------------------------------------
   @author Peter Olson
   @version 4/26/23
*/
public class Islands {

   public Object EMPTY_CELL_VALUE;
   private final int NO_EMPTY_ROW_FOUND = -1;
   private final int NO_EMPTY_COL_FOUND = -1;

   /**
      Create an Islands object, which is used to get islands or clear islands.
      
      The 'EMPTY_CELL_VALUE' parameter should be the value used to specify the separating data
      that defines an island (meaning that an island should be defined as being surrounded by
      EMPTY_CELL_VALUE values).
      
      See the java doc comments for the class for examples and details
      
      @param EMPTY_CELL_VALUE An object containing the value used to denote 'empty space' that
                              surrounds an island
   */
   public Islands( Object EMPTY_CELL_VALUE ) {
      this.EMPTY_CELL_VALUE = EMPTY_CELL_VALUE;
   }
   
   /**
      Get the 'empty value' of this Islands object.
      
      The empty value is the data that differentiates groups of data defined as 'islands' and the empty
      space inbetween. In the current version (12/30/22), only a single empty value is accepted (eg. such
      as a zero '0' that separates all other numbers). However, implementing a version that allows for
      multiple empty value types wouldn't be too hard to implement.
      
      @return Object The empty value
   */
   public Object getEmptyValue() {
      return EMPTY_CELL_VALUE;
   }
   
   /**Clears islands within a region, if they are smaller than a certain size. See clearIslandsInRegionOfSizeBetweenXAndY(...)*/
   public void clearIslandsInRegionOfSizeXAndBelow( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, int maxSize, boolean includeDiagonals ) {
      clearIslandsInRegionOfSizeBetweenXAndY(grid,topLeftCoordRow,topLeftCoordCol,
         bottomRightCoordRow,bottomRightCoordCol,1,maxSize,includeDiagonals);
   }
   /**Clears islands within a region, if they are smaller than a certain size. See clearIslandsInRegionOfSizeBetweenXAndY(...)*/
   public void clearIslandsInRegionOfSizeXAndAbove( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, int minSize, boolean includeDiagonals ) {
      clearIslandsInRegionOfSizeBetweenXAndY(grid,topLeftCoordRow,topLeftCoordCol,
         bottomRightCoordRow,bottomRightCoordCol,minSize,grid.length*grid[0].length,includeDiagonals);
   }
   /**
      Clears islands within a region that are between a minimum and a maximum size (inclusive). Note that
      islands must be fully within the region in order to be removed (islands that touch the boundary are
      not removed)
      
      @param grid The grid to clear islands from
      @param topLeftCoordRow The coordinate defining the row of the top left boundary (exclusive)
                             of the region being assessed for island clearing
      @param topLeftCoordCol The coordinate defining the column of the top left boundary (exclusive)
                             of the region being assessed for island clearing
      @param bottomRightCoordRow The coordinate defining the row of the bottom right boundary (exclusive)
                                 of the region being assessed for island clearing
      @param bottomRightCoordCol The coordinate defining the column of the bottom right boundary (exclusive)
                                 of the region being assessed for island clearing
      @param minSize The minimum size that an island needs to be in order to be cleared (inclusive)
      @param maxSize The maximum size that an island can be in order to be cleared (inclusive)
      @param includeDiagonals True if data should be considered to be 'connected' diagonally, false
                              otherwise
   */
   public void clearIslandsInRegionOfSizeBetweenXAndY( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol,
                                     int minSize, int maxSize, boolean includeDiagonals ) {
      if( topLeftCoordRow < -1 || bottomRightCoordRow > grid.length ||
          topLeftCoordCol < -1 || bottomRightCoordCol > grid[0].length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesBoundaries( islandList, topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol ) &&
             islandList.length >= minSize && islandList.length <= maxSize )
            removeIsland( checkMap, islandList, grid ); //Turns values into empty values
         else //vv Unchecks these values, but does not change their value vv
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**Clear islands smaller than a given size (inclusive). See clearIslandsOfSizeBetweenXAndY(...)*/
   public void clearIslandsOfSizeXAndBelow( Object[][] grid, int maxSize, boolean includeDiagonals ) {
      clearIslandsOfSizeBetweenXAndY( grid, 1, maxSize, includeDiagonals );
   }
   /**Clear islands smaller than a given size (inclusive). See clearIslandsOfSizeBetweenXAndY(...)*/
   public void clearIslandsOfSizeXAndAbove( Object[][] grid, int minSize, boolean includeDiagonals ) {
      clearIslandsOfSizeBetweenXAndY( grid, minSize, grid.length*grid[0].length, includeDiagonals );
   }
   /**
      Clear islands between a minimum and a maximum size (inclusive).
      
      @param grid The grid to clear islands from
      @param minSize The minimum size that an island needs to be in order to be cleared (inclusive)
      @param maxSize The maximum size that an island can be in order to be cleared (inclusive)
      @param includeDiagonals True if data should be considered to be 'connected' diagonally, false
                              otherwise
   */
   public void clearIslandsOfSizeBetweenXAndY( Object[][] grid, int minSize, int maxSize, boolean includeDiagonals ) {
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( -1, -1, grid.length, grid[0].length,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( islandList.length >= minSize && islandList.length <= maxSize )
            removeIsland( checkMap, islandList, grid ); //Turns values into empty values
         else //vv Unchecks these values, but does not change their value vv
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**
      Clears islands within a region. Note that islands must be fully within the region in order to be
      removed.
      
      @param grid The grid to clear islands from
      @param topLeftCoordRow The coordinate defining the row of the top left boundary (exclusive)
                             of the region being assessed for island clearing
      @param topLeftCoordCol The coordinate defining the column of the top left boundary (exclusive)
                             of the region being assessed for island clearing
      @param bottomRightCoordRow The coordinate defining the row of the bottom right boundary (exclusive)
                                 of the region being assessed for island clearing
      @param bottomRightCoordCol The coordinate defining the column of the bottom right boundary (exclusive)
                                 of the region being assessed for island clearing
      @param includeDiagonals True if data should be considered to be 'connected' diagonally, false
                              otherwise
   */
   public void clearIslandsInRegion( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, boolean includeDiagonals ) {
      if( topLeftCoordRow < -1 || bottomRightCoordRow > grid.length ||
          topLeftCoordCol < -1 || bottomRightCoordCol > grid[0].length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesBoundaries( islandList, topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol ) )
            removeIsland( checkMap, islandList, grid ); //Turns values into empty values
         else //vv Unchecks these values, but does not change their value vv
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   /**Clears all islands that are not touching the first row. See other overloaded method*/
   public void clearIslandsBelowRow( Object[][] grid, boolean includeDiagonals ) { clearIslandsBelowRow(grid,0,includeDiagonals); }
   /**
      Clear all islands beneath a given row. Any islands that are touching the row are not removed.
      
      @param grid The grid to remove islands from
      @param row Any islands touching this row are not removed
      @param includeDiagonals True if data is considered 'connected' diagonally, false otherwise
   */
   public void clearIslandsBelowRow( Object[][] grid, int row, boolean includeDiagonals ) {
      if( row < 0 || row >= grid.length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapDown( row, checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesRowBelow( islandList, row ) )
            removeIsland( checkMap, islandList, grid );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**Clear all islands above the bottom row. See other overloaded method for details*/
   public void clearIslandsAboveRow( Object[][] grid, boolean includeDiagonals ) { clearIslandsAboveRow(grid,grid.length-1,includeDiagonals); }
   /**
      Clear all islands above a given row. Any islands that are touching the row are not removed.
      
      @param grid The grid to remove islands from
      @param row Any islands touching this row are not removed
      @param includeDiagonals True if data is considered 'connected' diagonally, false otherwise
   */
   public void clearIslandsAboveRow( Object[][] grid, int row, boolean includeDiagonals ) {
      if( row < 0 || row >= grid.length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearAboveRow = fillMapUp( row, checkMap, grid );
      clearBetween( grid, -1, clearAboveRow, -1, grid[0].length ); //exclusive
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesRowAbove( islandList, row ) )
            removeIsland( checkMap, islandList, grid );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**Clear all islands right of the first column*/
   public void clearIslandsRightCol( Object[][] grid, boolean includeDiagonals ) { clearIslandsRightCol(grid,0,includeDiagonals); }
   /**
      Clear all islands right of a given column. Any islands that are touching the column are not removed.
      
      @param grid The grid to remove islands from
      @param col Any islands touching this column are not removed
      @param includeDiagonals True if data is considered 'connected' diagonally, false otherwise
   */
   public void clearIslandsRightCol( Object[][] grid, int col, boolean includeDiagonals ) {
      if( col < 0 || col >= grid.length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearRightCol = fillMapRight( col, checkMap, grid );
      clearBetween( grid, -1, grid.length, clearRightCol, grid[0].length ); //exclusive bounds
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesColRight( islandList, col ) )
            removeIsland( checkMap, islandList, grid );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**Clear all islands left of the rightmost column. See other overloaded method for details*/
   public void clearIslandsLeftCol( Object[][] grid, boolean includeDiagonals ) { clearIslandsLeftCol(grid,0,includeDiagonals); }
   /**
      Clear all islands left of a given column. Any islands that are touching the column are not removed.
      
      @param grid The grid to remove islands from
      @param col Any islands touching this column are not removed
      @param includeDiagonals True if data is considered 'connected' diagonally, false otherwise
   */
   public void clearIslandsLeftCol( Object[][] grid, int col, boolean includeDiagonals ) {
      if( col < 0 || col >= grid.length )
         return;
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearLeftCol = fillMapLeft( col, checkMap, grid );
      clearBetween( grid, -1, grid.length, -1, clearLeftCol ); //exclusive bounds
      
      // Get island, check island, remove island
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesColLeft( islandList, col ) )
            removeIsland( checkMap, islandList, grid );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
   }
   
   /**
      Determines if the list of Points are below the given row or not (inclusive)
      
      @param islandList The list of Points which compose an island
      @param row The row (x coordinate of the grid) to compare to the island x coordinates
      @return boolean True if the island touches the row, false otherwise
   */
   private boolean islandTouchesRowBelow( Point[] islandList, int row ) {
      for( Point p : islandList )
         if( p.x <= row )
            return true;
      return false;
   }
   /**Determines if the list of Points are above the given row or not. See islandTouchesRowBelow(...)*/
   private boolean islandTouchesRowAbove( Point[] islandList, int row ) {
      for( Point p : islandList )
         if( p.x >= row )
            return true;
      return false;
   }
   /**Determines if the list of Points are right of the given column or not. See islandTouchesRowBelow(...)*/
   private boolean islandTouchesColRight( Point[] islandList, int col ) {
      for( Point p : islandList )
         if( p.y <= col )
            return true;
      return false;
   }
   /**Determines if the list of Points are left of the given column or not. See islandTouchesRowBelow(...)*/
   private boolean islandTouchesColLeft( Point[] islandList, int col ) {
      for( Point p : islandList )
         if( p.y >= col )
            return true;
      return false;
   }
   /**
      Determines if the island is outside the given boundaries or not (inclusive)
      
      @param islandList The list of Points that compose the island
      @param startRow The index of the top row of the boundary
      @param startCol The index of the left column of the boundary
      @param endRow The index of the bottom row of the boundary
      @param endCol The index of the right column of the boundary
      @return boolean True if the island is outside or touching the boundaries, false otherwise
   */
   private boolean islandTouchesBoundaries( Point[] islandList, int startRow, int startCol, int endRow, int endCol ) {
      for( Point p : islandList )
         if( p.x <= startRow || p.x >= endRow || p.y <= startCol || p.y >= endCol )
            return true;
      return false;
   }
   
   /**
      Removes the island from the grid by settings its values to empty values.
      Removes all entries from the uncheck map, so that these values will not be checked again
      
      @param checkMap The map that contains Point data based on coordinate String keys, which is used
                      to tell whether the Point has been checked or not
      @param islandList The list of Points that composed the island which is to be removed
      @param grid The grid of data that is being processed
   */
   private void removeIsland( TreeMap<String, Point> checkMap, Point[] islandList, Object[][] grid ) {
      for( int rep = 0; rep < islandList.length; rep++ ) {
         Point p = islandList[rep];
         ((Pixel)grid[p.x][p.y]).setColor( ((Pixel)EMPTY_CELL_VALUE).getColor() );
         checkMap.remove( p.toString() );
      }
   }
   
   /**
      Removes the island from the uncheck map. The uncheck map keeps track of the 'splashes', which are the
      Points that are flagged as the island is being 'discovered'. Once a Point has been selected to be
      splashed, or for the Points that are splashed are splashed, they are removed from the uncheck map
      
      @param checkMap The map that keeps track of which Points have not been checked
      @param islandList The island to remove from the uncheck map
   */
   private void removeIslandFromCheckMap( TreeMap<String, Point> checkMap, Point[] islandList ) {
      for( int rep = 0; rep < islandList.length; rep++ ) {
         Point p = islandList[rep];
         checkMap.remove( p.toString() );
      }
   }
   
   /**
      Adds all Points to the uncheck map that are below a given row (inclusive).
      If an empty row is encountered below the starting row, quit checking early and return the
      index of the empty row. If no empty rows are found below the starting row, return
      NO_EMPTY_ROW_FOUND (usually -1)
      
      @param startRow The row to start filling the uncheck map at from within the grid
      @param checkMap The map that contains the Points that have not been checked yet
      @param grid The grid of values that is being processed
      @return int The index of the empty row where the fill map method stopped. If no empty row was
                  found, then NO_EMPTY_ROW_FOUND is returned
   */
   private int fillMapDown( int startRow, TreeMap<String, Point> checkMap, Object[][] grid ) {
      int clearRowIndex = NO_EMPTY_ROW_FOUND;
      for( int row = startRow; row < grid.length; row++ ) {
         boolean emptyRow = true;
         for( int col = 0; col < grid[0].length; col++ ) {
            if( !isEmpty(row,col,grid) ) {
               emptyRow = false;
               Point p = new Point(row,col);
               checkMap.put( p.toString(), p );
            }
         }
         //If an empty row is found, save the row index and stop searching for islands to fill the checkSet
         if( emptyRow && clearRowIndex == NO_EMPTY_ROW_FOUND && row != startRow ) {
            clearRowIndex = row;
            break;
         }
      }
      
      return clearRowIndex;
   }
   /**Fills the uncheck map starting from the given row, going upward. See fillMapDown(...) for details*/
   private int fillMapUp( int startRow, TreeMap<String, Point> checkMap, Object[][] grid ) {
      int clearRowIndex = NO_EMPTY_ROW_FOUND;
      for( int row = startRow; row >= 0; row-- ) {
         boolean emptyRow = true;
         for( int col = 0; col < grid[0].length; col++ ) {
            if( !isEmpty(row,col,grid) ) {
               emptyRow = false;
               Point p = new Point(row,col);
               checkMap.put( p.toString(), p );
            }
         }
         //If an empty row is found, save the row index and stop searching for islands to fill the checkSet
         if( emptyRow && clearRowIndex == NO_EMPTY_ROW_FOUND && row != startRow ) {
            clearRowIndex = row;
            break;
         }
      }
      
      return clearRowIndex;
   }
   /**Fills the uncheck map starting at the given column, and going right. See fillMapDown(...) for details*/
   private int fillMapRight( int startCol, TreeMap<String, Point> checkMap, Object[][] grid ) {
      int clearColIndex = NO_EMPTY_COL_FOUND;
      for( int col = startCol; col < grid[0].length; col++ ) {
         boolean emptyCol = true;
         for( int row = 0; row < grid.length; row++ ) {
            if( !isEmpty(row,col,grid) ) {
               emptyCol = false;
               Point p = new Point(row,col);
               checkMap.put( p.toString(), p );
            }
         }
         //If an empty col is found, save the col index and stop searching for islands to fill the checkSet
         if( emptyCol && clearColIndex == NO_EMPTY_COL_FOUND && col != startCol ) {
            clearColIndex = col;
            break;
         }
      }
      
      return clearColIndex;
   }
   /**Fill the uncheck map with Points starting at the given column, and going left. See fillMapDown(...)
      for details*/
   private int fillMapLeft( int startCol, TreeMap<String, Point> checkMap, Object[][] grid ) {
      int clearColIndex = NO_EMPTY_COL_FOUND;
      for( int col = startCol; col >= 0; col-- ) {
         boolean emptyCol = true;
         for( int row = 0; row < grid.length; row++ ) {
            if( !isEmpty(row,col,grid) ) {
               emptyCol = false;
               Point p = new Point(row,col);
               checkMap.put( p.toString(), p );
            }
         }
         //If an empty col is found, save the col index and stop searching for islands to fill the checkSet
         if( emptyCol && clearColIndex == NO_EMPTY_COL_FOUND && col != startCol ) {
            clearColIndex = col;
            break;
         }
      }
      
      return clearColIndex;
   }
   /**
      Fill the uncheck map with Points taken from the given region. The region is defined by startRow
      (inclusive), startCol (inclusive), endRow (exclusive), and endCol (exclusive).
      
      In order to define the full grid region, -1, -1, grid.length, and grid[0].length should be used.
      
      If a row is found that is empty, stop checking and return the row index of the empty row. If no
      empty rows are found (below the starting row), return NO_EMPTY_ROW_FOUND (usually -1)
      
      @param startRow The index of the row to begin filling the uncheck map at
      @param startCol The index of the column to begin filling the uncheck map at
      @param endRow The index of the row to stop at (exclusive)
      @param endCol The index of the column to stop at (exclusive)
      @param checkMap The uncheck map that contains the list of Points that still need to be checked
      @param grid The grid of values that is being processed
      @return int The 
   */
   private int fillMapRegion( int startRow, int startCol, int endRow, int endCol,
                              TreeMap<String, Point> checkMap, Object[][] grid ) {
      int clearRowIndex = NO_EMPTY_ROW_FOUND;
      
      //Since the clearing region can stretch to the edges of the grid (which would be specified by the exclusive boundaries)
      //of -1 for the starting row and column, and grid.length and grid[0].length for the ending row and column
      //...but for this function, this needs to be amended in order to not start checking out of bounds of the grid
      if( startRow == -1 ) startRow = 0;
      if( startCol == -1 ) startCol = 0;
      
      for( int row = startRow; row < endRow; row++ ) {
         boolean emptyRow = true;
         for( int col = startCol; col < endCol; col++ ) {
            if( !isEmpty(row,col,grid) ) {
               emptyRow = false;
               Point p = new Point(row,col);
               checkMap.put( p.toString(), p );
            }
         }
         //If an empty row is found, save the row index and stop searching for islands to fill the checkSet
         if( emptyRow && clearRowIndex == NO_EMPTY_ROW_FOUND && row > endRow ) {
            clearRowIndex = row;
            break;
         }
      }
      
      return clearRowIndex;
   }
   
   /**
      Clear values to the empty value within the given region (exclusive coordinates)
      
      @param grid The grid that is being processed
      @param rowStartExclusive The index of the row that is begun clearing after
      @param rowEndExclusive The index of the row that specifies when clearing stops
      @param colStartExclusive The index of the column that is begun clearing after
      @param colEndExclusive The index of the column that specifies when clearing stops
   */
   private void clearBetween( Object[][] grid, int rowStartExclusive, int rowEndExclusive,
                                           int colStartExclusive, int colEndExclusive ) {
      //No empty rows or columns found... clearing not needed
      if( rowStartExclusive < 0 && colStartExclusive < 0 &&
          ( rowEndExclusive >= grid.length || rowEndExclusive < 0 ) && 
          ( colEndExclusive >= grid[0].length || colEndExclusive < 0 ) )
         return;
      
      for( int row = rowStartExclusive + 1; row < rowEndExclusive; row++ ) {
         for( int col = colStartExclusive + 1; col < colEndExclusive; col++ ) {
            grid[row][col] = EMPTY_CELL_VALUE;
         }
      }
   }
   
   /**
      Get the island that is connected to the given point.
      
      An island is determined by all connected values that are not the 'empty value'.
      'Connectedness' is determined whether values are adjacent to one another, and whether
      diagonal adjacency is allowed or not
      
      getIsland(...) uses a non-linear, neighbor-based, radial algorithm. A series of 'splashes' are used
      to expand the search for island Points in a circular-based approach. See below:
      
      Initial Grid     First Splash     Second Splash    Island Found
      Start Point:
      P(3,3)
      0 0 0 0 0 0 0    0 0 0 0 0 0 0    0 0 0 0 0 0 0    0 0 0 0 0 0 0
      0 1 1 1 1 1 0    0 1 1 1 1 1 0    0 U U U U U 0    0 C C C C C 0   U - unchecked
      0 1 2 2 2 1 0    0 1 U U U 1 0    0 U C C C U 0    0 C C C C C 0   C - checked
      0 1 2 3 2 1 0 -> 0 1 U C U 1 0 -> 0 U C C C U 0 -> 0 C C C C C 0
      0 1 2 2 2 1 0    0 1 U U U 1 0    0 U C C C U 0    0 C C C C C 0
      0 1 1 1 1 1 0    0 1 1 1 1 1 0    0 U U U U U 0    0 C C C C C 0
      0 0 0 0 0 0 0    0 0 0 0 0 0 0    0 0 0 0 0 0 0    0 0 0 0 0 0 0
      
      The first starting point is immediately taken out of the uncheck map, the structure that remembers
      which points have not been checked and added to the list of Points that compose the island.
      
      The first splash is the ring of neighbors surrounding the starting point. In the example above,
      diagonal neighbors are included. For the second splash, a ring around all points that make up the
      first splash are added to the second splash list. Any checked points and any points that are part
      of the first splash are not added (no duplicates). Then the first splash points are checked, added
      to the island list, and removed from the unchecked list. All points in the second splash list are
      then moved over to the first splash list, and the process is repeated until there are no points
      found in the first splash list.
      
      In terms of efficiency, at worst, the operation is similar to pathfinding, such as A*. Thus, this
      algorithm operates similar to O(b^d), where b = 8 and d = n^2,
      where n = grid.length > grid[0].length ? grid.length : grid[0].length, when diagonals are used.
      
      However, because of its radial nature, there isn't much backtracking, so on average, we would expect
      half as in value for b and d, so (1/4)O(b^d), b = 8, d = n^2, all of which is going to roughly mimic
      O(n^2log(n)) at worst and O(n^2) at best
      
      @param p The starting Point. From this Point, all other Points that belong to this island are
               searched for
      @param grid The grid that contains all of the values and empty values
      @param includeDiagonals True if diagonal adjacency is allowed for islands, false otherwise
      @return Point[] The list of Points that compose this island
   */
   public Point[] getIsland( Point p, Object[][] grid, boolean includeDiagonals ) {
      if( isEmpty(p,grid) )
         return new Point[]{};
      if( !hasNeighbors(p,grid,includeDiagonals) )
         return new Point[]{p};
      
      //Map for marking the state of each element within the grid
      TreeMap<String, Boolean> markMap = new TreeMap<String, Boolean>();
      //List for marking which elements need to be marked
      LinkedList<Point> splashNodes = new LinkedList<Point>();
      LinkedList<Point> secondSplash = new LinkedList<Point>();
      
      //Continue splashing and marking until there are no more splash nodes to mark
      addSplash( p, grid, markMap, splashNodes, includeDiagonals );
      do {
         addSecondSplash( grid, markMap, splashNodes, secondSplash, includeDiagonals );
         relocateSplash( splashNodes, secondSplash );
         
      } while( !splashNodes.isEmpty() );
      
      //Convert to list of Points that constitute the island
      Set<String> coordKeys = markMap.keySet();
      Point[] islandList = new Point[ coordKeys.size() ];
      LinkedList<Point> convertedKeys = new LinkedList<Point>();
      convertKeys( coordKeys, convertedKeys );
      islandList = convertedKeys.toArray( islandList );
      
      return islandList;
   }
   
   /**
      Get a list of all the islands within a given region. The borders of the region defined are exclusive,
      meaning that any islands that touch these borders are not included in the list of islands that are
      returned
      
      @param grid The grid that has the values and empty values
      @param topLeftCoordRow The top left x coordinate of the region. Any islands lying on the border of this region are not included
      @param topLeftCoordCol The top left y coordinate of the region
      @param bottomRightCoordRow The bottom right x coordinate of the region
      @param bottomRightCoordCol The bottom right y coordinate of the region
      @param includeDiagonals True if the islands should be determined using diagonals, false for only orthagonally adjacent neighboring
      @return LinkedList<Point[]> The list of islands, each of which is made up of Points
   */
   public LinkedList<Point[]> getIslandsInRegion( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, boolean includeDiagonals ) {
      if( topLeftCoordRow < -1 || bottomRightCoordRow > grid.length ||
          topLeftCoordCol < -1 || bottomRightCoordCol > grid[0].length )
         return new LinkedList<Point[]>();
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, add island
      LinkedList<Point[]> islandsInRegion = new LinkedList<Point[]>();
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesBoundaries( islandList, topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol ) )
            islandsInRegion.add( islandList );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
      
      return islandsInRegion;
   }
   
   /**Get a list of the islands in the given region whose size are X or greater. See getIslandsInRegionOfSizeBetweenXAndY(...) for details*/
   public LinkedList<Point[]> getIslandsInRegionOfSizeXAndAbove( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, int minSize, boolean includeDiagonals ) {
      return getIslandsInRegionOfSizeBetweenXAndY(grid,topLeftCoordRow,topLeftCoordCol,
               bottomRightCoordRow,bottomRightCoordCol,minSize,grid.length*grid[0].length,includeDiagonals);
   }
   /**Get a list of the islands in the given region whose size are X or less. See getIslandsInRegionOfSizeBetweenXAndY(...) for details*/
   public LinkedList<Point[]> getIslandsInRegionOfSizeXAndBelow( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol, int maxSize, boolean includeDiagonals ) {
      return getIslandsInRegionOfSizeBetweenXAndY(grid,topLeftCoordRow,topLeftCoordCol,
               bottomRightCoordRow,bottomRightCoordCol,1,maxSize,includeDiagonals);
   }
   /**
    * Get a list of the islands in the given region whose size are between X and Y
    *
    * @param grid The grid that has the values and empty values
    * @param topLeftCoordRow The top left x coordinate of the region. Any islands lying on the border of this region are not included
    * @param topLeftCoordCol The top left y coordinate of the region
    * @param bottomRightCoordRow The bottom right x coordinate of the region
    * @param bottomRightCoordCol The bottom right y coordinate of the region
    * @param minSize The minimum size (inclusive) that an island must be in order to be included
    * @param maxSize The maximum size (inclusive) that an island can be in order to be included
    * @param includeDiagonals True if the islands should be determined using diagonals, false for only orthagonally adjacent neighboring
    * @return LinkedList<Point[]> The list of islands, each of which is made up of Points
   */
   public LinkedList<Point[]> getIslandsInRegionOfSizeBetweenXAndY( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
                                     int bottomRightCoordRow, int bottomRightCoordCol,
                                     int minSize, int maxSize, boolean includeDiagonals ) {
      if( topLeftCoordRow < -1 || bottomRightCoordRow > grid.length ||
          topLeftCoordCol < -1 || bottomRightCoordCol > grid[0].length )
         return new LinkedList<Point[]>();
      if( minSize <= 0 )
         return new LinkedList<Point[]>();
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, get island
      LinkedList<Point[]> islandSet = new LinkedList<Point[]>();
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( !islandTouchesBoundaries( islandList, topLeftCoordRow, topLeftCoordCol, bottomRightCoordRow, bottomRightCoordCol ) &&
             islandList.length >= minSize && islandList.length <= maxSize )
            islandSet.add( islandList );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
      
      return islandSet;
   }
   
   public LinkedList<Point[]> getIslandsOfSizeXAndAbove( Object[][] grid, int minSize, boolean includeDiagonals ) {
      return getIslandsOfSizeBetweenXAndY(grid,minSize,grid.length*grid[0].length,includeDiagonals);
   }
   public LinkedList<Point[]> getIslandsOfSizeXAndBelow( Object[][] grid, int maxSize, boolean includeDiagonals ) {
      return getIslandsOfSizeBetweenXAndY(grid,1,maxSize,includeDiagonals);
   }
   /**
    * Get a list of the islands within the grid that have a size between X and Y, inclusive 
    *
    * @param grid The grid of data that is being processed
    * @param minSize The minimum size that an island must be in order to be included
    * @param maxSize The maximum size that an island can be in order to be included
    * @param includeDiagonals True if diagonals are allowed for determining islands, false if islands are only composed of orthagonally adjacent neighbors
    * @return LinkedList<Point[]> The list of islands found in the grid that are between the two sizes
   */
   public LinkedList<Point[]> getIslandsOfSizeBetweenXAndY( Object[][] grid, int minSize, int maxSize, boolean includeDiagonals ) {
      if( minSize <= 0 )
         return new LinkedList<Point[]>();
      
      TreeMap<String, Point> checkMap = new TreeMap<String, Point>();
      // Fill map with elements to check
      int clearBelowRow = fillMapRegion( -1, -1, grid.length, grid[0].length,
                                         checkMap, grid );
      clearBetween( grid, clearBelowRow, grid.length, -1, grid[0].length ); //exclusive
      
      // Get island, check island, get island
      LinkedList<Point[]> islandSet = new LinkedList<Point[]>();
      while( !checkMap.isEmpty() ) {
         Point islandPoint = checkMap.firstEntry().getValue();
         Point[] islandList = getIsland( islandPoint, grid, includeDiagonals );
         if( islandList.length >= minSize && islandList.length <= maxSize )
            islandSet.add( islandList );
         else
            removeIslandFromCheckMap( checkMap, islandList );
      }
      
      return islandSet;
   }
   
   /**
    * Adds all surrounding neighbors to the splash list. The splash list is later splashed again, for each Point in the splash list.
    * The first splash list Points are checked off on the map and cannot be splashed again.
    *
    * In non-technical terms, this method finds all neighbors of a point and adds them to a list. These points are checked off so that
    * they cannot be checked again, and these points' neighbors will later be added in addSecondSplash(...)
    *
    * @param p The Point being splashed. All neighboring Points are added to the splash list
    * @param grid The grid being processed
    * @param markMap The check map that keeps track of which Points have been checked or not
    * @param splashNodes The list of Points from the first splash. These Points are later themselves splashed in addSecondSplash(...)
    * @param includeDiagonals True if diagonals are used to create islands, false if no diagonals are allowed
   */
   private void addSplash( Point p, Object[][] grid, TreeMap<String, Boolean> markMap, LinkedList<Point> splashNodes,
                           boolean includeDiagonals ) {
      String currentKey = p.toString();
      markMap.put( currentKey, true );
      Point[] neighbors = getNeighbors( p, grid, includeDiagonals );
      for( int rep = 0; rep < neighbors.length; rep++ ) {
         String key = neighbors[rep].toString();
         if( !markMap.containsKey( key ) ) {
            splashNodes.add( neighbors[rep] );
            markMap.put( key, true );
         }
      }
   }
   
   /**
    * Splashes all Points that are in the splash list. The Points that are splashed are removed
    *
    * In non-technical terms, this method finds all of the neighbors of a list of Points and adds them to a list. In order to be added,
    * the Points have to not have been checked before. The new set of neighbors added are added to the check list. Later, in
    * relocateSplash(...), the Points in the second splash list are moved to the first splash list, the process starts over, eventually
    * terminating when the first splash list is empty, meaning that all Points have been found that make up the island
    *
    * @param grid The grid that is being processed
    * @param markMap The map that keeps track of which Points have been checked and which have not
    * @param splashNodes The list of Points that need to be splashed
    * @param secondSplash The list of Points resulting from the secondSplash
    * @param includeDiagonals True if diagonals can be used to determine an island, false if no diagonals are used
   */
   private void addSecondSplash( Object[][] grid, TreeMap<String, Boolean> markMap,
                                 LinkedList<Point> splashNodes, LinkedList<Point> secondSplash, boolean includeDiagonals ) {
      int totalSplashNodes = splashNodes.size();
      for( int rep = 0; rep < totalSplashNodes; rep++ ) {
         Point popPoint = splashNodes.pop();
         Point[] neighbors = getNeighbors( popPoint, grid, includeDiagonals );
         for( int index = 0; index < neighbors.length; index++ ) {
            String key = neighbors[index].toString();
            if( !markMap.containsKey( key ) ) {
               secondSplash.add( neighbors[index] );
               markMap.put( key, true );
            } else {
               splashNodes.remove( neighbors[index] );
            }
         }
      }
   }
   
   /**
    * Moves all of the Points in the secondSplash list to the firstSplash list. After this method, secondSplash should be empty
    *
    * @param splashNodes The list of Points in the firstSplash list
    * @param secondSplash The list of Points in the secondSplash list
   */
   private void relocateSplash( LinkedList<Point> splashNodes, LinkedList<Point> secondSplash ) {
      while( !secondSplash.isEmpty() )
         splashNodes.add( secondSplash.pop() );
   }
   
   /**
    * Converts a Set of keys (Strings, in the form of "x,y") to a list of Points
    *
    * @param coordKeys The Set of keys (Strings)
    * @param convertedKeys The list of Points
   */
   private void convertKeys( Set<String> coordKeys, LinkedList<Point> convertedKeys ) {
      String[] arr = new String[ coordKeys.size() ];
      arr = coordKeys.toArray( arr );
      for( int rep = 0; rep < arr.length; rep++ ) {
         int px = Integer.parseInt( arr[rep].substring( 0, arr[rep].indexOf(",") ) );
         int py = Integer.parseInt( arr[rep].substring( arr[rep].indexOf(",") + 1 ) );
         Point p = new Point( px, py );
         convertedKeys.add(p);
      }
   }
   
   /**
    * Finds the total number of neighbors that a Point has. See getNeighbors(...)
    *
    * Note that a neighbor is defined as a non-empty Point that is within the bounds of the grid
    *
    * @param p The target Point
    * @param grid The grid that contains all the data
    * @param includeDiagonals True if diagonals determine islands, false if diagonals are not used
    * @return int The total number of neighbors that this Point has
   */
   public int totalNeighbors( Point p, Object[][] grid, boolean includeDiagonals ) {
      return getNeighbors( p, grid, includeDiagonals).length;
   }
   
   /**
    * Determines whether the given Point has any neighbors or not
    *
    * Note that a neighbor is defined as a non-empty Point that is within the bounds of the grid
    *
    * @param p The target Point
    * @param grid The grid that contains all the data
    * @param includeDiagonals True if diagonals determine islands, false otherwise
    * @return boolean True if the Point has one or more neighbors, false otherwise
   */
   public boolean hasNeighbors( Point p, Object[][] grid, boolean includeDiagonals ) {
      if( includeDiagonals )
         return hasNeighborsDiagonals( p.x, p.y, grid );
      else
         return hasNeighborsNoDiagonals( p.x, p.y, grid );
   }
   /**Determines whether the given Point has any neighbors or not. See hasNeighbors(...)*/
   private boolean hasNeighborsDiagonals( int x, int y, Object[][] grid ) {
      for( int row = x-1; row <= x+1; row++ ) {
         for( int col = y-1; col <= y+1; col++ ) {
            if( !isInBounds(row,col,grid) )
               continue;
         
            if( (row == x && col == y) ||
                isEmpty(row,col,grid) )
               continue;
            else
               return true;
         }
      }
      return false;
   }
   /**Determines whether the given Point has any neighbors or not. See hasNeighbors(...)*/
   private boolean hasNeighborsNoDiagonals( int x, int y, Object[][] grid ) {
      for( int row = x-1; row <= x+1; row++ ) {
         for( int col = y-1; col <= y+1; col++ ) {
            if( !isInBounds(row,col,grid) )
               continue;
         
            if( (row == x && col == y) ||
                (row != x && col != y) ||
                isEmpty(row,col,grid) )
               continue;
            else
               return true;
         }
      }
      return false;
   }
   
   /**
    * Gets a list of the neighbors of the given Point
    *
    * Note that a neighbor is defined as a non-empty Point that is within the bounds of the grid
    *
    * @param p The target Point
    * @param grid The grid that contains the values
    * @param includeDiagonals True if diagonals determine islands, false otherwise
    * @return Point[] The list of Points that are neighbors to the given Point
   */
   public Point[] getNeighbors( Point p, Object[][] grid, boolean includeDiagonals ) {
      if( includeDiagonals )
         return getNeighborsDiagonals( p.x, p.y, grid );
      else
         return getNeighborsNoDiagonals( p.x, p.y, grid );
   }
   /**Gets a list of the neighbors of the given Point. See getNeighbors(...)*/
   private Point[] getNeighborsDiagonals( int x, int y, Object[][] grid ) {
      LinkedList<Point> list = new LinkedList<Point>();
      for( int row = x-1; row <= x+1; row++ ) {
         for( int col = y-1; col <= y+1; col++ ) {
            if( !isInBounds(row,col,grid) )
               continue;
         
            if( (row == x && col == y) ||
                isEmpty(row,col,grid) )
               continue;
            
            Point iterPoint = new Point( row, col );
            list.add( iterPoint );
         }
      }
      return toPointArr( list );
   }
   /**Gets a list of the neighbors of the given Point. See getNeighbors(...)*/
   private Point[] getNeighborsNoDiagonals( int x, int y, Object[][] grid ) {
      LinkedList<Point> list = new LinkedList<Point>();
      for( int row = x-1; row <= x+1; row++ ) {
         for( int col = y-1; col <= y+1; col++ ) {
            if( !isInBounds(row,col,grid) )
               continue;
         
            if( (row == x && col == y) ||
                (row != x && col != y) ||
                isEmpty(row,col,grid) )
               continue;
            
            Point iterPoint = new Point( row, col );
            list.add( iterPoint );
         }
      }
      return toPointArr( list );
   }
   
   /**
    * Determines whether the two Points are neighbors within the grid or not
    *
    * Note that a neighbor is defined as a non-empty Point that is within the bounds of the grid
    *
    * @param p1 The first Point of interest
    * @param p2 The second Point of interest
    * @param grid The grid that contains the values
    * @param includeDiagonals True if diagonals determine islands, false otherwise
    * @return boolean True if the Points are neighbors, false otherwise
   */
   public boolean isNeighbor( Point p1, Point p2, Object[][] grid, boolean includeDiagonals ) {
      if( includeDiagonals )
         return isNeighborDiagonals( p1.x, p1.y, p2.x, p2.y, grid );
      else
         return isNeighborNoDiagonals( p1.x, p1.y, p2.x, p2.y, grid );
   }
   /**Determines whether the two Points are neighbors within the grid or not. See isNeighbor(...)*/
   private boolean isNeighborDiagonals( int x1, int y1, int x2, int y2, Object[][] grid ) {
      if( ((Math.abs( x1 - x2 ) <= 1 && Math.abs( y1 - y2 ) == 1) ||
          (Math.abs( y1 - y2 ) <= 1 && Math.abs( x1 - x2 ) == 1)) &&
          (!isEmpty(x1,y1,grid) && !isEmpty(x2,y2,grid)) )
         return true;
      return false;
   }
   /**Determines whether the two Points are neighbors within the grid or not. See isNeighbor(...)*/
   private boolean isNeighborNoDiagonals( int x1, int y1, int x2, int y2, Object[][] grid ) {
      if( ((Math.abs( x1 - x2 ) <= 1 && Math.abs( y1 - y2 ) == 0) ||
          (Math.abs( y1 - y2 ) <= 1 && Math.abs( x1 - x2 ) == 0)) &&
          (!isEmpty(x1,y1,grid) && !isEmpty(x2,y2,grid)) )
         return true;
      return false;   
   }

   /**
    * Determines whether the given Point is on the edge of the grid, or not
    *
    * The edge of the grid is defined as being any coordinate whose x or y value is 1 away from
    * being out of bounds (eg. ArrayOutOfBoundsException)
    *
    * @param p The target Point
    * @param grid The grid that contains the values
    * @return boolean True if the Point is on the edge, false otherwise
   */
   public boolean isOnEdge( Point p, Object[][] grid ) {
      return isOnEdge( p.x, p.y, grid );
   }
   /**Determines whether the given Point is on the edge of the grid, or not. See isOnEdge( Point p, Object[][] grid )*/
   private boolean isOnEdge( int x, int y, Object[][] grid ) {
      if( x == 0 || y == 0 || x == grid.length-1 || y == grid[0].length-1 )
         return true;
      return false;
   }
   
   /**
    * Determines whether the given Point is in bounds or not. 
    *
    * A Point that is in bounds is any Point (regardless of its value) that is within the bounds of the grid
    *
    * @param p The target Point
    * @param grid The grid with values
    * @return boolean True if the Point is in bounds, false otherwise
    */
   public boolean isInBounds( Point p, Object[][] grid ) {
      return isInBounds( p.x, p.y, grid );
   }
   /**Determines whether the coordinate is in bounds or not. See isInBounds( Point p, Object[][] grid )*/
   private boolean isInBounds( int x, int y, Object[][] grid ) {
      if( x >= 0 && y >= 0 && x < grid.length && y < grid[0].length )
         return true;
      return false;
   }
   
   /**
    * Determines whether the given Point is empty or not.
    *
    * An empty Point is one whose value is equal to the EMPTY_CELL_VALUE
    *
    * @param p The target Point
    * @param grid The grid of values
    * @return boolean True if the Point is empty, false otherwise
   */
   public boolean isEmpty( Point p, Object[][] grid ) {
      return isEmpty( p.x, p.y, grid );
   }
   /**Determines whether the value at the given coordinate is empty or not. See isEmpty( Point p, Object[][] grid )*/
   private boolean isEmpty( int x, int y, Object[][] grid ) {
      if( !isInBounds( x, y, grid ) )
         return true;

      if( ((Pixel)grid[x][y]).equals( (Pixel)EMPTY_CELL_VALUE ) )
         return true;
      return false;
   }
   
   /**
    * Converts a List of Points to an array of Points 
    *
    * @param list The List of Points
    * @return Point[] An array of Points
   */
   public Point[] toPointArr( List<Point> list ) {
      int size = list.size();
      Point[] arr = new Point[size];
      for( int rep = 0; rep < size; rep++ ) {
         Point p = new Point( list.get(rep).x, list.get(rep).y );
         arr[rep] = p;
      }
      return arr;
   }
   public int[] toIntArr( List<Integer> list ) {
      return list.stream().mapToInt(i->i).toArray();
   }
}