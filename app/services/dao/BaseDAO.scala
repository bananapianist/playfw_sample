package services.dao

import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

trait BaseDAO[A,B] {


  def all(): Future[List[_]]

  def count(): Future[Int]

  def findById(id: B): Future[Option[A]]

  def create(copydata: A): Future[Int]

  def update(updatedata: A): Future[Unit]
  
}