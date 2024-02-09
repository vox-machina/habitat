(ns voxmachina.habitat
  (:require [babashka.cli :as cli]
            [babashka.pods :as pods]
            [clojure.edn :refer [read-string]]
            [clojure.string :refer [ends-with? includes?]]
            [clojure.tools.logging :as log]
            [taoensso.timbre :refer [info merge-config!]]
            [taoensso.timbre.appenders.core :refer [spit-appender]]))
            
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(def cli-options {:src {:coerce :string}
                  :dir {:default "." :coerce :string}
                  :help {:coerce :boolean}})

(merge-config! {:appenders {:spit (spit-appender {:fname "logs/habitat.log"})}})

;; REVIEW: will need to set up more exclusions to avoid logging trivia
(defn event-valid? [{:keys [path] :as event}]
  (if (or (ends-with? path ".lock") (ends-with? path ".log") (includes? path "#") (includes? path "/.git")) false true))

(defn -main [& args]
  (let [cfg (cli/parse-opts *command-line-args* {:spec cli-options})
        v (:habitat (read-string (slurp "version.edn")))]
    (info {:main/start {:data (str "starting habitat v" v) :metadata {:habitat/version v}}})
    (info {:filesystem/watch {:data (:src cfg) :metadata {:habitat/version v}}})
    (fw/watch (:src cfg) (fn [{:keys [path] :as event}]
                           (when (event-valid? event)
                             (info {:filesystem/event {:data event :metadata {:habitat/version v}}}))) {:recursive true})
    (while true (Thread/sleep 1000))))
