/**
 * Created by kenny.lee on 2015/1/2.
 */

import com.twitter.finagle.{Resolver, Thrift, Service}
import com.twitter.finagle.example.thriftscala.Hello.FinagledClient
import com.twitter.util.{Await, Future}
import com.twitter.finagle.example.thriftscala.Hello

object ThriftClient {
  def main(args: Array[String]): Unit = {
    //#thriftclientapi
    val dest = Resolver.eval("inet!127.0.0.1:8080")
    val service = Thrift.newService(dest,"ThriftClient")
    //val clientIface = Thrift.newIface[Hello.FutureIface]("localhost:8080")
    val client = new FinagledClient(service)

    val response: Future[String] = client.hi()
    response onSuccess { response =>
      println("Received response: " + response)
    }  ensure {
      service.close()
      println("service close")
    }

    response onFailure  { cause: Throwable =>
      println("failed with " + cause)
    }

    Await.result(response)
    Thread.sleep(100)
    //#thriftclientapi

  }
}
