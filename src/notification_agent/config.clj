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

(cc/defprop-optstr amqp-uri
  "The uri used to connect to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.uri" "amqp://guest:guestPW@localhost:5672")

(cc/defprop-optstr exchange-name
  "The name of the AMQP exchange."
  [props config-valid configs]
  "notificationagent.amqp.exchange.name" "de")

(cc/defprop-optstr exchange-type
  "The name of the AMQP exchange type."
  [props config-valid configs]
  "notificationagent.amqp.exchange.type" "topic")

(cc/defprop-optboolean exchange-durable?
  "Indicates whether or not the AMQP exchange should be declared as durable."
  [props config-valid configs]
  "notificationagent.amqp.exchange.durable" true)

(cc/defprop-optboolean exchange-auto-delete?
  "Indicates whether or not the AMQP exchange should be declared as auto-delete."
  [props config-valid configs]
  "notificationagent.amqp.exchange.auto-delete" false)

(cc/defprop-optstr queue-name
  "The name of the AMQP queue attached to the exchange."
  [props config-valid configs]
  "notificationagent.amqp.queue.name" "events.notification-agent.queue")

(cc/defprop-optboolean queue-durable?
  "Whether or not the queue is durable."
  [props config-valid configs]
  "notificationagent.amqp.queue.durable" true)

(cc/defprop-optboolean queue-auto-delete?
  "Whether or not the queue is automatically deleted."
  [props config-valid configs]
  "notificationagent.amqp.queue.auto-delete" false)

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
