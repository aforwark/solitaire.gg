package controllers

import java.util.UUID

import models.database.queries.UserFeedbackQueries
import models.user.UserFeedback
import org.joda.time.LocalDateTime
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.MailerClient
import play.api.mvc.Action
import services.EmailService
import services.database.Database

import scala.concurrent.Future

class HomeController @javax.inject.Inject() (val messagesApi: MessagesApi, mailer: MailerClient) extends BaseController {
  def index() = withSession { implicit request =>
    Future.successful(Ok(views.html.index(request.identity)))
  }

  def untrail(path: String) = Action.async {
    Future.successful(MovedPermanently("/" + path))
  }

  def about = withSession { implicit request =>
    Future.successful(Ok(views.html.about(request.identity)))
  }

  def feedbackForm = withSession { implicit request =>
    Future.successful(Ok(views.html.feedback(request.identity)))
  }

  def submitFeedback = withSession { implicit request =>
    request.body.asFormUrlEncoded match {
      case Some(form) => form.get("feedback") match {
        case Some(feedback) =>
          val obj = UserFeedback(
            id = UUID.randomUUID,
            userId = request.identity.id,
            activeGameId = None,
            feedback = feedback.mkString("\n\n"),
            occurred = new LocalDateTime()
          )

          new EmailService(mailer).feedbackSubmitted(obj, request.identity)

          Database.execute(UserFeedbackQueries.insert(obj)).map { x =>
            Redirect(routes.HomeController.feedbackForm()).flashing("success" -> "Your feedback has been submitted. Thanks!")
          }
        case None => Future.successful(Redirect(routes.HomeController.feedbackForm()).flashing("error" -> "Please include some feedback."))
      }
      case None => Future.successful(Redirect(routes.HomeController.feedbackForm()).flashing("error" -> "Please include some feedback."))
    }
  }
}
