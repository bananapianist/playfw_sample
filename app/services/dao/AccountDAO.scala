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

class AccountDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[AccountRow, Int]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val accountquery = TableQuery[Account]
 

  def authenticate(email: String, password: String): Future[Option[AccountRow]] = {
    this.findByEmail(email).map( accountOpt =>
      accountOpt.flatMap { accountdata =>
        if (BCrypt.checkpw(password, accountdata.password))
          Some(new AccountRow(
              accountdata.id,
              accountdata.email,
              accountdata.password,
              accountdata.name,
              accountdata.role
              ))
        else
          None
      }
     )
  }
  

  def all(): Future[List[AccountRow]] = {
    dbConfig.db.run(accountquery.sortBy(c => c.id.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(accountquery.sortBy(c => c.id.desc).length.result)
  }

  def findByEmail(email: String): Future[Option[AccountRow]] = {
    dbConfig.db.run(accountquery.filter(_.email === email).result.map(_.headOption))
  }

  def findById(id: Int): Future[Option[AccountRow]] = {
    dbConfig.db.run(accountquery.filter(_.id === id).result.headOption)
  }

  def create(account: AccountRow): Future[Int] = {
    val c = account.copy(
        password = this.encryptpassword(account.password)
    )
    dbConfig.db.run(accountquery += c)
  }

  def update(account: AccountRow): Future[Unit] = {
    dbConfig.db.run(accountquery.filter(_.id === account.id).update(account).map(_ => ()))
  }

  def update_mappinged(account: AccountRow): Future[Int] = {
    Option(account.password) match{
      case Some(s) if ((s == null) || (s.trim.isEmpty)) =>
         dbConfig.db.run(accountquery.filter(_.id === account.id).map(
           c => (
              c.name,
              c.email,
              c.role
              )
          ).update(
            (
            account.name,
            account.email,
            account.role
            )
          )
        )
        
      case _ =>
         dbConfig.db.run(accountquery.filter(_.id === account.id).map(
           c => (
              c.name,
              c.email,
              c.password,
              c.role
              )
          ).update(
            (
            account.name,
            account.email,
            this.encryptpassword(account.password),
            account.role
            )
          )
        )
        
    }
  }
  
  def delete(id: Int): Future[Int] = dbConfig.db.run(accountquery.filter(_.id === id).delete)

 def encryptpassword(password: String):String = {
    return  BCrypt.hashpw(password, BCrypt.gensalt())
  }

}

