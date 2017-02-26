(ns dw-todo.web-server
  (:require [com.stuartsierra.component :as component]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]))

(defn routes
  [web-server]
  ["" (yada/yada (new-classpath-resource "public" {:index-files ["index.html"]}))])

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
   []))
