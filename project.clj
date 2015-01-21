(defproject scaling-robot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.7.0-alpha5"]
                 [bencode "0.2.5"]
                 [org.flatland/useful "0.11.3"]
                 ]
  :main ^:skip-aot scaling-robot.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.8"]
                                  [org.clojure/java.classpath "0.2.2"]]}}
  )
