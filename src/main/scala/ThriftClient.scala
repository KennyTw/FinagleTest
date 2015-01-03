/**
 * Created by kenny.lee on 2015/1/2.
 */

import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}
import com.twitter.finagle.example.thriftscala.Hello
import com.twitter.finagle.Service

object ThriftClient {
  def main(args: Array[String]): Unit = {
    //#thriftclientapi
    val client = Thrift.newIface[Hello.FutureIface]("localhost:8080", "ThriftClient")

    /*val rawSvc: Service[Int,String] = Service.mk[Int, String] { int: Int => client.hi() }

    val response: Future[String] = rawSvc.apply(0)
    response onSuccess { response =>
      println("Received response: " + response)
    }

    response onFailure  { cause: Throwable =>
      println("failed with " + cause)
    }

    Await.result(response)
    rawSvc.close()*/



    val response: Future[String] = client.hi()
    response onSuccess { response =>
      println("Received response: " + response)
    }

    response onFailure  { cause: Throwable =>
      println("failed with " + cause)
    }

    Await.result(response)


    //#thriftclientapi
    //Thrift.newService()

  }
}
