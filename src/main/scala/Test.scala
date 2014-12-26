package com.twitter.finagle.example.http

import com.twitter.common.quantity.{Time, Amount}
import com.twitter.finagle.SimpleFilter
import com.twitter.finagle.builder.{ServerBuilder, Cluster, ClientBuilder}
import java.net.InetSocketAddress
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finagle.zookeeper.{ZookeeperServerSetCluster, ZkResolver}
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import com.twitter.finagle.http.Http
import com.twitter.finagle.Service
import com.twitter.io.Charsets
import com.twitter.util.{Await, Future}
import com.twitter.finagle.tracing.{ConsoleTracer, Trace}
import com.twitter.io.Charsets.Utf8
import com.twitter.common.zookeeper.{ ServerSets, ServerSetImpl }
import com.twitter.common.zookeeper.ZooKeeperClient
import com.twitter.finagle.zookeeper.ZkClientFactory
import org.jboss.netty.util.CharsetUtil


/**
 * A somewhat advanced example of using Filters with Clients. Below, HTTP 4xx and 5xx
 * class requests are converted to Exceptions. Additionally, two parallel requests are
 * made and when they both return (the two Futures are joined) the TCP connection(s)
 * are closed.
 */
object Test {
  class InvalidRequest extends Exception

  /**
   * Convert HTTP 4xx and 5xx class responses into Exceptions.
   */
  class HandleErrors extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      // flatMap asynchronously responds to requests and can "map" them to both
      // success and failure values:
      service(request) flatMap { response =>
        response.getStatus match {
          case OK        => Future.value(response)
          case FORBIDDEN => Future.exception(new InvalidRequest)
          case _         => Future.exception(new Exception(response.getStatus.getReasonPhrase))
        }
      }
    }
  }




  def main(args: Array[String]) {
    val zipkinTracer = ZipkinTracer.mk(host = "192.168.1.9", port = 9410, sampleRate = 1.0f)
  //  val zookeeperHost: java.net.InetSocketAddress
   // val zkClient = new ZooKeeperClient(Amount.of(100, Time.MILLISECONDS), new InetSocketAddress(2181))
  //  val serverSet = new ServerSetImpl(zkClient, "/kenny")
  //  val serverSetCluster = new ZookeeperServerSetCluster(serverSet)

    //val zkClientFactory = new ZkClientFactory(com.twitter.util.Duration.fromSeconds(5))
   // val zkResolver = new ZkResolver(zkClientFactory)
   // val clust = zkResolver.bind("localhost:3006!/kenny!")

    val clientWithoutErrorHandling: Service[HttpRequest, HttpResponse] = ClientBuilder()
     .codec(Http().enableTracing(true))
   //   .codec(Http())
      //.hosts("192.168.1.44:3006")
      .name("ClientClient")
      .hostConnectionLimit(1)
     //.cluster(serverSetCluster)
     .tracer(zipkinTracer)
     .dest("zk!localhost:2181!/finagle")
     .build()

       //Trace.pushTracer(ConsoleTracer)

    val handleErrors = new HandleErrors

    // compose the Filter with the client:
    val client: Service[HttpRequest, HttpResponse] = handleErrors andThen clientWithoutErrorHandling


   // println("))) Issuing two requests in parallel: ")
    val request1 = makeAuthorizedRequest(client)

   // val request2 = makeUnauthorizedRequest(client)

    // When both request1 and request2 have completed, close the TCP connection(s).
 //  (request1 join request2) ensure {
  // (request1) ensure {
    // client.close()
    //}




  }

  private[this] def makeAuthorizedRequest(client: Service[HttpRequest, HttpResponse]) = {
    val authorizedRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
    authorizedRequest.headers().add(HttpHeaders.Names.AUTHORIZATION, "open sesame")
    Trace.record("starting some extremely expensive computation")
    client(authorizedRequest) onSuccess { response =>
      val responseString = response.getContent.toString(Charsets.Utf8)
      println("))) Received result for authorized request: " + responseString)
    }
  }

  private[this] def makeUnauthorizedRequest(client: Service[HttpRequest, HttpResponse]) = {
    val unauthorizedRequest = new DefaultHttpRequest(
      HttpVersion.HTTP_1_1, HttpMethod.GET, "/ok")

    // use the onFailure callback since we convert HTTP 4xx and 5xx class
    // responses to Exceptions.
    client(unauthorizedRequest) onFailure { error =>
      println("))) Unauthorized request errored (as desired): " + error.getClass.getName)
    }

    client(unauthorizedRequest) onSuccess  { response =>
      val responseString = response.getContent.toString(Charsets.Utf8)
      println("2))) Received result for authorized request: " + responseString)
    }
  }
}

