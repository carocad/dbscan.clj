(defproject dbscan "0.1.0"
  :description "Clojure implementation of the DBSCAN clustering algorithm"
  :url "https://github.com/carocad/dbscan.clj"
  :license {:name "LGPL v3"
            :url "https://github.com/carocad/dbscan.clj/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0"]
                                  [org.clojure/test.check "0.9.0"]
                                  ;[incanter/incanter-charts "1.5.5"]
                                  ;[incanter/incanter-core "1.5.5"]
                                  ]}})

(comment
  incanter-core and incanter-charts are included mainly to visualize the
  data to be clustered in case that it is not clear why was there an error)
