import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.io.Charsets
import org.jboss.netty.handler.codec.http._

/**
 * Created by kenny.lee on 2014/11/11.
 */
object zipkin  extends App {
  println("end")
  //ZipkinTracer(scribeHost = "10.0.0.1", scribePort = 1234, sampleRate = 0.4)
  val zipkinTracer = ZipkinTracer.mk(host = "192.168.1.104", port = 9900, sampleRate = 1.0f)
  val client = ClientBuilder()
    .hosts("localhost:3000")
    .hostConnectionLimit(1) // TODO testing
    .name("Client in FE Server ")
    .codec(Http().enableTracing(true))
   // .tracer(zipkinTracer)
    .retries(5)
    .build()

  val Req = new DefaultHttpRequest(
    HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
  client.apply(Req)

}
