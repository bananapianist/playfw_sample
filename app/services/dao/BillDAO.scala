package services.dao

import java.util.Calendar
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import models.TablesExtend._
import play.api._
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

class BillDAO @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[BillRow, Long]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val billquery = TableQuery[Bill]
 

  def all(): Future[List[BillRow]] = {
    dbConfig.db.run(billquery.sortBy(c => c.id.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(billquery.sortBy(c => c.id.desc).length.result)
  }

 
  def findById(id: Long): Future[Option[BillRow]] = {
    dbConfig.db.run(billquery.filter(_.id === id).result.headOption)
  }


  def create(bill: BillRow): Future[Int] = {
    val c = bill.copy(
        createdDate = Option(new Date),
        updatedDate = new Date
    )
    dbConfig.db.run(billquery += c)
  }

  def update(bill: BillRow): Future[Unit] = {
    dbConfig.db.run(billquery.filter(_.id === bill.id).update(bill).map(_ => ()))
  }

  def update_mappinged(bill: BillRow): Future[Int] = {
     dbConfig.db.run(billquery.filter(_.id === bill.id).map(
         c => (
            c.billName,
            c.billEmail,
            c.billTel,
            c.billAddress,
            c.updatedDate
            )
      ).update(
          (
        bill.billName,
        bill.billEmail,
        bill.billTel,
        bill.billAddress,
        new Date
        )
      )
    )
  }
  
  def delete(id: Long): Future[Int] = dbConfig.db.run(billquery.filter(_.id === id).delete)

 

}

