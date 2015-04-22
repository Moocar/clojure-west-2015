(ns cassandra
  (:require [com.stuartsierra.component :as component]))

;;-------------Cassandra----------------

(defn start-connection [hosts keyspace]
  (println "starting connection")
  (Object.))

(defn stop-connection [conn]
  (println "stopping connection"))

;; Stateful Component
(defrecord Cassandra [;; Static Configuration
                      hosts keyspace
                      ;; once started
                      conn]
  component/Lifecycle
  (start [this]
    (if conn
      this
      (assoc this :conn (start-connection hosts keyspace))))
  (stop [this]
    (if conn
      (do (stop-connection conn)
          (assoc this :conn nil))
      this)))

(defn new-cassandra [config keyspace]
  (let [hosts (get-in config [:cassandra :hosts])]
    (map->Cassandra {:hosts hosts
                     :keyspace keyspace})))

;;--------------------API----------------------

(defn insert [cassandra thing]
  (let [{:keys [conn]} cassandra]
    ;; Do some insertion stuff
    ))
