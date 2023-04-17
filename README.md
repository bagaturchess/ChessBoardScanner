# License

The license is GPL-2.0-only

# Overview

This project aims to demonstrate how to extract the chess position in a machine readable format called Forsyth–Edwards Notation (FEN) having the 2D picture of the board.
At the moment (17.04.2023) it supports 7 chess sets = 3 book sets + 2 chess.com sets + 1 chess24.com set + 1 lichess.org set.
The architecture is extensible and different chess sets could be added additionally.

# Android Application

The code is used productively by this app, which could be found here: https://metatransapps.com/chess-board-scanner-and-analyzer/

# Deep technical details

If you need technical explanation of the algorithms used, please check this [YoutTube video](https://youtu.be/PS5xAGx89mU)

# Advanced technologies solve complex problems
 - Computer Vision (CV) subdomain of Artificial Intelligence: It uses OpenCV as a computer vision library or framework, which helps in extracting the chess board from the image and transforming it to ideal square.
 - Machine Learning (ML) subdomain of Artificial Intelligence: Both libraries or frameworks Deep Netts Community Edition and Deeplearning4j frameworks as a basis for convolutional neural  networks for chess board classification and chess pieces recognition. It uses both ML frameworks in order to demonstrate how to integrate your code with both, but the code works well with them separately as well.

# Running the program

The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deep Netts and Deeplearning4j are included into the libs folder, but you need to add the OpenCV library version 4.5.1 (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html
The basic main classes could be found in the description below.

# How to Train the CNN and give it a try?

 - You could train the CNN by using the bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Edition_Community12 class.
 - Run it and wait the training to achieve accuracy equal to 1. Stop the training and now we have the CNN file, Let’s say dnet_books_set_2_extended.dnet. It may take more time on laptop/computer, some trainings needs minutes and some hours.
 - Now let’s try it.
 - You could run it by using the main function inside the bagaturchess.scanner.RecognitionMain_DeepNetts class.
 - First we add in the code our new CNN file, like netsNames.add("dnet_books_set_2_extended.dnet"); and then run it ...

# How to create datasets and then train and test CNNs for chess pieces?

 - The app uses the input chess board images inside ./BoardScanner/res/cnn/* directory. Currently (17.04.2023) there are 7 chess pieces sets = 3 book sets + 2 chess.com sets + 1 chess24.com set + 1 lichess.org set.
 - How to add an additional dataset for given chess set is described in the next paragraph.
 - First we generate datasets for these initial board positions in each chess pieces set. We use bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage main function ofr this purpose. All datasets are generated under ./BoardScanner/datasets_deepnetts/*
 - We create new Convolutional Neuraw Networks (CNNs) by bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Edition_Community12 main function. The training may take a while. It is using multi-threaded executor in order to train all CNNs in parallel. The learning rate is of key importance for the results and it may vary by datasets althugh the CNNs architecture is the same, so pay attention. The default values are set to working well values (e.g. 0.01f). We are waiting untill all trainings achieve accuracy equal to 1.
 - And last but not least, we run all tests we have to main function bagaturchess.scanner.tests.Test. It is using the same datasets as the training so normally the accuracy should be 100%.
 - There is also an additional main function, in case you would like to experiment with a single input image of your choise - bagaturchess.scanner.RecognitionMain_DeepNetts.

# How to create an additional dataset for given chess set and do a CNN training?

 - Obtain an image of the initial board, which contains all chess pieces for both sides/colors.
 - Add raw image of the initial board, containing all pieces to input directory of your choise. let's say"./BoardScanner/res/books_set_7/input1.png".
 - Than crop the image (transform the chess board to ideal square) by using bagaturchess.scanner.RecognitionMain_DeepNetts main class. First, set CROP_BOARD_FROM_IMAGE to true and start main function. Than checked if the cropped board "./data/OpenCV_board_croped.png" is correctly cropped. If yes, than go to next step, otherwise try with another image.
 - Copy the cropped board image as original image under "./BoardScanner/res/books_set_7/input1.png"
 - In bagaturchess.scanner.machinelearning.SupervisedData add the image and its fen to source_set_all array.
 - Use/edit the main function of bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage to generate the new dataset. In the specified by you folder, the call will generate a dataset as well as labels.txt and index.txt files, which are necessary for Deep Netts training.

# Directions for further development
  -  Extend the solution to work with chess diagrams drawn on paper, this will save a lot of positions from old books in computer format.
  -  Add support for additional popular online chess sites, which can be recognized by the program.
  -  Improve board corners detection, besides the currently used algorithms: findChessBoardCorners by build-in function, findChessBoardCorners by Hough lines and findChessBoardCorners by contours.

If you would like to contribute to the project, do not hesitate to contact me.

# Do you like the project?

You could give it a star by clicking 'Star' button placed top-right of the page.
This will rank better the project and will allow other people to easily find it out on github.

# Author

The author of the project is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.

# Credits

This project would not be possible without:
  -  [OpenCV](https://opencv.org/)
  -  [DeepNetts](https://www.deepnetts.com/)
  -  [Deeplearning4j](https://deeplearning4j.org/)
  -  [StackOverflow](https://stackoverflow.com/)

Good luck!

