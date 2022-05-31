FROM clojure:openjdk-11-slim-buster

WORKDIR /app

RUN mkdir db

COPY deps.edn /app/deps.edn
COPY build.clj /app/build.clj
COPY resources /app/resources
COPY src /app/src

RUN clojure -T:build uberjar

CMD ["java", "-jar", "target/cby.li.jar"]
