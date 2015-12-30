(ns dbscan.core
  "functional implementation of the DBSCAN clustering algorithm as per
  'https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf'. Note that the functional
  implementation is quite different from the original proposed algorithm:
  - region-query is called at the beginning of the algorithm for all the points
  - no point is marked as visited but rather each point is compared to the
    current neighborhood"
  (:require [clojure.set :as cset]
            [dbscan.symmat :as sym]
            [dbscan.util :refer [euclidean-distance]]
            :reload))

; the return includes the target point itself
(defn- region-query
  "Based on a point to point distance matrix (p2p-dist) find all point-indexes
  whose distance to the target point is less than eps."
  [p2p-dist eps target]
  (keep-indexed (fn [index value] (if (< value eps) index nil))
                (sym/symmetric-row p2p-dist target)))

(defn- build-query-fn
  "based on the provided data, compute the distance among all points and fix
  that information on the region-query function."
  [data dist-fn]
  (let [p2p-dist   (sym/compute-triangular (count data)
                       (fn [i j] (dist-fn (get data i) (get data j))))]
    (partial region-query p2p-dist)))

; This function can be parallelized provided that query-fn and dist-fn
;  don't have any side effects
(defn- find-relations
  "based on the query function and the eps distance, build a hash-map of
  [index neighbors]"
  [data query-fn eps]
  (into (hash-map) (map (fn [index] [index (query-fn eps index)])
                        (range (count data)))))

(defn- not-clustered?
  "check if a point is not present on any cluster.
  clusters is a sequence of hash-sets"
  [clusters [index _]]
  (not-any? (fn [cluster] (cluster index)) clusters))

(defn- enough-neighbors?
  [[index neighborhood] minpts]
  (if (> (count neighborhood) minpts) index nil))

(defn- cluster
  "Cluster points around the seed-index that have more than minpts neighbors.
  The unclassified hash-map is used to know the neighbors of each point"
  [seed minpts unclassified]
  (loop [neighbors (into (hash-set) (unclassified seed))
         edge      neighbors]; points that are currently being analyzed
    (let [new-neighbors (->> (map #(unclassified %) edge)
                             (filter #(> (count %) minpts))
                             (apply concat)
                             (into (hash-set))
                             (cset/difference neighbors))]
      (if (empty? new-neighbors)
        neighbors
        (recur (cset/union neighbors new-neighbors) new-neighbors)))))

(defn DBSCAN
  "cluster the data points based on the distance eps and the requirement that
  at least minpts are nearby. Optionally a particular distance and query function
  can be used. Those default to the Euclidean distance and an square distance
  matrix.
  The return value is of the form (clusters noise), where clusters is a vector
  of sets and noise is a simple sequence."
  ([data eps minpts]
   (DBSCAN data eps minpts euclidean-distance (build-query-fn data euclidean-distance)))
  ([data eps minpts dist-fn]
   (DBSCAN data eps minpts dist-fn (build-query-fn data dist-fn)))
  ([data eps minpts dist-fn query-fn]
   (loop [clusters     []
          unclassified (find-relations data query-fn eps)
          seed         (some #(enough-neighbors? % minpts) unclassified)]
     (if (nil? seed)
       [clusters (map first unclassified)] ; clusters, noise
       (let [curr-clusters       (conj clusters (cluster seed minpts unclassified))
             still-unclassified  (into (hash-map) (filter #(not-clustered? curr-clusters %) unclassified))
             new-seed            (some #(enough-neighbors? % minpts) still-unclassified)]
         (recur curr-clusters still-unclassified new-seed))))))
