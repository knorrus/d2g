import _root_.akka.actor.{Props, ActorSystem}
import _root_.akka.routing.SmallestMailboxRouter
import org.d2g.controller._
import org.d2g.service.UserServiceActor
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

	val system = ActorSystem("d2g")

	val userServiceActor = system.actorOf(Props[UserServiceActor].withRouter(SmallestMailboxRouter(nrOfInstances = 10)), "userRouter")

	override def init(context: ServletContext) {
		context.mount(new UserController(system, userServiceActor), "/rest")
	}

	override def destroy(context: ServletContext) {
		system.shutdown()
	}
}
