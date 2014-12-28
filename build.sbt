name := "finagletest"

version := "1.0"

fork in run := true

resolvers += "twitter" at "http://maven.twttr.com"

libraryDependencies += "com.twitter"  %% "finagle-http" % "6.22.0"

libraryDependencies += "com.twitter" %% "finagle-zipkin" % "6.22.0"

libraryDependencies += "com.twitter" %% "finagle-ostrich4" % "6.22.0"

libraryDependencies += "com.twitter" %% "finagle-serversets" % "6.22.0"

libraryDependencies += "com.twitter.common.zookeeper" % "server-set" % "1.0.9"

//javaOptions in run += "-Dcom.twitter.finagle.zipkin.host=192.168.1.9:9410"

//javaOptions in run += "-Dcom.twitter.finagle.zipkin.initialSampleRate=1.0"
