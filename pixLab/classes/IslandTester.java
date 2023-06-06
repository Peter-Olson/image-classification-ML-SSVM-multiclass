
import java.util.*;

/**
 * IslandTester.java
 * 
 * Runs unit tests for Islands.java
 * 
 * @author Peter Olson
 * @version 1/1/23
 */
public class IslandTester {
   
   public static void main( String[] args ) {
      Integer emptyValue = -1;
      Islands islands = new Islands(emptyValue);
      
      Integer[][] exampleGrid1 = {{ 3, 2, 1, 1, 2, 2, 3, 3, 2, 1 },
                              { 3, 1, 2, 2, 2, 3, 3, 1, 3, 3 },
                              { 3, 2, 1, 1, 2, 2, 1, 1, 2, 3 },
                              { 2, 2,-1, 3,-1,-1,-1, 3,-1, 1 },
                              {-1,-1,-1,-1,-1,-1, 3, 3, 2,-1 },
                              {-1,-1, 1,-1, 2, 2,-1, 3,-1,-1 },
                              { 3, 2, 1,-1,-1, 2,-1,-1,-1, 4 },
                              {-1, 3,-1,-1,-1,-1,-1,-1, 4,-1 },
                              {-1,-1,-1, 2,-1,-1, 4, 3, 2, 2 },
                              {-1,-1,-1,-1,-1,-1, 4, 4, 2,-1 }};
      Integer[][] exampleGrid2 = {{ 3, 2, 1, 1, 2, 2, 3, 3, 2, 1 },
                              { 3, 1, 2, 2, 2, 3, 3, 1, 3, 3 },
                              { 3, 2, 1, 1, 2, 2, 1, 1, 2, 3 },
                              { 2, 2,-1, 3,-1,-1,-1, 3,-1, 1 },
                              {-1,-1,-1,-1,-1,-1, 3, 3, 2,-1 },
                              {-1,-1, 1,-1, 2, 2,-1, 3,-1,-1 },
                              { 3, 2, 1,-1,-1, 2,-1,-1,-1, 4 },
                              {-1, 3,-1,-1,-1,-1,-1,-1, 4,-1 },
                              {-1,-1,-1, 2,-1,-1, 4, 3, 2, 2 },
                              {-1,-1,-1,-1,-1,-1, 4, 4, 2,-1 }};
      
      SOPln("Original grid:");
      printGrid( exampleGrid1, islands );
      SOPln("----------------------------");
      SOPln("Islands removed (diagonals included):");
      islands.clearIslandsBelowRow( exampleGrid1, true );
      printGrid( exampleGrid1, islands );
      SOPln("----------------------------");
      SOPln("Islands removed (no diagonals):");
      islands.clearIslandsBelowRow( exampleGrid2, false );
      printGrid( exampleGrid2, islands );
      
      boolean allPassed = runUnitTests( islands );
      
      if( allPassed ) SOPln("\nAll tests passed!");
      else            SOPln("\nOne or more tested failed!");
      
         
   }
   
