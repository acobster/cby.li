(ns li.cby.core
  (:require
    [li.cby.db :as db]))

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
