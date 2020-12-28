Play 2.8, Synchronous Slick 5 
==================
[![Build Status](https://travis-ci.com/nemoo/play-slick-synchronous-example.svg?branch=master)](https://travis-ci.com/nemoo/play-slick-synchronous-example)

An example app using Play Framework 2.8, Slick 5

To use slick in the most simple, synchronous way, this project uses the great [blocking-slick api](https://github.com/takezoe/blocking-slick).     

* Most templates stop at hello world. This template shows you how you can structure your app into controllers and repositories.
* It shows how to model relationships between entities and perform basic operations on entities.
* It shows how to handle situations where various database operations have to be performed within a single transaction.
* The focus is on how to model your domain, leaving out authorization and ui libraries.
* Easy to read and understand
* Uses the beautiful slick 2 blocking api
* If you prefer asynchronous database access, have a look at https://github.com/nemoo/play-slick3-example

Repositories handle interactions with domain aggregates. All public methods are exposed as Futures. Internally, in some cases we need to compose various queries into one block that is carried out within a single transaction. In this case, the individual queries return DBIO query objects. A single public method runs those queries and exposes a Future to the client.


1. Install [JDK 11](https://adoptopenjdk.net/).
2. Install SBT
3. Run `sbt ~run` for continuous recompilation of the server app.

Done: [http://localhost:9000/](http://localhost:9000/)
