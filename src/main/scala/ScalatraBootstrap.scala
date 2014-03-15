import _root_.akka.actor.{Props, ActorSystem}
import _root_.akka.routing.SmallestMailboxRouter
import org.d2g.controller._
import org.d2g.connector.ReactiveMongo
import org.d2g.service.UserServiceActor
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

	implicit val actorSystem = ActorSystem("d2g")

	implicit val swaggerDocs = new ApiSwagger

	ReactiveMongo.init(actorSystem)

	val userServiceActor = actorSystem.actorOf(Props[UserServiceActor].withRouter(SmallestMailboxRouter(nrOfInstances = 10)), "userRouter")

	override def init(context: ServletContext) {
		ReactiveMongo.instance.setup()

		context mount(new ResourcesApp(), "/api-docs/*")
		context.mount(new UserController(userServiceActor), "/api/user/*")
	}



	override def destroy(context: ServletContext) {
		actorSystem.shutdown()
		ReactiveMongo.instance.shutdown()
	}
}
