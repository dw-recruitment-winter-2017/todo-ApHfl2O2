(ns dw-todo.todo-db
  (:require [clojure.spec :as s]
            [com.stuartsierra.component :as component]))

(s/def ::id uuid?)
(s/def ::completed? boolean?)
(s/def ::text string?)
(s/def ::todo (s/keys :req-un [::id ::completed? ::text]))
(s/def ::todo-add (s/keys :req-un [::completed? ::text]))
(s/def ::todo-amend (s/keys :opt-un [::completed? ::text]))

(defprotocol TodoStore
  (add* [self data])
  (fetch* [self todo-id])
  (amend* [self data])
  (delete* [self todo-id])
  (fetch-all* [self]))

(defn add
  [todo-store data]
  {:pre [(s/valid? ::todo-add data)]
   :post [(s/valid? ::todo %)]}
  (add* todo-store data))

(defn fetch
  [todo-store todo-id]
  {:pre [(s/valid? ::id todo-id)]
   :post [(s/valid? (s/nilable ::todo) %)]}
  (fetch* todo-store todo-id))

(defn amend
  [todo-store data]
  {:pre [(s/valid? ::todo-amend data)]
   :post [(s/valid? ::todo %)]}
  (amend* todo-store data))

(defn delete
  [todo-store todo-id]
  {:pre [(s/valid? ::id todo-id)]
   :post [(s/valid? boolean? %)]}
  (delete* todo-store todo-id))

(defn fetch-all
  [todo-store]
  {:post [(s/valid? (s/coll-of ::todo) %)]}
  (fetch-all* todo-store))

(defrecord TodoDb [store]
  component/Lifecycle
  (start [self]
    (assoc self :store (atom '())))
  (stop [self]
    (dissoc self :store))

  TodoStore
  (add* [self data]
    (let [id (java.util.UUID/randomUUID)
          todo (assoc data :id id)]
      (swap! (:store self) conj todo)
      todo))
  (fetch* [self todo-id]
    (first (filter #(= (:id %) todo-id) @(:store self))))
  (amend* [self data]
    (let [id (:id data)
          to-amend (fetch self id)
          amended (merge to-amend data)]
      (swap! (:store self) #(replace {to-amend amended} %))
      amended))
  (delete* [self todo-id]
    (let [before-db @(:store self)]
      (swap! (:store self) (fn [store] (remove #(= (:id %) todo-id) store)))
      (not= (count before-db) (count @(:store self)))))
  (fetch-all* [self]
    @(:store self)))

(defn new-todo-db
  []
  (component/using
   (map->TodoDb {})
   []))
