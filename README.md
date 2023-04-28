# Overview

This project aims to demonstrate how to extract the chess position in a machine readable format called Forsyth–Edwards Notation (FEN) having the 2D picture of the board.
At the moment (17.04.2023) it supports 7 chess sets = 3 book sets + 2 chess.com sets + 1 chess24.com set + 1 lichess.org set.
The architecture is extensible and different chess sets could be added additionally.

# Android Application

The code is used productively by this app, which could be found here: https://metatransapps.com/chess-board-scanner-and-analyzer/

# Advanced technologies solve complex problems
 - Machine Learning (ML) subdomain of Artificial Intelligence: Both libraries or frameworks Deep Netts Community Edition and Deeplearning4j frameworks as a basis for convolutional neural  networks (CNN) for chess board classification and chess pieces recognition. It uses both ML frameworks in order to demonstrate how to integrate your code with both, but the code works well with them separately as well.
 - Computer Vision (CV) subdomain of Artificial Intelligence: It uses OpenCV as a computer vision library or framework, which helps in extracting the chess board from the image and transforming it to ideal square.

# Deep technical details

If you need technical explanation of the algorithms used, please check this [YoutTube video](https://youtu.be/PS5xAGx89mU).
The description in this video is still valid and the related source code exists, although there are some changes in the general concept of the app in last 1-2 years, giving more attention on the CNNs for all purposes and having Computer Vision for edvanced and more convenient features for the users.

# Running the program

The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deep Netts and Deeplearning4j are included into the libs folder, but you need to add the OpenCV library version 4.5.1 (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html
The basic main classes could be found in the description below.

# How to Create and Train convolutional neural networks (CNNs) from given datasets with images of chess boards faving different chess pieces?

 - You could create and train CNNs by using the main function of <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/learning/impl_deepnetts/ScannerLearning_Edition_Community12.java/">bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Edition_Community12</a> class.
 - There are 2 options for the source/input datasets, which contain images as examples of chess pieces in chess board suqare. This is the source of the learning/training:
   - Datasets option 1: Generate the datasets by Yourself and by running the <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/dataset/DatasetGenerator_ByBoardImage.java/">bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage</a> main function. More details could be found in the next chapter - "How to create the Datasets by Yourself?".
   - Datasets option 2: Extract the datasates from the uploaded archives files. Select <a href="https://github.com/bagaturchess/ChessBoardScanner/releases/download/v100/datasets_deepnetts_T1_2023.04.24.zip">datasets_deepnetts_T1_2023.04.24.zip (80 MB)</a> or <a href="https://github.com/bagaturchess/ChessBoardScanner/releases/download/v100/datasets_deepnetts_T2_2023.04.25.zip">datasets_deepnetts_T2_2023.04.25.zip (850 MB)</a>. Extract them locally, so after the extraction of the archives, the datasets_deepnetts directory is existing into the current folder /datasets_deepnetts/*
 - After running ScannerLearning_Edition_Community12, we are waiting the training to complete. We wait for all and each CNN training, to achieve accuracy equal to 1 (or close to it e.g. 99.9). In the console logs you could observe the count of epochs and current accuracy level as well as many other information, so you could monitor and supervise (if necessary) the training(s) in order to complete them successuflly and for a minimal amount of time. There is an exemplary log file in ./console.log file.
 - Also, the training may take more or less time on laptop/computer, some trainings need minutes and some hours or even days.
 - One additional aspect is the trainings are using multi-threaded executor in order to train all CNNs in parallel.
 - The learning rate is of key importance for the results and it may vary by datasets although the CNNs architecture is the same, so pay attention in case you plan changes of learning rate or auto learning rate logic. The default values are set to working in a stable way for the given datasets.
 - After the training has finished, there are CNN files saving the learned experience, so it can be open by the program later again from a file (e.g. dnet_books_set_1_extended.dnet).
 - You may need to run a few runs in order to achieve high accuracy (>= 99.9%) for all neural networks and trainings. If you achieve high accuracy for some of the trainings, than you could exclude them from the code and run only the one which is needed in order to save CPU time and resources.
 - And last but not least, we could validate the newly created CNNs by running all the tests inside the main function of <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/tests/Test.java/">bagaturchess.scanner.tests.Test</a>. It can use as test source, the same datasets as the training process as well as different datasets specified programmatically via a string path (in different SupervisedData objects).
 - And last, after mentioning all of this, the code should work well most of the time. It is especially true, after the adjustments of learning rates and other parameters related with the  CNN architecture as described in the chapter "# How to find the best parameters for a CNN to be trained and achive high accuracy ...".

# How to create the Datasets by Yourself?

 - The app uses the input chess board images inside <a href="https://github.com/bagaturchess/ChessBoardScanner/tree/main/res/cnn/">./res/cnn/*</a> directory. Currently (19.04.2023) there are 7 chess pieces sets = 3 book sets + 2 chess.com sets + 1 chess24.com set + 1 lichess.org set.
 - We generate datasets for these initial board positions in each chess pieces set. We use <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/dataset/DatasetGenerator_ByBoardImage.java/">bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage</a> main function for this purpose. All datasets are generated under ./datasets_deepnetts/*
 - They are generated automatically by croping the source files in <a href="https://github.com/bagaturchess/ChessBoardScanner/tree/main/res/cnn/">./res/cnn/*</a> as well as by adding additinal images generated via translations +/-1 pixel and/or +/-2 pixels (all combinations over x and y, 9-1=8 times more images with 1 pixel and 25-1=24 times more images with 2 pixels) as well as via rotations with +/-1%.

# How to create an additional dataset for given chess set and do a CNN training of a new Convolutional Neural Network?

 - Obtain an image of the initial board, which contains all chess pieces for both sides/colors.
 - Add raw image of the initial board, containing all pieces to input directory of your choise. let's say"./res/cnn/books/set7/input1_crop_test1.png".
 - Than crop the image (transform the chess board to ideal square) by using the <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/RecognitionMain_DeepNetts.java/">bagaturchess.scanner.RecognitionMain_DeepNetts</a> main class. First, set CROP_BOARD_FROM_IMAGE to true and start main function. Than checked if the cropped board "./data/OpenCV_board_croped.png" is correctly cropped. If yes, than go to next step, otherwise try with another image.
 - Copy the cropped board image as original image under your new folder "./res/cnn/books/set7/input1.png"
 - Add the source board images in <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/SupervisedData.java/">bagaturchess.scanner.machinelearning.SupervisedData</a> as well as their FEN strings to source_set_all array.
 - Use/edit the main function of <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/dataset/DatasetGenerator_ByBoardImage.java/">bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage</a> to generate the new dataset. In a specified by you folder, the call will generate a dataset as well as labels.txt and index.txt files, which are necessary for Deep Netts training.

# How to find the best parameters for a CNN to be trained and achive high accuracy (>= 99.9%) as well as the training to complete for the less possible time?

 - There are one main challenge when you want to find out why the CNN is not working for a given dataset. This is when the training finishes with Zero or NaN as an output on one extreme and on the other the extreme, having long time strongly fluctoating accuracy level between the ephos, which cannot get stable and grow, so you are not able to produce the trained network file (*.dnet).
 - For the existing datsets, the tests show that the CNN architecture is almost static for the different datasets as follows:
   - 32x32 gray input layer. 64x64 works slower and needs more time for tests so it is postponed currently.
   - 2 convolutional layers with filter size 2
   - 2 maxpoling layers (1 after each convolutional layer) of size 3 and step 2
   - Fully connected layer size = 117
   - Output layer outputs = 13
   - hidden activation function = TANH
   - output layer activation function = SOFTMAX
   - loss function = CROSS ENTROPY
 - This "best" CNN architecture was found by the tuning code inside the <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/learning/impl_deepnetts/ScannerLearning_Tuning_Edition_Community12.java/">bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Tuning_Edition_Community12</a> class. Its purpuse is to find out the best parameters which leads to faster training, which on first place completes with high accuracy (>= 99.9%) as well as takes as less as possible time on second place. It could varies all possible parameters of convolutional layers count, existance of maxpooling layer after the convolutional layer (or not), filter sizes for maxpooling layer and convolutional layer, step of maxpooling layer filter, learning rates and learning rates correction percents and the size of the last fully connected layer.
 - The code runs and generates logs in the console as well as report files on the file system with statistical data for each CNN. Examples could be found under <a href="https://github.com/bagaturchess/ChessBoardScanner/tree/main/tuning/">./tuning/*.dnet.txt</a> directory.
 - The algorithm uses iterative deepening over the epochs count, starting from 2 and increasing with 2 each iteration, to 4, 6 and etc. We start with 2 epochs, because we need at least 2 sequential accuracies from the training process of 2 epochs, so we could compare them. We don't need trainings wich deviate too much epoch after epoch, as well  as trainings where the accuracy is growing too slow. The first one (which deviate too much) we remove by using the setting INITIAL_LEARNING_RATE_MAX_TOLERANCE, which could be found inside the <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/learning/impl_deepnetts/ScannerLearning_Tuning_Edition_Community12.java/">bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Tuning_Edition_Community12</a> class. For better idea, if for example it is set to 0.157f, this means the maximal drop down of the accuracy epoch after epoch cannot be more than 15.7%. Normally the accuracy is going up most of the time, epoch after epoch. When the oposite happened - the  epoch's accuracy is 0.10 and the next epoch's accuracy is 0.09 will be still be interesting for us and we will continue trying it, but if it is 0.10 and drops for even one epoch with more than 15.7% to let's say 0.08 in our case than we will stop evaluating this option anymore. So the statistics inside <a href="https://github.com/bagaturchess/ChessBoardScanner/blob/main/src/bagaturchess/scanner/machinelearning/learning/impl_deepnetts/ScannerLearning_Tuning_Edition_Community12.java/">bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Tuning_Edition_Community12</a> directory, are based on the "current" INITIAL_LEARNING_RATE_MAX_TOLERANCE = 0.157f (15.7%). In general, they can be done better and provide even more accurate data (e.g. having more clusters for all parameteres matrix elements).
 - Based with this data we need less time to design and train our CNN to achieve good accuracy level. We need only a few tries with neighbur learning rates (2f * learning_rate and learning rate / 2f) or to find the best values, which in some cases need many runs to find out how to complete with high accuracy (>= 99.9%) for a reasonable time.

# Directions for further development

  -  Simplification and automation of the process so we have predictable results each time.
  -  Add support for additional popular online chess sites, which can be recognized by the program.
  -  Extend the solution to work with chess diagrams drawn on paper, this will save a lot of positions from old books in computer format.
  -  Restructuring/Refactoring/Improvements of the old code base.
  -  Improve board corners detection, besides the currently used algorithms: findChessBoardCorners by build-in function, findChessBoardCorners by Hough lines and findChessBoardCorners by contours. This is not highest prio after the implementation of the manual correction capabilities for the board corners into the Android version of the app.

# Еxtra
 - There are an old sub-project related with DeepLearning4J framework, which is now on hold and put as archive <a href="https://github.com/bagaturchess/ChessBoardScanner/releases/download/v100/Archive_DL4J_Jars_Shrinker_Tool.zip">Archive_DL4J_Jars_Shrinker_Tool.zip</a>. It aims to create smaller DeepLearning4J jar file library. It is currently on hold sub-project, because DeepNetts framework is in focus and is much smaller in size.

# Do you like the project?

You could give it a star by clicking 'Star' button placed top-right of the page.
This will rank better the project and will allow other people to easily find it out on github.

If you have a question or would like to contribute to the project, do not hesitate to contact me.

# Author

The author of the project is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.

# Credits

This project would not be possible without:
  -  [OpenCV](https://opencv.org/)
  -  [DeepNetts](https://www.deepnetts.com/)
  -  [Deeplearning4j](https://deeplearning4j.org/)
  -  [StackOverflow](https://stackoverflow.com/)

Good luck!

# License

The license is GPL-2.0-only
