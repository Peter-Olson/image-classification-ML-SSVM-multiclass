import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.event.WindowEvent;
import java.text.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.nio.file.*;
import javax.swing.JFrame;

/**
 * This class uses machine learning techniques to determine the binary classification of
 * an image. The following categories that this program is able to classify are below:
 * 
 * a) Written zeros and ones
 * 
 * The ML algorithm used falls most closely under SVM (Support Vector Machines). However,
 * unlike a regular SVM ML application, this algorithm has been stripped of most statistics
 * and calculus (including typical linear regression techniques) in order for the program to
 * be more easily understood by high school students.
 * 
 * This program uses a series of features to grab data from images, and then averages those
 * feature data to determine whether the image is of one category or another. The feature list
 * can be amended, appended, or truncated, which may yield better or worse results over the
 * course of more training data. The weights of these features are saved, averaged, and adjusted
 * after each training data image, ie this is the part where the machine 'learns'.
 * 
 * Unlike some weight-adjusting ML algorithms, neither forward nor-backward propogation is used,
 * but rather adjusts weight (inversely) towards the feature averages. Simply put, the algorithm
 * favors features that better polarize the classification of the image.
 *
 * @author Peter Olson mrpeterfolson@gmail.com
 * @version 7/15/22
 */
public class MLDetector {
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    /* @@@@@@@@@@@@@@@ BEGIN FEATURE METHODS @@@@@@@@@@@@@@@ */
    
    public double numIntersections( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        int numOfIntersections = 0;
        double avgNumIntersections = 0.0;
        boolean inWhiteSection = false;
        boolean hasEncounteredWhitePixelInRow = false;
        int totalRowsWithWhitePixels = 0;
        
        for( int row = 0; row < pixels.length; row++ ) {
            for( int col = 0; col < pixels[row].length; col++ ) {
                if( !inWhiteSection && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    inWhiteSection = true;
                    numOfIntersections++;
                    if( !hasEncounteredWhitePixelInRow )
                        totalRowsWithWhitePixels++;
                    hasEncounteredWhitePixelInRow = true;
                } else if( inWhiteSection && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    inWhiteSection = false;
                }
            }
            avgNumIntersections += (double)numOfIntersections;
        }
        
        avgNumIntersections /= totalRowsWithWhitePixels;
        
        return avgNumIntersections;
    }
    
    /**
     * [B/W] Feature based on the ratio of the average object width (based on white pixels)
     * against the width of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the average object width based on white pixels
     *                divided by the width of the Picture
     */
    public double avgObjectWidth( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        ArrayList<Double> widths = new ArrayList<Double>();
        double totalWidth = pixels[0].length;
        
        for( int row = 0; row < pixels.length; row++ ) {
            double lineWidth = 0.0;
            double firstWhitePixelCol = pixels[0].length;
            double lastWhitePixelCol = 0.0;
            boolean firstWhitePixelFound = false;
            for( int col = 0; col < pixels[0].length; col++ ) {
                if( !firstWhitePixelFound && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    if( col < firstWhitePixelCol )
                        firstWhitePixelCol = col;
                    
                    firstWhitePixelFound = true;
                }
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    if( col > lastWhitePixelCol )
                        lastWhitePixelCol = col;
            }
            
            lineWidth = lastWhitePixelCol - firstWhitePixelCol;
            
            if( lineWidth > 0 )
                widths.add( lineWidth );
        }
        
        int size = widths.size();
        double sumOfWidths = 0.0;
        for( int rep = 0; rep < size; rep++ )
            sumOfWidths += widths.get(rep);
        
        return (sumOfWidths / (double)size) / (double)totalWidth;
    }
    
