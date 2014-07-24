/*
 * Copyright (c) 2014, Malte Isberner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.misberner.scalamacros.updated

object Compat {
  type Context = scala.reflect.macros.Context
  
  def CASEACCESSOR(implicit c: Context) = scala.reflect.internal.Flags.CASEACCESSOR.toLong.asInstanceOf[c.universe.FlagSet]
  def SYNTHETIC(implicit c: Context) = scala.reflect.internal.Flags.SYNTHETIC.toLong.asInstanceOf[c.universe.FlagSet]
  
  def termNames(implicit c: Context) = c.universe.nme
  def TermName(nme: String)(implicit c: Context) = c.universe.newTermName(nme)
  def TypeName(nme: String)(implicit c: Context) = c.universe.newTypeName(nme)
}
