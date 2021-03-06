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

import scala.tools.nsc.plugins.PluginComponent

/**
 * Compatibility enabler for the nsc.plugins.PluginComponent class.
 * 
 *  @author Malte Isberner 
 */
abstract class CompatPluginComponent extends PluginComponent {
  // In 2.10, there is no description field. Without this declaration,
  // it would be illegal to write `override val description` in the component
  // declaration.
  val description : String
}
