package io.scalajs.nodejs

import io.scalajs.nodejs.net.{ListenerOptions, Socket}
import io.scalajs.util.ScalaJsHelper._

import scala.concurrent.Promise
import scala.scalajs.js

/**
  * http package object
  * @author lawrence.daniels@gmail.com
  */
package object http {

  /**
    * Http Extensions
    * @author lawrence.daniels@gmail.com
    */
  implicit class HttpExtensions(val http: Http) extends AnyVal {

    /**
      * @see [[Http.createServer()]]
      */
    @inline
    def createServerFuture: Promise[(Server, ClientRequest, ServerResponse)] = {
      val task           = Promise[(Server, ClientRequest, ServerResponse)]()
      var server: Server = null
      server = http.createServer((request: ClientRequest, response: ServerResponse) => {
        task.success((server, request, response))
      })
      task
    }

    /**
      * @see [[Http.get()]]
      */
    @inline
    def getFuture(options: RequestOptions): Promise[ServerResponse] =
      futureCallbackA1[ServerResponse](http.get(options, _))

    /**
      * @see [[Http.get()]]
      */
    @inline
    def getFuture(url: String): Promise[ServerResponse] = futureCallbackA1[ServerResponse](http.get(url, _))

    /**
      * @see [[Http.request()]]
      */
    @inline
    def requestFuture(options: RequestOptions): Promise[ServerResponse] =
      futureCallbackE1[js.Error, ServerResponse](http.request(options, _))

    /**
      * @see [[Http.request()]]
      */
    @inline
    def requestFuture(url: String): Promise[ServerResponse] =
      futureCallbackE1[js.Error, ServerResponse](http.request(url, _))

  }

  /**
    * Server Events
    * @author lawrence.daniels@gmail.com
    */
  implicit class ServerEvents(val server: Server) extends AnyVal {

    /**
      * Emitted each time a request with an http Expect: 100-continue is received. If this event isn't listened for,
      * the server will automatically respond with a 100 Continue as appropriate.
      *
      * Handling this event involves calling response.writeContinue() if the client should continue to send the request
      * body, or generating an appropriate HTTP response (e.g., 400 Bad Request) if the client should not continue to
      * send the request body.
      *
      * Note that when this event is emitted and handled, the 'request' event will not be emitted.
      * @example server.on("checkContinue", function (request, response) { ... })
      */
    @inline
    def onCheckContinue(callback: (ClientRequest, ServerResponse) => Any): server.type =
      server.on("checkContinue", callback)

    /**
      * If a client connection emits an 'error' event, it will be forwarded here. Listener of this event is responsible
      * for closing/destroying the underlying socket. For example, one may wish to more gracefully close the socket with
      * an HTTP '400 Bad Request' response instead of abruptly severing the connection.
      *
      * Default behavior is to destroy the socket immediately on malformed request.
      *
      * socket is the net.Socket object that the error originated from.
      * @example server.on("clientError", function (exception, socket) { ... })
      */
    @inline
    def onClientError(callback: (js.Error, Socket) => Any): server.type = server.on("clientError", callback)

    @inline
    def onConnect(callback: js.Function): server.type = server.on("connect", callback)

    @inline
    def closeFuture(): Promise[Unit] = futureCallbackE0[js.Error](server.close)

    @inline
    def getConnectionsFuture: Promise[Int] = futureCallbackE1[js.Error, Int](server.getConnections)

    @inline
    def listenFuture(options: ListenerOptions): Promise[Unit] = futureCallbackE0[js.Error](server.listen(options, _))

    @inline
    def onRequest(callback: js.Function): server.type = server.on("request", callback)

    @inline
    def onUpgrade(callback: js.Function): server.type = server.on("upgrade", callback)

  }

}
