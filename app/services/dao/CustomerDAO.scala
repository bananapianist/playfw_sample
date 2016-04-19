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

class CustomerDAO @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[CustomerRow, Long]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val customerquery = TableQuery[Customer]
 

  def all(): Future[List[CustomerRow]] = {
    dbConfig.db.run(customerquery.sortBy(c => c.id.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(customerquery.sortBy(c => c.id.desc).length.result)
  }

  def paginglist(page: Int, offset: Int): Future[(Int, Seq[CustomerRow])] = {
    val pagelistsql = (for {
        count <- customerquery.filter(n => (n.isDisabled === false)).sortBy(c => c.id.desc).length.result
        customers <- customerquery.filter(n => (n.isDisabled === false)).drop((page-1) * offset).take(offset).sortBy(c => c.id.desc).result
    }yield (count, customers)
    )
    dbConfig.db.run(pagelistsql.transactionally)
  }

  def findById(id: Long): Future[Option[CustomerRow]] = {
    dbConfig.db.run(customerquery.filter(_.id === id).result.headOption)
  }

  def getNotificationList(): Future[List[CustomerRow]] =
    dbConfig.db.run(customerquery.filter(n => (n.isDisabled === false) && (n.notificationDate < new Date)).sortBy(c => c.id.desc).result).map(_.toList)

  def create(customer: CustomerRow): Future[Int] = {
    val c = customer.copy(
        notificationDate = Option(this.calcNotificationDate(customer.actionDate.getOrElse(new Date), customer.notificationPeriod.getOrElse(0))),
        isDisabled = Option(false),
        createdDate = Option(new Date),
        updatedDate = new Date
    )
    dbConfig.db.run(customerquery += c)
  }

  def update(customer: CustomerRow): Future[Unit] = {
    dbConfig.db.run(customerquery.filter(_.id === customer.id).update(customer).map(_ => ()))
  }

  def update_mappinged(customer: CustomerRow): Future[Int] = {
     dbConfig.db.run(customerquery.filter(_.id === customer.id).map(
         c => (
            c.name,
            c.email,
            c.tel,
            c.address,
            c.comment,
            c.actionDate,
            c.notificationPeriod,
            c.notificationDate,
            c.isDisabled,
            c.updatedDate
            )
      ).update(
          (
        customer.name,
        customer.email,
        customer.tel,
        customer.address,
        customer.comment,
        customer.actionDate,
        customer.notificationPeriod,
        Option(this.calcNotificationDate(customer.actionDate.getOrElse(new Date), customer.notificationPeriod.getOrElse(0))),
        customer.isDisabled,
        new Date
        )
      )
    )
  }
  
  def delete(id: Long): Future[Int] = dbConfig.db.run(customerquery.filter(_.id === id).delete)

  private def calcNotificationDate(actionDate: Date, notificationPeriod: Int): Date = {
    val cl = Calendar.getInstance
    cl.setTime(actionDate)
    cl.add(Calendar.MINUTE, -notificationPeriod)
    cl.getTime
  }
 

}

