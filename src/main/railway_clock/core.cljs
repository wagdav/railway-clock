(ns railway-clock.core
  (:require [goog.dom :as gdom]
            [reagent.core :as r]
            [reagent.dom.client :as rclient]
            [goog.string :as gstring]
            [goog.string.format]))

(defn tick [{:keys [cx cy r1 r2 angle]} style]
  [:line (merge
           {:x1 cx :y1 (- cy r1)
            :x2 cx :y2 (- cy r2)
            :transform (gstring/format "rotate(%d,%d,%d)" angle cx cy)}
           style)])

(defn second-hand [{:keys [cx cy r1 r2 angle] :as c}]
  (let [seconds (/ angle 6)
        stop-for-seconds 1.5
        remaining-seconds (- 60 seconds stop-for-seconds)]
    [:g
     {:stroke "red" :fill "red"}
     [:line {:x1 cx :y1 (- cy r1) :x2 cx :y2 (- cy r2)}]
     [:circle {:cx cx :cy (- cy r2) :r 4}]
     [:animateTransform
      {:attributeName "transform"
       :type "rotate"
       :attributeType "XML"
       :from (gstring/format "%d %d %d" angle cx cy)
       :to (gstring/format "%d %d %d" 360 cx cy)
       :dur (gstring/format "%ds" remaining-seconds)
       :repeatCount 1}]]))

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
            {:stroke "black" :stroke-width 5}]
      ; minute
      [tick {:cx 50 :cy 50 :r1 -10 :r2 44 :angle minute-angle}
            {:stroke "black" :stroke-width 4}]
      ; second
      [second-hand {:cx 50 :cy 50 :r1 -13 :r2 33 :angle second-angle}]]])

(defn main []
  (let [timer (r/atom (js/Date.))]
    (fn []
      (let [hours (.getHours @timer)
            minutes (.getMinutes @timer)
            seconds (.getSeconds @timer)
            updater (js/setTimeout (fn update-time-and-restart-animation []
                                     (reset! timer (js/Date.))) (* 1000 (- 60 (.getSeconds @timer)))
                                     (doseq [animation (gdom/getElementsByTagName "animateTransform")]
                                        (.beginElement animation)))]

        [clock (* (/ 360 12) (+ (mod hours 12) (/ minutes 60)))
               (* (/ 360 60) minutes)
               (* (/ 360 60) seconds)]))))

(defonce dom-root
     (rclient/create-root (gdom/getElement "app")))

(defn ^:dev/after-load start []
    (rclient/render dom-root [main]))

(defn init []
    (start))

(comment
  ; Evaluate these lines to enter into a ClojureScript REPL
  (require '[shadow.cljs.devtools.api :as shadow])
  (shadow/repl :app)
  ; Exit the CLJS session
  :cljs/quit)
