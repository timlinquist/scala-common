package org.mulesoft.common.core

import org.mulesoft.common.functional.Monad
import org.mulesoft.common.functional.MonadInstances.Identity
import scala.language.higherKinds

/** Runs a function I => O proxied through a cache
  * @tparam I input type of the function
  * @tparam O output type of the function
  * @tparam C context for the output type (e.g. Option)
  */
trait CachedFunction[I, O, C[_]] {
  protected val cache: Cache[I, O] = Cache.empty[I, O]

  def invalidateCache(): Unit = cache.invalidate()

  /**
    * Executes the [[run]] function with caching. On cache hit returns the cached result it otherwise execute run and
    * store the result in the cache.
    * @param i input for the [[run]] function
    * @param monad monad for mapping over the result context
    * @return
    */
  def runCached(i: I)(implicit monad: Monad[C]): C[O] =
    cache
      .get(i)
      .map(monad.pure)
      .getOrElse {
        val resultContext = run(i)
        monad.map(resultContext)(result => {
          cache.put(i, result)
          result
        })
      }

  def run: I => C[O]
}

object CachedFunction {
  def fromMonadic[I, O, C[_]](fn: I => C[O]): CachedFunction[I, O, C] = new CachedFunction[I, O, C] {
    override def run: I => C[O] = fn
  }

  def from[I, O](fn: I => Identity[O]): CachedFunction[I, O, Identity] = new CachedFunction[I, O, Identity] {
    override def run: I => Identity[O] = fn
  }
}
