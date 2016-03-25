package models

import java.sql.Timestamp
import java.util.Date
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  
  implicit def javaDateMapper = MappedColumnType.base[Date, Timestamp](
      dt => new Timestamp(dt.getTime),
      ts => new Date(ts.getTime)
  )
  
  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Customer.schema ++ PlayEvolutions.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Customer
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param tel Database column tel SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param comment Database column comment SqlType(MEDIUMTEXT), Length(16777215,true), Default(None)
   *  @param actionDate Database column action_date SqlType(DATETIME), Default(None)
   *  @param notificationPeriod Database column notification_period SqlType(INT), Default(None)
   *  @param notificationDate Database column notification_date SqlType(DATETIME), Default(None)
   *  @param isDisabled Database column is_disabled SqlType(TINYINT), Default(None)
   *  @param createdDate Database column created_date SqlType(DATETIME), Default(None)
   *  @param updatedDate Database column updated_date SqlType(TIMESTAMP) */
  case class CustomerRow(id: Long, name: Option[String] = None, email: Option[String] = None, tel: Option[String] = None, address: Option[String] = None, comment: Option[String] = None, actionDate: Option[Date] = None, notificationPeriod: Option[Int] = None, notificationDate: Option[Date] = None, isDisabled: Option[Boolean] = None, createdDate: Option[Date] = None, updatedDate: Date)
  /** GetResult implicit for fetching CustomerRow objects using plain SQL queries */
  implicit def GetResultCustomerRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Date]], e3: GR[Option[Int]], e4: GR[Option[Boolean]], e5: GR[Date]): GR[CustomerRow] = GR{
    prs => import prs._
    CustomerRow.tupled((<<[Long], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Date], <<?[Int], <<?[Date], <<?[Boolean], <<?[Date], <<[Date]))
  }
  /** Table description of table customer. Objects of this class serve as prototypes for rows in queries. */
  class Customer(_tableTag: Tag) extends Table[CustomerRow](_tableTag, "customer") {
    def * = (id, name, email, tel, address, comment, actionDate, notificationPeriod, notificationDate, isDisabled, createdDate, updatedDate) <> (CustomerRow.tupled, CustomerRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), name, email, tel, address, comment, actionDate, notificationPeriod, notificationDate, isDisabled, createdDate, Rep.Some(updatedDate)).shaped.<>({r=>import r._; _1.map(_=> CustomerRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(255,varying=true), O.Default(None))
    /** Database column email SqlType(VARCHAR), Length(255,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(255,varying=true), O.Default(None))
    /** Database column tel SqlType(VARCHAR), Length(255,true), Default(None) */
    val tel: Rep[Option[String]] = column[Option[String]]("tel", O.Length(255,varying=true), O.Default(None))
    /** Database column address SqlType(VARCHAR), Length(255,true), Default(None) */
    val address: Rep[Option[String]] = column[Option[String]]("address", O.Length(255,varying=true), O.Default(None))
    /** Database column comment SqlType(MEDIUMTEXT), Length(16777215,true), Default(None) */
    val comment: Rep[Option[String]] = column[Option[String]]("comment", O.Length(16777215,varying=true), O.Default(None))
    /** Database column action_date SqlType(DATETIME), Default(None) */
    val actionDate: Rep[Option[Date]] = column[Option[Date]]("action_date", O.Default(None))
    /** Database column notification_period SqlType(INT), Default(None) */
    val notificationPeriod: Rep[Option[Int]] = column[Option[Int]]("notification_period", O.Default(None))
    /** Database column notification_date SqlType(DATETIME), Default(None) */
    val notificationDate: Rep[Option[Date]] = column[Option[Date]]("notification_date", O.Default(None))
    /** Database column is_disabled SqlType(TINYINT), Default(None) */
    val isDisabled: Rep[Option[Boolean]] = column[Option[Boolean]]("is_disabled", O.Default(None))
    /** Database column created_date SqlType(DATETIME), Default(None) */
    val createdDate: Rep[Option[Date]] = column[Option[Date]]("created_date", O.Default(None))
    /** Database column updated_date SqlType(TIMESTAMP) */
    val updatedDate: Rep[Date] = column[Date]("updated_date")
  }
  /** Collection-like TableQuery object for table Customer */
  lazy val Customer = new TableQuery(tag => new Customer(tag))

  /** Entity class storing rows of table PlayEvolutions
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param hash Database column hash SqlType(VARCHAR), Length(255,true)
   *  @param appliedAt Database column applied_at SqlType(TIMESTAMP)
   *  @param applyScript Database column apply_script SqlType(MEDIUMTEXT), Length(16777215,true), Default(None)
   *  @param revertScript Database column revert_script SqlType(MEDIUMTEXT), Length(16777215,true), Default(None)
   *  @param state Database column state SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param lastProblem Database column last_problem SqlType(MEDIUMTEXT), Length(16777215,true), Default(None) */
  case class PlayEvolutionsRow(id: Int, hash: String, appliedAt: java.sql.Timestamp, applyScript: Option[String] = None, revertScript: Option[String] = None, state: Option[String] = None, lastProblem: Option[String] = None)
  /** GetResult implicit for fetching PlayEvolutionsRow objects using plain SQL queries */
  implicit def GetResultPlayEvolutionsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Option[String]]): GR[PlayEvolutionsRow] = GR{
    prs => import prs._
    PlayEvolutionsRow.tupled((<<[Int], <<[String], <<[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table play_evolutions. Objects of this class serve as prototypes for rows in queries. */
  class PlayEvolutions(_tableTag: Tag) extends Table[PlayEvolutionsRow](_tableTag, "play_evolutions") {
    def * = (id, hash, appliedAt, applyScript, revertScript, state, lastProblem) <> (PlayEvolutionsRow.tupled, PlayEvolutionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(hash), Rep.Some(appliedAt), applyScript, revertScript, state, lastProblem).shaped.<>({r=>import r._; _1.map(_=> PlayEvolutionsRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column hash SqlType(VARCHAR), Length(255,true) */
    val hash: Rep[String] = column[String]("hash", O.Length(255,varying=true))
    /** Database column applied_at SqlType(TIMESTAMP) */
    val appliedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("applied_at")
    /** Database column apply_script SqlType(MEDIUMTEXT), Length(16777215,true), Default(None) */
    val applyScript: Rep[Option[String]] = column[Option[String]]("apply_script", O.Length(16777215,varying=true), O.Default(None))
    /** Database column revert_script SqlType(MEDIUMTEXT), Length(16777215,true), Default(None) */
    val revertScript: Rep[Option[String]] = column[Option[String]]("revert_script", O.Length(16777215,varying=true), O.Default(None))
    /** Database column state SqlType(VARCHAR), Length(255,true), Default(None) */
    val state: Rep[Option[String]] = column[Option[String]]("state", O.Length(255,varying=true), O.Default(None))
    /** Database column last_problem SqlType(MEDIUMTEXT), Length(16777215,true), Default(None) */
    val lastProblem: Rep[Option[String]] = column[Option[String]]("last_problem", O.Length(16777215,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table PlayEvolutions */
  lazy val PlayEvolutions = new TableQuery(tag => new PlayEvolutions(tag))
}
