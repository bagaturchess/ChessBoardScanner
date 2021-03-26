# Overview

This project aims to demonstrate how to extract the chess position in a machine readable format (FEN) having the 2D picture of the board.
Initially it supports chess.com and lichess.org images, although the architecture is extensible and different chess boards could be added as well.
It uses OpenCV as a computer vision library and Deeplearning4j as a basis for convolutional networks for chess board classification.

# Running the program
The project structure is an eclipse based project, which can be directly imported into the Eclipse IDE.
All necessary libraries for Deeplearning4j are included into the libs folder, but you need to add the OpenCV library (java and native parts) and refer them from the project as described here: https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html

The basic main class is bagaturchess.scanner.patterns.AllMain

# Author

The author of the project is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.
