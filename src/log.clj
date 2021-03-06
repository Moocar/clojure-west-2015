(ns log
  "Interesting functions you shoul call when your app starts to ensure
  that logging works sanely"
  (:import (org.slf4j.bridge SLF4JBridgeHandler)))

(defn bridge-jul->slf4j
  "Redirects all Java.util.logging logs to sl4fj. Should be called
  upon application startup"
  []
  (SLF4JBridgeHandler/removeHandlersForRootLogger)
  (SLF4JBridgeHandler/install))

(defn set-default-uncaught-exception-handler
  "Installs a default exception handler to log any exception which is
  neither caught by a try/catch nor captured as the result of a
  Future. Should be called upon application startup"
  [logger]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ thread throwable]
       (.error logger "Uncaught exception" throwable)))))
