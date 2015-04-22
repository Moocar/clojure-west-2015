(ns daemon
  (:require [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [system])
  (:gen-class :implements [org.apache.commons.daemon.Daemon]))

(def system nil)

(defn- load-config [daemon-context]
  (-> daemon-context
      .getArguments
      first
      slurp
      edn/read-string))

(defn -init [_ daemon-context]
  (->> daemon-context
       load-config
       system/new-system
       constantly
       (alter-var-root #'system)))

(defn -start [_]
  (alter-var-root #'system component/start))

(defn -stop [_]
  (alter-var-root #'system component/stop))

(defn -destroy [_]
  (alter-var-root #'system (constantly nil)))

;;----------------Usage---------------------

;; jsvc \
;; -user blah \
;; -out-file /var/log/blah/out.log \
;; -Xmx3072m \
;; -cp system.jar
;; daemon
;; config.edn
