package services.dao

import java.util.Calendar
import java.util.Date
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import models.TablesExtend._
import play.api._
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider

import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import java.util.UUID

class AccessTokenDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[AccessTokenRow, String]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val accesstokenquery = TableQuery[AccessToken]
 

  

  def all(): Future[List[AccessTokenRow]] = {
    dbConfig.db.run(accesstokenquery.sortBy(c => c.accessToken.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(accesstokenquery.sortBy(c => c.accessToken.desc).length.result)
  }

  def findById(token: String): Future[Option[AccessTokenRow]] = {
    dbConfig.db.run(accesstokenquery.filter(_.accessToken === token).result.headOption)
  }

  def findByAccessToken(token: String): Future[Option[AccessTokenRow]] = {
    findById(token)
  }

  def findByRefreshToken(token: String): Future[Option[AccessTokenRow]] = {
    dbConfig.db.run(accesstokenquery.filter(_.refreshToken === token).result.headOption)
  }

  def findByToken(userGuid: UUID, clientId: Option[String]): Future[Option[AccessTokenRow]] = {
    dbConfig.db.run(accesstokenquery.filter(a => a.oauthClientId === clientId && a.userGuid === userGuid).result.headOption)
  }

  def create(account: AccessTokenRow): Future[Int] = {
    val c = account.copy(
        createdDate = new Date
    )
    dbConfig.db.run(accesstokenquery += c)
  }

  def update(accesstoken: AccessTokenRow): Future[Unit] = {
    dbConfig.db.run(accesstokenquery.filter(_.accessToken === accesstoken.accessToken).update(accesstoken).map(_ => ()))
  }

  
  def deleteExistingAndCreate(accessToken: AccessTokenRow, userGuid: UUID, clientId: Option[String]): Future[Int] = {
    val action =
    (for {
     _ <- accesstokenquery.filter(a => a.oauthClientId === clientId && a.userGuid === userGuid).delete
     newId <- accesstokenquery += accessToken
  } yield (newId) )

   dbConfig.db.run(action.transactionally)
  }
}

