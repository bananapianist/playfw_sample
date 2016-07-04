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

import utilities._

class OauthUserDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[OauthUserRow, UUID]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val oauthuserquery = TableQuery[OauthUser]
   

  def all(): Future[List[OauthUserRow]] = {
    dbConfig.db.run(oauthuserquery.sortBy(c => c.guid.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(oauthuserquery.sortBy(c => c.guid.desc).length.result)
  }

  def findById(id: UUID): Future[Option[OauthUserRow]] = {
    dbConfig.db.run(oauthuserquery.filter(_.guid === id).result.headOption)
  }

  def create(oauthuser: OauthUserRow): Future[Int] = {
    val c = oauthuser.copy(
        guid = java.util.UUID.randomUUID
    )
    dbConfig.db.run(oauthuserquery += c)
  }

  def update(oauthuser: OauthUserRow): Future[Unit] = {
    dbConfig.db.run(oauthuserquery.filter(_.guid === oauthuser.guid).update(oauthuser).map(_ => ()))
  }

  def update_mappinged(oauthuser: OauthUserRow): Future[Int] = {
     dbConfig.db.run(oauthuserquery.filter(_.guid === oauthuser.guid).map(
       c => (
          c.name
          )
      ).update(
        (
        oauthuser.name
        )
      )
    )
        
  }
  
  def delete(id: UUID): Future[Int] = dbConfig.db.run(oauthuserquery.filter(_.guid === id).delete)

  def getValidListForSelectOption(): Future[Seq[(String,String)]] = {
    val query = (for {
      oauthuser <- oauthuserquery.sortBy(c => c.guid.desc)
    } yield (oauthuser.guid, oauthuser.name))
    
    dbConfig.db.run(query.result.map(rows => rows.map{case (guid, name) => (guid.toString, name)}))
  }
  
  def findByNameList(name: String): Future[Seq[String]] = {
    val query = (for {
      oauthuser <- oauthuserquery.filter(n => ( (n.name like (name + "%")) || ( n.name like (StringHelper.convertHiraganaToKatakana(name) + "%")) || ( n.name like (StringHelper.convertKatakanaToHiragana(name) + "%")))).sortBy(c => c.guid.desc)
    } yield (oauthuser.name))
    dbConfig.db.run(query.result.map(rows => rows.map{case (name) => name}))
  }

}

