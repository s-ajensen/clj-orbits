(ns orbits.main
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [orbits.orbits :refer :all]))

(declare trigonometry)

(defn settings []
  (q/smooth 2))
(defn setup []
  {:root (add-child (new-body 25 0 1 (rand-color))
                    (add-child (new-body 10 100 5000 (rand-color))
                               (new-body 5 50 1000 (rand-color))))})
(defn update [state]
  state)

(defn draw [state]
  (q/background 255) 
  
  (q/with-translation [250 250]
    (draw-system (:root state))))

(defn -main [& args]
  (q/defsketch trigonometry
    :size [500 500]
    :settings settings
    :setup setup
    :update update
    :draw draw
    :middleware [m/fun-mode]))