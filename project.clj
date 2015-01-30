(defproject communicate "0.1.0-SNAPSHOT"
  :description "Communicate is a Clojure library for working with Nexmo"
  :url "https://github.com/ianhedoesit/communicate"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.0.1"] ;; I might want to change this to clj-http.lite
                 [cheshire "5.4.0"]
                 [environ "1.0.0"]])
