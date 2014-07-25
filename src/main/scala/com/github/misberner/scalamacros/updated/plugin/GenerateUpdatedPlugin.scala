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
package com.github.misberner.scalamacros.updated.plugin

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import nsc.transform.Transform
import nsc.transform.TypingTransformers
import nsc.transform.InfoTransform

import generator.UpdatedMethodGenerator

/**
 * New Scala Compiler plugin to automatically generate `updated` methods
 * for case classes.
 * 
 * @author Malte Isberner
 */
class GenerateUpdatedPlugin(val global: Global) extends Plugin {
  plugin =>
    
  import global._

  val name = "generateUpdated"
  val description = "Generates `updated` methods for case classes"
    
  val components = List[PluginComponent](Component)
  
  val gen = new UpdatedMethodGenerator(global)
  
  // Main component
  private object Component extends CompatPluginComponent
                           with    Transform
                           with    TypingTransformers {
    
    val global: plugin.global.type = plugin.global
    
    val runsAfter = List[String]("parser")
    override val runsRightAfter = Some("parser")
    
    val phaseName = plugin.name
    override val description = plugin.description
    
    def newTransformer(unit: CompilationUnit) = new GenerateUpdatedTransformer(unit)
    
    // Applies the transformation adding an updated method wherever applicable
    class GenerateUpdatedTransformer(unit: CompilationUnit) extends TypingTransformer(unit) {
      override def transform(tree: Tree) : Tree = {
        val newTree = tree match {
          case cls_ : ClassDef =>
            val cls = cls_.asInstanceOf[gen.u.ClassDef]
            if (gen.isApplicable(cls)) gen.process(cls).asInstanceOf[ClassDef]
            else cls_
          case _ => tree
        }
        super.transform(newTree)
      }
    }
  }
}
