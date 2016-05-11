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

class AppAccountDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)  extends BaseDAO[AppAccountRow, Long]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val appaccountquery = TableQuery[AppAccount]
   

  def all(): Future[List[AppAccountRow]] = {
    dbConfig.db.run(appaccountquery.sortBy(c => c.id.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(appaccountquery.sortBy(c => c.id.desc).length.result)
  }

  def findById(id: Long): Future[Option[AppAccountRow]] = {
    dbConfig.db.run(appaccountquery.filter(_.id === id).result.headOption)
  }

  def create(appaccount: AppAccountRow): Future[Int] = {
    dbConfig.db.run(appaccountquery += appaccount)
  }

  def update(appaccount: AppAccountRow): Future[Unit] = {
    dbConfig.db.run(appaccountquery.filter(_.id === appaccount.id).update(appaccount).map(_ => ()))
  }

  def update_mappinged(appaccount: AppAccountRow): Future[Int] = {
     dbConfig.db.run(appaccountquery.filter(_.id === appaccount.id).map(
       c => (
          c.name
          )
      ).update(
        (
        appaccount.name
        )
      )
    )
        
  }
  
  def delete(id: Long): Future[Int] = dbConfig.db.run(appaccountquery.filter(_.id === id).delete)

  def getValidListForSelectOption(): Future[Seq[(String,String)]] = {
    val query = (for {
      appaccount <- appaccountquery.sortBy(c => c.id.desc)
    } yield (appaccount.id, appaccount.name))
    
    dbConfig.db.run(query.result.map(rows => rows.map{case (id, name) => (id.toString, name)}))
  }
  
  def findByNameList(name: String): Future[Seq[String]] = {
    val query = (for {
      appaccount <- appaccountquery.filter(n => ( (n.name like (name + "%")) || ( n.name like (StringHelper.convertHiraganaToKatakana(name) + "%")) || ( n.name like (StringHelper.convertKatakanaToHiragana(name) + "%")))).sortBy(c => c.id.desc)
    } yield (appaccount.name))
    dbConfig.db.run(query.result.map(rows => rows.map{case (name) => name}))
  }

}

