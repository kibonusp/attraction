(ns attraction.particle 
  (:require [attraction.utils :refer [createVector exp mag multiplyVector
                                     setMag subtractVectors sumVectors]]
            [quil.core :as q]))

(def G 50)
(def fmax 6)
(def rmin 60)
(def rmax 300)

(defn createParticle [x y]
  {:pos (createVector x y)
   :prev (createVector x y)
   :vel (createVector)
   :acc (createVector)})

(defn updateParticle [particle]
  (let [added-vel (assoc particle :vel (sumVectors (:vel particle) (:acc particle)))]
    (assoc added-vel 
           :pos (sumVectors (:pos added-vel) (:vel added-vel)) 
           :acc (multiplyVector (:acc added-vel) 0)
           :prev (:pos particle))))

(defn show [particle]
  (q/stroke 255 150)
  (q/stroke-weight 4)
  (q/line (get-in particle [:pos :x]) (get-in particle [:pos :y])
          (get-in particle [:prev :x]) (get-in particle [:prev :y])))

(defn attracted [particle attractor]
  (let [dir (subtractVectors attractor (:pos particle))
        d (mag dir)
        magnitude (/ G (exp d 2))] 
    #_{:clj-kondo/ignore [:redundant-let]}
    (let [vec (setMag dir magnitude)]
      (multiplyVector
       vec
       (if (>= d 0)
         (if (< d rmin)
         (- (/ d rmin) 1)
         (if (< d (/ (+ rmax rmin) 2))
           (- (/ (* 2 fmax d) (- rmax rmin)) (/ (* 2 fmax rmin) (- rmax rmin)))
           (if (<= d rmax)
             (- (/ (* 2 fmax d) (- rmin rmax)) (/ (* 2 fmax rmax) (- rmin rmax)))
             0
             ))
        )
         -1))
      )))

(defn showAttractor [attractor] 
  (q/stroke-weight 4) 
  (q/stroke 0 0 255)
  (q/fill 255 0 0 0)
  (q/point (:x attractor) (:y attractor))
  (q/stroke 0 255 0)
  (q/stroke-weight 2) 
  (q/ellipse (:x attractor) (:y attractor) (* 2 rmin) (* 2 rmin))
  (q/stroke 255 255 0)
  (q/ellipse (:x attractor) (:y attractor) (+ rmax rmin) (+ rmax rmin))
  (q/stroke 255 0 0)
  (q/ellipse (:x attractor) (:y attractor) (* 2 rmax) (* 2 rmax)))
