(ns li.cby.core
  (:require
    [clojure.string :as string]
    [selmer.filters :as sf]
    [selmer.parser :as html]
    [li.cby.db :as db]
    [li.cby.random :as random])
  (:import
    [java.net URLEncoder]))

(defn- url-encode [s]
  (URLEncoder/encode s))

(sf/add-filter! :url-encode url-encode)

(comment
  (URLEncoder/encode "encode this plz?")
  (url-encode "this too maybe?")
  (html/render "{{s|url-encode}}" {:s "?encoded?"}))

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

(defn- valid-url? [url]
  (re-matches
    #"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
    url))

(defn- generate-slug []
  (loop [slug (random/rand-slug)]
    (if (db/get-expanded slug)
      (recur (random/rand-slug))
      slug)))

(defn- slug->link [config slug]
  (str (:base-uri config) "/" slug))

(defn create [{:keys [params config]}]
  (let [{:keys [url slug]} params
        existing (and (seq slug) (db/get-expanded slug))]
    (cond
      (not (valid-url? url))
      (let [html (html/render-file
                   "html/home.html"
                   {:error "Invalid URL"
                    :url url
                    :base-uri (str (:base-uri config))})]
        {:status 400
         :headers {"content-type" "text/html"}
         :body html})

      existing
      (let [html (html/render-file
                   "html/home.html"
                   {:error (str (slug->link config slug) " already exists!")
                    :url url
                    :base-uri (str (:base-uri config) "/")})]
        {:status 400
         :headers {"content-type" "text/html"}
         :body html})

      :else
      (let [slug (if (seq slug) slug (generate-slug))]
        (db/create! {:url url :slug slug})
        (let [html (html/render-file
                     "html/created.html"
                     {:link (slug->link config slug)})]
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
