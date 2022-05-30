package thecoda.aws

import scala.collection.immutable.SortedMap

trait SparsePrint {
  self: Product =>
  def sparseProduct: Product = new Product {
    def elemMap: SortedMap[String, Any] = SortedMap.from(
      self.productElementNames zip self.productIterator map {
        case (k, Some(v)) => Some(k -> v)
        case (k, None) => None
        case (k, v) => Some(k -> v)
      } collect {
        case Some(kv) => kv
      }
    )

    def keyValueAt(n: Int): (String, Any) =
      if (n >= 0 && n < productArity) elemMap.drop(n).head
      else throw new IndexOutOfBoundsException(s"$n is out of bounds (min 0, max ${productArity - 1})")

    override def productPrefix: String = self.productPrefix

    override def productArity: Int = elemMap.size

    override def productElement(n: Int): Any = keyValueAt(n)._2

    override def productElementName(n: Int): String = keyValueAt(n)._1

    override def canEqual(that: Any): Boolean = self.canEqual(that)

    override def productIterator: Iterator[Any] = elemMap.valuesIterator

    override def productElementNames: Iterator[String] = elemMap.keysIterator
  }

}
