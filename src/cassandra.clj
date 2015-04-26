(ns cassandra
  (:require [com.stuartsierra.component :as component]))

;;-------------Cassandra----------------

(defn start-connection
  "Wwould normally create a Cassandra connection object given the host
  and keyspace configuration. But within this demo just returns a new
  instance of Object."
  [hosts keyspace]
  (println "starting connection")
  (Object.))

(defn stop-connection
  "Would normally stop the Cassandra connection. But within this demo,
  does nothing"
  [conn]
  (println "stopping connection"))

;; Stateful Component
(defrecord Cassandra [;; Static Configuration. Should be added upon
                      ;; construction
                      hosts keyspace
                      ;; Database connection object. Should be added
                      ;; in start
                      conn]
  component/Lifecycle
  (start [this]
    ;; Check for truthy conn is to ensure that if the component has
    ;; already been started, it is returned unchanged. Thus ensuring
    ;; idempotent operations
    (if conn
      this
      (assoc this :conn (start-connection hosts keyspace))))
  (stop [this]
    (if conn
      (do (stop-connection conn)
          (assoc this :conn nil))
      this)))

(defn new-cassandra
  "Creates a new Cassandra Component"
  [config keyspace]
  (let [hosts (get-in config [:cassandra :hosts])]
    (map->Cassandra {:hosts hosts
                     :keyspace keyspace})))

;;--------------------API----------------------

(defn insert
  "Example of a public API function that uses the Cassandra component"
  [cassandra thing]
  (let [{:keys [conn]} cassandra]
    ;; Do some insertion stuff
    ))
