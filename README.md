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
 - Machine Learning (ML) subdomain of Artificial Intelligence: Both libraries or frameworks Deep Netts Community Edition and Deeplearning4j frameworks as a basis for convolutional neural  networks (CNN) for chess board classification and chess pieces recognition. It uses both ML frameworks in order to demonstrate how to integrate your code with both, but the code works well with them separately as well.

# Running the program

The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deep Netts and Deeplearning4j are included into the libs folder, but you need to add the OpenCV library version 4.5.1 (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html
The basic main classes could be found in the description below.

# How to Create and Train convolutional neural networks (CNNs)?

 - You could create and train CNNs by using the main function of bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Edition_Community12 class.
 - There are 2 options for the source/input datasets, which contain images as examples of chess pieces in chess board suqare. This is the source of the learning/training:
  - Datasets option 1: Generate the datasets by Yourself and by running the bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage main function. More details could be found in the next chapter - "How to create the Datasets by Yourself?".
  - Datasets option 2: Extract the datasates from the uploaded archives files. Select datasets_deepnetts_2023.04.20_T1p or datasets_deepnetts_2023.04.20_T5p_part1 + datasets_deepnetts_2023.04.20_T5p_part2, so after the extraction of the archives, the datasets_deepnetts directory is existing into the current folder /datasets_deepnetts/*
 - We create a new Convolutional Neuraw Networks (CNNs) by bagaturchess.scanner.machinelearning.learning.impl_deepnetts.ScannerLearning_Edition_Community12 main. After running it, we are waiting the training to complete. We wait for all and each CNN training, to achieve accuracy equal to 1 (or close to it e.g. 99.9). In the console logs you could observe the count of epochs and current accuracy level as well as many other information, so you could monitor and supervise (if necessary) the training(s) in order to complete them successuflly and for a minimal amount of time. There is an exemplary log file in ./console.log file. After mentioning all this, the code should work well automatically most of the time. Last days, this is especially true, after the implementation/enablement of an automatic optimal learning rate auto-selection. Also, the training may take more or less time on laptop/computer, some trainings need minutes and some hours or even days. One additinal aspect is the trainings are using multi-threaded executor in order to train all CNNs in parallel. The learning rate is of key importance for the results and it may vary by datasets although the CNNs architecture is the same, so pay attention in case the auto learning rate is disabled. The default values are set to working in a stable way (e.g. value for learning rate 0.01f).
 - After the training has finished, there are CNN files saving the learned experience, so it can be open by the program later again from a file (e.g. dnet_books_set_1_extended.dnet). 
 - And last but not least, we could validate the newly created CNNs by running all the tests inside the main function of bagaturchess.scanner.tests.Test. It can use as test source, the same datasets as the training process as well as different datasets specified programmatically via a string path.

# How to create the Datasets by Yourself?

 - The app uses the input chess board images inside ./res/cnn/* directory. Currently (19.04.2023) there are 7 chess pieces sets = 3 book sets + 2 chess.com sets + 1 chess24.com set + 1 lichess.org set.
 - How to add an additional dataset for given chess set is described in the next paragraph.
 - First we generate datasets for these initial board positions in each chess pieces set. We use bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage main function ofr this purpose. All datasets are generated under ./datasets_deepnetts/*

# How to create an additional dataset for given chess set and do a CNN training of a new Convolutional Neural Network?

 - Obtain an image of the initial board, which contains all chess pieces for both sides/colors.
 - Add raw image of the initial board, containing all pieces to input directory of your choise. let's say"./res/books_set_7/input1.png".
 - Than crop the image (transform the chess board to ideal square) by using bagaturchess.scanner.RecognitionMain_DeepNetts main class. First, set CROP_BOARD_FROM_IMAGE to true and start main function. Than checked if the cropped board "./data/OpenCV_board_croped.png" is correctly cropped. If yes, than go to next step, otherwise try with another image.
 - Copy the cropped board image as original image under your new folder "./BoardScanner/res/books_set_7/input1.png"
 - In bagaturchess.scanner.machinelearning.SupervisedData add the image and its FEN strings to source_set_all array.
 - Use/edit the main function of bagaturchess.scanner.machinelearning.dataset.DatasetGenerator_ByBoardImage to generate the new dataset. In the specified by you folder, the call will generate a dataset as well as labels.txt and index.txt files, which are necessary for Deep Netts training.

# Directions for further development

  -  Simplification and automation of the process so we have predictable results each time.
  -  Extend the solution to work with chess diagrams drawn on paper, this will save a lot of positions from old books in computer format.
  -  Add support for additional popular online chess sites, which can be recognized by the program.
  -  Restructuring of the old code base.
  -  Improve board corners detection, besides the currently used algorithms: findChessBoardCorners by build-in function, findChessBoardCorners by Hough lines and findChessBoardCorners by contours. This is not highest prio after the implementation of the manual correction capabilities for the board corners into the Android version of the app.

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

