package org.yaml.model

import scala.collection.GenSeq

/**
  * A Yaml Sequence
  */
class YSequence private(c: IndexedSeq[YPart]) extends YAggregate(c) with YValue {

  /** The Sequence nodes */
  val nodes: IndexedSeq[YNode] = c.collect { case a: YNode => a }.toArray[YNode]

  override def hashCode(): Int = nodes.hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case s: YSequence => this.nodes.equals(s.nodes)
    case s: GenSeq[_] => this.nodes.equals(s)
    case _            => false
  }

  override def toString: String = nodes.mkString("[", ", ", "]")
}

object YSequence {
  val empty                                  = new YSequence(IndexedSeq.empty)
  def apply(c: IndexedSeq[YPart]): YSequence = new YSequence(c)
  def apply(elems: YNode*): YSequence     = new YSequence(elems.toArray[YNode])
}
