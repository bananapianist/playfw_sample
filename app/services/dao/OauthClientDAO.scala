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

import utilities._


class OauthClientDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[OauthClientRow, String]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val oauthclientquery = TableQuery[OauthClient]
  private val appaccountquery = TableQuery[AppAccount]
 


  

  def all(): Future[List[OauthClientRow]] = {
    dbConfig.db.run(oauthclientquery.sortBy(c => c.oauthClientId.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(oauthclientquery.sortBy(c => c.oauthClientId.desc).length.result)
  }

  def paginglist(page: Int, offset: Int, urlquery: Map[String, String]): Future[(Int, Seq[(OauthClientRow, AppAccountRow)])] = {
    var joinquery = (for{
      oauthclientlist <- filterQueryOauthClient(oauthclientquery, urlquery) join filterQueryAppAccount(appaccountquery, urlquery) on (_.applicationId === _.id) 
    }yield (oauthclientlist)
    ).sortBy(oauthclientlist => oauthclientlist._1.createdDate.desc)

    val pagelistsql = (for {
        count <- joinquery.length.result
        joinquerylist <- joinquery.drop((page-1) * offset).take(offset).result
    }yield (count, joinquerylist)
    )
    dbConfig.db.run(pagelistsql.transactionally)
  }

  def findById(id: String): Future[Option[OauthClientRow]] = {
    dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === id).result.headOption)
  }

  def create(oauthclient: OauthClientRow): Future[Int] = {
    val c = oauthclient.copy(
        oauthClientId = java.util.UUID.randomUUID.toString,
        expiresIn = 0
    )
    dbConfig.db.run(oauthclientquery += c)
  }

  def update(oauthclient: OauthClientRow): Future[Unit] = {
    dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === oauthclient.oauthClientId).update(oauthclient).map(_ => ()))
  }

  def update_mappinged(oauthclient: OauthClientRow): Future[Int] = {
     dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === oauthclient.oauthClientId).map(
       c => (
          c.applicationId,
          c.clientSecret,
          c.redirectUri,
          c.grantType,
          c.expiresIn
          )
      ).update(
        (
        oauthclient.applicationId,
        oauthclient.clientSecret,
        oauthclient.redirectUri,
        oauthclient.grantType,
        oauthclient.expiresIn
        )
      )
    )

  }
  
  def delete(id: String): Future[Int] = dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === id).delete)

  def filterQueryOauthClient(tablequery:TableQuery[OauthClient], urlquery: Map[String, String]) = {
    var returnquery = tablequery.filterNot(_.oauthClientId === "")
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "applicationId" => if(querytuple._2 != "-1") returnquery = returnquery.filter(_.applicationId === querytuple._2.toLong.bind) 
          case _ => 
        }
    }
    returnquery
  }

  def filterQueryAppAccount(tablequery:TableQuery[AppAccount], urlquery: Map[String, String]) = {
    var returnquery = tablequery.filter(_.id > 0.toLong)
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "applicationName" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.name like (StringHelper.trim(querytuple._2) + "%").bind) 
          case _ => 
        }
    }
    returnquery
  }

}

