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
  "A callback for resetting the state after server interactions"
  [e]
  (reset! todo-state (read-string (-> e .-target .getResponseText))))

(defonce todo-state
  (let [state* (r/atom '())]
    (xhr/send "/todo-api/fetch-all"
              new-state-cb)
    state*))

(defn check-todo
  "Sends check and uncheck events to the backend"
  [id completed?]
  (let [todo (first (filter #(= id (:id %)) @todo-state))
        new-todo (assoc todo :completed? completed?)]
    (xhr/send "/todo-api/amend"
              new-state-cb
              "POST"
              (pr-str new-todo)
              #js {"Content-Type" "application/edn"})))

(defn delete-todo
  "Sends delete requests to the backend"
  [id]
  (xhr/send "/todo-api/delete"
            new-state-cb
            "POST"
            (pr-str id)
            #js {"Content-Type" "application/edn"}))

(defn new-todo
  "Sends requests to add todos to the backend"
  [e text]
  (when (= (.-which e) 13) ;; Enter key
    (xhr/send "/todo-api/add"
              #(do
                 (new-state-cb %)
                 (reset! text ""))
              "POST"
              (pr-str {:text @text :completed? false})
              #js {"Content-Type" "application/edn"})))

(defn todo-item
  "A Reagent component for rendering a single todo"
  [id text completed?]
  [:tr
   [:td [:input {:type "checkbox"
                 :checked completed?
                 :on-change #(check-todo id (not completed?))}]]
   [:td (when completed? {:class "completed"}) text]
   [:td [:a {:href "#"
             :class "delete"
             :on-click (fn [e]
                         (.preventDefault e)
                         (delete-todo id))}
         "x"]]])

(defn todo-list
  "A Reagent component for rendering all the todos"
  []
  [:table
   [:tbody
    (for [{:keys [id text completed?]} @todo-state]
      ^{:key id} [todo-item id text completed?])]])

(defn todo-input
  "A Reagent component for the todo input box"
  []
  (let [text (r/atom "")]
    (fn []
      [:input {:type "text"
               :placeholder "Add todo"
               :value @text
               :on-change #(reset! text (-> % .-target .-value))
               :on-key-down #(new-todo % text)}])))

;; Routing

(defmulti contents identity)

(defmethod contents :index
  []
  [:div
   [:h2 "Todos"]
   [todo-input]
   [todo-list]])

(defmethod contents :about
  []
  [:div
   [:p
    "This project, completed for Democracy Works, demonstrates a simple todo app "
    "in Clojure and ClojureScript. It implements these features:"]
   [:ul
    [:li "Add a new TODO (initially incomplete)"]
    [:li "Mark a TODO as completed"]
    [:li "Unmark a TODO as completed (i.e. return it to incomplete state)"]
    [:li "Delete existing TODOs"]]
   [:p
    [:a {:href "https://github.com/juxt/yada"} "Yada"] " is used"
    " is used on the backend to serve static assets and respond "
    "to API requests while on the frontend, rendering is done with "
    [:a {:href "https://github.com/reagent-project/reagent"} "Reagent"]
    " and navigation/routing is handled by "
    [:a {:href "https://github.com/venantius/accountant"} "Accountant"]
    " and "
    [:a {:href "https://github.com/juxt/bidi"} "Bidi"]
    ". For portability, data is persisted in-memory in an atom. If desired, the atom-based "
    "storage can be easily replaced by extending the TodoStore protocol, and "
    "modifying the system (that is, a "
    [:a {:href "https://github.com/stuartsierra/component"} "Component"]
    " system) to use the new storage."]])

(defn page
  "A template for pages; looks up the current route and calls
  the `contents` multimethod to retrieve the appropriate content"
  []
  [:div
   [:a {:href (bidi/path-for routes :index)} "Todos"]
   [:span " | "]
   [:a {:href (bidi/path-for routes :about)} "About"]
   (contents @location)])

(defn render!
  "Mount the main component"
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
