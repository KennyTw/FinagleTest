/**
 * Created by kenny.lee on 2015/1/2.
 */

import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}
import com.twitter.finagle.example.thriftscala.Hello

object ThriftServer {
  def main(args: Array[String]): Unit = {
    //#thriftserverapi
    val server = Thrift.serveIface("FinagleThriftServer=:8080", new Hello[Future] {
      def hi() = Future.value("hi")
    })
    Await.ready(server)
    //#thriftserverapi
  }
}
