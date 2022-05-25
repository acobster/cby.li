(ns li.cby.random
  (:require
    [clojure.string :as string]))

(defonce alphabet
  (let [alpha "abcdefghijklmnopqrstuvwxyz"]
    (str alpha (string/upper-case alpha))))
(defonce with-special-chars
  (str alphabet "_-"))

(defn rand-slug []
  (str
    (rand-nth alphabet)
    (rand-nth with-special-chars)
    (rand-nth with-special-chars)
    (rand-nth with-special-chars)
    (rand-nth alphabet)))

(comment
  (rand-slug))
