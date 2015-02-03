(ns communicate.nexmo.core)

(defn endpoint [& opts] (apply str "https://rest.nexmo.com" opts))
