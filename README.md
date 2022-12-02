# image-classification-ML-SSVM-multiclass

A machine learning program in Java that makes multi-class image classifications. The ML algorithm used is a variant of the Support Vector Machine algo, a simplified version that was designed to be more palatable for novice programming students. This program is in the works to be a multi-class machine learning program, see my other repo [image-classification-ML-SSVM](https://github.com/Peter-Olson/image-classification-ML-SSVM) (binary classification). Currently, the project is operational, but not accurate due to the feature set. This initial project used the MNIST data set identifying between zeros and ones, but is being updated to interpret 

I have found that the algorithm suffers from overfitting (not surprising), however, I do believe that this is a principal issue of the features chosen and not the algorithmic process itself. Feature-creation, along with swapping out features and swapping in new features, is very easy to do, and any number of features can be added to the algorithm without increasing processing time substantially. (See pros and cons below). I hypothesize that an increase of feature size will produce an increase of accuracy, but I haven't had the time to further work on the features to work out different pairings of instance data.

This program also has a large set of image processing edge-detection functions for pre-processing more complicated images. Large complicated images can be reduced to edges inside and outside the image with little 'noise' within the image. These pre-processing functions are not very fast, but are effective, and have proven to work with complicated images that would otherwise not work with typical edge detection functionalities.

MLRunner.java runs ML training sets and can optionally run ML test sets of data, based on the labeled images which should contain the tokens 'training' or 'test'. MLDetector.java has the feature functions and the sSVM algorithm. Picture.java has a series of image processing functions that can be viewed in a separate window.

### Notes on the sSVM Algorithm

sSVM stands for 'Simplified Support Vector Machine' algorithm, which is an algorithm that I created and implemented. The notes below detail how the algorithm works, and why it was used instead of an SVM algorithm. In short, the algorithm was designed to reduce the mathematics of the SVM algorithm so that it would be more accessible and understandable by high school students. 

### Technical Notes:

*Wait, how is this a SVM algorithm?!*
    
Good question. It’s not. It would be more accurate to call it something else, but I liken it most closely to the SVM concept. I stripped the algorithm of its math and made many simplifications. Let me explain what I did, how it relates to SVM, how the math is reduced while maintaining the qualities of separation binary instances, and how the algorithm works without having to think about hyperplanes or additional dimensionality.
  
Note that this technical section is for the instructor, not for students. They really do not need to know this stuff, but it may be of interest to you.
  
In SVM, the idea is that there is a predictable, separable boundary that a computer can learn to adjust based on a set of features that ‘graphs’ the locations of objects. If the object lies on one side of the boundary, the program guesses that it is of one type, and if it is on the other side of the boundary, it guesses that it is of the other type. Here, we are using binary labels and binary classification (using reinforcement learning), and we want to be able to use a decent number of features, so SVM is a good fit.
  
I had two goals in finding the right algorithm for the machine learning design:
* Allow for scalable feature addition and removal
* Remove vector-based mathematics

Taking out the V in SVM does change things up quite a bit. This algorithm uses no KKT conditions (calculus) and no Lagrange multipliers, and instead focuses on the margin between instances. In order to reduce complexity, I turned to averages, something easily understandable by a high school student. In order to reduce dimensionality and the graphical nature of multi-feature instances, features are normalized and then averaged to produce a single value. This value is produced based on the weights of the features that are saved over multiple iterations. From there, you have three numbers: the current instance averaged feature value, and the weighted values that have been learned over past iterations. The ‘margin’ is the differences between the current instance value and the binary weighted values—whichever is closest to the feature value wins out the guess from the program, and the weights are adjusted by averaging the feature value into the weights, according to the ‘weight’ (proportional to the total iterations) of the current iteration. Thus, the program is not learning where to put the line, like a typical SVM program, but rather it is adjusting two lines, one on either side of the feature instance, and seeing which one the feature instance is closest two (we’re talking about 1 dimension though, not 2D). So, the algorithm draws on some neural network design without doing any crazy backward/forward propagation.
  
No calculus needed, no dimensionality, no vectors, and students don’t even need much algebraic knowledge outside of averages and weighted averages.
  
This algorithm, like any, has pros and cons:

**Pros:**
* Simplifies mathematics
* Allows for large feature set
* Less overfitting with larger feature sets than SVM
* Outliers are less likely to result in overfitting
* No confusing multi-dimensional abstraction
* Can be used to scale from binary to non-binary classification
* Doesn’t require a large number of training data images

**Cons:**
* Requires reinforcement learning
* Features have equal weighting (none are more important than another)
* Loss of dimensionality could result in imprecise classifications (tight margins) if certain feature pairs mirror one another in value
* Greater imprecision with lower numbers of features (although you could argue this is a positive)

The code itself is more readable than SVM (since it has been purged of most mathematics) and can be easily adjusted by the removal or addition of feature methods. There is additional flexibility for the data set being used as well—one simply needs to follow the reset instructions in the MLDetector.java file and make sure the training and test data sets following the image name instructions—so this detecting algorithm can work for any two binary classifications of images, as it should, so long as you have a decent data set to work with.
  
Lastly, it is worth discussing that this algorithm can be expanded into a non-binary classification system by continuing to add in new binary comparisons, and establishing a vocabulary of recognizable objects. This system would be pseudo-non-binary… if the vocabulary of the program becomes too great, it will lead to more and more imprecision. What this limit would be, I am not sure, but it would be a fun project to look into.
