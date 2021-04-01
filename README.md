# Overview

This project aims to demonstrate how to extract the chess position in a machine readable format called Forsyth–Edwards Notation (FEN) having the 2D picture of the board.
Initially it supports chess.com and lichess.org images, although the architecture is extensible and different chess boards could be added as well.
It uses OpenCV as a computer vision library and Deeplearning4j as a basis for convolutional networks for chess board classification.

# Running the program

The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deeplearning4j are included into the libs folder, but you need to add the OpenCV library (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html

The basic main class is bagaturchess.scanner.patterns.AllMain

# Directions for further development

  -  Improve board corners detection, besides the currently used algorithms: findChessBoardCorners by build-in function, findChessBoardCorners by Hough lines and findChessBoardCorners by contours.
  -  Add additional popular online chess sites, which can be recognized by the program, like for example chess24.com

If you would like to contribute to the project, do not hesitate to contact me.

# Android Application

The code is used productively by this app, which could be found here: https://play.google.com/store/apps/details?id=com.chessboardscanner

# Credits

This project would not be possible without:
  -  [OpenCV](https://opencv.org/)
  -  [Deeplearning4j](https://deeplearning4j.org/)
  -  [StackOverflow](https://stackoverflow.com/)

# Like the project?

You could give it a star by clicking 'Star' button placed top-right of the page.
This will rank better the project and will allow other people to easily find it out on github.

# Author

The author of the project is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.