    /**
     * [B/W] Feature based on the ratio of the average object height (based on white pixels)
     * against the height of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the average object height based on white pixels
     *                divided by the height of the Picture
     */
    public double avgObjectHeight( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        ArrayList<Double> heights = new ArrayList<Double>();
        double totalHeight = pixels.length;

        for( int col = 0; col < pixels[0].length; col++ ) {
            double colHeight = 0.0;
            double firstWhitePixelRow = pixels.length;
            double lastWhitePixelRow = 0.0;
            boolean firstWhitePixelFound = false;
            for( int row = 0; row < pixels.length; row++ ) {
                if( !firstWhitePixelFound && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    if( row < firstWhitePixelRow )
                        firstWhitePixelRow = row;
                        
                    firstWhitePixelFound = true;
                }
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    if( row > lastWhitePixelRow )
                        lastWhitePixelRow = row;
            }
            
            colHeight = lastWhitePixelRow - firstWhitePixelRow;
            
            if( colHeight > 0 )
                heights.add( colHeight );
        }
        
        int size = heights.size();
        double sumOfHeights = 0.0;
        for( int rep = 0; rep < size; rep++ )
            sumOfHeights += heights.get(rep);
        
        return (sumOfHeights / (double)size) / (double)totalHeight;
    }
    
    /**
     * [B/W] Feature based on the ratio of the object width based on white pixels
     * against the width of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the object width based on white pixels
     *                divided by the width of the Picture
     */
    public double maxObjectWidth( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        double maxWidth = 0.0;
        double firstWhitePixelCol = pixels[0].length;
        double lastWhitePixelCol = 0.0;
        double totalWidth = pixels[0].length;
        
        for( int row = 0; row < pixels.length; row++ ) {
            double lineWidth = 0.0;
            boolean firstWhitePixelFound = false;
            for( int col = 0; col < pixels[0].length; col++ ) {
                if( !firstWhitePixelFound && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    if( col < firstWhitePixelCol )
                        firstWhitePixelCol = col;
                    
                    firstWhitePixelFound = true;
                }
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    if( col > lastWhitePixelCol )
                        lastWhitePixelCol = col;
            }
            
            lineWidth = lastWhitePixelCol - firstWhitePixelCol;
            
            if( lineWidth > maxWidth )
                maxWidth = lineWidth;
        }
        
        return maxWidth / totalWidth;
    }
    
    /**
     * [B/W] Feature based on the ratio of the object height based on white pixels
     * against the height of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the object height based on white pixels
     *                divided by the height of the Picture
     */
    public double maxObjectHeight( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        double maxHeight = 0.0;
        double firstWhitePixelRow = pixels.length;
        double lastWhitePixelRow = 0.0;
        double totalHeight = pixels.length;

        for( int col = 0; col < pixels[0].length; col++ ) {
            double colHeight = 0.0;
            boolean firstWhitePixelFound = false;
            for( int row = 0; row < pixels.length; row++ ) {
                if( !firstWhitePixelFound && pixels[row][col].getColor().equals( Color.WHITE ) ) {
                    if( row < firstWhitePixelRow )
                        firstWhitePixelRow = row;
                        
                    firstWhitePixelFound = true;
                }
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    if( row > lastWhitePixelRow )
                        lastWhitePixelRow = row;
            }
            
            colHeight = lastWhitePixelRow - firstWhitePixelRow;
            
            if( colHeight > maxHeight )
                maxHeight = colHeight;
        }
        
        return maxHeight / totalHeight;
    }
    
    /**
     * [B/W] Feature based on the ratio of black to white pixels
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of white pixels to total pixels
     */
    public double totalWhitePixels( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        double totalWhite = 0.0;
        double totalPixels = pixels.length * pixels[0].length;
        
        for( int row = 0; row < pixels.length; row++ )
            for( int col = 0; col < pixels[0].length; col++ )
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    totalWhite++;
        
        return totalWhite / totalPixels;
    }
    
    /**
     * [B/W] Feature based on the ratio of the largest width of continuous
     * white pixels of the object detected against the width of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the largest band of white pixels in a row
     *                divided by the width of the Picture
     */
    public double whiteWidth( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        double maxWidth = 0.0;
        double totalWidth = pixels[0].length;
        
        for( int row = 0; row < pixels.length; row++ ) {
            double lineWidth = 0.0;
            for( int col = 0; col < pixels[0].length; col++ ) {
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    lineWidth += 1.0;
            }
            
            if( lineWidth > maxWidth )
                maxWidth = lineWidth;
        }
        
        return maxWidth / totalWidth;
    }
    
