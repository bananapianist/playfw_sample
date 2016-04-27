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

import utilities._

class ContractDAO @Inject()(dbConfigProvider: DatabaseConfigProvider) extends BaseDAO[ContractRow, Long]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val contractquery = TableQuery[Contract]
  private val billquery = TableQuery[Bill]
  private val customerquery = TableQuery[Customer]
 

  def all(): Future[List[ContractRow]] = {
    dbConfig.db.run(contractquery.sortBy(c => c.id.desc).result).map(_.toList)
  }

  def count(): Future[Int] = {
    dbConfig.db.run(contractquery.sortBy(c => c.id.desc).length.result)
  }

  def paginglist(page: Int, offset: Int, urlquery: Map[String, String]): Future[(Int, Seq[((ContractRow, CustomerRow),Option[BillRow])])] = {
    var joinquery = (for{
      contractlist <- filterQueryContract(contractquery, billquery,urlquery) join filterQueryCustomer(customerquery, urlquery) on (_.customerId === _.id) joinLeft billquery on (_._1.id === _.contractId)
    }yield (contractlist)
    ).sortBy(contractlist => contractlist._1._1.id.desc)

    val pagelistsql = (for {
        count <- joinquery.length.result
        joinquerylist <- joinquery.drop((page-1) * offset).take(offset).result
    }yield (count, joinquerylist)
    )
    dbConfig.db.run(pagelistsql.transactionally)
  }


  def findById(id: Long): Future[Option[ContractRow]] = {
    dbConfig.db.run(contractquery.filter(_.id === id).result.headOption)
  }


  def create(contract: ContractRow): Future[Int] = {
    val c = contract.copy(
        isDisabled = Option(false),
        createdDate = Option(new Date),
        updatedDate = new Date
    )
    dbConfig.db.run(contractquery += c)
  }

    def create(contractbill: ContractBillRow, bill: BillRow): Future[Long] = {
      val contractnew = new ContractRow(0, contractbill.customerId, contractbill.status, contractbill.comment, contractbill.contractDate, contractbill.cancelDate, Option(false), Option(new Date),new Date)
      val action =
      (for {
       newId <- (contractquery returning contractquery.map(_.id) += contractnew)
       // その結果を使って更新
       _ <- billquery += bill.copy(
           contractId = newId,
           createdDate = Option(new Date),
            updatedDate = new Date
           )
    } yield (newId) )

     dbConfig.db.run(action.transactionally)
  }

  def update(contract: ContractRow): Future[Unit] = {
    dbConfig.db.run(contractquery.filter(_.id === contract.id).update(contract).map(_ => ()))
  }

  def update_mappinged(contract: ContractRow): Future[Int] = {
     dbConfig.db.run(contractquery.filter(_.id === contract.id).map(
         c => (
            c.customerId,
            c.status,
            c.comment,
            c.contractDate,
            c.cancelDate,
            c.isDisabled,
            c.updatedDate
            )
      ).update(
          (
        contract.customerId,
        contract.status,
        contract.comment,
        contract.contractDate,
        contract.cancelDate,
        contract.isDisabled,
        new Date
        )
      )
    )
  }
 
  def update_mappinged(contractbill: ContractBillRow, bill: BillRow): Future[Int] = {
    val action =
    (for {
       updatedContractId <- contractquery.filter(_.id === contractbill.id).map(
           c => (
              c.customerId,
              c.status,
              c.comment,
              c.contractDate,
              c.cancelDate,
              c.isDisabled,
              c.updatedDate
              )
        ).update(
            (
          contractbill.customerId,
          contractbill.status,
          contractbill.comment,
          contractbill.contractDate,
          contractbill.cancelDate,
          contractbill.isDisabled,
          new Date
          )
        )
       // その結果を使って更新
       _ <- billquery.filter(b => (b.id === bill.id) && (b.contractId === contractbill.id)).map(
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
    } yield (updatedContractId) )
    
    dbConfig.db.run(action.transactionally)
  }
  def delete(id: Long): Future[Int] = dbConfig.db.run(contractquery.filter(_.id === id).delete)

   def deleteAll(id: Long): Future[Int] = {
       val action =
      (for {
         deleteContractId <- contractquery.filter(_.id === id).delete
         _ <- billquery.filter(_.contractId === id).delete
      }yield (deleteContractId) )
    dbConfig.db.run(action.transactionally)
  }
  
  def filterQueryContract(tablequery:TableQuery[Contract], subtablequery:TableQuery[Bill], urlquery: Map[String, String]) = {
    var returnquery = tablequery.filter(_.isDisabled === false)
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "customerId" => if(querytuple._2 != "-1") returnquery = returnquery.filter(_.customerId === querytuple._2.toLong.bind) 
          case "status" =>  if(querytuple._2 != "-1") returnquery = returnquery.filter(_.status === StringHelper.trim(querytuple._2).bind) 
          case "contractDateFrom" => if(!querytuple._2.isEmpty()) returnquery = returnquery.filter(_.contractDate >= DateHelper.stringToDate(querytuple._2, "yyyy-MM-dd").bind) 
          case "contractDateTo" =>  if(!querytuple._2.isEmpty()) returnquery = returnquery.filter(_.contractDate <= DateHelper.stringToDate(querytuple._2, "yyyy-MM-dd").bind) 
          case "cancelDateFrom" =>  if(!querytuple._2.isEmpty()) returnquery = returnquery.filter(_.cancelDate >= DateHelper.stringToDate(querytuple._2, "yyyy-MM-dd").bind) 
          case "contractDateTo" =>  if(!querytuple._2.isEmpty()) returnquery = returnquery.filter(_.cancelDate <= DateHelper.stringToDate(querytuple._2, "yyyy-MM-dd").bind) 
          case "billName" | "billEmail" | "billTel" | "billAddress" =>
            var subquery = subtablequery.filter(_.id > 0.toLong)
            querytuple._1 match{
              case "billName" => if(!StringHelper.trim(querytuple._2).isEmpty()) subquery = subquery.filter(_.billName like (StringHelper.trim(querytuple._2) + "%").bind) 
              case "billEmail" => if(!StringHelper.trim(querytuple._2).isEmpty()) subquery = subquery.filter(_.billEmail like (StringHelper.trim(querytuple._2) + "%").bind) 
              case "billTel" => if(!StringHelper.trim(querytuple._2).isEmpty()) subquery = subquery.filter(_.billTel like (StringHelper.trim(querytuple._2) + "%").bind) 
              case "billAddress" => if(!StringHelper.trim(querytuple._2).isEmpty()) subquery = subquery.filter(_.billAddress like (StringHelper.trim(querytuple._2) + "%").bind) 
              case _ => 
            }
            returnquery = returnquery.filter(_.id in subquery.map(_.contractId))
          case _ => 
        }
    }
    returnquery
  }
  def filterQueryBill(tablequery:TableQuery[Bill], urlquery: Map[String, String]) = {
    var returnquery = tablequery.filter(_.id > 0.toLong)
     urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "billName" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.billName like (StringHelper.trim(querytuple._2) + "%").bind) 
          case "billEmail" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.billEmail like (StringHelper.trim(querytuple._2) + "%").bind) 
          case "billTel" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.billTel like (StringHelper.trim(querytuple._2) + "%").bind) 
          case "billAddress" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.billAddress like (StringHelper.trim(querytuple._2) + "%").bind) 
          case _ => 
        }
    }
    returnquery
  }
  def filterQueryCustomer(tablequery:TableQuery[Customer], urlquery: Map[String, String]) = {
    var returnquery = tablequery.filter(_.id > 0.toLong)
    urlquery.foreach { querytuple =>
        querytuple._1 match{
          case "customerName" => if(!StringHelper.trim(querytuple._2).isEmpty()) returnquery = returnquery.filter(_.name like (StringHelper.trim(querytuple._2) + "%").bind) 
          case _ => 
        }
    }
    returnquery
  }

}

