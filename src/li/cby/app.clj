(ns li.cby.app
  (:require
    [clojure.core.match :refer [match]]
    [org.httpkit.server :as http]
    [ring.middleware.defaults :as mid]
    [selmer.parser :as html]
    [li.cby.db :as db]
    [li.cby.core :as core])
  (:gen-class))


;;; APP

(defn app [req]
  (match [(:request-method req) (:uri req)]

    ;; Have to match specific URLs first. Start with the home page.
    [:get "/"]
    (core/home req)

    ;; POSTing to the home page creates a new link redirect.
    [:post "/"]
    (core/create req)

    ;; Redirect to original URL (or 404).
    [:get _]
    (core/redirect req)

    ;; Anything else is an error.
    [_ _]
    (core/error {:message "DON'T DO THAT."})))



;;; CONFIG

(defonce env
  {:env (keyword (or (System/getenv "ENV") "production"))
   :base-uri (or (System/getenv "BASE_URI") "https://cby.li")})

(when (not= :production env)
  (html/cache-off!))

(def defaults
  {:dev
   (-> mid/site-defaults
       (assoc :cookies false)
       (assoc :session false)
       (assoc-in [:security :anti-forgery] false))
   :production
   (-> mid/secure-site-defaults
       (assoc :cookies false)
       (assoc :session false)
       (assoc-in [:security :anti-forgery] false))})

(defn wrap-config [handler]
  (fn [req]
    (handler (assoc req :config (select-keys env [:base-uri])))))



;;; HTTP SERVER

(defonce stop-server! (atom nil))

(defn start! []
  (when (fn? @stop-server!)
    (@stop-server!))
  (let [ring-defaults (get defaults (:env env))
        middleware
        (fn [handler]
          (-> handler
              (wrap-config)
              (mid/wrap-defaults ring-defaults)))
        app (middleware app)]
    (reset!
      stop-server!
      (http/run-server app {:port 9002})))
  nil)

(comment
  (start!))

(defn -main [& _]
  (db/start!)
  (start!))
