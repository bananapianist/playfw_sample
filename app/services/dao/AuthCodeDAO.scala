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

class AuthCodeDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[AuthCodeRow, String]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val authcodequery = TableQuery[AuthCode]
 

  

  def all(): Future[List[AuthCodeRow]] = {
    dbConfig.db.run(authcodequery.sortBy(c => c.authorizationCode.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(authcodequery.sortBy(c => c.authorizationCode.desc).length.result)
  }

  def findById(code: String): Future[Option[AuthCodeRow]] = {
    dbConfig.db.run(authcodequery.filter(_.authorizationCode === code).result.headOption)
  }

  def findByAuthorizationCode(code: String): Future[Option[AuthCodeRow]] = {
    findById(code)
  }

  def findByGUID(userGuid: UUID, clientId: UUID): Future[Option[AuthCodeRow]] = {
    dbConfig.db.run(authcodequery.filter(a => a.oauthClientId === clientId && a.userGuid === userGuid).result.headOption)
  }

  def create(account: AuthCodeRow): Future[Int] = {
    val c = account.copy(
        createdDate = new Date
    )
    dbConfig.db.run(authcodequery += c)
  }

  def update(authcode: AuthCodeRow): Future[Unit] = {
    dbConfig.db.run(authcodequery.filter(_.authorizationCode === authcode.authorizationCode).update(authcode).map(_ => ()))
  }

  
  def deleteExistingAndCreate(authcode: AuthCodeRow, userGuid: UUID, clientId: UUID): Future[Int] = {
    val action =
    (for {
     _ <- authcodequery.filter(a => a.oauthClientId === clientId && a.userGuid === userGuid).delete
     newId <- authcodequery += authcode
  } yield (newId) )

   dbConfig.db.run(action.transactionally)
  }
}

