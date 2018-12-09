package pme123.form.server.entity

import akka.actor.ActorRef

object ActorMessages {

  case object InitActor

  case class SubscribeClient(wsActor: ActorRef)

  case class UnSubscribeClient()

}
