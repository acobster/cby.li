# cby.li

A dead-simple link shortener written in Clojure.

## Goals

* Fun.
* Minimal dependencies.
* Something I can self-host.
* Play with Fly.io.
* Nice reminders of recently created links!

## Deployment

For now, cby.li runs on [Fly.io](https://fly.io). In theory, it can run anywhere you can run Docker. Just set the right environment variables and you're good to go:

```
BASE_URL=https://cby.li
ENV=production
PORT=9002
```

The values noted above are the defaults.

To deploy, just do:

```sh
fly deploy
```

In short, the deployed app runs a Docker container that in turn runs the compiled Clojure app.

The app is compiled as an [uberjar](https://newbedev.com/what-is-an-uber-jar), i.e. the compiled clojure code and its runtime dependencies (including assets). The build process uses [tools.build](https://clojure.org/guides/tools_build) to build the uberjar. See Dockerfile and `build.clj` for details: both are extremely simple.

## Development

```sh
BASE_URI=http://localhost:9002 ENV=dev clojure -M:repl
```

## TODO

* Favicon
