(defproject me.moocar/clojure-west-2015-talk "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.stuartsierra/component "0.2.3"]
                 [clj-http "1.1.0"]

                 ;; Logging
                 [org.slf4j/log4j-over-slf4j "1.7.12"]
                 [org.slf4j/jcl-over-slf4j "1.7.12"]
                 [org.slf4j/jul-to-slf4j "1.7.12"]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [ch.qos.logback/logback-classic "1.0.13"]

                 ;; Daemonizing
                 [commons-daemon "1.0.15"]])
