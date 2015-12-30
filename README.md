# dbscan
[![Build Status](https://travis-ci.org/carocad/dbscan.clj.svg?branch=master)](https://travis-ci.org/carocad/dbscan.clj)
[![License](https://img.shields.io/badge/license-LGPL%20v3-blue.svg)](https://github.com/carocad/dbscan.clj/blob/master/LICENSE)

A Clojure library that implements a functional version of DBSCAN.
You can find the original algorithm [here](https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf)

You should note that the algorithm has been modified to adapt to a more functional approach
since the original algorithm does a lot of in-place state mutations.

This project is still under heavy development, beware of the version number.
(semantic versioning)

## Usage
*data* must be a vector of vectors representing the points to cluster. This is
due to the difference in lookup performance between list and vectors in clojure.

The DBSCAN function can be used as:
```Clojure
(DBSCAN data eps minpts)
```
DBSCAN returns a vector whose first value is a vector of clusters (hash-sets) and
whose second value is a sequence of points classified as noise.

DBSCAN uses the Euclidean distance as a default metric and an square distance matrix
(represented as a lower triangular matrix) as a region-query function. It is also possible
to use a custom distance and a region-query function as 4th and 5th parameters. Both the
distance and the region-query function MUST be free of side effects.
Also the distance function should comply with the second and third properties of
[metric functions](https://en.wikipedia.org/wiki/Metric_%28mathematics%29). Otherwise
you would have to use your own region-query function in combination with your distance function.

The region-query function should be callable as:
```Clojure
(region-query eps index)
```
where index is the index at which a specific point can be found in the *data* sequence

###Example
The following example was taken from: https://www.npmjs.com/package/density-clustering
```Clojure
(DBSCAN [[1,1] [0,1] [1,0] [10,10] [10,13] [13,13] [54,54] [55,55] [89,89] [57,55]]
        5 2)
; [[#{0 1 2} #{7 6 9} #{4 3 5}] (8)]
```

##Test
simply do
```bash
$ git clone https://github.com/carocad/dbscan.clj.git
$ cd ./dbscan.clj
$ lein test
```

The performed test are much more interesting than the previously shown example. I encourage you
to add even more (generative) tests.

##Miscelaneous
- There is currently no benchmark
- The algorithm is still very basic, probably a lot of performance optimizations can be made
- Some more clustering test will be eventually added to check the proper functioning of the algorithm
- Don't expect this repository to include an R\* tree indexing structure. That is outside of the scope of this project. If you know one, by all means let me know so that I can link it here for other people to use them together.

## License

Copyright © 2015 Camilo Roca

Distributed under the LGPL v3 License
