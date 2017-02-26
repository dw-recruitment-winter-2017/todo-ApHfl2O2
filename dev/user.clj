(ns user
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.string :as str]
            [clojure.test :as test]
            [dw-todo.system :as system]
            [reloaded.repl :refer [system init start stop go reset reset-all set-init!]]
            [yada.yada :refer [request-for response-for]]
            [figwheel-sidecar.repl-api :as ra :refer [start-figwheel! stop-figwheel! cljs-repl]]))

(set-init! #(system/new-system))
