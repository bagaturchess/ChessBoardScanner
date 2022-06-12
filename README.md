# License

The license is GPL-2.0-only

# Overview

This project aims to demonstrate how to extract the chess position in a machine readable format called Forsyth–Edwards Notation (FEN) having the 2D picture of the board.
At the moment it supports 4 chess sets: default chess sets from chess.com, lichess.org and chess24.com as well as one set from paper chess book. Of course, the architecture is extensible and different chess sets could be added additionally.

# Advanced technologies solve complex problems
 - Computer Vision (CV) subdomain of Artificial Intelligence: It uses OpenCV as a computer vision library or framework, which helps in extracting the chess board from the image and transforming it to ideal square.
 - Machine Learning (ML) subdomain of Artificial Intelligence: Both libraries or frameworks Deep Netts Community Edition and Deeplearning4j frameworks as a basis for convolutional nueral networks for chess board classification and chess pieces recognition. It uses both ML frameworks in order to demonstrate how to integrate your code with both, but the code works well with them separately as well.

# Credits

This project would not be possible without:
  -  [OpenCV](https://opencv.org/)
  -  [DeepNetts](https://www.deepnetts.com/)
  -  [Deeplearning4j](https://deeplearning4j.org/)
  -  [StackOverflow](https://stackoverflow.com/)

# Directions for further development
  -  Extend the solution to work with chess diagrams drawn on paper, this will save a lot of positions from old books in computer format.
  -  Add support for additional popular online chess sites, which can be recognized by the program.
  -  Improve board corners detection, besides the currently used algorithms: findChessBoardCorners by build-in function, findChessBoardCorners by Hough lines and findChessBoardCorners by contours.

If you would like to contribute to the project, do not hesitate to contact me.

# Android Application

The code is used productively by this app, which could be found here: https://metatransapps.com/chess-board-scanner-and-analyzer/

# Technical details

If you need technical explanation of the algorithms used, please check this [YoutTube video](https://youtu.be/PS5xAGx89mU)

# Running the program

The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deep Netts and Deeplearning4j are included into the libs folder, but you need to add the OpenCV library version 4.5.1 (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html

The basic main classes are in bagaturchess.scanner root package:
 - RecognitionMain_DeepLearning4J, runs chess position recognition for specified image using DeepLearning4J framework. 
 - RecognitionMain_DeepNetts_ChessPiecesProviderClassifier, runs chess position recognition for specified image using Deep Netts framework. 

# How to create a Dataset for CNN training by the image of the initial board, which contains all chess pieces for both sides/colors
1.	Add raw image of the initial board, containing all pieces to input directory of your choise. let's say".\res\books_set_2_input1.png".
2. 	Than crop the image (transform the chess board to ideal square) by using bagaturchess.scanner.RecognitionMain_DeepNetts main class. First, set CROP_BOARD_FROM_IMAGE to true and start main function. Than checked if the cropped board "./data/OpenCV_board_croped.png" is correctly cropped. If yes, than go to next step, otherwise try with another image.
3.	Copy cropped board image inside ".\res\cnn\books\setXYZ\input2.png"
4.	In bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage, add the input png (example: ".\res\cnn\books\setXYZ\input1.png") to the INPUT_FILES array.
5.	Set OUTPUT_DIR to the directory, where the pieces’ images will be generated (let’s say "./datasets_deepnetts/dataset_books_set_2_extended/") and run the main function in this class for the generation process itself. It will take around 10 seconds.
6.	Add labels.txt file in the output directory with this content: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12. Separate the numbers by new lines instead of by comma.
7.	Run deepnettsplatform64.exe and import output directory. DeepNettsPlatform UI will create index.txt file in the same directory.
8.	Now you have a DataSet and we are ready for training CNN for this chess pieces set with DeepNetts.


# Do you like the project?

You could give it a star by clicking 'Star' button placed top-right of the page.
This will rank better the project and will allow other people to easily find it out on github.

# Author

The author of the project is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.
