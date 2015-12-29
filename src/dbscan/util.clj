(ns dbscan.util
  (:require [clojure.math.numeric-tower :as math]))

(defn- pow2
  "Uses the numeric tower expt to square a number"
  [x]
  (math/expt x 2))

(defn squared-distance
  "Computes the Euclidean squared distance between two sequences"
  [a b]
  (reduce + (map (comp pow2 -) a b)))

(defn euclidean-distance
  "Computes the Euclidean distance between two sequences"
  [a b]
  (math/sqrt (squared-distance a b)))
