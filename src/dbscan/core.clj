(ns dbscan.core
  (:require [clojure.core.matrix :refer [compute-matrix get-row
                                         row-count distance]] :reload))

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
  (not-any? (fn [cluster] (cluster index) clusters)))

(defn- enough-neighbors?
  [neighborhood minpts]
  (> (count neighborhood) minpts))

; data is a sequence of vectors with index as first value and a vector as
; second value
; currently index MUST start at 0 and increase thereon
(defn DBSCAN
  ([data eps minpts]
   (DBSCAN data eps minpts distance (make-query-fn data distance)))
  ([data eps minpts dist-fn]
   (DBSCAN data eps minpts dist-fn (make-query-fn data dist-fn)))
  ([data eps minpts dist-fn query-fn]
   (loop [clusters     (transient [])
          unclassified (into (hash-map) (map (fn [[index _]] [index (query-fn index eps)])
                                             data))
          seed         (some #(enough-neighbors? % eps) unclassified)]
     (if (nil? seed)
       (persistent! clusters)   ;make a set with the unclassified points = noise
       (let [new-clusters      (conj! clusters (cluster seed minpts unclassified query-fn eps))
             still-unclassified (into {} (filter #(not-clustered? new-clusters %) unclassified))
             new-seed           (some #(enough-neighbors? % minpts) still-unclassified)]
         (recur new-clusters still-unclassified new-seed))))))

(defn- cluster
  [seed minpts unclassified query-region eps]
  (loop [neighbors (query-region seed eps)
         edge      neighbors]
    (let [far-neighbors     (map #(query-region eps %) edge)
          good-neighboor    (filter #(> (count %) minpts) far-neighbors)
          unique-neighbors  (into (hash-set) (apply concat good-neighboor))
          new-neighbors     (filter #(not (neighbors %)) unique-neighbors)]
      (if (empty? new-neighbors)
        (persistent! neighbors)
        (recur (conj! neighbors new-neighbors) new-neighbors)))))
