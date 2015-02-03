(ns communicate.sms
  "Implements the Nexmo SMS API: https://docs.nexmo.com/index.php/sms-api"
  (:require [clj-http.lite.client :refer [post put]]
            [clojure.java.io :refer [file]]
            [communicate.core :refer :all] ;; I'll want to trim this down
            [cheshire.core :refer [generate-string]]))
