(ns attraction.core
  #_{:clj-kondo/ignore [:refer-all]}
  (:require [attraction.particle :refer :all]
            [attraction.utils :refer :all]
            [quil.core :as q]
            [quil.middleware :as m]) 
  (:gen-class))

(def n-particles 3000)
(def n-particles-per-row (Math/floor (Math/sqrt n-particles)))

(defn take-picture [state event]
  (when (= (:raw-key event) \space)
    (q/save "generated/image.png"))
  state)

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
   (apply pmap
          (fn [& pgroups] (doall (apply map f pgroups)))
          (map (partial partition-all grain-size) colls))))

(defn mouse-pressed [state _] 
  (let [{:keys [attractors particles]} state]
    {:attractors (conj attractors (createVector (q/mouse-x) (q/mouse-y)))
     :particles  particles}))

(defn setup []
  (q/frame-rate 30)
  ; (reduce (fn [particles _] (conj particles (createParticle (rand-int (q/width)) (rand-int (q/height)))))  '() (range n-particles))
  {:attractors '()
   :particles (for [x (range 0 (q/width) (int (Math/floor (/ (q/width) n-particles-per-row)))) 
                    y (range 0  (q/height) (int (Math/floor (/ (q/height) n-particles-per-row))))]
                (createParticle x y))
   })

(defn accumulate-force [particle attractor]
  (let [new-acc (attracted particle attractor)]
     (assoc particle :acc (sumVectors new-acc (:acc particle)))))

(defn update-particle [particle attractors]
  ((comp updateParticle #(reduce accumulate-force % attractors)) particle))

(defn update-state [state] 
  (let [{:keys [attractors particles]} state]
    {:attractors attractors
     :particles (doall (ppmap (int (Math/floor (* (exp n-particles-per-row 2) 0.2))) #(update-particle % attractors) particles))
     }))

(defn draw [state]
  (q/background 51)
  (let [{:keys [attractors particles]} state]
    (doseq [particle particles] (show particle))
    (doseq [attractor attractors] (showAttractor attractor))
    ))

(defn -main []
  (q/sketch :title "Particle attraction" 
            :size [700 700]
            :setup setup 
            :update update-state
            :mouse-pressed mouse-pressed
            :key-pressed take-picture
            :draw draw 
            :features [:keep-on-top]
            :middleware [m/fun-mode]))