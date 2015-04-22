(ns system
  (:require [cassandra]
            [clj-http.client :as http]
            [clojure.pprint :refer [pprint]]
            [com.stuartsierra.component :as component]
            [push]))

;; -------Push-----------------------

(defprotocol SMS
  (send-sms [this phone-number text]))

;; Service Component
(defrecord HTTPSMS [host port]
  SMS
  (send-sms [this phone-number text]
    (http/post (str "http://" host \: port "/SMS")
               {:form-params {:phone-number phone-number
                              :text text}})))

(defn new-http-sms [config]
  (map->HTTPSMS (:sms config)))







;;-------------Uploader------------------

;; Composite Component
(defrecord Uploader [sms-text ;; config
                     cassandra sms ;; dependencies
                     ])

(defn new-uploader [config]
  (component/using (map->Uploader (:uploader config))
    {:cassandra :receipt-cassandra
     :sms :sms}))

(defn upload-receipt
  [uploader receipt]
  (let [{:keys [cassandra sms sms-text]} uploader
        {:keys [phone-number]} receipt]
    (cassandra/insert cassandra receipt)
    (send-sms sms phone-number sms-text)))



;; ------------without component----------

(comment
  (defn old-upload-receipt
    [receipt]
    (cassandra/insert receipt)
    (send-sms (:phone-number receipt) sms-text)))

























(defrecord Redis [host db-name])
(defn new-redis [config db-name]
  (map->Redis {:host (get-in config [:redis :host])
               :db-name db-name}))

(defrecord CustomerAPI [cassandra redis])
(defn new-customer-api [config]
  (component/using (map->CustomerAPI {})
    {:cassandra :customer-cassandra
     :redis :store-redis}))

(defrecord ReceiptService [port cassandra])
(defn new-receipt-service [config]
  (component/using
    (map->ReceiptService (:receipt-service config))
    {:cassandra :receipt-cassandra}))

(defn make-config []
  {:sms {:host "localhost"
         :port 8081}
   :uploader {:sms-text "Struth, you got a receipt!"}
   :cassandra {:hosts ["1.1.1.1" "1.1.1.2"]}})






;;-------------System---------------------

(defn new-system [config]
  (component/system-map

   ;; Databases
   :receipt-cassandra (cassandra/new-cassandra config
                                               :receipt)
   :customer-cassandra (cassandra/new-cassandra config
                                                :customer)
   :store-redis (new-redis config :store)

   ;; business logic
   :customer-api (new-customer-api config)
   :uploader (new-uploader config)

   ;; services
   :receipt-service (new-receipt-service config)

   ;; Push
   :sms (new-http-sms config)
   :apple-push (push/new-apple-push config)
   :google-push (push/new-google-push config)))
