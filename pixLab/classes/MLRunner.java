
/**
 * Runs the machine learning image detection program
 *
 * @author Peter Olson mrpeterfolson@gmail.com
 */
public class MLRunner {
    public static void main( String[] args ) {
        System.out.print('\u000C'); //Clear terminal
        
        MLDetector mld = new MLDetector();
        mld.runDetector(
            5/*iterations*/,
            true/*usingTrainingData -- change this to false if using test data*/ );
    }
}
