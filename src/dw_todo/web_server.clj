(ns dw-todo.web-server
  (:require [com.stuartsierra.component :as component]
            [dw-todo.todo-api :as todo-api]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]))

(defn routes
  [web-server]
  ["" [["/todo-api/" (todo-api/routes (:todo-api web-server))]
       ["" (yada/yada (new-classpath-resource "public" {:index-files ["index.html"]}))]]])

(defrecord WebServer [port listener]
  component/Lifecycle
  (start [self]
    (if listener
      self
      (let [listener (yada/listener (routes self) {:port port})]
        (assoc self :listener listener))))
  (stop [self]
    (when-let [close (get-in self [:listener :close])]
      (close))
    (dissoc self :listener)))

(defn new-web-server
  []
  (component/using
   (map->WebServer {:port 3000})
   [:todo-api]))
