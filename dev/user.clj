(ns user
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.reflect :as reflect :refer [reflect]]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.repl :refer :all]
            [bencode.core :as bc]
            ))

;; ==================== helpers

(defn alias-dir-fn [nsname]
  (-> (ns-aliases *ns*)
      (get nsname)
      (or nsname)
      dir-fn))


(defmacro alias-dir
  "Prints a sorted directory of public vars in a namespace or an alias
  namespace."
  [nsname]
  `(doseq [v# (alias-dir-fn '~nsname)]
     (println v#)))

(defn class-exists? [c]
  (reflect/resolve-class (.getContextClassLoader (Thread/currentThread)) c))

(defn class-methods [c]
  (->> (reflect c)
       :members
       (filter #(:public (:flags %)))
       (map :name)
       (into #{})))

(defn important-class-methods [c]
  (->> (class-methods c)
       (#(set/difference % (class-methods java.lang.Object)))
       sort))

(defn full-dir-fn
  "sym can be a namespace, an alias to a namespace, or a java class."
  [sym]
  (let [aliases (ns-aliases *ns*)
        imports (ns-imports *ns*)]
    (cond
      (contains? imports sym) (important-class-methods (get imports sym))
      (class-exists? sym) (important-class-methods (resolve sym))
      (contains? aliases sym) (dir-fn (get aliases sym))
      :else (dir-fn sym))))

(defmacro full-dir
  "Prints a sorted directory of public vars in a namespace or an alias
  namespace, or the unique methods on a class (i.e. not methods
  inherited from Object)."
  [sym]
  `(doseq [v# (full-dir-fn '~sym)]
     (println v#)))

;; ==================== Meat

(def ubuntu-torrent "resources/ubuntu.torrent")

(defn read-torrent [torrent-file]
  (-> (io/input-stream torrent-file)
      (bc/bdecode)
      (update-in [:info :pieces] (constantly []))))


#_ (defn read-announce [torrent-dict]
     (let [url (:announce torrent-dict)]
       (slurp url)))
