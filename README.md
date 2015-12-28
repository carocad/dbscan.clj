# dbscan
[![License](https://img.shields.io/badge/license-LGPL%20v3-blue.svg)](https://github.com/carocad/dbscan.clj/blob/master/LICENSE)

A Clojure library that implements a functional version of DBSCAN.
You can find the original algorithm [here](https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf)

You should note that the algorithm has been modified to adapt to a more functional approach
since the original algorithm does a lot of in-place state mutations.

## Usage
In the following examples, data is a sequence of vectors representing the
points to cluster.

The DBSCAN function can be used as:
```Clojure
(DBSCAN data eps minpts)
```
DBSCAN returns a vector whose first value is a vector of clusters (hash-sets) and
whose second value is a sequence of points classified as noise.

DBSCAN uses the Euclidean distance as a default metric and an square distance matrix
as a region-query function. It is also possible to use a custom distance and a region-query
function as 4th and 5th parameters. Please note that both the distance and the region-query function
MUST be free of side effects. Also, the region-query function should be callable as:
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

## License

Copyright © 2015 Camilo Roca

Distributed under the LGPL v3 License
