# Human(o)ID — human friendly IDs for Functional Scala

Functional Scala library introducing human-friendly, typesafe prefixes for better human
experience with universal/globally unique IDs.

Inspired by Stripe's Object IDs (here's [an article](https://dev.to/stripe/designing-apis-for-humans-object-ids-3o5a)
about it).

## Getting started

### SBT

```scala
libraryDependencies += "me.wojnowski" %% "humanoid" % "<version>"
libraryDependencies += "me.wojnowski" %% "humanoid-scuid" % "<version>" // optional, for Cuid2 integration
libraryDependencies += "me.wojnowski" %% "humanoid-uuid" % "<version>" // optional, for UUID integration
libraryDependencies += "me.wojnowski" %% "humanoid-circe" % "<version>" // optional, for Circe codecs
libraryDependencies += "me.wojnowski" %% "humanoid-tapir" % "<version>" // optional, for Tapir schema/codecs
```

### Usage

#### Simple `String` ID

Let's define an ID with prefix "inv" and `String` as its the underlying ID type:

```scala
type InvoiceId = PrefixedId["inv", String]
val InvoiceId = new PrefixedIdOps["inv", String]

type CustomerId = PrefixedId["cus", String]
val CustomerId = new PrefixedIdOps["cus", String]

val invoiceId1: Either[String, InvoiceId] = InvoiceId.parseRequirePrefix("inv_1234")
val invoiceId2: InvoiceId = InvoiceId.fromId("2345")

val customerId1: Either[String, CustomerId] = CustomerId.parseRequirePrefix("cus_1234").toOption.get

// val customerId2: CustomerId = invoiceId2 // ⚠️ compile error, as different prefixes mean different types
```

#### Random IDs

For random IDs it is possible to generate the ID:

```scala
type CustomerId = PrefixedId["cus", Cuid2]
val CustomerId = new PrefixedIdOps["cus", Cuid2]

val id: F[CustomerId] = CustomerId.random[F]
```

### Supported ID types
_Psst:_ Don't know which random ID to choose? Go with `Cuid2`!

| Type        | Validation | Generation | Double-click-selectable | Example                                    |
|-------------|------------|------------|-------------------------|--------------------------------------------|
| `String`    | ❌          | ❌          | ❓                       | `cus_mystring`                             |
| `UUID`      | ✅          | ✅          | ❌                       | `cus_550e8400-e29b-41d4-a716-446655440000` | 
| `Cuid2`     | ✅          | ✅          | ✅                       | `cus_zwiz9glzoec3hk4ji5mgm4mp`             |
| `Cuid2Long` | ✅          | ✅          | ✅                       | `cus_yaajofh4u0ycvs3tbasjwoofrujvuhoq`     |

If you'd like to use a different ID type, don't hesitate to create an issue!

### Defining custom ID type
To support custom ID type (like UUID, Cuid2, etc.) an implicit instance of `IdConverter` must be provided:

```scala
implicit val myIdConverter: IdConverter[MyId] = new IdConverter[MyId] {
  def renderString(id: Id): String = ???

  def fromString(rawString: String): Either[String, Id] = ???
}
```

For random IDs, an implicit instance of `IdGenerator` can be provided:

```scala
implicit val myIdGenerator: IdGenerator[MyId] = IdGenerator[F, MyId] {
  def generate: F[MyId] = ???
}
```

### Circe integration
```scala
libraryDependencies += "me.wojnowski" %% "humanoid-circe" % "<version>"
```

```scala
import me.wojnowski.humanoid.circe.strict._  // require prefix when decoding, encode with prefix
import me.wojnowski.humanoid.circe.relaxed._ // don't require prefix when decoding, encode with prefix
```

### Tapir integration
```scala
libraryDependencies += "me.wojnowski" %% "humanoid-tapir" % "<version>"
```

```scala
import me.wojnowski.humanoid.tapir.strict._  // require prefix when decoding, encode with prefix
import me.wojnowski.humanoid.tapir.relaxed._ // don't require prefix when decoding, encode with prefix
```
