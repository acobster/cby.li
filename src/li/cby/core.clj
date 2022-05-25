(ns li.cby.core
  (:require
    [selmer.parser :as html]
    [li.cby.db :as db]))

(defn home [{:keys [params config]}]
  (let [checking? (:slug params)
        redirect (when checking?
                   (db/get-expanded (:slug params)))]
    (cond
      (and checking? redirect)
      {:status 400
       :headers {"content-type" "text/html"}
       :body redirect}

      checking?
      {:status 200
       :headers {"content-type" "text/html"}
       :body "OK"}

      :else
      (let [recent (db/get-recent)
            html (html/render-file
                   "html/home.html"
                   {:base-uri (str (:base-uri config) "/")
                    :recent recent})]
        {:status 200
         :headers {"content-type" "text/html"}
         :body html}))))

(defn create [{:keys [params config]}]
  (let [short (:slug params)
        url (db/get-expanded short)
        link (str (:base-uri config) "/" short)]
    (if url
      (let [html (html/render-file
                   "html/home.html"
                   {:error (str link " already exists!")
                    :url (:url params)
                    :base-uri (str (:base-uri config) "/")})]
        {:status 400
         :headers {"content-type" "text/html"}
         :body html})
      (do
        (db/create! params)
        (let [html (html/render-file
                     "html/created.html"
                     {:link link})]
          {:status 200
           :headers {"content-type" "text/html"}
           :body html})))))

(defn redirect [{:keys [uri]}]
  (let [slug (subs uri 1)
        expanded (db/get-expanded slug)]
    (if expanded
      {:body ""
       :headers {"Location" expanded}
       :status 301}
      {:body (str "Not found: " slug)
       :status 404})))

(defn error [{:keys [message status]}]
  {:body message
   :status (or status 400)})
