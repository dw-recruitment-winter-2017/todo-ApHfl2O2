(ns dw-todo.todo-api
  (:require [com.stuartsierra.component :as component]
            [dw-todo.todo-db :as todo-db]
            [yada.yada :as yada]))

(defrecord TodoApi [])

(defn new-todo-api
  []
  (component/using
   (map->TodoApi {})
   [:todo-db]))

(defn add-resource [todo-api]
  (yada/resource
   {:methods
    {:post
     {:consumes "application/edn"
      :produces "application/edn"
      ;;:parameters {:body todo-db/}
      :response #(let [todo-db (:todo-db todo-api)]
                   (todo-db/add todo-db (:body %))
                   (pr-str (todo-db/fetch-all todo-db)))}}}))

(defn routes
  [todo-api]
  {"add" (add-resource todo-api)})
