package com.github.misberner.scalamacros.updated.generator

import scala.reflect.api.Universe

/**
 * Compatibility enabler for the `scala.reflect.api.Universe` trait
 * and its `Flags` member.
 * 
 * @author Malte Isberner
 */
object UniverseCompat {
  // Not a member of Flags in 2.10 
  def CASEACCESSOR(implicit u: Universe) =
    scala.reflect.internal.Flags.CASEACCESSOR.toLong.asInstanceOf[u.FlagSet]
  // Not a member of Flags in 2.10
  def SYNTHETIC(implicit u: Universe) =
    scala.reflect.internal.Flags.SYNTHETIC.toLong.asInstanceOf[u.FlagSet]
  
  // The values accessible through termNames in 2.11+ used to be
  // in nme (deprecated since 2.11)
  def termNames(implicit u: Universe) = u.nme
  // Old (2.10) instantiation of term names (deprecated since 2.11)
  def TermName(nme: String)(implicit u: Universe) = u.newTermName(nme)
  // Old (2.10) instantiation of type names (deprecated since 2.11)
  def TypeName(nme: String)(implicit u: Universe) = u.newTypeName(nme)
}
