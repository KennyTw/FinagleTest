/**
 * Created by kenny.lee on 2014/11/8.
 */

import java.util.concurrent.TimeUnit
import com.twitter.finagle.service.RetryPolicy
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await,Future}
import java.net.InetSocketAddress
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil
import com.twitter.util.Duration
import com.twitter.finagle.SimpleFilter
import com.twitter.util.Timer
import com.twitter.finagle.Resolver
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.service.{RetryingFilter,RetryPolicy}
import com.twitter.finagle.util.{DefaultTimer}

class TimeoutFilter[Req, Rep](timeout: Duration, timer: Timer)
  extends SimpleFilter[Req, Rep]
{
  def apply(request: Req, service: Service[Req, Rep]): Future[Rep] = {
    val res = service(request)
    res.within(timer, timeout)
  }
}

object HelloTest extends App {

  val retry = new RetryingFilter[String, String](
    retryPolicy = RetryPolicy.tries(3),
    timer = DefaultTimer.twitter
  )

   val dest = Resolver.eval(
    "localhost:3000,localhost:3001")

  val client: Service[HttpRequest, HttpResponse] =
    Http.newService(dest,"qq")


  val client2 = Http.newClient(dest,"qq")

  //val newClient = retry andThen client2

  val request =  new DefaultHttpRequest(
    HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
   // val hd = request.headers()
   // hd.set(HttpHeaders.Names.HOST,"www.104.com.tw")
   // hd.set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE)
  /*
  def go(callback: () => Unit) {
    println("ohoh")
    callback()
  }
  def timeFlies():Int = {
    println("time flies like an arrow ...")

  }
  go(timeFlies)*/
  val response: Future[HttpResponse] = client(request)
  val response2:Future[HttpResponse] = client(request)

  response onSuccess { resp: HttpResponse =>
    println("GET success: " + resp.getStatus())
    val  content = resp.getContent()
    if (content.readable()) {
        println("1" + content.toString(CharsetUtil.UTF_8))
    }
  }


  response2 onSuccess { resp: HttpResponse =>
    println("GET success: " + resp.getStatus())
    val  content = resp.getContent()
    if (content.readable()) {
      println("2" + content.toString(CharsetUtil.UTF_8))
    }
  }

  response2 onFailure {
     t => println("An error has occured: " + t.getMessage)
  }


  response onFailure {
     t => println("An error has occured: " + t.getMessage)
  }
  println("end")
 //val oo =  Await.result(response,Duration(100, TimeUnit.SECONDS))
//  println("Await.result: " + oo.getStatus())
  Await.ready(response)
  Await.ready(response2)



}