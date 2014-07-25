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
package com.github.misberner.scalamacros.updated.macroimpl

import MacroCompat._
import com.github.misberner.scalamacros.updated.generator.UpdatedMethodGenerator

/**
 * Implementation of the `generateUpdated` macro.
 * <p>
 * An incredibly helpful source of inspiration for implementing this macro was
 * Adam Warski's tutorial/example on automatic generation of delegate
 * methods, see https://github.com/adamw/scala-macro-aop and
 * http://www.warski.org/blog/2013/09/automatic-generation-of-delegate-methods-with-macro-annotations/ . 
 */
private[updated] object generateUpdatedMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*) : c.Expr[Any] = {
    import c.universe._
    
    val gen = new UpdatedMethodGenerator(c.universe)
    
    val inputs = annottees.map(_.tree).toList
    
    if (inputs.isEmpty) {
      throw new AssertionError("Got empty list of annottees! This should not happen!") 
    }
    
    val outputs = inputs match {
      case (cls_ : ClassDef) :: rest =>
        val cls = cls_.asInstanceOf[gen.u.ClassDef]
        if (!gen.isCaseClass(cls)) {
          c.error(c.enclosingPosition, "Annotation can only be used on case classes")
          inputs
        }
        else {
          gen.process(cls).asInstanceOf[ClassDef] :: rest
        }
      case _ =>
        c.error(c.enclosingPosition, "Annotation can only be used on classes")
        inputs
    }
    
    c.Expr[Any](outputs.head)
  }
}
