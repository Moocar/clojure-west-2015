(ns system-test
  (:require [clojure.core.async :as async :refer [<!!]]
            [clojure.test :refer [deftest is run-tests]]
            [com.stuartsierra.component :as component]
            [system]))

;;----------------Mocks---------------------

(defrecord ChanSMS [ch]
  system/SMS
  (send-sms [this phone-number text]
    (async/put! ch {:phone-number phone-number
                    :text text})))

(defn new-chan-sms
  "Creates a new SMS sender that simply puts all SMS
  requests onto the supplied channel"
  [ch]
  (map->ChanSMS {:ch ch}))

;;-----------------Simple Test--------------------

(deftest t-receipt-upload-simple
  (let [sms-text "Booyah!"
        config {:uploader {:sms-text sms-text}}
        sms-ch (async/chan 1)
        sms (new-chan-sms sms-ch)
        uploader (assoc (system/new-uploader config)
                        :sms sms)
        phone-number "1111111111"
        receipt {:phone-number phone-number}]
    (system/upload-receipt uploader receipt)
    (is (= (<!! sms-ch)
           {:phone-number phone-number
            :text sms-text})
        "should send sms to receipt phone number")))

;;--------------Full System Tests---------------

(defn push-mock-system [config]
  (component/system-map
   :sms (new-chan-sms (async/chan 1))
   :apple-push {}
   :google-push {}))

(deftest t-receipt-upload-full-system
  (let [config (system/make-config)
        phone-number "1112223333"
        system (-> (system/new-system config)
                   (merge (push-mock-system config))
                   (component/start))
        sms-ch (:ch (:sms system))
        receipt {:phone-number phone-number}
        sms-text (get-in system [:uploader :sms-text])]
    (system/upload-receipt (:uploader system) receipt)
    (is (= (<!! sms-ch)
           {:phone-number phone-number
            :text sms-text})
        "should send sms to receipt phone number")))
