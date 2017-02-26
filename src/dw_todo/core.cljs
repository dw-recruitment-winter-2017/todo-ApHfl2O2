(ns dw-todo.core
  (:require [reagent.core :as r]
            [bidi.bidi :as bidi]
            [accountant.core :as acc]
            [goog.net.XhrIo :as xhr]
            [cljs.reader :refer [read-string]]))

(def routes
  ["/"
   [["" :index]
    ["about" :about]]])

(defonce location (r/atom nil))

(declare todo-state)

(defn new-state-cb
  [e]
  (reset! todo-state (read-string (-> e .-target .getResponseText))))

(defonce todo-state
  (let [state* (r/atom '())]
    (xhr/send "/todo-api/fetch-all"
              new-state-cb)
    state*))

(defn todo-item
  [id text completed?]
  [:tr
   [:td [:input {:type "checkbox"
                 :checked completed?}]]
   [:td (when completed? {:class "completed"}) text]
   [:td [:a {:href "#"
             :class "delete"}
         "x"]]])

(defn todo-list
  []
  [:table
   [:tbody
    (for [{:keys [id text completed?]} @todo-state]
      ^{:key id} [todo-item id text completed?])]])

(defn new-todo
  [e text]
  (when (= (.-which e) 13) ;; Enter key
    (xhr/send "/todo-api/add"
              #(do
                 (new-state-cb %)
                 (reset! text ""))
              "POST"
              (pr-str {:text @text :completed? false})
              #js {"Content-Type" "application/edn"})))

(defn todo-input
  []
  (let [text (r/atom "")]
    (fn []
      [:input {:type "text"
               :placeholder "Add todo"
               :value @text
               :on-change #(reset! text (-> % .-target .-value))
               :on-key-down #(new-todo % text)}])))

(defmulti contents identity)

(defmethod contents :index
  []
  [:div
   [:h2 "Todos"]
   [todo-input]
   [todo-list]])

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
