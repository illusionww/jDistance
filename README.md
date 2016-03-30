# jDistance
jDistance is a library for computing distances in graphs.

## Requirements:
* Oracle Java 8
* Gnuplot

## About
Module jDistance contains:
* Distances:
  * "Plain" Walk;
  * Walk;
* ["Plain"] Forest;
  * Logarithmic Forest;
  * Communicability;
  * Logarithmic Communicability;
  * Randomized Shortest Path;
  * Helmholtz Free Energy;
  * Shortest Path - Commute Time combination.
* Classifying:
  * kNN classifier;
* Clustering:
  * Min Spanning Tree method;
  * Ward method;
* Graph generators:
  * G(n, p_{in}, p_{out}) clustered graph generator

Module jDistanceImpl contains:
* parallel GridSearch;
* competitions;
* methods to check the quality of classification or clusterization;
* adapter for [dcrgenerator](http://i11www.iti.uni-karlsruhe.de/en/projects/spp1307/dyngen);
* adapter for [JavaPlot](http://javaplot.panayotis.com/);
* GraphML and CSV export/import

and also some experimental stuff to learn how metrics work.
