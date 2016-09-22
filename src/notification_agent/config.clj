(ns notification-agent.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.config :as cc]))

(def ^:private props
  "A ref for storing the configuration properties."
  (ref nil))

(def ^:private config-valid
  "A ref for storing a configuration validity flag."
  (ref true))

(def ^:private configs
  "A ref for storing the symbols used to get configuration settings."
  (ref []))

(cc/defprop-optstr db-driver-class
  "The name of the JDBC driver to use."
  [props config-valid configs]
  "notificationagent.db.driver" "org.postgresql.Driver")

(cc/defprop-optstr db-subprotocol
  "The subprotocol to use when connecting to the database (e.g. postgresql)."
  [props config-valid configs]
  "notificationagent.db.subprotocol" "postgresql")

(cc/defprop-optstr db-host
  "The host name or IP address to use when connecting to the database."
  [props config-valid configs]
  "notificationagent.db.host" "dedb")

(cc/defprop-optstr db-port
  "The port number to use when connecting to the database."
  [props config-valid configs]
  "notificationagent.db.port" "5432")

(cc/defprop-optstr db-name
  "The name of the database to connect to."
  [props config-valid configs]
  "notificationagent.db.name" "notifications")

(cc/defprop-optstr db-user
  "The username to use when authenticating to the database."
  [props config-valid configs]
  "notificationagent.db.user" "de")

(cc/defprop-optstr db-password
  "The password to use when authenticating to the database."
  [props config-valid configs]
  "notificationagent.db.password" "notprod")

(cc/defprop-optboolean email-enabled
  "True if e-mail notifications are enabled."
  [props config-valid configs]
  "notificationagent.enable-email" true)

(cc/defprop-optstr email-url
  "The URL used to connect to the mail service."
  [props config-valid configs]
  "notificationagent.email-url" "http://iplant-email:60000")

(cc/defprop-optstr amqp-host
  "The name of the host where the AMQP broker is running."
  [props config-valid configs]
  "notificationagent.amqp.host" "rabbit")

(cc/defprop-optint amqp-port
  "The port to use when connecting to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.port" 5672)

(cc/defprop-optstr amqp-user
  "The username to use when authenticating to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.user" "guest")

(cc/defprop-optstr amqp-password
  "The password to use when authenticating to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.password" "guest")

(cc/defprop-optstr amqp-exchange-name
  "The name of the AMQP exchange."
  [props config-valid configs]
  "notificationagent.amqp.exchange.name" "de")

(cc/defprop-optstr amqp-exchange-vhost
  "The name of the vhost where the AMQP exchange resides."
  [props config-valid configs]
  "notificationagent.amqp.exchange.vhost" "/")

(cc/defprop-optstr amqp-exchange-type
  "The name of the AMQP exchange type."
  [props config-valid configs]
  "notificationagent.amqp.exchange.type" "topic")

(cc/defprop-optboolean amqp-exchange-durable
  "Indicates whether or not the AMQP exchange should be declared as durable."
  [props config-valid configs]
  "notificationagent.amqp.exchange.durable" true)

(cc/defprop-optboolean amqp-exchange-auto-delete
  "Indicates whether or not the AMQP exchange should be declared as auto-delete."
  [props config-valid configs]
  "notificationagent.amqp.exchange.auto-delete" false)

(cc/defprop-optint listen-port
  "The port to listen to for incoming connections."
  [props config-valid configs]
  "notificationagent.listen-port" 60000)

(defn- validate-config
  "Validates the configuration settings after they've been loaded."
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:type :clojure-commons.exception/invalid-cfg})))

(defn load-config-from-file
  "Loads the configuration settings from a file."
  [cfg-path]
  (cc/load-config-from-file cfg-path props)
  (cc/log-config props)
  (validate-config))
