package utilities

import play.twirl.api.HtmlFormat
import java.util.Date

object ViewHelper {
  def nl2br(inputstr: Option[String], withescape:Boolean = true):String ={
    inputstr match{
      case Some(str) => 
        if (withescape) 
          HtmlFormat.escape(str).toString.replaceAll("(\r\n|\r|\n)","<br/>")
        else
          str.replaceAll("(\r\n|\r|\n)","<br/>")
      case _ => ""
    }
    
  }
  
  def dateFormat(inputdata: Option[Date], formatstr: String):String = {
    inputdata match{
      case Some(date) => formatstr format date
      case _ => ""
    }
  }
}