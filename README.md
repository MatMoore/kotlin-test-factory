# Test factory

Test factory lets you define factory classes for use in unit tests,
inspired by FactoryBot in ruby. Test factories provide default values
for constructing kotlin classes, so that you only have to specify things
that matter to your test.

This is an experimental learning project.

## Usage

### Defining test factories

Test factories should inherit from the TestFactory class like so:

```kotlin
class FooFactory: TestFactory<Foo>(Foo::class) {
    val bar = 123
    val baz = "hello"
}
```

### Using test factories

In your tests you can now construct objects like this:

```kotlin
val foo = FooFactory().build()
```

This will call the primary constructor of the class with the values defined on the factory.

The constructor parameters can be arbitrarily overriden at the call site by creating an anonymous object that
subclasses the factory, e.g.

```kotlin
val foo = object : FooFactory() { override val bar = 2 }.build()
```