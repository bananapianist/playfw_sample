package utilities


object StringHelper {
  /**
   * ひらがなをカタカナに変換する
   */
  def convertHiraganaToKatakana(str:String):String = {
    var sb = new StringBuilder
    val map = (('ぁ' to 'ん').zip(('ァ' to 'ン'))).toMap
    for(c <- str) sb.append(map.getOrElse(c, c))
    sb.toString
  }
  /**
   * カタカナをひらがなに変換する
   */
  def convertKatakanaToHiragana(str:String):String = {
    var sb = new StringBuilder
    val map = (('ぁ' to 'ん').zip(('ァ' to 'ン'))).toMap
    for(c <- str) sb.append(map.getOrElse(c, c))
    sb.toString
  }
}