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
      :response #(let [todo-db (:todo-db todo-api)]
                   (todo-db/add todo-db (:body %))
                   (pr-str (todo-db/fetch-all todo-db)))}}}))

(defn amend-resource [todo-api]
  (yada/resource
   {:methods
    {:post
     {:consumes "application/edn"
      :produces "application/edn"
      :response #(let [todo-db (:todo-db todo-api)]
                   (todo-db/amend todo-db (:body %))
                   (pr-str (todo-db/fetch-all todo-db)))}}}))

(defn delete-resource [todo-api]
  (yada/resource
   {:methods
    {:post
     {:consumes "application/edn"
      :produces "application/edn"
      :response #(let [todo-db (:todo-db todo-api)]
                   (todo-db/delete todo-db (:body %))
                   (pr-str (todo-db/fetch-all todo-db)))}}}))

(defn fetch-all-resource [todo-api]
  (yada/resource
   {:methods
    {:get
     {:produces "application/edn"
      :response #(pr-str (todo-db/fetch-all (:todo-db todo-api)))}}}))

(defn routes
  [todo-api]
  {"add" (add-resource todo-api)
   "amend" (amend-resource todo-api)
   "delete" (delete-resource todo-api)
   "fetch-all" (fetch-all-resource todo-api)})
