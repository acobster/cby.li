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
      [[:slug "varchar(255)" :primary :key "not null"]
       [:url "varchar(255)" "not null"]
       [:created_at :datetime :default :current_timestamp]]
      {:conditional? true}))
  nil)

(defn create! [{:keys [slug url]}]
  (->> ["INSERT INTO links (slug, url) VALUES (?, ?)"
        slug url]
       (jdbc/execute! db))
  nil)

(defn get-expanded [slug]
  (->> ["SELECT * FROM links WHERE slug = ?" slug]
       (jdbc/query db)
       first
       :url))

(defn get-recent []
  (jdbc/query
    db
    ["SELECT * FROM links ORDER BY created_at DESC LIMIT 10"]))

(comment
  (start!)
  (get-recent)
  (jdbc/query db "pragma table_info(links)")
  (jdbc/query db "SELECT * FROM links")
  (jdbc/query db ["SELECT * FROM links WHERE slug = ?" "asdf"])

  (create! {:slug "asdf" :url "https://github.com/asdf-vm/asdf"})
  (create! {:slug "ls" :url "https://lobste.rs"})

  (get-expanded "does not exist")
  (get-expanded "asdf")
  (get-expanded "ls")

  (jdbc/execute! db "DELETE FROM links")
  (jdbc/execute! db "DELETE FROM links WHERE slug = 'false'")
  (jdbc/execute! db "DELETE FROM links WHERE slug = 'asdf'")

  ;;
  )
