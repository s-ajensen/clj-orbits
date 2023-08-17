(ns orbits.orbits-spec
  (:require [speclj.core :refer :all]
            [orbits.orbits :as sut]
            [quil.core :as q]))

(def body  (sut/blank-body))
(def child (sut/blank-body))
(def body  (sut/add-child body child))

(defn should-approximate 
  ([n m]
   (if (and (vector? n) (vector? m))
     (let [[x1 y1] n
           [x2 y2] m]
       (should-approximate x1 x2)
       (should-approximate y1 y2))
     (if (> 0.000001 (abs (- n m)))
       (should true)
       (do (prn n " did not approximate " m)
           (should false))))))

(describe "Orbit"
  (context "body"
    (context "positions are calculated"
      (context "at a given time"
        (it "with radius 1"
            (should-approximate [1.0  0.0] (sut/pos 0               {:radius 1}))
            (should-approximate [0    1.0] (sut/pos q/HALF-PI       {:radius 1}))
            (should-approximate [-1.0 0.0] (sut/pos q/PI            {:radius 1}))
            (should-approximate [0   -1.0] (sut/pos (* 3 q/HALF-PI) {:radius 1})))

        (it "with variable radius"
            (should-approximate [2.0  0.0] (sut/pos 0               {:radius 2}))
            (should-approximate [0    2.0] (sut/pos q/HALF-PI       {:radius 2}))
            (should-approximate [-2.0 0.0] (sut/pos q/PI            {:radius 2}))
            (should-approximate [0   -2.0] (sut/pos (* 3 q/HALF-PI) {:radius 2})))

        (it "with given period"
            (should-approximate [1.0  0.0] (sut/pos 0    {:radius 1 :period 1000}))
            (should-approximate [0    1.0] (sut/pos 500  {:radius 1 :period 2000}))
            (should-approximate [-1.0 0.0] (sut/pos 2000 {:radius 1 :period 4000}))
            (should-approximate [0   -1.0] (sut/pos 3000 {:radius 1 :period 4000})))

        (it "with an origin"
            (should-approximate [2.0  1.0] (sut/pos 0               {:radius 1 :origin [1 1]}))
            (should-approximate [1    2.0] (sut/pos q/HALF-PI       {:radius 1 :origin [1 1]}))
            (should-approximate [0.0  1.0] (sut/pos q/PI            {:radius 1 :origin [1 1]}))
            (should-approximate [1    0.0] (sut/pos (* 3 q/HALF-PI) {:radius 1 :origin [1 1]})))))
    
    (context "children"
      (it "are added"
          (should= child (-> body :children first))))
    
    (context "rendering"
      (with-stubs)
      (redefs-around [q/fill (stub :fill)
                      q/ellipse (stub :ellipse)
                      sut/now (stub :now {:return 500})]) 

      (context "is performed for root" 
        (it "at given position"
            (let [[x y] (sut/pos (sut/now) body)]
              (sut/draw-system body)
              (should-have-invoked :ellipse {:with [x y 25 25]})))
        
        (it "with specified color"
            (sut/draw-system body)
            (should-have-invoked :fill {:with (:color body)})))
      
      (context "is preformed for children"
        (it "for corresponding positions"
            (let [[x y] (sut/pos (sut/now) child)]
              (sut/draw-system body) 
              (should-have-invoked :ellipse {:with [x y (:size child) (:size child)]})))))))