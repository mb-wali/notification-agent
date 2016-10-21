(ns notification-agent.amqp
  (:use [slingshot.slingshot :only [try+]])
  (:require [cheshire.core :as cheshire]
            [clojure.tools.logging :as log]
            [langohr.basic :as lb]
            [langohr.channel :as lch]
            [langohr.consumers :as lc]
            [langohr.core :as rmq]
            [langohr.exchange :as le]
            [langohr.queue :as lq]
            [notification-agent.config :as config]
            [notification-agent.db :as db]))


(def local-channel (ref nil))

(defn channel
  ([]
   (deref local-channel))
  ([val]
   (dosync (ref-set local-channel val))))

(defn- declare-queue
  [channel {exchange-name :name} queue-cfg topics]
  (lq/declare channel (:name queue-cfg) (assoc queue-cfg :exclusive false))
  (doseq [key topics]
    (lq/bind channel (:name queue-cfg) exchange-name {:routing-key key})))

(defn- declare-exchange
  [channel {exchange-name :name :as exchange-cfg}]
  (le/topic channel exchange-name exchange-cfg))

(defn- message-router
  [handlers channel {:keys [delivery-tag routing-key] :as metadata} msg]
  (let [handler (get handlers routing-key)]
    (if-not (nil? handler)
      (handler channel metadata msg)
      (log/error (format "[amqp/message-router] [%s] [%s] unroutable" routing-key (String. msg))))))

(defn connect
  [exchange-cfg queue-cfg routing-keys]
  (let [channel (lch/open (rmq/connect {:uri (config/amqp-uri)}))]
    (log/info (format "[amqp/connect] [%s]" (config/amqp-uri)))
    (declare-exchange channel exchange-cfg)
    (declare-queue channel exchange-cfg queue-cfg routing-keys)
    channel))

(defn subscribe
  [channel queue-name handlers]
  (lc/blocking-subscribe channel queue-name (partial message-router handlers)))

(defn publish
  [channel routing-key msg msg-meta]
  (lb/publish channel (config/exchange-name) routing-key msg msg-meta))

(defn publish-msg
  [user msg]
  (try+
   (publish (channel)
            (str "notification." user)
            (cheshire/encode {:message msg
                              :total   (db/count-matching-messages user {:seen false})})
            {:content-type "application/json"})
   (catch Object _
     (log/error (:throwable &throw-context) "unable to publish message:" (cheshire/encode msg)))))

(defn publish-system-msg
  [msg]
  (try+
   (publish (channel) "system_message" msg {:content-type "application/json"})
   (catch Object _
     (log/error (:throwable &throw-context) "unable to publish system message:" (cheshire/encode msg)))))
