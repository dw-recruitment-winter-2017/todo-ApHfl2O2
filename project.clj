(defproject dw-todo "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.473"]
                 [com.stuartsierra/component "0.3.1"]
                 [reloaded.repl "0.2.2"]
                 [aleph "0.4.2-alpha8"]
                 [yada "1.2.1" :exclusions [ring/ring-core commons-fileupload org.clojure/core.async]]
                 [bidi "2.0.16"]
                 [venantius/accountant "0.1.9"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.9" :exclusions [org.clojure/clojure]]]

  :source-paths ["src"]
  :main dw-todo.core
  :clean-targets ^{:protect false} ["out" "target" "resources/public/cljs"]

  :profiles {:dev {:source-paths ["src" "dev"]
                   :dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.9" :exclusions [org.clojure/core.async
                                                                         org.clojure/tools.analyzer.jvm
                                                                         org.clojure/tools.analyzer
                                                                         org.clojure/core.memoize
                                                                         org.clojure/core.cache
                                                                         org.clojure/data.priority-map]]
                                  [org.clojure/test.check "0.9.0"]
                                  [reagent "0.6.0"]
                                  [sablono "0.7.4"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :main user}}

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src" "dev"]
                :figwheel true
                :compiler {:output-to     "resources/public/cljs/dw-todo.js"
                           :output-dir    "resources/public/cljs/out"
                           :source-map    true
                           :source-map-timestamp true
                           :main dw-todo.core
                           :asset-path    "cljs/out"
                           :optimizations :none
                           :recompile-dependents true
                           :parallel-build true
                           :cache-analysis true}}]})
