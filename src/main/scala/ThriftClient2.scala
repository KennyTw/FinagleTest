/**
 * Created by kenny.lee on 2015/1/3.
 */

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.example.thriftscala.Hello.FinagledClient
import com.twitter.finagle.thrift.{ThriftClientFramedCodec, ThriftClientRequest}
import com.twitter.finagle.Service
import java.net.InetSocketAddress
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import org.apache.thrift.protocol.TBinaryProtocol


object ThriftClient2 {

  def main(args: Array[String]): Unit = {

    val zipkinTracer = ZipkinTracer.mk(host = "192.168.1.9", port = 9410, sampleRate = 1.0f)
    val service: Service[ThriftClientRequest, Array[Byte]] = ClientBuilder()
      .hosts(new InetSocketAddress("localhost", 8080))
      .codec(ThriftClientFramedCodec())
      .hostConnectionLimit(1)
      .name("ThriftClient2")
      .tracer(zipkinTracer)
      .build()

    val client = new FinagledClient(service, new TBinaryProtocol.Factory(),"ThriftClient2")

    client.hi() onSuccess { response =>
      println("Received response: " + response)
    } ensure {
      //service.release()
      service.close()
    }
  }
}
