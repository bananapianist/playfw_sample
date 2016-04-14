package utilities

import play.twirl.api.HtmlFormat
import java.util.Date

import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

 import play.api.Logger

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
  
  def makeOptionSeq(optionvalue:Seq[String], messagekeyname:String, messages:play.api.i18n.Messages) ={
    this.makeSeqWithMessageApi(optionvalue, messagekeyname, messages).toSeq.sortBy(_._1)
  }
  def makeOptionMap(optionvalue:Seq[String], messagekeyname:String, messages:play.api.i18n.Messages) ={
    this.makeSeqWithMessageApi(optionvalue, messagekeyname, messages)
  }
  def makeSeqWithMessageApi( s:Seq[String] , messagekeyname:String, messages:play.api.i18n.Messages) = {
    def count( l:Seq[String], m:Map[String, String] ):Map[String, String] = {
      if( l.isEmpty ){  // lが空Listだったら再帰終了
        m
      }else {
        val str = messages(messagekeyname + '.' + l.head )
        count( l.tail, m ++ Map( l.head -> str ) )
      }
    }
    
     count( s, Map.empty[String,String] )
  }
  def getOptionView(optionval:String, maplist:Map[String, String]):String = {
    maplist(optionval)
    
  }
}