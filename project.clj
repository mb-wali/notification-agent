(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.cyverse/notification-agent "2.8.1-SNAPSHOT"
  :description "A web service for storing and forwarding notifications."
  :url "https://github.com/cyverse-de/notification-agent"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "notification-agent-standalone.jar"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.5.0"
                  :exclusions [[com.fasterxml.jackson.dataformat/jackson-dataformat-cbor]
                               [com.fasterxml.jackson.dataformat/jackson-dataformat-smile]
                               [com.fasterxml.jackson.core/jackson-annotations]
                               [com.fasterxml.jackson.core/jackson-databind]
                               [com.fasterxml.jackson.core/jackson-core]]]
                 [compojure "1.5.0"]
                 [org.cyverse/clojure-commons "2.8.0"]
                 [org.cyverse/kameleon "2.8.0"]
                 [org.cyverse/common-cli "2.8.0"]
                 [org.cyverse/service-logging "2.8.0"]
                 [org.cyverse/event-messages "0.0.1"]
                 [me.raynes/fs "1.4.6"]
                 [clj-http "2.0.0"]
                 [clj-time "0.11.0"]
                 [slingshot "0.12.2"]
                 [clojurewerkz/quartzite "2.0.0"]
                 [com.mchange/c3p0 "0.9.5.2"]
                 [com.novemberain/langohr "3.5.1"]
                 [korma "0.4.2"
                  :exclusions [c3p0]]]
  :eastwood {:exclude-namespaces [:test-paths]
             :linters [:wrong-arity :wrong-ns-form :wrong-pre-post :wrong-tag :misplaced-docstrings]}
  :plugins [[lein-ring "0.8.13"]
            [lein-marginalia "0.7.0"]
            [test2junit "1.2.2"]
            [jonase/eastwood "0.2.3"]]
  :ring {:handler notification-agent.core/app
         :init notification-agent.core/load-config-from-file
         :port 31320}
  :profiles {:dev {:resource-paths ["conf/test"]}}
  :extra-classpath-dirs ["conf/test"]
  :aot [notification-agent.core]
  :main notification-agent.core
  :uberjar-exclusions [#"(?i)[.]sf"]
  :jvm-opts ["-Dlogback.configurationFile=/etc/iplant/de/logging/notificationagent-logging.xml"])