   /**
      Runs unit tests for the Islands class, testing all public functions, using the assertTruth(...)
      functions for comparing outcome results to expected results.
      
      @param islands The Islands object for running unit tests against
      @return boolean True if all unit tests have been passed, false otherwise
   */
   public static boolean runUnitTests( Islands islands ) {
      boolean passed = true;
      int testNumber = 0;
   
      //Data
      LinkedList<Point> list = new LinkedList<Point>();
      Point[] expected = new Point[]{ new Point(0,0), new Point(1,0), new Point(2,0), 
                                      new Point(0,1), new Point(1,1), new Point(2,1), 
                                      new Point(0,2), new Point(1,2), new Point(2,2) };
      
      Point[] pList1 = new Point[]{ new Point(0,1), new Point(1,0), new Point(1,1) };
      Point[] pList2 = new Point[]{ new Point(0,1), new Point(1,0) };
      Point[] pList3 = new Point[]{ new Point(0,0), new Point(0,1), new Point(0,2), 
                                    new Point(1,0),                 new Point(1,2), 
                                    new Point(2,0), new Point(2,1), new Point(2,2) };
      Point[] pList4 = new Point[]{ new Point(0,1), new Point(1,0), new Point(1,2), new Point(2,1) };
      Point[] pList5 = new Point[]{ new Point(2,0) };
      Point[] pEmpty = new Point[]{};
      Point[] pList6 = new Point[]{ new Point(1,2), new Point(2,1), new Point(2,2) };
      Point[] pList7 = new Point[]{ new Point(1,2), new Point(2,1) };
      Point[] pList8 = new Point[]{ new Point(1,3), new Point(2,3), new Point(3,1), new Point(3,3) };
      Point[] pList9 = new Point[]{ new Point(2,3) };
      Point[] pList10 = new Point[]{ new Point(0,0), new Point(0,1), new Point(0,2), 
                                    new Point(1,0), new Point(1,1), new Point(1,2), 
                                    new Point(2,0), new Point(2,1), new Point(2,2) };
      Point[] pList11 = new Point[]{ new Point(3,0), new Point(2,0), new Point(1,0) };
      Point[] pList12 = new Point[]{ new Point(1,1), new Point(1,2), new Point(2,1), new Point(2,2) };
      Point[] pList13 = new Point[]{ new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1) };
      Point[] pList14 = new Point[]{ new Point(2,2), new Point(2,3), new Point(3,2), new Point(3,3) };
      Point[] pList15 = new Point[]{ new Point(0,2), new Point(0,3), new Point(1,3), new Point(2,3),
                                     new Point(2,2), new Point(3,3), new Point(3,0), new Point(3,1) };
      Point[] pList16 = new Point[]{ new Point(0,2), new Point(0,3), new Point(1,3), new Point(2,3),
                                     new Point(2,2), new Point(3,3) };
      Point[] pList17 = new Point[]{ new Point(1,0) };
      Point[] pList18 = new Point[]{ new Point(3,0), new Point(3,1) };
      Point[] pList19 = new Point[]{ new Point(0,1), new Point(0,3), new Point(1,0), new Point(1,2),
                                     new Point(2,1), new Point(2,3), new Point(3,0), new Point(3,2) };
      Point[] pList20 = new Point[]{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4),
                                     new Point(0,5), new Point(0,6), new Point(1,6), new Point(2,6), new Point(2,5),
                                     new Point(2,4), new Point(2,3), new Point(2,2), new Point(2,1), new Point(2,0),
                                     new Point(1,0) };
      Point[] pList21 = new Point[]{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4),
                                     new Point(0,5), new Point(0,6), new Point(1,6), new Point(2,6), new Point(3,6),
                                     new Point(4,6), new Point(5,6), new Point(6,6), new Point(7,6), new Point(7,5),
                                     new Point(7,4), new Point(7,3), new Point(7,2), new Point(7,1), new Point(7,0),
                                     new Point(6,0), new Point(5,0), new Point(4,0), new Point(3,0), new Point(2,0),
                                     new Point(2,1), new Point(2,2), new Point(2,3), new Point(2,4), new Point(3,4),
                                     new Point(4,4), new Point(5,3), new Point(4,2) };
      Point[] pList22 = new Point[]{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4),
                                     new Point(0,5), new Point(0,6), new Point(1,6), new Point(2,6), new Point(3,6),
                                     new Point(4,6), new Point(5,6), new Point(6,6), new Point(7,6), new Point(7,5),
                                     new Point(7,4), new Point(7,3), new Point(7,2), new Point(7,1), new Point(7,0),
                                     new Point(6,0), new Point(5,0), new Point(4,0), new Point(3,0), new Point(2,0),
                                     new Point(2,1), new Point(2,2), new Point(2,3), new Point(2,4), new Point(3,4),
                                     new Point(4,4) };
          
      addArr( list, expected );
      
      Point pNeg = new Point(-1,-1);
      Point p00 = new Point(0,0); Point p01 = new Point(0,1); Point p02 = new Point(0,2); Point p03 = new Point(0,3);
      Point p10 = new Point(1,0); Point p11 = new Point(1,1); Point p12 = new Point(1,2); Point p13 = new Point(1,3);
      Point p20 = new Point(2,0); Point p21 = new Point(2,1); Point p22 = new Point(2,2); Point p23 = new Point(2,3);
      Point p30 = new Point(3,0); Point p31 = new Point(3,1); Point p32 = new Point(3,2); Point p33 = new Point(3,3);
      Point p40 = new Point(4,0); Point p44 = new Point(4,4);
      Point p50 = new Point(5,0);
      
      Integer[][] g1 = {{0,0,0},{0,0,0},{0,0,0}}; Integer[][] gx1 = {{0,0,0},{0,0,0},{0,0,0}};
      Integer[][] g2 = {{-1},{0},{0},{0},{-1}};   Integer[][] gx2 = {{-1},{-1},{-1},{-1},{-1}};
      Integer[][] g3 = {{-1,-1},{-1,-1},{-1,-1}}; Integer[][] gx3 = {{-1,-1},{-1,-1},{-1,-1}};
      Integer[][] g4 = {{-1,-1,-1,-1},            
                        {-1, 0, 0,-1},
                        {-1, 0, 0,-1},
                        {-1,-1,-1,-1}};
      Integer[][] gx4 = {{-1,-1,-1,-1},            
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g5 = {{ 0, 0,-1,-1},
                        { 0, 0,-1,-1},
                        {-1,-1,-1,-1},
                        {-1,-1,-1,-1}};
      Integer[][] gx5 = {{ 0, 0,-1,-1},
                         { 0, 0,-1,-1},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g6 = {{-1,-1,-1,-1},
                        {-1,-1,-1,-1},
                        {-1,-1, 0, 0},
                        {-1,-1, 0, 0}};
      Integer[][] g7 = {{-1,-1, 0, 0},
                        { 0,-1,-1, 0},
                        {-1,-1, 0, 0},
                        { 0, 0,-1, 0}};
      Integer[][] gx7 = {{-1,-1, 0, 0},
                         {-1,-1,-1, 0},
                         {-1,-1, 0, 0},
                         {-1,-1,-1, 0}};
      Integer[][] g8 = {{-1, 0,-1, 0},
                        { 0,-1, 0,-1},
                        {-1, 0,-1, 0},
                        { 0,-1, 0,-1}};
      Integer[][] gx8 = {{-1, 0,-1, 0},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g9 = {{ 0, 0, 0, 0, 0, 0, 0},
                        { 0,-1,-1,-1,-1,-1, 0},
                        { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx9 = {{ 0, 0, 0, 0, 0, 0, 0},
                         { 0,-1,-1,-1,-1,-1, 0},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g10 = {{ 0, 0, 0, 0, 0, 0, 0},
                         {-1,-1,-1,-1,-1,-1, 0},
                         { 0, 0, 0, 0, 0,-1, 0},
                         { 0,-1,-1,-1, 0,-1, 0},
                         { 0,-1, 0,-1, 0,-1, 0},
                         { 0,-1,-1, 0,-1,-1, 0},
                         { 0,-1,-1,-1,-1,-1, 0},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx10 = {{ 0, 0, 0, 0, 0, 0, 0},
                          {-1,-1,-1,-1,-1,-1, 0},
                          { 0, 0, 0, 0, 0,-1, 0},
                          { 0,-1,-1,-1, 0,-1, 0},
                          { 0,-1,-1,-1, 0,-1, 0},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g11 = {{ 0, 0,-1,-1},
                         { 0, 0,-1,-1},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g12 = {{-1, 0,-1, 0},
                         { 0,-1, 0,-1},
                         {-1, 0,-1, 0},
                         { 0,-1, 0,-1}};
      Integer[][] gx12 = {{-1,-1,-1,-1},
                          {-1,-1,-1,-1},
                          {-1, 0,-1, 0},
                          { 0,-1, 0,-1}};
      Integer[][] g13 = {{-1, 0,-1, 0},
                         { 0,-1, 0,-1},
                         {-1, 0,-1, 0},
                         { 0,-1, 0,-1}};
      Integer[][] gx13 = {{-1, 0,-1, 0},
                          { 0,-1, 0,-1},
                          {-1, 0,-1, 0},
                          { 0,-1, 0,-1}};
      Integer[][] g14 = {{-1,-1,-1,-1},            
                         {-1, 0, 0,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g15 = {{-1,-1,-1,-1},            
                         {-1, 0, 0,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1,-1}};
      Integer[][] gx15 = {{-1,-1,-1,-1},            
                          {-1, 0, 0,-1},
                          {-1, 0, 0,-1},
                          {-1,-1,-1,-1}};
      Integer[][] g16 = {{-1,-1, 0, 0},            
                         {-1, 0,-1,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx16 = {{-1,-1,-1,-1},            
                          {-1, 0,-1,-1},
                          {-1, 0, 0,-1},
                          {-1,-1,-1,-1}};
      Integer[][] g17 = {{-1,-1, 0, 0},            
                         {-1, 0,-1,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx17 = {{-1,-1, 0, 0},            
                          {-1, 0,-1,-1},
                          {-1, 0, 0,-1},
                          {-1,-1,-1, 0}};
      Integer[][] g18 = {{-1, 0,-1, 0},
                         { 0,-1, 0,-1},
                         {-1, 0,-1, 0},
                         { 0,-1, 0,-1}};
      Integer[][] gx18 = {{-1, 0,-1, 0},
                          { 0,-1, 0,-1},
                          {-1, 0,-1, 0},
                          { 0,-1, 0,-1}};
      Integer[][] g19 = {{ 0, 0,-1,-1},
                         { 0, 0,-1,-1},
                         {-1,-1,-1,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g20 = {{-1,-1, 0, 0},            
                         {-1, 0,-1,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx20 = {{-1,-1, 0, 0},            
                          {-1, 0,-1,-1},
                          {-1, 0, 0,-1},
                          {-1,-1,-1, 0}};
      Integer[][] g21 = {{-1,-1, 0, 0},            
                         {-1, 0,-1,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx21 = {{-1,-1, 0, 0},            
                          {-1,-1,-1,-1},
                          {-1,-1,-1,-1},
                          {-1,-1,-1, 0}};
      Integer[][] g22 = {{-1,-1,-1,-1},
                         {-1, 0, 0,-1},
                         {-1, 0, 0,-1},
                         {-1,-1,-1,-1}};
      Integer[][] g23 = {{ 0, 0, 0, 0},
                         { 0,-1,-1, 0},
                         { 0,-1,-1, 0},
                         { 0, 0, 0, 0}};
      Integer[][] g24 = {{-1,-1, 0, 0},            
                         { 0, 0,-1,-1},
                         { 0,-1,-1,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx24 = {{-1,-1, 0, 0},            
                          {-1,-1,-1,-1},
                          {-1,-1,-1,-1},
                          {-1,-1,-1, 0}};
      Integer[][] g25 = {{-1,-1, 0, 0},            
                         { 0, 0,-1,-1},
                         { 0,-1,-1,-1},
                         {-1,-1,-1, 0}};
      Integer[][] gx25 = {{-1,-1, 0, 0},            
                          { 0, 0,-1,-1},
                          { 0,-1,-1,-1},
                          {-1,-1,-1, 0}};
      Integer[][] g26 = {{ 0, 0, 0, 0},
                         { 0,-1,-1, 0},
                         { 0,-1,-1, 0},
                         { 0, 0, 0, 0}};
      Integer[][] gx26 = {{ 0, 0, 0, 0},
                          { 0,-1,-1, 0},
                          { 0,-1,-1, 0},
                          { 0, 0, 0, 0}};
      Integer[][] g27 = {{ 0, 0, 0, 0, 0, 0, 0},
                         {-1,-1,-1,-1,-1,-1, 0},
                         { 0, 0, 0, 0, 0,-1, 0},
                         { 0,-1,-1,-1, 0,-1, 0},
                         { 0,-1,-1,-1, 0,-1, 0},
                         { 0,-1,-1,-1,-1,-1, 0},
                         { 0,-1,-1,-1,-1,-1, 0},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx27 = {{ 0, 0, 0, 0, 0, 0, 0},
                          {-1,-1,-1,-1,-1,-1, 0},
                          { 0, 0, 0, 0, 0,-1, 0},
                          { 0,-1,-1,-1, 0,-1, 0},
                          { 0,-1,-1,-1, 0,-1, 0},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g28 = {{-1,-1, 0, 0, 0,-1,-1},
                         {-1,-1, 0,-1,-1, 0,-1},
                         { 0,-1, 0,-1,-1,-1,-1},
                         { 0,-1,-1,-1, 0,-1,-1},
                         {-1,-1, 0,-1,-1,-1,-1},
                         {-1, 0, 0,-1, 0, 0,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx28 = {{-1,-1, 0, 0, 0,-1,-1},
                          {-1,-1, 0,-1,-1, 0,-1},
                          { 0,-1, 0,-1,-1,-1,-1},
                          { 0,-1,-1,-1,-1,-1,-1},
                          {-1,-1, 0,-1,-1,-1,-1},
                          {-1, 0, 0,-1, 0, 0,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g29 = {{-1,-1, 0, 0, 0,-1,-1},
                         {-1,-1, 0,-1,-1, 0,-1},
                         { 0,-1, 0,-1,-1,-1,-1},
                         { 0,-1,-1,-1, 0,-1,-1},
                         {-1,-1, 0,-1,-1,-1,-1},
                         {-1, 0, 0,-1, 0, 0,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx29 = {{-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1, 0,-1},
                          { 0,-1,-1,-1,-1,-1,-1},
                          { 0,-1,-1,-1, 0,-1,-1},
                          {-1,-1, 0,-1,-1,-1,-1},
                          {-1, 0, 0,-1, 0, 0,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g30 = {{0,0,0},{0,0,0},{0,0,0}}; Integer[][] gx30 = {{0,0,0},{0,0,0},{0,0,0}};
      Integer[][] g31 = {{ 0, 0,-1, 0},
                         {-1,-1,-1,-1},
                         {-1, 0, 0,-1},
                         { 0,-1,-1,-1}};
      Integer[][] g32 = {{ 0, 0,-1, 0},
                         {-1,-1, 0, 0},
                         {-1, 0, 0,-1},
                         { 0,-1,-1, 0}};
      Integer[][] gx32 = {{-1,-1,-1, 0},
                          {-1,-1, 0, 0},
                          {-1, 0, 0,-1},
                          {-1,-1,-1,-1}};
      Integer[][] g33 = {{-1,-1,-1,-1,-1,-1,-1},
                         {-1,-1, 0,-1,-1, 0,-1},
                         { 0, 0, 0,-1,-1,-1, 0},
                         { 0,-1,-1, 0, 0,-1,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         {-1, 0, 0,-1, 0, 0,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx33 = {{-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1, 0,-1,-1,-1,-1},
                          { 0, 0, 0,-1,-1,-1,-1},
                          { 0,-1,-1, 0, 0,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g34 = {{-1,-1,-1,-1,-1,-1,-1},
                         {-1,-1, 0,-1,-1, 0,-1},
                         { 0, 0, 0,-1,-1,-1, 0},
                         { 0,-1,-1, 0, 0,-1,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         {-1, 0, 0,-1, 0, 0,-1},
                         {-1,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx34 = {{-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g35 = {{-1,-1,-1,-1,-1,-1,-1},
                         {-1,-1,-1,-1,-1, 0,-1},
                         { 0,-1, 0, 0,-1,-1, 0},
                         { 0,-1,-1,-1,-1,-1,-1},
                         {-1,-1, 0,-1, 0,-1,-1},
                         {-1, 0, 0,-1,-1, 0,-1},
                         { 0,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx35 = {{-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1, 0,-1},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0,-1,-1,-1,-1,-1,-1},
                          {-1,-1, 0,-1, 0,-1,-1},
                          {-1, 0, 0,-1,-1, 0,-1},
                          { 0,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] g36 = {{-1,-1,-1,-1,-1,-1,-1},
                         {-1,-1,-1,-1,-1, 0,-1},
                         { 0,-1, 0, 0,-1,-1, 0},
                         { 0,-1,-1,-1,-1,-1,-1},
                         {-1,-1, 0,-1, 0,-1,-1},
                         {-1, 0, 0,-1,-1, 0,-1},
                         { 0,-1,-1,-1,-1,-1,-1},
                         { 0, 0, 0, 0, 0, 0, 0}};
      Integer[][] gx36 = {{-1,-1,-1,-1,-1,-1,-1},
                          {-1,-1,-1,-1,-1, 0,-1},
                          { 0,-1,-1,-1,-1,-1, 0},
                          { 0,-1,-1,-1,-1,-1,-1},
                          {-1,-1, 0,-1,-1,-1,-1},
                          {-1, 0, 0,-1,-1, 0,-1},
                          { 0,-1,-1,-1,-1,-1,-1},
                          { 0, 0, 0, 0, 0, 0, 0}};
   
      //toPointArr( LinkedList<Point> list )
      SOPln("toPointArr(...) tests");
      passed &= assertTruth( islands.toPointArr(list), expected, testNumber++ ); list.clear(); //#0
      passed &= assertTruth( islands.toPointArr(list), new Point[]{}, testNumber++ );
      
      //isEmpty( Point p, Object[][] grid )
      SOPln("\nisEmpty(...) tests");
      passed &= assertTruth( !islands.isEmpty(p00,g1), true, testNumber++ ); //#2
      passed &= assertTruth( islands.isEmpty(p00,g2), true, testNumber++ );
      passed &= assertTruth( islands.isEmpty(p11,g3), true, testNumber++ );
      passed &= assertTruth( !islands.isEmpty(p11,g4), true, testNumber++ );
      passed &= assertTruth( !islands.isEmpty(p10,g7), true, testNumber++ );
      passed &= assertTruth( islands.isEmpty(p13,g6), true, testNumber++ );
      
      //isInBounds( Point p, Object[][] grid )
      SOPln("\nisInBounds(...) tests");
      passed &= assertTruth( !islands.isInBounds(pNeg,g1), true, testNumber++ ); //#8
      passed &= assertTruth( !islands.isInBounds(p40,g1), true, testNumber++ );
      passed &= assertTruth( !islands.isInBounds(p22,g3), true, testNumber++ );
      passed &= assertTruth( islands.isInBounds(p00,g1), true, testNumber++ );
      passed &= assertTruth( islands.isInBounds(p40,g2), true, testNumber++ );
      passed &= assertTruth( islands.isInBounds(p33,g4), true, testNumber++ );
      
      //isOnEdge( Point p, Object[][] grid )
      SOPln("\nisOnEdge(...) tests");
      passed &= assertTruth( islands.isOnEdge(p00,g1), true, testNumber++ ); //#14
      passed &= assertTruth( !islands.isOnEdge(p11,g1), true, testNumber++ );
      passed &= assertTruth( islands.isOnEdge(p21,g3), true, testNumber++ );
      passed &= assertTruth( islands.isOnEdge(p40,g2), true, testNumber++ );
      passed &= assertTruth( islands.isOnEdge(p23,g4), true, testNumber++ );
      passed &= assertTruth( !islands.isOnEdge(p11,g4), true, testNumber++ );
      passed &= assertTruth( !islands.isOnEdge(p22,g7), true, testNumber++ );
      
      //isNeighbor( Point p1, Point p2, Object[][] grid, boolean includeDiagonals )
      SOPln("\nisNeighbor(...) tests");
      passed &= assertTruth( islands.isNeighbor(p00,p01,g1,true), true, testNumber++ ); //#21
      passed &= assertTruth( islands.isNeighbor(p01,p11,g1,true), true, testNumber++ );
      passed &= assertTruth( islands.isNeighbor(p10,p20,g2,true), true, testNumber++ );
      passed &= assertTruth( islands.isNeighbor(p00,p11,g1,true), true, testNumber++ );
      passed &= assertTruth( !islands.isNeighbor(p00,p11,g1,false), true, testNumber++ );
      passed &= assertTruth( islands.isNeighbor(p00,p01,g1,false), true, testNumber++ );
      passed &= assertTruth( islands.isNeighbor(p01,p11,g1,false), true, testNumber++ );
      passed &= assertTruth( !islands.isNeighbor(p00,p21,g4,true), true, testNumber++ );
      passed &= assertTruth( !islands.isNeighbor(p00,p00,g2,true), true, testNumber++ );
      passed &= assertTruth( !islands.isNeighbor(p00,p00,g2,false), true, testNumber++ );
      
      //getNeighbors( Point p, Object[][] grid, boolean includeDiagonals )
      SOPln("\ngetNeighbors(...) tests");
      passed &= assertTruth( islands.getNeighbors(p00,g1,true), pList1, testNumber++ ); //#31
      passed &= assertTruth( islands.getNeighbors(p00,g1,false), pList2, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g1,true), pList3, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g1,false), pList4, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p10,g2,true), pList5, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p10,g2,false), pList5, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g3,true), pEmpty, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g3,false), pEmpty, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g4,true), pList6, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p11,g4,false), pList7, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p22,g7,true), pList8, testNumber++ );
      passed &= assertTruth( islands.getNeighbors(p22,g7,false), pList9, testNumber++ );
      
      //hasNeighbors( Point p, Object[][] grid, boolean includeDiagonals )
      SOPln("\nhasNeighbors(...) tests");
      passed &= assertTruth( islands.hasNeighbors(p00,g1,true), true, testNumber++ ); //#43
      passed &= assertTruth( islands.hasNeighbors(p10,g2,true), true, testNumber++ );
      passed &= assertTruth( islands.hasNeighbors(p10,g2,false), true, testNumber++ );
      passed &= assertTruth( !islands.hasNeighbors(p21,g3,true), true, testNumber++ );
      passed &= assertTruth( !islands.hasNeighbors(p21,g3,false), true, testNumber++ );
      passed &= assertTruth( !islands.hasNeighbors(p10,g7,true), true, testNumber++ );
      passed &= assertTruth( !islands.hasNeighbors(p10,g7,false), true, testNumber++ );
      passed &= assertTruth( islands.hasNeighbors(p23,g8,true), true, testNumber++ );
      passed &= assertTruth( !islands.hasNeighbors(p23,g8,false), true, testNumber++ );
      
      //totalNeighbors( Point p, Object[][] grid, boolean includeDiagonals )
      SOPln("\ntotalNeighbors(...) tests");
      passed &= assertTruth( islands.totalNeighbors(p00,g1,true), 3, testNumber++ ); //#52
      passed &= assertTruth( islands.totalNeighbors(p00,g1,false), 2, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p11,g1,true), 8, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p11,g1,false), 4, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p10,g1,true), 5, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p10,g1,false), 3, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p10,g7,true), 0, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p10,g7,false), 0, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p10,g7,true), 0, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p32,g8,true), 2, testNumber++ );
      passed &= assertTruth( islands.totalNeighbors(p32,g8,false), 0, testNumber++ );
      
      //getIsland( Point p, Object[][] grid, boolean includeDiagonals )
      SOPln("\ngetIsland(...) tests");
      passed &= assertTruth( islands.getIsland(p00,g1,true), rano(pList10), testNumber++ ); //#63
      passed &= assertTruth( islands.getIsland(p21,g1,true), rano(pList10), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g1,true), rano(pList10), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g2,true), rano(pList11), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g2,false), rano(pList11), testNumber++ );
      passed &= assertTruth( islands.getIsland(p00,g1,false), rano(pList10), testNumber++ );
      passed &= assertTruth( islands.getIsland(p21,g1,false), rano(pList10), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g1,false), rano(pList10), testNumber++ );
      passed &= assertTruth( islands.getIsland(p30,g2,true), rano(pList11), testNumber++ );
      passed &= assertTruth( islands.getIsland(p30,g2,false), rano(pList11), testNumber++ );
      passed &= assertTruth( islands.getIsland(p00,g3,true), rano(pEmpty), testNumber++ ); //#73
      passed &= assertTruth( islands.getIsland(p00,g3,false), rano(pEmpty), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g4,true), rano(pList12), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g4,false), rano(pList12), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g5,true), rano(pList13), testNumber++ );
      passed &= assertTruth( islands.getIsland(p11,g5,false), rano(pList13), testNumber++ );
      passed &= assertTruth( islands.getIsland(p23,g6,true), rano(pList14), testNumber++ );
      passed &= assertTruth( islands.getIsland(p23,g6,false), rano(pList14), testNumber++ ); //#80
      passed &= assertTruth( islands.getIsland(p03,g7,true), rano(pList15), testNumber++ );
      passed &= assertTruth( islands.getIsland(p03,g7,false), rano(pList16), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g7,true), rano(pList17), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g7,false), rano(pList17), testNumber++ );
      passed &= assertTruth( islands.getIsland(p30,g7,true), rano(pList15), testNumber++ );
      passed &= assertTruth( islands.getIsland(p30,g7,false), rano(pList18), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g8,true), rano(pList19), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g8,false), rano(pList17), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g9,true), rano(pList20), testNumber++ );
      passed &= assertTruth( islands.getIsland(p10,g9,false), rano(pList20), testNumber++ ); //#90
      passed &= assertTruth( islands.getIsland(p00,g10,true), rano(pList21), testNumber++ );
      passed &= assertTruth( islands.getIsland(p00,g10,false), rano(pList22), testNumber++ );
      passed &= assertTruth( islands.getIsland(p44,g10,true), rano(pList21), testNumber++ );
      passed &= assertTruth( islands.getIsland(p44,g10,false), rano(pList22), testNumber++ );
      
      //clearIslandsBelowRow( Object[][] grid, int row, boolean includeDiagonals )
      SOPln("\nclearIslandsBelowRow(...) tests");     islands.clearIslandsBelowRow(g1,true);
      passed &= assertTruth( g1, gx1, testNumber++ ); islands.clearIslandsBelowRow(g2,0,true);  //#95
      passed &= assertTruth( g2, gx2, testNumber++ ); islands.clearIslandsBelowRow(g3,0,true);
      passed &= assertTruth( g3, gx3, testNumber++ ); islands.clearIslandsBelowRow(g4,0,true);
      passed &= assertTruth( g4, gx4, testNumber++ ); islands.clearIslandsBelowRow(g5,0,true);
      passed &= assertTruth( g5, gx5, testNumber++ ); islands.clearIslandsBelowRow(g6,0,true); 
      passed &= assertTruth( g6, gx4, testNumber++ ); islands.clearIslandsBelowRow(g7,0,false); //#100
      passed &= assertTruth( g7, gx7, testNumber++ ); islands.clearIslandsBelowRow(g8,0,false);
      passed &= assertTruth( g8, gx8, testNumber++ ); islands.clearIslandsBelowRow(g9,0,true);
      passed &= assertTruth( g9, gx9, testNumber++ ); islands.clearIslandsBelowRow(g10,0,false);
      passed &= assertTruth( g10, gx10, testNumber++ );
      
      //clearIslandsAboveRow( Object[][] grid, int row, boolean includeDiagonals )
      SOPln("\nclearIslandsAboveRow(...) tests"); islands.clearIslandsAboveRow(g11,g11.length-1,true);
      passed &= assertTruth( g11, gx4, testNumber++ ); islands.clearIslandsAboveRow(g12,g12.length-2,false); //#105
      passed &= assertTruth( g12, gx12, testNumber++ ); islands.clearIslandsAboveRow(g13,g13.length-2,true);
      passed &= assertTruth( g13, gx13, testNumber++ );
      
      //clearIslandsRightCol( Object[][] grid, int col, boolean includeDiagonals )
      SOPln("\nclearIslandsRightCol(...) tests"); islands.clearIslandsRightCol(g1,0,true);
      passed &= assertTruth( g1, gx1, testNumber++ ); islands.clearIslandsRightCol(g1,0,false); //#108
      passed &= assertTruth( g1, gx1, testNumber++ ); islands.clearIslandsRightCol(g14,0,true);
      passed &= assertTruth( g14, gx4, testNumber++ ); islands.clearIslandsRightCol(g15,1,true);
      passed &= assertTruth( g15, gx15, testNumber++ ); islands.clearIslandsRightCol(g16,1,false);
      passed &= assertTruth( g16, gx16, testNumber++ ); islands.clearIslandsRightCol(g17,1,true);
      passed &= assertTruth( g17, gx17, testNumber++ );
      
      //clearIslandsLeftCol( Object[][] grid, int col, boolean includeDiagonals )
      SOPln("\nclearIslandsLeftCol(...) tests"); islands.clearIslandsLeftCol(g1,0,true);
      passed &= assertTruth( g1, gx1, testNumber++ ); islands.clearIslandsLeftCol(g1,0,false); //#114
      passed &= assertTruth( g1, gx1, testNumber++ ); islands.clearIslandsLeftCol(g18,2,true);
      passed &= assertTruth( g18, gx18, testNumber++ ); islands.clearIslandsLeftCol(g19,3,true);
      passed &= assertTruth( g19, gx4, testNumber++ ); islands.clearIslandsLeftCol(g20,3,true);
      passed &= assertTruth( g20, gx20, testNumber++ ); islands.clearIslandsLeftCol(g21,3,false);
      passed &= assertTruth( g21, gx21, testNumber++ );
      
      //clearIslandsInRegion( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
      //                      int bottomRightCoordRow, int bottomRightCoordCol, boolean includeDiagonals )
      SOPln("\nclearIslandsInRegion(...) tests"); islands.clearIslandsInRegion(g22,0,0,3,3,true);
      passed &= assertTruth( g22, gx4, testNumber++ ); islands.clearIslandsInRegion(g23,-1,-1,4,4,true); //#120
      passed &= assertTruth( g23, gx4, testNumber++ ); islands.clearIslandsInRegion(g24,-1,-1,4,2,false);
      passed &= assertTruth( g24, gx24, testNumber++ ); islands.clearIslandsInRegion(g25,-1,-1,4,2,true);
      passed &= assertTruth( g25, gx25, testNumber++ ); islands.clearIslandsInRegion(g26,0,0,3,3,true);
      passed &= assertTruth( g26, gx26, testNumber++ ); islands.clearIslandsInRegion(g27,2,0,6,4,true);
      passed &= assertTruth( g27, gx27, testNumber++ ); islands.clearIslandsInRegion(g28,1,1,5,5,true);
      passed &= assertTruth( g28, gx28, testNumber++ ); islands.clearIslandsInRegion(g29,-1,1,3,5,false);
      passed &= assertTruth( g29, gx29, testNumber++ );
      
      //clearIslandsOfSizeXAndBelow( Object[][] grid, int islandLimit, boolean includeDiagonals )
      SOPln("\nclearIslandsOfSizeXAndBelow(...) tests"); islands.clearIslandsOfSizeXAndBelow(g30,0,true);
      passed &= assertTruth( g30, gx30, testNumber++ ); islands.clearIslandsOfSizeXAndBelow(g3,3,true); //#128
      passed &= assertTruth( g3, gx3, testNumber++ ); islands.clearIslandsOfSizeXAndBelow(g31,3,true);
      passed &= assertTruth( g31, gx4, testNumber++ ); islands.clearIslandsOfSizeXAndBelow(g32,3,false);
      passed &= assertTruth( g32, gx32, testNumber++ ); islands.clearIslandsOfSizeXAndBelow(g33,6,true);
      passed &= assertTruth( g33, gx33, testNumber++ ); islands.clearIslandsOfSizeXAndBelow(g34,6,false);
      passed &= assertTruth( g34, gx34, testNumber++ );
      
      //clearIslandsInRegionOfSizeXAndBelow( Object[][] grid, int topLeftCoordRow, int topLeftCoordCol,
      //    int bottomRightCoordRow, int bottomRightCoordCol, int islandLimit, boolean includeDiagonals )
      SOPln("\nclearIslandsInRegionOfSizeXAndBelow(...) tests"); islands.clearIslandsInRegionOfSizeXAndBelow(g35,1,1,5,5,2,true);
      passed &= assertTruth( g35, gx35, testNumber++ ); islands.clearIslandsInRegionOfSizeXAndBelow(g36,1,1,5,5,2,false);
      passed &= assertTruth( g36, gx36, testNumber++ );
      
      
      
      return passed;
   }
   
   /**
      Adds an array of Point objects to a List of Point objects
      
      @param list The List of Points
      @param arr The array of Points
   */
   public static void addArr( List<Point> list, Point[] arr ) {
      for( int rep = 0; rep < arr.length; rep++ )
         list.add( arr[rep] );
   }
   
   /** Shorthand for the reorderArrayNaturalOrder( Point[] arr ) function */
   private static Point[] rano( Point[] arr ) { return reorderArrayNaturalOrder( arr ); }
   /**
      Reorders an array of Points by using the natural ordering hashing of the TreeMap class.
      The keys used for the Points are made using the Point class's toString() method
      
      @param arr The array of Points to reorder
      @return Point[] The reordered array of Points
   */
   public static Point[] reorderArrayNaturalOrder( Point[] arr ) {
      TreeMap<String, Point> orderedMap = new TreeMap<String, Point>();
      //Order map
      for( int rep = 0; rep < arr.length; rep++ ) {
         String key = arr[rep].toString();
         orderedMap.put( key, arr[rep] );
      }
      //Get ordered array
      Collection pointList = orderedMap.values();
      int pointListSize = pointList.size();
      Point[] returnList = new Point[ pointListSize ];
      returnList = (Point[])pointList.toArray( returnList );
      
      return returnList;
   }
   
   /**
      Determines whether the LinkedList of Points and the array of Points have the same Point objects or not
      
      @param outcome A LinkedList of Points
      @param expected An array of Points
      @param testNumber Keeps track of the current test number
      @return boolean True if the list and array have the same Point objects, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( LinkedList<Point> outcome, Point[] expected, int testNumber ) {
      int listSize = outcome.size();
      if( listSize != expected.length )
         return ffalse( testNumber );
      
      for( int rep = 0; rep < listSize; rep++ ) {
         if( !outcome.get(rep).equals( expected[rep] ) )
            return ffalse( testNumber );
      }
      
      return ttrue( testNumber );
   }
   /**
      Determines whether the outcome array of ints and the expected array of ints have the same value or not
      
      @param outcome An array of ints
      @param expected An array of ints
      @param testNumber Keeps track of the current test number
      @return boolean True if the outcome and expected arrays are the same, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( Integer[][] outcome, Integer[][] expected, int testNumber ) {
      if( outcome.length != expected.length || outcome[0].length != expected[0].length )
         return ffalse( testNumber );
      
      for( int row = 0; row < outcome.length; row++ ) {
         for( int col = 0; col < outcome[row].length; col++ ) {
            if( !outcome[row][col].equals(expected[row][col]) )
               return ffalse( testNumber );
         }
      }
      
      return ttrue( testNumber );
   }
   /**
      Determines whether the outcome array of Points and the expected array of Points have the same Points or not
      
      @param outcome An array of Points
      @param expected An array of Points
      @param testNumber Keeps track of the current test number
      @return boolean True if the outcome and expected arrays have the same Points, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( Point[] outcome, Point[] expected, int testNumber ) {
      if( outcome.length != expected.length )
         return ffalse( testNumber );
      
      for( int rep = 0; rep < outcome.length; rep++ ) {
         if( !outcome[rep].equals( expected[rep] ) )
            return ffalse( testNumber );
      }
      
      return ttrue( testNumber );
   }
   /**
      Determines if the two boolean values are equal or not
      
      @param outcome The result of some function, expressed as a boolean value
      @param expected The expected result of some function, expressed as a boolean value
      @param testNumber Keeps track of the current test number
      @return boolean True if the boolean values are equal, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( boolean outcome, boolean expected, int testNumber ) {
      if( outcome != expected )
         return ffalse( testNumber );
      return ttrue( testNumber );
   }
   /**
      Determines if the two int values are equal or not
      
      @param outcome The result of some function, expressed as an int value
      @param expected The expected result of some function, expressed as an int value
      @param testNumber Keeps track of the current test number
      @return boolean True if the int values are equal, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( int outcome, int expected, int testNumber ) {
      if( outcome != expected )
         return ffalse( testNumber );
      return ttrue( testNumber );
   }
   /**
      Determines if the two Objects are equal or not
      
      @param outcome The result of some function, expressed as an Object
      @param expected The expected result of some function, expressed as an Object
      @param testNumber Keeps track of the current test number
      @return boolean True if the Objects are equal, false otherwise
      @see ttrue( int testNumber )
      @see ffalse( int testNumber )
   */
   public static boolean assertTruth( Object outcome, Object expected, int testNumber ) {
      if( !outcome.equals(expected) )
         return ffalse( testNumber );
      else
         return ttrue( testNumber );
   }
   
   /**
      Prints a true result statement based on the assertTruth function
      
      @param testNumber Keeps track of the current test number
      @return boolean The result of the comparison between the outcome and the expected results were equal
   */
   public static boolean ttrue( int testNumber ) {
      SOPln("Passed Test #" + testNumber + "!");
      return true;
   }
   /**
      Prints a false result statement based on the assertTruth function
      
      @param testNumber Keeps track of the current test number
      @return boolean The result of the comparison between the outcome and the expected results were not equal
   */
   public static boolean ffalse( int testNumber ) {
      SOPln("FAILED Test #" + testNumber + "!");
      return false;
   }
   
   /**
      Print a grid of int values, leaving out the int values that represent empty space, as speislandsied
      by the Islands's empty value field, accessed by getEmptyValue()
      
      @param grid The grid to print. The grid should have (but does not have to have) empty values, which are represented as spaces
      @param islands The Islands object that is used to access the value of the empty value field which is used to represent
   */
   public static void printGrid( Object[][] grid, Islands islands ) {
      for( int row = 0; row < grid.length; row++ ) {
         for( int col = 0; col < grid[row].length; col++ ) {
            if( grid[row][col].equals( islands.getEmptyValue() ) )
               SOP( "  " );
            else
               SOP( grid[row][col] + " " );
         }
         SOPln();
      }
   }
   
   public static void SOPln( String str ) {
      System.out.println( str );
   }
   public static void SOP( String str ) {
      System.out.print( str );
   }
   public static void SOPln() {
      System.out.println("");
   }
   public static void SOP() {
      System.out.print("");
   }
   
}