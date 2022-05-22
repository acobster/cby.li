(ns li.cby.core
  (:require
    [clojure.java.jdbc :as jdbc]))

(defn home [req]
  {:body "HOME"
   :status 200})

(defn create [req]
  {:body "CREATE"
   :status 200})

(defn redirect [{:keys [uri]}]
  {:body (str "redirect to: " (subs uri 1))
   :status 200})

(defn error [{:keys [message status]}]
  {:body message
   :status (or status 400)})