    /**
     * [B/W] Feature based on the ratio of the largest height of continuous
     * white pixels of the object detected against the height of this picture
     * 
     * @param pic The instance being looked at (the picture)
     * @return double The ratio of the largest band of white pixels in a column
     *                divided by the height of the Picture
     */
    public double whiteHeight( Picture pic ) {
        Pixel[][] pixels = pic.getPixels2D();
        
        double maxHeight = 0.0;
        double totalWidth = pixels[0].length;
        
        for( int col = 0; col < pixels[0].length; col++ ) {
            double colWidth = 0.0;
            for( int row = 0; row < pixels.length; row++ ) {
                if( pixels[row][col].getColor().equals( Color.WHITE ) )
                    colWidth += 1.0;
            }
            
            if( colWidth > maxHeight )
                maxHeight = colWidth;
        }
        
        return maxHeight / totalWidth;
    }
    
    /* @@@@@@@@@@@@@@@@@ END FEATURE METHODS @@@@@@@@@@@@@@@@@ */
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    
    /**
     * Method that detects whether an image is of one category (called the
     * 'first' category) or another (called the 'second' category).
     * 
     * This method grabs images out of the images folder that follow
     * the format "img_#.jpg"
     * 
     * The training images and test images can be differentiated by adjusting
     * the 'imageBaseName' variable
     * 
     * After each run, note that a few things change and have to be manually reset
     * (if you are running from scratch and don't want to keep weights and
     * progress):
     * 
     *   1) Weights.txt  --> Weights are automatically updated after each instance.
     *                       In order to start anew, delete all text in this file
     *                       Note that the initial weights are set randomly when the
     *                       first instance is run
     *   2) Progress.txt --> The success rate of each guess by the program is recorded
     *                       in this file. In order to start anew, delete all text in
     *                       this file.
     *   3) 'trained' folder --> In the images folder where the training and test images
     *                           are kept is a folder labeled 'trained'. After an image
     *                           is used, for training or testing, it is moved to this
     *                           folder. In order to start from scratch, move all of these
     *                           images back into the 'images' folder from the 'trained'
     *                           folder
     * 
     * This program takes guesses based on a series of feature values on whether an image
     * is of one category or another. It then confirms its guess as correct or incorrect
     * before making adjustments. These weights are adjusted over time using averages
     * of feature data from past and current iterations, holding weights for both
     * classifications of the image.
     * 
     * This means that...
     * 
     *    1) Feature 'methods' can be added or taken away without 'breaking' anything, so
     *       long as the feature method works correctly and returns a double value between
     *       zero and one
     *    2) Any images can be tested, so long as only two categories of images are tested
     *       at any given time.
     *    3) The accuracy of the program is refined within a smaller amount of training
     *       images, in comparison to neural networks
     *    4) The accuracy of the program is as only good as the feature method abilities to
     *       find distinguishing traits between two images. Not all feature method are
     *       effective for any two classifications of images
     *    5) Feature methods or preprocessing methods that are more complex will quickly
     *       limit speed and the feasibility in using large images
     * 
     * The class 'Picture' has a large list of preprocessing methods, some of which are
     * designed to improve edge detections techniques. This class has a simple list of
     * feature methods. In general, it is recommended that all image processing methods
     * to be put in the Picture class and all feature methods to be put in this class.
     * 
     * @param iterations The number of instances (images) to test
     * @param usingTrainingData True if the program is currently using training data.
     *                          False if the program is currently using test data
     */
    public void runDetector( int iterations, boolean usingTrainingData ) {
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("0.##");
        Scanner scanner = new Scanner( System.in );
        
         /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
         /*@@@@@@ NOTE: These variables may need to be changed if using different types @@@@@@*/
         /*@@@          of data, or if using testing data instead of training data         @@@*/
         /*@@@ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -   @@@*/
         /*@@@*/ String CLASS_A_NAME = "Zero";                                           /*@@@*/
         /*@@@*/ String CLASS_B_NAME = "One";                                            /*@@@*/
         /*@@@*/                                                                         /*@@@*/
         /*@@@*/ String imageBaseName = usingTrainingData ? "img_" : "test_";            /*@@@*/
         /*@@@*/ String ext = ".jpg";                                                    /*@@@*/
         /*@@@*/ String weightsFileName  = "Weights.txt";                                /*@@@*/
         /*@@@*/ String progressFileName = "Progress.txt";                               /*@@@*/
         /*@@@ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - @@@*/
         /*@@@@@@                                                                       @@@@@@*/
         /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
         
        ArrayList<File> imgFiles = getImageFiles( imageBaseName );
        int trainingDataImagesSize = imgFiles.size();
        
        /* Weights are only generated if the feature methods being run are different
           than those listed in the Weights.txt file, or if the file is blank */
        if( usingTrainingData )
            generateWeights( weightsFileName );
        
        for( int rep = 0; rep < iterations && trainingDataImagesSize > 0; rep++ ) {
            int fileNumber = random.nextInt( trainingDataImagesSize-- );
            File file = imgFiles.remove( fileNumber );
            String fileName = file.getName();
            
            Picture pic = new Picture( fileName );
            
            //Image Preprocessing Methods
            pic.toBW();
            
            //Open image viewer
            JFrame frame = pic.explore();
            
            /* @@@ KEEP THE FOLLOWING COMMENT -- DO NOT CHANGE IT @@@ */
            //Feature Grabbing Methods
            double X_totalWhitePixels = totalWhitePixels( pic );
            double X_whiteWidth       = whiteWidth( pic );
            double X_whiteHeight      = whiteHeight( pic );
            double X_maxObjectWidth   = maxObjectWidth( pic );
            double X_maxObjectHeight  = maxObjectHeight( pic );
            double X_avgObjectWidth   = avgObjectWidth( pic );
            double X_avgObjectHeight  = avgObjectHeight( pic );
            double X_numIntersections = numIntersections( pic );
            
            /* @@@ DO NOT ADD NON-FEATURE CODE ABOVE THIS LINE @@@ */
            
            //@@DEBUG -- It is recommended that you test your feature methods before
            //           integrating them into the ML part of the program.
            /*
            SOPln( "% White pixels: "           + df.format(X_totalWhitePixels) );
            SOPln( "% Width of white pixels: "  + df.format(X_whiteWidth)       );
            SOPln( "% Height of white pixels: " + df.format(X_whiteHeight)      );
            SOPln( "% Object width: "           + df.format(X_maxObjectWidth)   );
            SOPln( "% Object height: "          + df.format(X_maxObjectHeight)  );
            SOPln( "% Average object width: "   + df.format(X_avgObjectWidth)   );
            SOPln( "% Average object height: "  + df.format(X_avgObjectHeight)  );
            */
            
            // Comment out the ML CODE section below when testing new feature methods
            /* @@@ BEGIN ML CODE @@@ */
            
            //Add data feature data to list
            ArrayList<Double> featureData = new ArrayList<Double>();
            
            featureData.add( X_totalWhitePixels );
            featureData.add( X_whiteWidth       );
            featureData.add( X_whiteHeight      );
            featureData.add( X_maxObjectWidth   );
            featureData.add( X_maxObjectHeight  );
            featureData.add( X_avgObjectWidth   );
            featureData.add( X_avgObjectHeight  );
            featureData.add( X_numIntersections );
            
            //Make guess based on feature data
            boolean guessIsA = makeGuess( weightsFileName, featureData );
            String guess = guessIsA ? CLASS_A_NAME : CLASS_B_NAME;
            SOPln("\nI think this image is a " + guess + "!\n");
            
            //Determine success or failure. Adjust accordingly
            boolean isA = false;
            if( usingTrainingData ) {
                SOPln("Is this image a " + CLASS_A_NAME + " or a " + CLASS_B_NAME + "?");
                String response = scanner.nextLine().trim().toLowerCase();
                
                if( response.contains( CLASS_A_NAME.toLowerCase() ) )
                    isA = true;
                
                changeWeights( isA, weightsFileName, featureData );
            
            
                //Update progress
                boolean success = (guessIsA && isA) || (!guessIsA && !isA);
                updateProgress( progressFileName, success );
            
            
                //Move file to the 'trained' folder, so it can't be used again
                try {
                    Files.move( Paths.get( "..\\images\\" + fileName ), Paths.get( "..\\images\\trained\\" + fileName ) );
                } catch( IOException e ) { e.printStackTrace(); }
            }
            
            //Close frame viewer
            frame.dispatchEvent( new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ) );
            
            /* @@@ END ML CODE @@@ */
        }
        
