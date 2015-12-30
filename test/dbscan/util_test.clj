(ns dbscan.util-test
  "useful functions to use when testing a clustering algorithm"
  (:require [clojure.math.numeric-tower :as math]
            [dbscan.util :refer [pow2]]))

(defn line
  "m: slope, b: vertical translation, x: horizontal axis"
  [m b x]
  [x (+ (* m x) b)])

(defn circle
  "parametric equations of a circle"
  [[a b] radius t]
  [(+ a (* radius (Math/cos (Math/toRadians t))))
   (+ b (* radius (Math/sin (Math/toRadians t))))])

(defn in-circle?
  [[xc yc radius] x y]
  (cond
   (> (pow2 radius) (+ (pow2 (- x xc)) (pow2 (- y yc)))) true
   :else false))

(defn add-noise
  [limit [x y]]
  [(+ x (rand limit)) (+ y (rand limit))])
