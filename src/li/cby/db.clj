(ns li.cby.db
  (:require
    [clojure.java.jdbc :as jdbc]))

(defonce db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/database.db"})

(defn start! []
  (jdbc/db-do-commands
    db
    (jdbc/create-table-ddl
      :links
      [[:shortened "varchar(255)" :primary :key]
       [:url "varchar(255)"]
       [:created_at :datetime :default :current_timestamp]]
      {:conditional? true}))
  nil)

(defn create! [{:keys [shortened url]}]
  (->> ["INSERT INTO links (shortened, url) VALUES (?, ?)"
        shortened url]
       (jdbc/execute! db))
  nil)

(defn get-expanded [shortened]
  (->> ["SELECT * FROM links WHERE shortened = ?" shortened]
       (jdbc/query db)
       first
       :url))

(comment
  (start!)
  (jdbc/query db "pragma table_info(links)")
  (query db "SELECT * FROM links")
  (query db ["SELECT * FROM links WHERE shortened = ?" "asdf"])

  (create! {:shortened "asdf" :url "https://github.com/asdf-vm/asdf"})
  (create! {:shortened "lobs" :url "https://lobste.rs"})

  (get-expanded "does not exist")
  (get-expanded "asdf")
  (get-expanded "lobs")

  (jdbc/execute! db "DELETE FROM links")

  ;;
  )
