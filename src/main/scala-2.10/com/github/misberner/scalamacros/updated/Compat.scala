package com.github.misberner.scalamacros.updated

object Compat {
  type Context = scala.reflect.macros.Context
  
  
  def CASEACCESSOR(implicit c: Context) = scala.reflect.internal.Flags.CASEACCESSOR.asInstanceOf[c.universe.FlagSet]
  def SYNTHETIC(implicit c: Context) = scala.reflect.internal.Flags.SYNTHETIC.asInstanceOf[c.universe.FlagSet]
  def termNames(implicit c: Context) = c.universe.nme
  def TermName(nme: String)(implicit c: Context) = c.universe.newTermName(nme)
  def TypeName(nme: String)(implicit c: Context) = c.universe.newTypeName(nme)
}
