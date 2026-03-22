# json-schema-lib

A Scala 3 library that leverages type classes and macros to derive JSON schemas from case classes at compile time.

## ⚠️ Project Status

This project is purely experimental and was created for me to experiment with [Claude Code](https://claude.com/claude-code) and explore vibe-based coding workflows. It's a learning exercise and playground for testing AI-assisted development patterns. Use at your own risk!

## Features

- **Compile-time schema generation** - Zero runtime overhead with macro-based derivation
- **Primitive type support** - String, Int, Long, Float, Double, Boolean
- **Enum support** - Scala 3 enums automatically map to JSON Schema enums
- **Seq support** - Mutable and immutable sequences
- **Option support** - Optional fields automatically excluded from required list
- **Circe integration** - Built-in JSON encoding for schemas

## Example

```scala
import jsonschema.*

enum Role:
  case Admin, User, Guest

case class Profile(
  @MinLength(3) @MaxLength(50) username: String,
  @MinimumInt(18) @MaximumInt(120) age: Int,
  @Minimum(0.0) score: Double,
  active: Boolean,
  role: Role,
  @MinItems(1) @MaxItems(10) tags: Seq[String],
  bio: Option[String]
)

object Profile:
    given JsonSchema[Profile] = DeriveJsonSchema.derived

val schema = JsonSchema[Profile].schema
val json = schema.toJson
```

**Generated JSON Schema:**

```json
{
  "type": "object",
  "properties": {
    "username": { "type": "string", "minLength": 3, "maxLength": 50 },
    "age": { "type": "integer", "minimum": 18, "maximum": 120 },
    "score": { "type": "number", "minimum": 0.0 },
    "active": { "type": "boolean" },
    "role": { "type": "string", "enum": ["Admin", "User", "Guest"] },
    "tags": {
      "type": "array",
      "items": { "type": "string" },
      "minItems": 1,
      "maxItems": 10
    },
    "bio": { "type": "string" }
  },
  "required": ["username", "age", "score", "active", "role", "tags"]
}
```
