package services.api

import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

trait UserService extends BaseService[User] with IdentityService[User] {

}
