(ns li.cby.app
  (:require
    [clojure.core.match :refer [match]]
    [org.httpkit.server :as http]
    [li.cby.core :as core]))


;;; APP

(defn app [req]
  (match [(:request-method req) (:uri req)]

    [:get "/"]
    (core/home req)

    [:post "/"]
    (core/error {:message "DON'T DO THAT."})

    ;; Redirect to original URL (or 404).
    [:get _]
    (core/redirect req)

    ;; Create a new shortened URL.
    [:post _]
    (core/create req)

    [_ _]
    (core/error {:message "DON'T DO THAT."})))



;;; HTTP SERVER

(defonce stop-server! (atom nil))

(defn start! []
  (when (fn? @stop-server!)
    (@stop-server!))
  (reset! stop-server!
          (http/run-server app {:port 9002})))

(comment
  (start!))

(defn -main [& _]
  (start!))
