(ns dw-todo.system
  (:require [com.stuartsierra.component :as component]
            [dw-todo.web-server :as web]))

(defn new-system
  "Create the system. See https://github.com/stuartsierra/component"
  []
  (component/system-map
   :web-server (web/new-web-server)))
