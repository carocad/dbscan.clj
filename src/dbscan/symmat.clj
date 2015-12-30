(ns dbscan.symmat
  "implementation of a symmetric matrix as a lower-triangular matrix computation
  and get functions.")

(defn compute-triangular
  "computes a triangular vector of vectors to represent a symmetric matrix
  without the space-overhead of the redundant elements"
  [length p2pfn]
  (into (vector)
        (for [i (range length)]
            (into (vector)
                  (for [j (range i)]
                      (p2pfn i j))))))

(defn symmetric-get
  "get the element at position i,j of the symmetric matrix"
  [matrix i j]
  (cond
   (= i j) 0
   (> i j) (get-in matrix [i j])
   (< i j) (symmetric-get matrix j i)))

(defn symmetric-row
  "construct a row representation of a symmetric matrix based on its lower
  triangular representation."
  [matrix i]
  (for [j (range (inc (count (peek matrix))))]
    (symmetric-get matrix i j)))
