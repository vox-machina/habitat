(ns voxmachina.habitat
  (:require [babashka.cli :as cli]
            [babashka.pods :as pods]))
            
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(def cli-options {:src {:coerce :string}
                  :dir {:default "." :coerce :string}
                  :help {:coerce :boolean}})

(defn -main [& args]
  (println "Habitat")
  (let [cfg (cli/parse-opts *command-line-args* {:spec cli-options})]
    (println "running watch on : " (:src cfg))
    (fw/watch (:src cfg) (fn [event] (prn event)) {:recursive true})
    (while true (Thread/sleep 1000))))
