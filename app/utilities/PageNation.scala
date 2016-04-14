package utilities

/** PageNation Class */
case class PageNation[T](currentPageNum: Int     // 現在のページ番号
                        ,totalDataCount: Int     // 全体件数
                        ,maxPageNum: Int = 5
                        ) {
  /** 最大ページ番号を返却 */
  def totalPageNum: Int = {
    if (totalDataCount % maxPageNum > 0) {
      // 端数が存在する場合１ページ追加
      totalDataCount / maxPageNum + 1
    } else {
      totalDataCount / maxPageNum
    }
  }

  /** ページ番号リストの作成起点となる番号を返却 */
  def startIndex: Int = {
    var ret: Int = 1
    // 現在のページ番号
    if (currentPageNum + PageNation.bfNum + 1 > totalPageNum) {
      ret = totalPageNum - (PageNation.bfNum * 2)
    } else {
      ret = currentPageNum - PageNation.bfNum
    }
    // 0以下だったら1を返却
    if (ret <= 0) {
      ret = 1
    }
    ret
  }

  /** ページ番号のリストを返却 */
  def pageNumList: List[Int] = {
    // 返却用
    var pageNumList: List[Int] = List.empty
    // ページ番号リストを作成
    for (i: Int <- startIndex to totalPageNum if pageNumList.size < (PageNation.bfNum * 2 + 1) && pageNumList.size < totalPageNum) {
      if (i >= 1 && totalPageNum >= i) {
        pageNumList = i :: pageNumList
      }
      i + 1
    }
    pageNumList.reverse
  }
}

/** PageNation Object */
object PageNation {
  val bfNum     : Int = 3 // 現在ページの両脇ページ番号を表示する件数
}
