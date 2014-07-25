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
package com.github.misberner.scalamacros.updated.generator

import scala.reflect.api.Universe

/**
 * This class contains the generation logic for `updated` methods for case
 * classes.
 * 
 * @author Malte Isberner
 */
class UpdatedMethodGenerator(u_ : Universe, val methName: String = "updated") {
  implicit val u = u_
  
  import u._
  import u.Flag._
  import UniverseCompat._
  
  // scala.Function1, as a raw type
  val function1Ref = Select(Ident(TermName("scala")), TypeName("Function1"))
    
  /**
   * Checks if the given class definition is a case class 
   */
  def isCaseClass(cls: ClassDef) = {
    val ClassDef(mods, _, _, _) = cls
    mods.hasFlag(CASE)
  }
  
  /**
   * Checks if the given class definition already contains a method which looks
   * like an `updated` method.
   * <p>
   * A method "look" like the `updated` method if it (a) has the same name, and
   * (b) has the same number of parameters that the method to be generated
   * would have.
   */  
  def hasUpdatedLikeMethod(cls: ClassDef, numVals: Int) = {
    val ClassDef(_, _, _, Template(_, _, body)) = cls
    body exists { 
      case DefDef(_, name, _, List(params), _, _) =>
        name.toString == methName && params.size == numVals
      case _ => false
    }
  }
  
  /*
   * Generates the `updated` method (DefDef) for the given class.
   */
  protected[this] def generateUpdated(clsName: TypeName, clsType: Tree, caseVals: List[ValDef]) = {
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
  def process(cls: ClassDef) : ClassDef = {
    if (!isCaseClass(cls)) {
      throw new IllegalArgumentException("Tried to generate `updated` method for " +
                                         "non-case class")
    }
    
    val ClassDef(mods, clsName, tparams, Template(parents, self, body)) = cls
    val clsType =
      if (tparams.isEmpty) Ident(clsName)
      else AppliedTypeTree(Ident(clsName), tparams.map{tp => Ident(tp.name)})
      
    val caseVals = body.collect {
      case vd @ ValDef(mods, _, _, _) if mods.hasFlag(CASEACCESSOR) => vd
    }
        
    val updated = generateUpdated(clsName, clsType, caseVals)
    ClassDef(mods, clsName, tparams, Template(parents, self, updated :: body))
  }
  
  /**
   * Checks if the generation can (probably) be applied to the given class definition.
   * <p>
   * This is the case if the class is a case class, and does not yet contain a method
   * that looks like the method to be generated. The notion of "looks like" is relatively
   * weak: any class with the same name and the same number of argument is interpreted
   * as a possibly conflicting method. This is due to the fact that the generation takes
   * place before any resolution, hence the information we would require is not available.
   */
  def isApplicable(cls: ClassDef) : Boolean = {
    if (!isCaseClass(cls))
      false
    else {
      val ClassDef(_, _, _, Template(_, _, body)) = cls
      val numCaseVals = body.count {
        case ValDef(mods, _, _, _) => mods.hasFlag(CASEACCESSOR)
        case _ => false
      }
      !hasUpdatedLikeMethod(cls, numCaseVals)
    }
  }
    

}