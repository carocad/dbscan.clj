(ns dbscan.core
  (:require [clojure.set :as cset]
            [clojure.core.matrix :refer [compute-matrix get-row row-count distance]]
            :reload))

; the return includes the target point itself
(defn- region-query
  [p2p-dist eps target]
  (keep-indexed (fn [index value] (if (< value eps) index nil))
                (get-row p2p-dist target)))

(defn- make-query-fn
  [data dist-fn]
  ;p2p-dist is diagonal-symetric ... should I create an upper triangular matrix instead?
  (let [length     (row-count data)
        p2p-dist   (compute-matrix :vectorz [length length]
                      (fn [i j] (dist-fn (second (get-row data i))
                                         (second (get-row data j)))))]
    (partial region-query p2p-dist)))

(defn- not-clustered?
  [clusters [index _]]
  (not-any? (fn [cluster] (cluster index)) clusters))

(defn- enough-neighbors?
  [[index neighborhood] minpts]
  (if (> (count neighborhood) minpts)
    index
    nil))

(defn- cluster
  [seed minpts unclassified]
  (loop [neighbors (into (hash-set) (unclassified seed))
         edge      neighbors]
    (let [far-neighbors     (map #(unclassified %) edge)
          good-neighboor    (filter #(> (count %) minpts) far-neighbors)
          unique-neighbors  (into (hash-set) (apply concat good-neighboor))
          new-neighbors     (filter #(not (neighbors %)) unique-neighbors)]
      (if (empty? new-neighbors)
        neighbors
        (recur (cset/union neighbors new-neighbors) new-neighbors)))))

; data is a sequence of vectors with index as first value and a vector as
; second value
; currently index MUST start at 0 and increase thereon
(defn DBSCAN
  ([data eps minpts]
   (DBSCAN data eps minpts distance (make-query-fn data distance)))
  ([data eps minpts dist-fn]
   (DBSCAN data eps minpts dist-fn (make-query-fn data dist-fn)))
  ([data eps minpts dist-fn query-fn]
   (loop [clusters     []
          unclassified (into (hash-map) (map (fn [[index _]] [index (query-fn eps index)])
                                             data))
          seed         (some #(enough-neighbors? % minpts) unclassified)]
     (if (nil? seed)
       [clusters (map first unclassified)]   ; TODO: make a set with the unclassified points = noise
       (let [new-clusters       (conj clusters (cluster seed minpts unclassified))
             still-unclassified (into (hash-map) (filter #(not-clustered? new-clusters %) unclassified))
             new-seed           (some #(enough-neighbors? % minpts) still-unclassified)]
         (recur new-clusters still-unclassified new-seed))))))

; NOTE: I think it is easier to make data be simple sequence of value vectors
; such that the indexing part is handled internally

(DBSCAN '([0 [1 2 3]] [1 [1 2 3]] [2 [1 2 3]] [3 [1 2 3]] [4 [0 0 0]] [5 [0 0 0]] [6 [0 0 0]]
          [7 [9 9 9]]) 1 2)
