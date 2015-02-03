(ns communicate.nexmo.developer
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
           ; [communicate.core :as communicate]
            )) ;; I don't know if I'll need this

(def nexmo-endpoint "https://rest.nexmo.com")

(defn account-get-balance
  "Retrieve current account balance (in Euro)."
  [{:keys [api-key api-secret data-format]
    :or {data-format :json}
    :as options}]
  (let [resp (http/get
              (str nexmo-endpoint
                   "/account/get-balance?api_key=" api-key
                   \& "api_secret=" api-secret)
              {:accept data-format})]
    (-> (:body resp)
        (json/decode)
        (get "value"))))

(defn account-pricing
  "Retrieve outbound pricing for a given country or international prefix.
   Currently returns the request as a Clojure map. I don't know what else to return."
  [{:keys [api-key api-secret data-format country prefix]
    :or {data-format :json}
    :as options}]
  (let [resp (http/get
              (str nexmo-endpoint
                   "/account/get-"
                   (when prefix "prefix-")
                   "pricing/outbound?api_key=" api-key
                   "&api_secret=" api-secret
                   (if country
                     (str "&country=" country)
                     (str "&prefix=" prefix)))
              {:accept data-format})]
    (json/decode (:body resp))))

(defn account-settings
  "Update account settings."
  [{:keys [api-key api-secret data-format new-secret mo-call-back-url dr-call-back-url]
    :or {data-format :json}
    :as options}]
  (let [resp (http/post
              (str nexmo-endpoint
                   "/account/settings?api_key=" api-key
                   "&api_secret=" api-secret
                   "&newSecret=" new-secret
                   "&moCallBackUrl=" mo-call-back-url
                   "&drCallBackUrl=" dr-call-back-url)
              {:accept data-format
               :headers {"Content-Type" "application/x-www-form-urlencoded"}})]
    (json/decode (:body resp))))

(defn account-top-up
  "Top-up account if you have turned on the 'auto-reload' feature for Nexmo.
   The top-up amount is the value associated with the 'auto-reload' transaction.
   Currently untested."
  [{:keys [api-key api-secret trx]
    :as options}]
  (let [resp (http/get
              (str nexmo-endpoint
                   "/account/top-up?api_key=" api-key
                   "&api_secret=" api-secret
                   "&trx=" trx))]
    (:status (json/decode (:body resp)))))

(defn account-numbers
  "Get all inbound numbers associated with your Nexmo account."
  [{:keys [api-key api-secret data-format index size pattern search-pattern]
    :or {data-format :json}
    :as options}]
  (let [resp (http/get
              (str nexmo-endpoint
                   "/account/numbers?api_key" api-key
                   "&api_secret=" api-secret
                   (when index
                     (str "&index=" index))
                   (when size
                     (str "&size=" size))
                   (when pattern
                     (str "&pattern" pattern))
                   (when search-pattern
                     (str "&search_pattern=" search-pattern)))
              {:accept data-format})]
    (json/decode (:body resp))))

(defn number-search
  "Get available inbound numbers for a given country."
  [{:keys [api-key api-secret data-format country pattern search-pattern features index size]
    :or {data-format :json}
    :as options}]
  (if country
    (let [resp (http/get
                (str nexmo-endpoint
                     "/number/search?api_key" api-key
                     "&api_secret=" api-secret
                     "&country=" country
                     (when pattern
                       (str "&pattern=" pattern))
                     (when search-pattern
                       (str "&search_pattern" search-pattern))
                     (when features
                       (str "&features=" features))
                     (when index
                       (str "&index=" index))
                     (when size
                       (str "&size=" size)))
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. ":country is required for communicate.nexmo.developer/number-search"))))

(defn number-buy
  "Purchase a given inbound number."
  [{:keys [api-key api-secret country msisdn]
    :as options}]
  (if (and country msisdn)
    (let [resp (http/post
                (str nexmo-endpoint
                     "/number/buy?api_key=" api-key
                     "&api_secret=" api-secret
                     "&country=" country
                     "&msisdn=" msisdn)
                {:headers {"Content-Type" "application/x-www-form-urlencoded"}})]
      (:status resp))
    (throw (Exception. "Both :country and :msisdn are required for communicate.nexmo.developer/number-buy"))))

(defn number-cancel
  "Cancel a given inbound number subscription."
  [{:keys [api-key api-secret country msisdn]
    :as options}]
  (if (and country msisdn)
    (let [resp (http/post
                (str nexmo-endpoint
                     "/number/cancel?api_key=" api-key
                     "&api_secret=" api-secret
                     "&country=" country
                     "&msisdn=" msisdn)
                {:headers {"Content-Type" "application/x-www-form-urlencoded"}})]
      (:status resp))
    (throw (Exception. "Both :country and :msisdn are required for communicate.nexmo.developer/number-cancel"))))

(defn number-update
  "Update your number callback."
  [{:keys [api-key api-secret country msisdn mo-http-url mo-smpp-sys-type voice-callback-type voice-callback-value voice-status-callback]
    :as options}]
  (if (and country msisdn)
    (let [resp (http/post
                (str nexmo-endpoint
                     "/number/update?api_key=" api-key
                     "&api_secret=" api-secret
                     "&country=" country
                     "&msisdn=" msisdn
                     (when mo-http-url
                       (str "&moHttpUrl=" mo-http-url))
                     (when mo-smpp-sys-type
                       (str "&moSmppSysType=" mo-smpp-sys-type))
                     (when voice-callback-type
                       (str "&voiceCallbackType=" voice-callback-type))
                     (when voice-callback-value
                       (str "&voiceCallbackValue=" voice-callback-value))
                     (when voice-status-callback
                       (str "&voiceStatusCallback=" voice-status-callback)))
                {:headers {"Content-Type" "application/x-www-form-urlencoded"}})]
      (:status resp))
    (throw (Exception. "Both :country and :msisdn are required for communicate.nexmo.developer/number-update"))))

(defn search-message
  "Search a previously sent message for a given message id."
  [{:keys [api-key api-secret data-format id]
    :or {data-format :json}
    :as options}]
  (if id
    (let [resp (http/get
                (str nexmo-endpoint
                     "/search/message?api_key=" api-key
                     "&api_secret=" api-secret
                     "&id=" id)
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. ":id is required for communicate.nexmo.developer/search-message"))))

(defn search-messages
  "Search sent messages."
  [{:keys [api-key api-secret data-format ids date to]
    :or {data-format :json}
    :as options}]
  (if (or (and ids (< (count ids) 11)) (and date to))
    (let [resp (http/get
                (str nexmo-endpoint
                     "/search/messages?api_key=" api-key
                     "&api_secret=" api-secret
                     (if ids
                       (str \& (clojure.string/join \& (map #(str "ids=" %) ids)))
                       (str "&date=" date
                            "&to=" to)))
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. "Either :id or :date and :to are required for communicate.nexmo.developer/search-messages"))))

(defn search-rejections
  "Search rejected messages."
  [{:keys [api-key api-secret data-format date to]
    :or {data-format :json}
    :as options}]
  (if date
    (let [resp (http/get
                (str nexmo-endpoint
                     "/search/rejections?api_key=" api-key
                     "&api_secret=" api-secret
                     "&date=" date
                     (when to
                       (str "&to=" to)))
                {:accept data-format})]
      (json/decode (:body resp)))
    (throw (Exception. ":date is required for communicate.nexmo.developer/search-rejections"))))
