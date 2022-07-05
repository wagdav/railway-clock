(ns railway-clock.core
  (:require [goog.dom :as gdom]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.string :as gstring]
            [goog.string.format]))

(defn main []
  [:div
    [:h1 "Railway clock"]])

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
