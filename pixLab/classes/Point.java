public class Point {
   public int x, y;
   public Point( int x, int y ) {
      this.x = x;
      this.y = y;
   }
   public boolean equals( Point p ) {
      if( p == null )
         return false;
      else if( this.getClass() != p.getClass() )
         return false;
      else if( this.x != p.x || this.y != p.y )
         return false;
      
      return true;
   }
   public String toString() {
      return x + "," + y;
   }
}