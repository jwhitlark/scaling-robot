(ns user
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.reflect :as reflect :refer [reflect]]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.repl :refer :all]
            [bencode.core :as bc]
            [bencode.metainfo.reader :as mr]
            [clojure.walk :as walk]
            )
  (:import [java.net URLEncoder])
  )

;; ==================== helpers

;; (defn alias-dir-fn [nsname]
;;   (-> (ns-aliases *ns*)
;;       (get nsname)
;;       (or nsname)
;;       dir-fn))


;; (defmacro alias-dir
;;   "prints a sorted directory of public vars in a namespace or an alias
;;   namespace."
;;   [nsname]
;;   `(doseq [v# (alias-dir-fn '~nsname)]
;;      (println v#)))

;; (defn class-exists? [c]
;;   (reflect/resolve-class (.getcontextclassloader (Thread/currentthread)) c))

;; (defn class-methods [c]
;;   (->> (reflect c)
;;        :members
;;        (filter #(:public (:flags %)))
;;        (map :name)
;;        (into #{})))

;; (defn important-class-methods [c]
;;   (->> (class-methods c)
;;        (#(set/difference % (class-methods java.lang.object)))
;;        sort))

;; (defn full-dir-fn
;;   "sym can be a namespace, an alias to a namespace, or a java class."
;;   [sym]
;;   (let [aliases (ns-aliases *ns*)
;;         imports (ns-imports *ns*)]
;;     (cond
;;       (contains? imports sym) (important-class-methods (get imports sym))
;;       (class-exists? sym) (important-class-methods (resolve sym))
;;       (contains? aliases sym) (dir-fn (get aliases sym))
;;       :else (dir-fn sym))))

;; (defmacro full-dir
;;   "prints a sorted directory of public vars in a namespace or an alias
;;   namespace, or the unique methods on a class (i.e. not methods
;;   inherited from object)."
;;   [sym]
;;   `(doseq [v# (full-dir-fn '~sym)]
;;      (println v#)))

;; ==================== state & samples

(def ubuntu-torrent "resources/ubuntu.torrent")

;; ==================== meat

(def url-enc #(URLEncoder/encode % "utf-8"))

(defn read-torrent [torrent-file]
  (mr/parse-metainfo (io/input-stream torrent-file)))

(defn make-params [metainfo]
  (->> {:info_hash  (mr/torrent-info-hash-str metainfo)
        :peer_id "AAAAAAAAAAAAAAAAAAAA"
        :port "6886"
        :uploaded "0"
        :downloaded "0"
        :left (str (mr/torrent-size metainfo))
        :compact "1"
        :event "started"}
       walk/stringify-keys
       (map #(format "%s=%s" (first %) (second %)))
       (str/join "&")))

(defn read-tracker-response [metainfo]
  (let [url (get metainfo "announce")
        params (make-params metainfo)
        full-url (str url "?" params)]
    (slurp full-url)))

#_ (-> ubuntu-torrent
       read-torrent
       make-params
       pp/pprint)
