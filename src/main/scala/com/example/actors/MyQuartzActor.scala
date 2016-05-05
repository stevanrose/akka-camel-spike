package com.example.actors


import akka.camel.{ CamelMessage, Consumer }

import com.typesafe.scalalogging._

class MyQuartzActor extends Consumer with LazyLogging {

  def endpointUri = "quartz2://example?cron=0/2+*+*+*+*+?"

  def receive = {

    case msg: CamelMessage => logger.info("***** firing quartz actor *****", msg)




  }

}
