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
import utilities.oauth.GrantType


class OauthClientDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[OauthClientRow, UUID]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val oauthclientquery = TableQuery[OauthClient]
  private val oauthuserquery = TableQuery[OauthUser]
 


  def validate(id: UUID, secret: Option[String], grantType: String): Future[Int] = {
    dbConfig.db.run(oauthclientquery.filter(n => (n.oauthClientId === id) && (n.clientSecret === secret) && (n.grantType === grantType)).length.result)
  }
  

  def all(): Future[List[OauthClientRow]] = {
    dbConfig.db.run(oauthclientquery.sortBy(c => c.oauthClientId.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(oauthclientquery.sortBy(c => c.oauthClientId.desc).length.result)
  }

  def paginglist(page: Int, offset: Int, urlquery: Map[String, String]): Future[(Int, Seq[(OauthClientRow, OauthUserRow)])] = {
    var joinquery = (for{
      oauthclientlist <- filterQueryOauthClient(oauthclientquery, urlquery) join filterQueryOauthUser(oauthuserquery, urlquery) on (_.oauthUserId === _.guid) 
    }yield (oauthclientlist)
    ).sortBy(oauthclientlist => oauthclientlist._1.createdDate.desc)

    val pagelistsql = (for {
        count <- joinquery.length.result
        joinquerylist <- joinquery.drop((page-1) * offset).take(offset).result
    }yield (count, joinquerylist)
    )
    dbConfig.db.run(pagelistsql.transactionally)
  }

  def findById(id: UUID): Future[Option[OauthClientRow]] = {
    dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === id).result.headOption)
  }

  def findByClientCredentials(clientId: UUID, clientSecret: String): Future[Option[OauthClientRow]] = {
    dbConfig.db.run(oauthclientquery.filter(n => (n.oauthClientId === clientId) && (n.clientSecret === clientSecret) && (n.grantType === GrantType.ClientCredentialsGrantType)).result.headOption)
  }

  def create(oauthclient: OauthClientRow): Future[Int] = {
    val c = oauthclient.copy(
        oauthClientId = java.util.UUID.randomUUID,
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
          c.oauthUserId,
          c.clientSecret,
          c.redirectUri,
          c.grantType,
          c.expiresIn
          )
      ).update(
        (
        oauthclient.oauthUserId,
        oauthclient.clientSecret,
        oauthclient.redirectUri,
        oauthclient.grantType,
        oauthclient.expiresIn
        )
      )
    )

  }
  
  def delete(id: UUID): Future[Int] = dbConfig.db.run(oauthclientquery.filter(_.oauthClientId === id).delete)

  def filterQueryOauthClient(tablequery:TableQuery[OauthClient], urlquery: Map[String, String]) = {
    val dummyid: UUID = null
    var returnquery = tablequery.filterNot(_.oauthClientId === dummyid)
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "oauthUserId" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.oauthUserId === java.util.UUID.fromString(querytuple._2).bind) 
          case _ => 
        }
    }
    returnquery
  }

  def filterQueryOauthUser(tablequery:TableQuery[OauthUser], urlquery: Map[String, String]) = {
    val dummyid: UUID = null
    var returnquery = tablequery.filterNot(_.guid === dummyid)
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "oauthUserName" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.name like (StringHelper.trim(querytuple._2) + "%").bind) 
          case _ => 
        }
    }
    returnquery
  }

}

