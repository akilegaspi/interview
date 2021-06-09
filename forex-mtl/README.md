#Running the forex application
---
Make sure you first have `sbt`, `docker` and `docker-compose` installed
When you have both of these installed, run `docker-compose` to bring up the OneFrame API

After having the OneFrame API up, just execute `sbt run` and access the API via `localhost:8080`

Design Decisions
---
Being limited by the amount of times I can do a request against hte OneFrame API, I decided to make the application do a scheduled request against the OneFrame API and cache all of the data in-memory using `Ref` from cats-effect so that the data is still highly-available during a scheduled update.
