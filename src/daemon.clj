(ns daemon
  "Implements the Commons Daemon interface by using component to do
  the actual system start/stop grunt work"
  (:require [clojure.edn :as edn]
            [com.stuartsierra.component :as component]
            [system])
  (:gen-class :implements [org.apache.commons.daemon.Daemon]))

(def system nil)

(defn- load-config
  "Given a daemons context, loads the supplied configuration file and
  returns it as a clojure data structure"
  [daemon-context]
  (-> daemon-context
      .getArguments
      first
      slurp
      edn/read-string))

(defn -init
  "Initializes a new system given a commons daemon context. Expects
  that the first argument passed to the application is the config file
  path"
  [_ daemon-context]
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

;;----------------Command Line Usage---------------------

;; jsvc \
;; -user blah \
;; -out-file /var/log/blah/out.log \
;; -Xmx3072m \
;; -cp system.jar
;; daemon
;; config.edn
