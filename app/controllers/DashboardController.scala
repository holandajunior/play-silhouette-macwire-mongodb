package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.Json
import silhouette.Roles.{AdminRole, UserRole}
import silhouette.WithRole
import silhouette.environments.DefaultEnv
import models.JsonFormatUser._

import scala.concurrent.Future

class DashboardController (silhouette: Silhouette[DefaultEnv]) extends BaseController{

  def index = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson(request.identity)))
  }

  def adminOnly = silhouette.SecuredAction( WithRole(AdminRole) ) { implicit request =>
    Ok("Success (only admin)")
  }

  def userOrAdmin = silhouette.SecuredAction( WithRole(AdminRole) || WithRole(UserRole) ) { implicit request =>
    Ok("Success (user or admin)")
  }
}
