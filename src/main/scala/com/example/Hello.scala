package com.example

import akka.actor.{Props, ActorSystem}
import akka.camel.CamelExtension
import com.example.actors.{MyQuartzActor, HelloWorldActor}
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
object HelloSimpleMain {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("my-system")
//    system.actorOf(Props[MyQuartzActor])

//    val initialActor = classOf[HelloWorldActor].getName

//    akka.Main.main(Array(initialActor))

    CamelExtension(system).context.addRoutes(new CustomRouteBuilder)
  }

  class CustomRouteBuilder extends RouteBuilder {
    def configure {
      from("quartz2://example?cron=0/10+*+*+*+*+?").process(new Processor() {
        def process(exchange: Exchange) {
          val format = new java.text.SimpleDateFormat("dd-MM-yyyy HH:MM:ss")
          exchange.getOut.setHeader("org.apache.camel.file.name", "steve.txt")
          exchange.getOut.setBody("message fired at : " +  format.format(new java.util.Date()))
        }
      })
      .to("file:/Users/stevanrose/dev/temp")
    }
  }

}
