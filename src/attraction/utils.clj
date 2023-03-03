(ns attraction.utils)

(defn createVector
  ([]
   (createVector 0 0))
  ([x y]
  {:x x :y y}
  ))

(defn hashmap-map [fn hash]
  (let [keys (keys hash)
        vals (vals hash)]
    (zipmap keys (mapv fn vals))))

(defn exp [x n]
  (reduce * (repeat n x)))

(defn sumVectors [vec-a vec-b]
  (merge-with + vec-a vec-b))

(defn subtractVectors [vec-a vec-b]
  (merge-with - vec-a vec-b))

(defn mag [vec]
  (Math/sqrt (reduce + (mapv #(exp % 2) (vals vec)))))

(defn setMag
  ([vec magnitude]
   (let [mag-vec (mag vec)
        unit-vec (hashmap-map #(/ % mag-vec) vec)]
    (hashmap-map #(* % magnitude) unit-vec))
   )
  ([vec]
   (setMag vec 1)))

(defn multiplyVector [vec n]
  (hashmap-map #(* n %) vec))