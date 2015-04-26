(ns system
  (:require [cassandra]
            [clj-http.client :as http]
            [clojure.pprint :refer [pprint]]
            [com.stuartsierra.component :as component]
            [push]))

;; -------Push-----------------------

(defprotocol SMS
  (send-sms [this phone-number text]
    "Should send the text to the given phone number via SMS"))

;; Service Component
(defrecord HTTPSMS [host port]
  SMS
  (send-sms [this phone-number text]
    (http/post (str "http://" host \: port "/SMS")
               {:form-params {:phone-number phone-number
                              :text text}})))

(defn new-http-sms
  "Creates a new SMS component that sends SMS via an http service"
  [config]
  (map->HTTPSMS (:sms config)))







;;-------------Uploader------------------

;; Composite Component
(defrecord Uploader [ ;; config. Required upon construction
                     sms-text
                     ;; dependencies. Will be inserted by component
                     ;; when the system starts
                     cassandra sms])

(defn new-uploader
  "Creates a new Receipt Uploader"
  [config]
  (component/using (map->Uploader (:uploader config))
    {:cassandra :receipt-cassandra
     :sms :sms}))

(defn upload-receipt
  "Performs business logic that should occur when a receipt is
  uploaded"
  [uploader receipt]
  (let [{:keys [cassandra sms sms-text]} uploader
        {:keys [phone-number]} receipt]
    (cassandra/insert cassandra receipt)
    (send-sms sms phone-number sms-text)))



;; ------------without component----------

(comment
  (defn old-upload-receipt
    "What our upload receipt function used to look like before we
  started using component. Yes, it is more aesthetically pleasing, but
  now all cassandra connections are global and singleton."
    [receipt]
    (cassandra/insert receipt)
    (send-sms (:phone-number receipt) sms-text)))























;; A bunch of other component examples. Just stubs

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

(defn make-config
  "Creates a default configuration map that is very similar to the edn
  that is saved on our production machines and passed to jsvc (see
  daemon namespace)"
  []
  {:sms {:host "localhost"
         :port 8081}
   :uploader {:sms-text "Struth, you got a receipt!"}
   :cassandra {:hosts ["1.1.1.1" "1.1.1.2"]}})






;;-------------System---------------------

(defn new-system
  "Creates a new system. This contains all the components required by
  the system we're trying to deploy. In this case, the end result is a
  service that can handle uploaded receipts, save them to cassandra
  and notify the customer via a push"
  [config]
  (component/system-map

   ;; Databases
   :receipt-cassandra (cassandra/new-cassandra config
                                               :receipt)
   :customer-cassandra (cassandra/new-cassandra config
                                                :customer)
   ;; business logic
   :customer-api (new-customer-api config)
   :uploader (new-uploader config)

   :store-redis (new-redis config :store)

   ;; services
   :receipt-service (new-receipt-service config)

   ;; Push
   :sms (new-http-sms config)
   :apple-push (push/new-apple-push config)
   :google-push (push/new-google-push config)))
