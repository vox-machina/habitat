(ns voxmachina.habitat
  (:require [babashka.cli :as cli]
            [babashka.pods :as pods]
            [clojure.edn :refer [read-string]]
            [clojure.string :refer [ends-with? includes?]]
            [clojure.tools.logging :as log]
            [org.httpkit.server :as srv]
            [taoensso.timbre :refer [info merge-config!]]
            [taoensso.timbre.appenders.core :refer [spit-appender]]
            [aero.core :refer [read-config]]))
            
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(def cfg (read-config "config.edn"))

(def cli-options {:src {:coerce :string}
                  :dir {:default "." :coerce :string}
                  :help {:coerce :boolean}})

(merge-config! {:appenders {:spit (spit-appender {:fname "logs/habitat.log"})}})

(defn event-valid? [{:keys [path] :as event}]
  (if (or (ends-with? path ".lock") (ends-with? path ".log") (some #{path} #{"#" "/.git" "target/classes"}) (not (includes? path "src/"))) false true))

(defn queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([capacity ^PersistentQueue buf x & xs]
    (if xs
      (recur capacity (queue capacity buf x) (first xs) (next xs))
      (let [b (if (= capacity (count buf))(pop buf) buf)]
        (conj b x)))))

(def q (atom (queue)))

(def q-cnt (:queue-size cfg))

(defn app [{:keys [:request-method :uri]}]
  (case [request-method uri]
    [:get "/"] {:body "Welcome!" :status 200}
    [:get "/queue"] {:body {:queue (seq @q) :queue-size (count @q)} :status 200 :headers {"Content-Type" "application/edn"}}))

(defn -main [& args]
  (let [cfg (cli/parse-opts *command-line-args* {:spec cli-options})
        v (:habitat (read-string (slurp "version.edn")))]
    (info {:main/start {:data (str "starting habitat v" v) :metadata {:habitat/version v}}})
    (info {:filesystem/watch {:data (:src cfg) :metadata {:habitat/version v}}})
    (fw/watch (:src cfg) (fn [{:keys [path] :as event}]
                           (when (event-valid? event)
                             (let [inf {:filesystem/event {:data event :metadata {:habitat/version v}}}]
                               (info inf)
                               (reset! q (queue q-cnt @q inf))))) {:recursive true})
    (srv/run-server app {:port (or (:port cfg) 8080)})
    @(promise)))
