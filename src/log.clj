(ns log
  (:import (org.slf4j.bridge SLF4JBridgeHandler)))

(defn bridge-jul->slf4j
  "Redirects all Java.util.logging logs to sl4fj"
  []
  (SLF4JBridgeHandler/removeHandlersForRootLogger)
  (SLF4JBridgeHandler/install))

(defn set-default-uncaught-exception-handler
  "Installs a default exception handler to log any exception which is
  neither caught by a try/catch nor captured as the result of a
  Future."
  [logger]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ thread throwable]
       (.error logger "Uncaught exception" throwable)))))
