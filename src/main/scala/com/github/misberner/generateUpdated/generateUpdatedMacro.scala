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
package com.github.misberner.generateUpdated

import scala.reflect.macros.blackbox.Context

/**
 * Implementation of the `generateUpdated` macro.
 * <p>
 * An incredibly helpful source of inspiration for implementing this macro was
 * Adam Warski's tutorial/example on automatic generation of delegate
 * methods, see https://github.com/adamw/scala-macro-aop and
 * http://www.warski.org/blog/2013/09/automatic-generation-of-delegate-methods-with-macro-annotations/ . 
 */
private[generateUpdated] object generateUpdatedMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*) : c.Expr[Any] = {
    import c.universe._
    import c.universe.Flag._
    
    // scala.Function1, as a raw type
    val function1Ref = Select(Ident(TermName("scala")), TypeName("Function1"))
    
    /*
     * Generates the `updated` method (DefDef) for the given class.
     */
    def generateUpdated(clsName: TypeName, clsType: Tree, caseVals: List[ValDef]) = {
      val this_ = This(clsName)
      
      // Params, of type T => T with default value _ => this.name
      val params = caseVals map { case vd @ ValDef(mods, name, tpe, _) =>
        val lifted = AppliedTypeTree(function1Ref, List(tpe, tpe))
        val defVal = Select(this_, name)
        val rhs = Function(List(ValDef(Modifiers(PARAM), TermName("x$1"), tpe, EmptyTree)), defVal)
        ValDef(Modifiers(PARAM | DEFAULTPARAM), name, lifted, rhs)
      }
      
      // Arguments that will be passed to the constructor: name(this.name), where
      // name : T => T is the function parameter
      val ctorArgs = caseVals map { case vd @ ValDef(_, name, _, _) =>
        val defVal = Select(this_, name)
        Apply(Ident(name), List(defVal))
      }
      
      // Function body: new clsType(name1(this.name), ...)
      val body = Apply(Select(New(clsType), termNames.CONSTRUCTOR), ctorArgs)
      
      DefDef(Modifiers(SYNTHETIC), TermName("updated"), List(), List(params), clsType, body)
    }
    
    /**
     * Processes (i.e., adds the `updated` method) to the given class. If this was
     * successful, Some(modifiedClass) is returned. Otherwise, the return value is None.
     */
    def process(cls: ClassDef, rest: List[Tree]) : Option[ClassDef] = {
      val ClassDef(mods, clsName, tparams, Template(parents, self, body)) = cls
      if (!mods.hasFlag(Flag.CASE)) {
        c.error(c.enclosingPosition, "Annotation can only be used on case classes")
        None
      }
      else {
        val clsType = AppliedTypeTree(Ident(clsName), tparams.map{tp => Ident(tp.name)})
        
        val caseVals = body.collect {
          case vd @ ValDef(mods, _, _, _) if mods.hasFlag(CASEACCESSOR) => vd
        }
        
        val updated = generateUpdated(clsName, clsType, caseVals)
        Some(ClassDef(mods, clsName, tparams, Template(parents, self, updated :: body)))
      }
    }
    
    val inputs = annottees.map(_.tree).toList
    val outputs = inputs match {
      case (cls : ClassDef) :: rest =>
        process(cls, rest) match {
          case Some(newCls) => newCls :: rest
          case _ => inputs 
        }
      case _ =>
        c.error(c.enclosingPosition, "Annotation can only be used on classes")
        inputs
    }
    c.Expr[Any](outputs.head)
  }
}
