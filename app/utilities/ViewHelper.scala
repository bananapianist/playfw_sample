package utilities

import play.twirl.api.HtmlFormat

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
}