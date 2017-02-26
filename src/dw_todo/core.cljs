(ns dw-todo.core
  (:require [reagent.core :as r]
            [bidi.bidi :as bidi]
            [accountant.core :as acc]))

(def routes
  ["/"
   [["" :index]
    ["about" :about]]])

(defonce location (r/atom nil))

(defmulti contents identity)

(defmethod contents :index
  []
  [:div "Index"])

(defmethod contents :about
  []
  [:div "About"])

(defn page
  []
  [:div
   [:a {:href (bidi/path-for routes :index)} "Todos"]
   [:span " | "]
   [:a {:href (bidi/path-for routes :about)} "About"]
   (contents @location)])

(defn render!
  []
  (r/render [page] (.getElementById js/document "app")))

(acc/configure-navigation!
 {:nav-handler (fn [path]
                 (let [match (bidi/match-route routes path)]
                   (reset! location (:handler match))))
  :path-exists? (fn [path]
                  (boolean (bidi/match-route routes path)))})
(acc/dispatch-current!)
(render!)
