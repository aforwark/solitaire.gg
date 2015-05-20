package services.user

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OpenIDInfo
import models.database.queries.auth.OpenIdInfoQueries
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.database.Database

import scala.concurrent.Future

object OpenIdInfoService extends DelegableAuthInfoDAO[OpenIDInfo] {
  override def save(loginInfo: LoginInfo, authInfo: OpenIDInfo) = {
    Database.transaction { conn =>
      Database.execute(OpenIdInfoQueries.UpdateOpenIdInfo(loginInfo, authInfo), conn).flatMap { rowsAffected =>
        if (rowsAffected == 0) {
          Database.execute(OpenIdInfoQueries.CreateOpenIdInfo(loginInfo, authInfo), conn).map(x => authInfo)
        } else {
          Future.successful(authInfo)
        }
      }
    }
  }

  override def find(loginInfo: LoginInfo) = {
    Database.query(OpenIdInfoQueries.FindOpenIdInfo(loginInfo))
  }
}
