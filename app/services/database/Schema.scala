package services.database

import models.database.queries.ddl.DdlQueries
import models.database.queries.ddl._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.Logging

import scala.concurrent.Await
import scala.concurrent.duration._

object Schema extends Logging {
  val tables = Seq(
    "adhoc_queries" -> CreateAdHocQueriesTable,
    "users" -> CreateUsersTable,
    "user_feedback" -> CreateUserFeedbackTable,
    "user_profiles" -> CreateUserProfilesTable,
    "password_info" -> CreatePasswordInfoTable,
    "oauth1_info" -> CreateOAuth1InfoTable,
    "oauth2_info" -> CreateOAuth2InfoTable,
    "openid_info" -> CreateOpenIdInfoTable,
    "session_info" -> CreateSessionInfoTable,
    "requests" -> CreateRequestsTable,
    "games" -> CreateGamesTable,
    "game_seeds" -> CreateGameSeedsTable,
    "game_cards" -> CreateGameCardsTable,
    "game_moves" -> CreateGameMovesTable,
    "daily_metrics" -> CreateDailyMetricsTable
  )

  def update() = tables.foreach { t =>
    Database.query(DdlQueries.DoesTableExist(t._1)).foreach { exists =>
      if (!exists) {
        log.info(s"Creating missing table [${t._1}].")
        val name = s"CreateTable-${t._1}"
        Database.raw(name, t._2.sql)
      }
    }
  }

  def wipe() = {
    log.warn("Wiping database schema.")
    val tableNames = tables.reverse.map(_._1)
    Database.execute(DdlQueries.TruncateTables(tableNames)).map(x => tableNames)
  }
}