        scanner.close();
    }
    
    /**
     * Method that updates the success rate across multiple instances in the
     * progress file
     * 
     * @param progressFileName The name of the text file that details the success rate
     *                         across multiple instances evaluated
     * @param success True if the program was successful on this instance, false otherwise
     */
    private void updateProgress( String progressFileName, boolean success ) {
        //Format of line: 1. Correct! 13/20 65%    ... or ...    2. Incorrect. 17/25 68%
        
        Scanner sc = getScanner( progressFileName );
        
        int correctSoFar = 0;
        int totalRuns = 1;
        
        while( sc.hasNextLine() ) {
            String line = sc.nextLine();
            
            if( line.isEmpty() ) break;
            
            if( sc.hasNextLine() ) continue;
            
            String[] parts = line.split(" ");
            String[] ratio = parts[2].split("/");
            correctSoFar = Integer.parseInt( ratio[0] );
            totalRuns    = Integer.parseInt( ratio[1] ) + 1;
        }
        
        sc.close();
        
        String response = success ? "Correct!" : "Incorrect.";
        if( success ) ++correctSoFar;
        int percent = (int)((((double)correctSoFar)/((double)totalRuns)) * 100.0);
        String line = totalRuns + ". " + response + " " + correctSoFar + "/" + totalRuns + " " + percent + "%\n";
        
        addToFile( progressFileName, line );
    }
    
    /**
     * Method that adjusts the weights based on the results of the last training image result.
     * Note that once the program is done training, this method should no longer be run
     * 
     * @param isA True if the image is a classification A, false if it is classification B
     * @param weightsFileName The name of the text file containing the weights
     * @param featureData The list of feature data from this instance
     * @return double The total number of iterations. This is used to keep track of progress
     */
    private double changeWeights( boolean isA, String weightsFileName, ArrayList<Double> featureData ) {
        ArrayList<Pair> weightList = getWeights( weightsFileName );
        ArrayList<String> featureWeightData = new ArrayList<String>();
        
        DecimalFormat df = new DecimalFormat("0.##");

        int totalFeatures      = weightList.size() - 1; //Last Pair holds the iteration total
        double totalIterations = weightList.get( totalFeatures ).a;
        for( int rep = 0; rep < totalFeatures; rep++ ) {
            double firstWeight  = 0.0;
            double secondWeight = 0.0;
            String placeholder  = "";
            
            double weightListA  = weightList.get(rep).a;
            double weightListB  = weightList.get(rep).b;
            double featureValue = featureData.get(rep);
            
            //Average the weight and the new feature value according to the weighted average across iterations so far
            if( isA ) firstWeight = ((weightListA * totalIterations) / (totalIterations + 1.0)) + (featureValue / (totalIterations + 1.0));
            else      firstWeight = weightListA;
                
            placeholder += df.format(firstWeight) + " ";
            
            if( !isA ) secondWeight = ((weightListB * totalIterations) / (totalIterations + 1.0)) + (featureValue / (totalIterations + 1.0));
            else       secondWeight = weightListB;
                
            placeholder += df.format(secondWeight) + " " + (totalIterations + 1.0);
            
            featureWeightData.add( placeholder );
        }
        
        updateWeights( weightsFileName, featureWeightData );
        
        return totalIterations;
    }
    
    /**
     * Method that updates the weights text file with the new weight data
     * 
     * @param weightsFileName The name of the weight text file
     * @param featureWeightData The String data to write to the weights file
     */
    private void updateWeights( String weightsFileName, ArrayList<String> featureWeightData ) {
        Scanner sc = getScanner( weightsFileName );
        
        String text = "";
        
        int rep = 0;
        while( sc.hasNextLine() ) {
            String[] line = sc.nextLine().split(" ");
            text += line[0] + " " + featureWeightData.get(rep++);
            if( sc.hasNextLine() )
                text += "\n";
        }
        
        sc.close();
        
        writeToFile( weightsFileName, text );
    }
    
    /**
     * Method that evaluates the feature data of this instance and the weights of previous
     * instances to make a guess on whether this image is of the first classification (true)
     * or the second classification (false)
     * 
     * @param weightsFileName The file name that contains the weights
     * @param featureDate The list of this instance's feature data
     * @return boolean True is the program believes this image is the first classification,
     *                 false if the program believes this image is the second classification
     */
    private boolean makeGuess( String weightsFileName, ArrayList<Double> featureData ) {
        ArrayList<Pair> weightList = getWeights( weightsFileName );
        
        double firstAverageWeight   = 0.0;
        double secondAverageWeight  = 0.0;
        double featureAverageWeight = 0.0;
        
        int totalFeatures = weightList.size() - 1; //Last Pair holds the iteration total\
        double totalIterations = weightList.get( totalFeatures ).a;
        for( int rep = 0; rep < totalFeatures; rep++ ) {
            double weightListA  = weightList.get(rep).a;
            double weightListB  = weightList.get(rep).b;
            double featureValue = featureData.get(rep);
            
            //Average the weight and the new feature value according to the weighted average across iterations so far
            firstAverageWeight   += ((weightListA * totalIterations ) / (totalIterations + 1.0)) + (featureValue / (totalIterations + 1.0));
            secondAverageWeight  += ((weightListB * totalIterations ) / (totalIterations + 1.0)) + (featureValue / (totalIterations + 1.0));
            
            featureAverageWeight += featureValue;
        }
        
        firstAverageWeight   /= totalFeatures;
        secondAverageWeight  /= totalFeatures;
        featureAverageWeight /= totalFeatures;
        
        double firstScore  = Math.abs( firstAverageWeight  - featureAverageWeight );
        double secondScore = Math.abs( secondAverageWeight - featureAverageWeight );
        
        boolean guessFirst = firstScore < secondScore;
        
        return guessFirst;
    }
    
    /**
     * Method that grabs that weight pair values from the weightsFileName text file
     * 
     * @param weightsFileName The name of the text file with the weight data
     * @return The list of Pair of weights for the first and second classification. The very last Pair contains the iteration total (both entries)
     */
    private ArrayList<Pair> getWeights( String weightsFileName ) {
        Scanner sc = getScanner( weightsFileName );
        
        ArrayList<Pair> weightPairs = new ArrayList<Pair>();
        while( sc.hasNextLine() ) {
            String[] line = sc.nextLine().split(" ");
            weightPairs.add( new Pair( Double.parseDouble( line[1] ), Double.parseDouble( line[2] ) ) );
            if( !sc.hasNextLine() ) //Add iteration count in the last Pair
                weightPairs.add( new Pair( Double.parseDouble( line[3] ), Double.parseDouble( line[3] ) ) );
        }
        
        sc.close();
        
        return weightPairs;
    }
    
    private class Pair {
        public double a, b;
        public Pair( double a, double b ) { this.a = a; this.b = b; }
    }
    
    /**
     * Method that generates the Weights.txt file for the first time. The file generated
     * will contain randomized weight scores for the feature methods being tested upon
     * compilation. After these weights are generated by this method, this method should
     * not be run again, unless the feature methods are changed, or unless the user wants
     * to scrub the weight adjustments and start anew.
     * 
     * @param weightsFileName The name of the file that holds the weights of the features
     */
    private void generateWeights( String weightsFileName ) {
        Scanner sc     = getScanner( weightsFileName );
        Scanner thisSc = getScanner( "MLDetector.java" );
        
        LinkedHashSet<String> textFileNames          = new LinkedHashSet<String>();
        LinkedHashSet<String> MLDetectorFeatureNames = new LinkedHashSet<String>();
        
        //Get Weights.txt method names
        while( sc.hasNextLine() ) {
            String line = sc.nextLine();
            
            if( line.isEmpty() ) break;
            
            String[] parts = line.split(" ");
            String weightName = parts[0];
            textFileNames.add( weightName );
        }
        
        //Get this file's feature method names
        while( thisSc.hasNextLine() ) {
            String line = thisSc.nextLine();
            
            if( line.trim().equals( "//Feature Grabbing Methods" ) ) {
                String nextLine = thisSc.nextLine();
                while( nextLine.contains("=") ) {
                    String token = nextLine.split("=")[1].trim();
                    String methodName = token.substring( 0, token.indexOf("(") ).trim();
                    MLDetectorFeatureNames.add( methodName );
                    nextLine = thisSc.nextLine();
                }
                break;
            }
        }
        
        //See if sets are equal. If so, do not continue, we do not want to overwrite the Weights.txt file
        if( textFileNames.equals( MLDetectorFeatureNames ) )
            return;
            
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("0.##");
        String text = "";
        Iterator iterator = MLDetectorFeatureNames.iterator();
        while( iterator.hasNext() ) {
            //                                      Classification 1                         Classification 2           Iteration #
            text += iterator.next() + " " + df.format( random.nextDouble() ) + " " + df.format( random.nextDouble() ) + " 1.0";
            if( iterator.hasNext() )
                text += "\n";
        }
        
        sc.close();
        thisSc.close();
        
        writeToFile( weightsFileName, text );
    }
    
    /**
     * Method that gets a Scanner object given the file name
     * 
     * @param fileName The name of the file to find
     * @return Scanner A Scanner object that is scanning the file found
     */
    private Scanner getScanner( String fileName ) {
        File file  = null;
        Scanner sc = null;
        try {
            file = getFile( fileName );
            sc = new Scanner( file );
        }
        catch( FileNotFoundException e ) { e.printStackTrace(); }
        catch( IOException e ) { e.printStackTrace(); }
        
        return sc;
    }
    
    /**
     * Gets a File based on the file name and the relative path
     * 
     * @param filePath The path of the File to be found
     * @return File The File found from this name. If not found, throws a FileNotFoundException
     */
    public File getFile( String filePath ) throws FileNotFoundException {
        File dir = new File(".");
        File[] filesList = dir.listFiles();
        for( File file: filesList )
            if( file.getName().equals( filePath ) )
                return file;
    
        throw new FileNotFoundException("File not found. Path of file not found: " + filePath );
    }
    
    /**
     * Gets a list of Files that have the given String in their file name
     * 
     * @param str The String being searched for in each file name
     * @return ArrayList<File> A list of files with that str in its name
     */
    public ArrayList<File> getImageFiles( String str ) {
        File dir = new File("..\\images\\");
        File[] filesList = dir.listFiles();
        ArrayList<File> foundFiles = new ArrayList<File>();
        for( File file: filesList )
            if( file.getName().contains( str ) )
                foundFiles.add( file );
        
        return foundFiles;
    }
    
    /**
     * Gets the contents of the File as a String
     * 
     * @param file The file to get the text from
     * @return String The contents of the File converted to a String
     */
    public String getFileText( File file ) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( file );
        } catch( FileNotFoundException e ) { e.printStackTrace(); }
        
        String text = "";
        while( scanner.hasNextLine() )
            text += scanner.nextLine();
        
        scanner.close();
        
        return text;
    }
    
    /**
     * Overwrite an existing File
     * 
     * @param fileLoc The location of the File to write to
     * @param text The text to write to the File
     */
    public void writeToFile( String fileLoc, String text ) {
        FileWriter fw = null;
        try {
            fw = new FileWriter( fileLoc );
            fw.write( text );
            fw.close();
        } catch( IOException e ) { e.printStackTrace(); }
    }
    
    /**
     * Add to an existing File
     * 
     * @param fileLoc The location of the File to write to
     * @param text The text to write to the File
     */
    public void addToFile( String fileLoc, String text ) {
        FileWriter fw = null;
        try {
            fw = new FileWriter( fileLoc, true );
            fw.write( text );
            fw.close();
        } catch( IOException e ) { e.printStackTrace(); }
    }
    
    private void SOPln(  String str ) { System.out.println( str );        }
    private void SOPln()              { System.out.println();             }
    private void SOP(    String str ) { System.out.print( str );          }
    private void SOP()                { /*Nothing!*/                      }
    private void nSOPln( String str ) { System.out.println( "\n" + str ); }
    private void nSOP(   String str ) { System.out.print( "\n" + str );   }
}
