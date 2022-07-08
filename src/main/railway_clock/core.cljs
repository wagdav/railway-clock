(ns railway-clock.core
  (:require [goog.dom :as gdom]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.string :as gstring]
            [goog.string.format]))

(defn rotate [& params]
  (str "rotate" "(" (clojure.string/join "," params) ")"))

(defn tick [{:keys [cx cy r1 r2 angle]} style]
  [:line (merge
           {:x1 cx :y1 (- cy r1)
            :x2 cx :y2 (- cy r2)
            :transform (rotate angle cx cy)}
           style)])

(defn second-hand [{:keys [cx cy r1 r2 angle] :as c} style]
  (lazy-seq
    [^{:key "second-hand-body"} [tick c style]
     ^{:key "second-hand-tip"}  [:circle {:cx cx :cy (- cy r2) :r 4 :fill "red"
                                          :transform (rotate angle cx cy)}]]))

(defn clock [hour-angle minute-angle second-angle]
  [:center
    [:svg
      {:view-box "0 0 100 100"
       :width 400
       :height 400}
      [:circle {:cx 50 :cy 50 :r 49 :stroke "gray" :stroke-width 1 :fill "none"}]
      (for [angle (range 0 360 30)]
        ^{:key (str "hours" angle)}
        [tick {:cx 50 :cy 50 :r1 37 :r2 46 :angle angle}
              {:stroke "black" :stroke-width 3}])
      (for [angle (range 0 360 5)]
        ^{:key (str "minutes" angle)}
        [tick {:cx 50 :cy 50 :r1 43 :r2 46 :angle angle}
              {:stroke "black" :stroke-width 1}])

      ; hour
      [tick {:cx 50 :cy 50 :r1 -10 :r2 30 :angle hour-angle}
            {:stroke "black" :stroke-width 6}]
      ; minute
      [tick {:cx 50 :cy 50 :r1 -10 :r2 44 :angle minute-angle}
            {:stroke "black" :stroke-width 4}]

      ; second
      (second-hand {:cx 50 :cy 50 :r1 -13 :r2 33 :angle second-angle}
                   {:stroke "red" :stroke-width 1})]])


(defonce timer (r/atom (js/Date.)))

(defonce time-updater (js/setInterval
                       #(reset! timer (js/Date.)) 1000))

(defn main []
  (let [hours (.getHours @timer)
        minutes (.getMinutes @timer)
        seconds (.getSeconds @timer)]
    [clock (* (/ 360 12) (mod hours 12))
           (* (/ 360 60) (mod minutes 60))
           (* (/ 360 60) (mod seconds 60))]))

(defn mount []
  (rdom/render [main] (gdom/getElement "app")))

(defn ^:dev/after-load on-reload []
  (mount))

(defonce startup (do (mount) true))

(comment
  ; Evaluate these lines to enter into a ClojureScript REPL
  (require '[shadow.cljs.devtools.api :as shadow])
  (shadow/repl :app)
  ; Exit the CLJS session
  :cljs/quit)
