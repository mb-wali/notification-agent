(ns notification-agent.core-test
  (:use clojure.test
        notification-agent.core)
  (:require [notification-agent.config :as config]))

(deftest test-configuration-defaults
  (require 'notification-agent.config :reload)
  (config/load-config-from-file "conf/test/empty.properties")
  (testing "default configuration settings"
    (is (= (config/db-subprotocol) "postgresql"))
    (is (= (config/db-host) "dedb"))
    (is (= (config/db-port) "5432"))
    (is (= (config/db-name) "notifications"))
    (is (= (config/db-user) "de"))
    (is (= (config/db-password) "notprod"))
    (is (true? (config/email-enabled)))
    (is (= (config/email-url) "http://iplant-email:60000"))
    (is (= (config/amqp-uri) "amqp://guest:guestPW@localhost:5672"))
    (is (= (config/exchange-name) "de"))
    (is (true? (config/exchange-durable?)))
    (is (false? (config/exchange-auto-delete?)))
    (is (= (config/queue-name) "events.notification-agent.queue"))
    (is (true? (config/queue-durable?)))
    (is (false? (config/queue-auto-delete?)))
    (is (= (config/listen-port) 60000))))
