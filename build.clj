(ns build
  (:require [clojure.tools.build.api :as b]))

(def major-v 0)
(def minor-v 1)
(def version (format "%d.%d.%s" major-v minor-v (b/git-count-revs nil)))
(def version-file "version.edn")

(defn release
  "marks a habitat release - setting the version in version.edn"
  [_]
  (println "building...")
  (println "version: " version)
  (b/write-file {:path version-file :content {:habitat version}}))
