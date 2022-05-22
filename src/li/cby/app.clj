(ns li.cby.app
  (:require
    [clojure.core.match :refer [match]]
    [org.httpkit.server :as http]
    [ring.middleware.defaults :as mid]
    [li.cby.db :as db]
    [li.cby.core :as core]))


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



;;; HTTP SERVER

(defonce stop-server! (atom nil))

(defn start! []
  (when (fn? @stop-server!)
    (@stop-server!))
  (let [;; TODO environment-specific middleware
        middleware
        #(mid/wrap-defaults % (-> mid/site-defaults
                                  (assoc :cookies false)
                                  (assoc :session false)
                                  (assoc-in [:security :anti-forgery] false)))
        app (middleware app)]
    (reset!
      stop-server!
      (http/run-server app {:port 9002}))))

(comment
  (start!))

(defn -main [& _]
  (db/start!)
  (start!))
