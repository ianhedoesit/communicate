(ns communicate.core
  (:require [clj-http.lite.client :as http]
            [cheshire.core :as json]))

(def nexmo-endpoint "https://rest.nexmo.com")

(comment
;(defn safe-parse
  

(defn api-call
  ([method end-point] (api-call method end-point nil nil))
  ([method end-point positional] (api-call method end-point positional nil))
  ([method end-point positional query]
   (let [query (or query {})
        ;; all-pages? (query :all-pages)
         exec-request-one (fn exec-request-one [req]
                            (safe-parse (http/request req)))
         exec-request (fn exec-request [req]
                        (let [resp (exec-request-one req)]
                          (if (-> resp meta :lines :next)
                            (let [new-req (update-req req (-> resp meta :links :next))]
                            (lazy-cat resp (exec-request new-req)))
                          resp)))]
   (exec-request req))))
)

(comment
(defn send
  [{:keys [to from message]}]
  (api-call :get "" nil nil))
)
;;(defn foo
;;  "I don't do a whole lot."
;;  [x]
;;  (println x "Hello, World!"))
