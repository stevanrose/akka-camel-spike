package com.example.actors

import akka.actor.Actor
import akka.camel.{Oneway, Producer}

class MyFileActor extends Actor with Producer with Oneway {

  def endpointUri: String = "/Users/stevanrose/dev/temp"

}
