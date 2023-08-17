(ns orbits.orbits
  (:require [quil.core :as q]))

(def two-pi (* 2 q/PI))
(def colors [[235 64 52]    ; red
             [235 161 52]   ; orange
             [52 235 73]    ; green
             [52 201 235]   ; blue
             [235 52 226]]) ; pink

(defn rand-color [] (rand-nth colors))
(defn now [] (inst-ms (java.util.Date.)))

(defn new-body [size radius period color]
  {:size size
   :radius radius
   :period period
   :color color
   :children []})
(defn blank-body []
  (new-body 25 50 1000 (rand-nth colors)))

(defn add-child [body child]
  (update-in body [:children] #(conj % child)))

(defn pos [time {:keys [radius period origin]}]
  (let [c      (/ two-pi (or period two-pi))
        [x y]  (or origin [0 0])]
    [(+ x (* radius (Math/cos (* c time))))
     (+ y (* radius (Math/sin (* c time))))]))

(defn draw-body [body]
  (let [[x y] (pos (now) body)]
    (apply q/fill (:color body))
    (q/ellipse x y (:size body) (:size body))))

(defn draw-system [root] 
  (let [origin (or (:origin root) [0 0])]
    (draw-body root)
    (doseq [child (:children root)] 
      (draw-system (assoc child :origin (pos (now) root))))))