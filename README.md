# jDistance
jDistance is a library for computing distances in graphs.

## Requirements:
* Java 1.8
* [jEigen](https://github.com/hughperkins/jeigen)

## About
Module jDistance contains:
* Distances:
  * Walk distances;
  * "Plain" Walk Distances;
  * Logarithmic Forest distances;
  * ["Plain"] Forest Distances;
  * Logarithmic Communicability Distances;
  * Communicability Distances;
  * Helmholtz free energy distances;
  * SP-CT.
* kNN classifier;
* Clusterer.

Module jDistanceImpl contains some examples how to use the library.
It contains common scenario to check the quality of classification or clusterization.
Also, it contains adapters for [dcrgenerator](http://i11www.iti.uni-karlsruhe.de/en/projects/spp1307/dyngen),
[JavaPlot](http://javaplot.panayotis.com/), and also parsers for GraphML and SimpleGraph formats.
