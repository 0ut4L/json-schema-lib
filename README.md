# json-schema-lib

A Scala 3 library for generating compile-time JSON schemas from case classes using type classes and macros.

## Features

- **Compile-time schema generation** - Zero runtime overhead with macro-based derivation
- **Type-safe constraints** - Use annotations to specify validation rules
- **Primitive type support** - String, Int, Long, Float, Double, Boolean
- **Option support** - Optional fields automatically excluded from required list
- **Circe integration** - Built-in JSON encoding for schemas

## Supported Primitive Types

- `String` - Maps to JSON Schema `"type": "string"`
- `Int` / `Long` - Maps to JSON Schema `"type": "integer"`
- `Double` / `Float` - Maps to JSON Schema `"type": "number"`
- `Boolean` - Maps to JSON Schema `"type": "boolean"`
- `Option[T]` - Makes field optional (excluded from `required` list)

## Constraint Annotations

### String Constraints
- `@MinLength(n)` - Minimum string length
- `@MaxLength(n)` - Maximum string length
- `@Pattern(regex)` - Regex pattern validation

### Integer Constraints
- `@MinimumInt(n)` - Minimum integer value
- `@MaximumInt(n)` - Maximum integer value
- `@ExclusiveMinimumInt(n)` - Exclusive minimum (value must be greater than n)
- `@ExclusiveMaximumInt(n)` - Exclusive maximum (value must be less than n)

### Number Constraints (Double/Float)
- `@Minimum(n)` - Minimum numeric value
- `@Maximum(n)` - Maximum numeric value
- `@ExclusiveMinimum(n)` - Exclusive minimum
- `@ExclusiveMaximum(n)` - Exclusive maximum

## Usage Examples

### Basic Case Class

```scala
import jsonschema.*
import jsonschema.derivation.DeriveJsonSchema

case class Person(name: String, age: Int)
given JsonSchema[Person] = DeriveJsonSchema.derived

val schema = JsonSchema[Person].schema
val json = schema.toJson
```

**Generated JSON Schema:**
```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "age": {"type": "integer"}
  },
  "required": ["name", "age"]
}
```

### With Constraints

```scala
case class User(
  @MinLength(3) @MaxLength(50) name: String,
  @MinimumInt(18) @MaximumInt(120) age: Int,
  @Pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") email: String
)
given JsonSchema[User] = DeriveJsonSchema.derived

val schema = JsonSchema[User].schema
```

**Generated JSON Schema:**
```json
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string",
      "minLength": 3,
      "maxLength": 50
    },
    "age": {
      "type": "integer",
      "minimum": 18,
      "maximum": 120
    },
    "email": {
      "type": "string",
      "pattern": "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    }
  },
  "required": ["name", "age", "email"]
}
```

### With Optional Fields

```scala
case class OptionalUser(
  id: Int,
  name: String,
  email: Option[String],
  phone: Option[String]
)
given JsonSchema[OptionalUser] = DeriveJsonSchema.derived

val schema = JsonSchema[OptionalUser].schema
```

**Generated JSON Schema:**
```json
{
  "type": "object",
  "properties": {
    "id": {"type": "integer"},
    "name": {"type": "string"},
    "email": {"type": "string"},
    "phone": {"type": "string"}
  },
  "required": ["id", "name"]
}
```

### All Primitive Types

```scala
case class AllTypes(
  str: String,
  int: Int,
  lng: Long,
  dbl: Double,
  flt: Float,
  bool: Boolean
)
given JsonSchema[AllTypes] = DeriveJsonSchema.derived
```

## Development

### Building

```bash
sbt compile              # Compile the project
sbt test                 # Run tests
```

### Code Quality

```bash
sbt scalafmtAll          # Format all code
sbt scalafixAll          # Run scalafix linting
```

## Architecture

The library uses:
- **Type classes** (`JsonSchema[A]`) for extensible schema generation
- **Scala 3 macros** for compile-time case class introspection
- **Quoted expressions** for annotation extraction
- **Circe** for JSON handling and encoding

## Project Structure

```
lib/
├── src/
│   ├── main/scala/jsonschema/
│   │   ├── Schema.scala           # Schema ADT (StringSchema, IntegerSchema, etc.)
│   │   ├── JsonSchema.scala       # Type class and primitive instances
│   │   ├── Constraints.scala      # Constraint annotations
│   │   └── derivation/
│   │       └── DeriveJsonSchema.scala  # Macro-based derivation
│   └── test/scala/jsonschema/
│       ├── JsonSchemaTest.scala         # Primitive type tests
│       └── DerivedJsonSchemaTest.scala  # Case class derivation tests
```

## Requirements

- Scala 3.7.3+
- Circe 0.14.15+
- Cats 2.13.0+

