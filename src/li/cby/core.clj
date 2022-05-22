(ns li.cby.core
  (:require
    [li.cby.db :as db]))

(defn home [{:keys [params]}]
  (let [checking? (:shortened params)
        redirect (when checking?
                   (db/get-expanded (:shortened params)))]
    (cond
      (and checking? redirect)
      {:status 400
       :body redirect}

      checking?
      {:status 200
       :body ""}

      :else
      {:status 200
       :body "HOME"})))

(defn create [{:keys [params]}]
  (let [short (:shortened params)
        url (db/get-expanded short)
        ;; TODO get this from config
        link (str "http://localhost:9002/" short)]
    (if url
      {:status 400
       :body (str link " already exists")}
      (do
        (db/create! params)
        {:status 200
         :body link}))))

(defn redirect [{:keys [uri]}]
  (let [shortened (subs uri 1)
        expanded (db/get-expanded shortened)]
    (if expanded
      {:body ""
       :headers {"Location" expanded}
       :status 301}
      {:body (str "Not found: " shortened)
       :status 404})))

(defn error [{:keys [message status]}]
  {:body message
   :status (or status 400)})
