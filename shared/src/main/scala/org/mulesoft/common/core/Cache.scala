package org.mulesoft.common.core

import scala.collection.mutable

/**
  * Generic cache that stores results of operations with input type I and output type O
  *
  * @tparam I input type
  * @tparam O output type
  */
class Cache[I, O] {
  protected val map: mutable.HashMap[I, O] = mutable.HashMap.empty

  def put(input: I, output: O): Unit = map.put(input, output)
  def get(input: I): Option[O]       = map.get(input)
  def remove(input: I): Unit         = map.remove(input)
}

object Cache {
  def empty[I, O] = new Cache[I, O]
}


