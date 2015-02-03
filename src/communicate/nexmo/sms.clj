(ns communicate.nexmo.sms
  (:refer-clojure :exclude [send keyword])
  (:require [clj-http.lite.client :as http]
            [cheshire.core :as json]
            [communicate.nexmo.core :refer [endpoint]]
            [cemerick.url :refer [url-encode]]))

(def sms-endpoint (endpoint "/sms"))
(def su-endpoint (endpoint "/sc/us"))

(comment
  this will need to be much more complex to support everything nexmo offers,
  but for now it'sjust a basic SMS sending function.)
(defn send
  "Sends an SMS message from from to to."
  [{:keys [api-key api-secret data-format from to text]
    :or {data-format :json}
    :as options}]
  (if (and from to text)
    (let [resp (http/get
                (str sms-endpoint \/ (name data-format)
                     "?api_key=" api-key
                     "&api_secret=" api-secret
                     "&from=" (url-encode from)
                     "&to=" (url-encode to)
                     "&text=" (url-encode text))
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. ":from, :to, and :text are required for communicate.nexmo.sms/send"))))

(defn two-factor-authentication
  "Sends an SMS message as a template (chosen in Nexmo dashboard) for use with two-factor authentication."
  [{:keys [api-key api-secret data-format to pin]
    :or {data-format :json}
    :as options}]
  (if (and to pin)
    (let [resp (http/get
                (str su-endpoint "/2fa/" (name data-format)
                     "?api_key=" api-key
                     "&api_secret=" api-secret
                     "&to=" to
                     "&pin=" pin)
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. ":to and :pin are required for communicate.nexmo.sms/two-factor-authentication"))))

(defn keyword-marketing
  "Sends an SMS message which expects a reply based on keywords."
  [{:keys [api-key api-secret data-format from keyword to text]
    :or {data-format :json}
    :as options}]
  (if (and from keyword to text)
    (let [resp (http/get
                (str su-endpoint "/marketing/" (name data-format)
                     "?api_key=" api-key
                     "&api_secret=" api-secret
                     "&from=" from
                     "&keyword=" (url-encode keyword)
                     "&to=" to
                     "&text=" (url-encode text))
                {:accept :json})]
      (json/decode (:body resp)))
    (throw (Exception. ":from, :keyword, :to, and :text are required for communicate.nexmo.sms/keyword-marketing"))))
