# Automatic Generation of `updated` Methods
This small library / compiler plugin allows to automatically generate `updated` methods for case classes in Scala.

## In a Nutshell

The goal is to have a means of conveniently manipulating single fields of case class instances in an immutable fashion (i.e., returning a new object with the changed value). If the notion of "manipulation" were limited to mere assignments, then the copy method accomplishes just this. Take the following example:
```scala
case class Person(name: String, age: Int)
```
If we want to change the name of `var p = Person("Jane Doe", 42)`, we can write `p = p.copy(name = "John Doe")`. For realizing his birthday, we could write `p = p.copy(age = p.age + 1)`, but here we need to repeat both `p` and `age`. Furthermore, this can quickly get cumbersome if `p` were not a plain `var` or `val`, but a more complex expression.

The idea of the `updated` method is to provide not a *new value*, but a *value transformer* (i.e., a function) to change the current value. We could then rewrite the "birthday" statement as `p = p.updated(age = _ + 1)`. An example realization can be found in [this Gist](https://gist.github.com/misberner/eec5e74f759353da3384). However, with this library/plugin, this manual implementation is no longer necessary.

## Usage as a Compiler Plugin

Once the compiler plugin is enabled, `updated` methods will be automatically generated for all case classes in the respective compilation units (exceptions see below). No further user interaction is required.

### Limitations
To enable reuse of the generation logic that was originally written for a macro context only, the compiler plugin runs at a very early phase (before any resolution is performed). This makes it impossible to determine if a method with the same signature as the `updated` method to be generated already exists in a case class: types in the signature might be unresolved aliases.

If the `updated` method were added anyway, this would result in a compilation error; the consequence being that either existing code has to be changed, or the plugin has to be disabled for the whole project. Since both options are undesirable, the plugin takes a conservative approach: if a case class has a method that "looks like" it would conflict with the method to be generated, no `updated` method is autogenerated (this can only be enforced using the
macro annotation approach). This is the case if it has a method with the same name and the same number of arguments as the generated method would have.

### Usage in SBT
Add the following line to your build configuration (in `build.sbt` or `Build.scala`):
```scala
addCompilerPlugin("com.github.misberner.scalamacros" % "updated" % "0.0.3" cross CrossVersion.binary)
```

### Usage in Maven
Add the following snippet to the `configuration` section of `scala-maven-plugin` in your POM:
```xml
<compilerPlugins>
  <compilerPlugin>
    <groupId>com.github.misberner.scalamacros</groupId>
    <artifactId>updated_<YOUR.SCALA.VERSION></artifactId>
    <version>0.0.3</version>
  </compilerPlugin>
</compilerPlugins>
```

## Usage as a Macro Annotation

In order to allow a more fine-grained control over which case classes should be extended with an `updated` method, the functionality is also available as a macro annotation (note: macro annotations require [Macro Paradise](https://github.com/scalamacros/paradise); instructions for setting this up with either SBT or Maven can be found [here](http://docs.scala-lang.org/overviews/macros/paradise.html)). The usage is still very simple: just annotate the case class that should be extended with an `updated` method with the `com.github.misberner.scalamacros.updated.generateUpdated` annotation:

```scala
import com.github.misberner.scalamacros.updated.generateUpdated

@generateUpdated
case class MyClass(intAttr: Int, listAttr: List[String], mapAttr: Map[String,Int]) {
  // ...
}
```

### Usage in SBT
After enabling Macro Paradise (see above), add the following line to your build configuration (in `build.sbt` or `Build.scala`):
```scala
libraryDependencies += "com.github.misberner.scalamacros" % "updated" % "0.0.3" % provided cross CrossVersion.binary
```

### Usage in Maven
After enabling Macro Paradise (see above), add the following snippet to the `dependencies` section in your POM:
```xml
<dependency>
  <groupId>com.github.misberner.scalamacros</groupId>
  <artifactId>updated_<YOUR.SCALA.VERSION></artifactId>
  <version>0.0.3</version>
  <scope>provided</scope>  <!-- non-transitive -->
</dependency>
```

# Acknowledgement
Realizing this library would not have been possible without Adam Warski's excellent [blog post](http://www.warski.org/blog/2013/09/automatic-generation-of-delegate-methods-with-macro-annotations/)/[example](https://github.com/adamw/scala-macro-aop) on automatic generation of delegate
