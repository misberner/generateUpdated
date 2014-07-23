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

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation


/**
 * Macro annotation for enriching case classes with an `updated` method.
 * <p>
 * The `updated` method can be used to create modified copies of a case class
 * instance. For every case class parameter `x` of type `T`, the `updated`
 * method contains a parameter named `x` of type `T => T`. The return value
 * of `updated` is a copy where each case class parameter is modified according
 * to the function passed to `updated`. All parameters of `updated` have default
 * values, namely functions that leave the value unchanged.  
 * <p>
 * It is illegal to use this annotation on anything but a case class.
 */
class generateUpdated extends StaticAnnotation {
  def macroTransform(annottees: Any*) : Any = macro generateUpdatedMacro.impl
}
