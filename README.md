Code examples that I walked through in my Clojure/West 2015 talk
["Clojure at Scale"](https://www.youtube.com/watch?v=av9Xi6CNqq4).

This is a shell of an application that looks very similar to how we
currently write Clojure applications. All state and configuration is
encapsulated in
[Components](https://github.com/stuartsierra/component). The
application is started and stopped with [Apache Commons
Daemon](http://commons.apache.org/proper/commons-daemon/). There are
also some useful log utilities in the log namespace.

**Readings/Watchings**

Here are the readings/watchings that I posted on my last slide if you want to dive deeper on this stuff:

- Release It! (book) - Mike Nygard
- [Component](https://www.youtube.com/watch?v=13cmHf_kt-Q) (youtube) - Stuart Sierra
- [Language of the System](https://www.youtube.com/watch?v=ROor6_NGIWU) (youtube) - Rich Hickey
- The Phoenix Project (book) - Gene Kim, Kevin Behr, George Spafford