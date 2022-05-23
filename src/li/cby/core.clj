(ns li.cby.core
  (:require
    [selmer.parser :as html]
    [li.cby.db :as db]))

(defn home [{:keys [params config]}]
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
      (let [html (html/render-file
                   "html/home.html"
                   {:base-uri (str (:base-uri config) "/")})]
        {:status 200
         :headers {"content-type" "text/html"}
         :body html}))))

(defn create [{:keys [params config]}]
  (let [short (:shortened params)
        url (db/get-expanded short)
        link (str (:base-uri config) "/" short)]
    (if url
      {:status 400
       :headers {"content-type" "text/html"}
       :body (str link " already exists")}
      (do
        (db/create! params)
        (let [html (html/render-file
                     "html/created.html"
                     {:link (str (:base-uri config) "/" short)})]
          {:status 200
           :headers {"content-type" "text/html"}
           :body html})))))

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
