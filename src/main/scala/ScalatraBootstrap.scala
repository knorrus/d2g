import _root_.akka.actor.{Props, ActorSystem}
import _root_.akka.routing.SmallestMailboxRouter
import org.d2g.controller._
import org.d2g.persistence.ReactiveMongo
import org.d2g.service.UserServiceActor
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

	val serviceSystem = ActorSystem("d2g")

	val mongoSystem = ReactiveMongo(serviceSystem)

	val defaultD2gDatabase = mongoSystem.getDatabase("d2g")

	val userServiceActor = serviceSystem.actorOf(Props(new UserServiceActor(defaultD2gDatabase)).withRouter(SmallestMailboxRouter(nrOfInstances = 10)), "userRouter")

	override def init(context: ServletContext) {
		context.mount(new UserController(serviceSystem, userServiceActor), "/rest")
	}

	override def destroy(context: ServletContext) {
		serviceSystem.shutdown()
		mongoSystem.shutdown()
	}
}
