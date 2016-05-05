package com.example

import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.camel.{Producer, CamelMessage, Consumer, CamelExtension}
import com.example.actors.{MyQuartzConsumer, HelloWorldActor}
import org.apache.camel.component.file.FileComponent
import org.apache.camel.{Exchange, Processor}
import org.apache.camel.builder.RouteBuilder

/**
 * This is actually just a small wrapper around the generic launcher
 * class akka.Main, which expects only one argument: the class name of
 * the application?s main actor. This main method will then create the
 * infrastructure needed for running the actors, start the given main
 * actor and arrange for the whole application to shut down once the main
 * actor terminates.
 *
 * Thus you could also run the application with a
 * command similar to the following:
 * java -classpath  akka.Main com.example.actors.HelloWorldActor
 *
 * @author alias
 */
object HelloSimpleMain  {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("my-system")
    val fileProducer = system.actorOf(Props[MyFileProducer])
    val httpProducer = system.actorOf(Props(classOf[MyHttpProducer], fileProducer))
    val quartzConsumer = system.actorOf(Props(classOf[MyQuartzConsumer], httpProducer))

  }

  class MyQuartzConsumer(httpProducer: ActorRef) extends Actor with Consumer {

    def endpointUri = "quartz2://example?cron=0/10+*+*+*+*+?"

    def receive = {

      case msg => httpProducer forward msg

    }

  }

  class MyHttpProducer(fileProducer: ActorRef) extends Actor with Producer {

    def endpointUri = "jetty:http://www.timeapi.org/utc/now.json"

    override def routeResponse(msg: Any) { fileProducer forward msg }

  }

  class MyFileProducer extends Actor with Producer {
    def endpointUri = "file:/Users/stevanrose/dev/temp"
  }

}
