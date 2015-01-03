/**
 * Created by kenny.lee on 2015/1/2.
 */

import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}
import com.twitter.finagle.example.thriftscala.Hello

object ThriftClient {
  def main(args: Array[String]): Unit = {
    //#thriftclientapi
    val client = Thrift.newIface[Hello.FutureIface]("localhost:8080")
    val response: Future[String] = client.hi()
    response onSuccess { response =>
      println("Received response: " + response)
    }

    response onFailure  { cause: Throwable =>
      println("failed with " + cause)
    }

    Await.result(response)
    //#thriftclientapi


  }
}
