(ns dbscan.core-test
  (:require [dbscan.core :refer [DBSCAN]]
            [dbscan.util-test :as geo]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            ;[incanter.charts :as chart]
            ;[incanter.core :as incanter]
            ))

(def num-pts (first (gen/sample (gen/choose 100 1000) 1)))
(def eps     (first (gen/sample (gen/choose 5 20) 1)))
(def min-pts (/ num-pts 100))
(def limit   (/ eps 3))

(def noise
  (let [value     (gen/double* {:infinite? false :NaN? false :min -100 :max 100})
        couple    (gen/tuple value value)]
    (gen/vector couple (Math/ceil (/ num-pts 50)))))

(def line-cluster
  (let [slope (first (gen/sample (gen/choose -10 10) 1))
        b     (first (gen/sample (gen/choose -10 10) 1))
        x         (gen/vector (gen/double* {:infinite? false :NaN? false :min -50 :max 50}) num-pts)
        line-fn  #(geo/line slope b %)
        noise-fn  (partial geo/add-noise limit)
        line-pts  (gen/bind x #(gen/return (map line-fn %)))]
    (gen/bind line-pts #(gen/return (map noise-fn %)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TEST ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defspec line-simple
  30
  (prop/for-all [L1      line-cluster]
    (let [clusters (DBSCAN L1 eps min-pts)]
      (and (= (count (second clusters)) 0) ; no noise
           (=  (count (first clusters)) 1) ; only one cluster found
           (= (count (ffirst clusters)) num-pts))))) ; the cluster has the size of the data
;(tc/quick-check 100 line-simple)

(defspec line-noise
  30
  (prop/for-all [L1      line-cluster
                 ran-pts noise]
    (let [data     (concat L1 ran-pts)
          clusters (DBSCAN data eps min-pts)]
      (and (<= (count (second clusters)) (Math/ceil (/ num-pts 50)))
           (=  (count (first clusters)) 1)
           (>= (count (ffirst clusters)) num-pts)))))
;(tc/quick-check 100 line-noise)


;; VISUALIZE
;(def foo2 (first (gen/sample line-cluster 1)))
;(time (DBSCAN foo2 10 3))
;(incanter/view (chart/scatter-plot (map first foo2) (map second foo2)))

;; TODO
(comment
  (defn rectangle-pts
  [no-pts delta]
  (let [[xc yc]         (gen/sample gen/int 2)
        [width height]  (gen/sample (gen/choose 5 40) 2); different than 0
        xp              (gen/sample (gen/choose (- xc (/ width 2)) (+ xc (/ width 2))) no-pts)
        yp              (gen/sample (gen/choose (- yc (/ width 2)) (+ yc (/ width 2))) no-pts)]
    (map #(geo/add-noise %1 %2 delta) xp yp)))
  )
