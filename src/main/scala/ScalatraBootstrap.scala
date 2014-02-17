import _root_.akka.actor.{Props, ActorSystem}
import _root_.akka.routing.SmallestMailboxRouter
import org.d2g.controller._
import org.d2g.connector.ReactiveMongo
import org.d2g.service.UserServiceActor
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

	val actorSystem = ActorSystem("d2g")

	ReactiveMongo.init(actorSystem)

	val userServiceActor = actorSystem.actorOf(Props[UserServiceActor].withRouter(SmallestMailboxRouter(nrOfInstances = 10)), "userRouter")

	override def init(context: ServletContext) {
		ReactiveMongo.instance.setup()
		context.mount(new UserController(actorSystem, userServiceActor), "/rest")
	}

	override def destroy(context: ServletContext) {
		actorSystem.shutdown()
		ReactiveMongo.instance.shutdown()
	}
}
