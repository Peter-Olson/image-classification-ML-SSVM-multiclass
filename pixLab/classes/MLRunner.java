
/**
 * Runs the machine learning image detection program
 *
 * @author Peter Olson mrpeterfolson@gmail.com
 */
public class MLRunner
{
    public static void main( String[] args )
    {
        System.out.print('\u000C'); //Clear terminal
        
        MLDetector mld = new MLDetector();
        mld.runDetector( 80    /*iterations*/,
                         true  /*usingTrainingData -- change this to false if using test data*/,
                         false /*promptUser -- change this to true to have the user determine the labeling*/
                       );
    }
}
