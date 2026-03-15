package jsonschema

import io.circe.parser.*
import jsonschema.derivation.DeriveJsonSchema
import munit.FunSuite

class DerivedJsonSchemaTest extends FunSuite:

  test("Derive schema for simple case class") {
    case class Person(name: String, age: Int)

    object Person:
      given JsonSchema[Person] = DeriveJsonSchema.derived

    val schema = JsonSchema[Person].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "name": {"type": "string"},
        "age": {"type": "integer"}
      },
      "required": ["name", "age"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema for case class with all primitive types") {
    case class AllPrimitives(
        str: String,
        int: Int,
        dbl: Double,
        lng: Long,
        flt: Float,
        bool: Boolean
    )

    object AllPrimitives:
      given JsonSchema[AllPrimitives] = DeriveJsonSchema.derived

    val schema = JsonSchema[AllPrimitives].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "str": {"type": "string"},
        "int": {"type": "integer"},
        "dbl": {"type": "number"},
        "lng": {"type": "integer"},
        "flt": {"type": "number"},
        "bool": {"type": "boolean"}
      },
      "required": ["str", "int", "dbl", "lng", "flt", "bool"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with string constraints") {
    case class Username(@MinLength(3) @MaxLength(20) username: String)

    object Username:
      given JsonSchema[Username] = DeriveJsonSchema.derived

    val schema = JsonSchema[Username].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "username": {
          "type": "string",
          "minLength": 3,
          "maxLength": 20
        }
      },
      "required": ["username"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with pattern constraint") {
    case class Email(@Pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") email: String)

    object Email:
      given JsonSchema[Email] = DeriveJsonSchema.derived

    val schema = JsonSchema[Email].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "email": {
          "type": "string",
          "pattern": "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        }
      },
      "required": ["email"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with integer constraints") {
    case class Age(@MinimumInt(0) @MaximumInt(150) age: Int)

    object Age:
      given JsonSchema[Age] = DeriveJsonSchema.derived

    val schema = JsonSchema[Age].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "age": {
          "type": "integer",
          "minimum": 0,
          "maximum": 150
        }
      },
      "required": ["age"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with double constraints") {
    case class Temperature(@Minimum(-273.15) @Maximum(1000.0) temp: Double)

    object Temperature:
      given JsonSchema[Temperature] = DeriveJsonSchema.derived

    val schema = JsonSchema[Temperature].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "temp": {
          "type": "number",
          "minimum": -273.15,
          "maximum": 1000.0
        }
      },
      "required": ["temp"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with multiple constraints on different fields") {
    case class User(
        @MinLength(3) @MaxLength(50) name: String,
        @MinimumInt(18) @MaximumInt(120) age: Int,
        @Pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") email: String
    )

    object User:
      given JsonSchema[User] = DeriveJsonSchema.derived

    val schema = JsonSchema[User].schema
    val json = schema.toJson

    val expected = parse("""{
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
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with exclusive minimum and maximum") {
    case class Score(
        @ExclusiveMinimumInt(0) @ExclusiveMaximumInt(100) score: Int
    )

    object Score:
      given JsonSchema[Score] = DeriveJsonSchema.derived

    val schema = JsonSchema[Score].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "score": {
          "type": "integer",
          "exclusiveMinimum": 0,
          "exclusiveMaximum": 100
        }
      },
      "required": ["score"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with all Option fields - no required fields") {
    case class OptionalUser(
        name: Option[String],
        age: Option[Int],
        email: Option[String]
    )

    object OptionalUser:
      given JsonSchema[OptionalUser] = DeriveJsonSchema.derived

    val schema = JsonSchema[OptionalUser].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "name": {"type": "string"},
        "age": {"type": "integer"},
        "email": {"type": "string"}
      }
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with mixed required and optional fields") {
    case class MixedUser(
        id: Int,
        name: String,
        email: Option[String],
        age: Option[Int]
    )

    object MixedUser:
      given JsonSchema[MixedUser] = DeriveJsonSchema.derived

    val schema = JsonSchema[MixedUser].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "id": {"type": "integer"},
        "name": {"type": "string"},
        "email": {"type": "string"},
        "age": {"type": "integer"}
      },
      "required": ["id", "name"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema with Option fields and constraints") {
    case class OptionalWithConstraints(
        @MinLength(3) @MaxLength(50) name: Option[String],
        @MinimumInt(0) @MaximumInt(150) age: Option[Int]
    )

    object OptionalWithConstraints:
      given JsonSchema[OptionalWithConstraints] = DeriveJsonSchema.derived

    val schema = JsonSchema[OptionalWithConstraints].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "name": {
          "type": "string",
          "minLength": 3,
          "maxLength": 50
        },
        "age": {
          "type": "integer",
          "minimum": 0,
          "maximum": 150
        }
      }
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema for simple enum") {
    enum Color:
      case Red, Green, Blue

    object Color:
      given JsonSchema[Color] = DeriveJsonSchema.derived

    val schema = JsonSchema[Color].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "string",
      "enum": ["Red", "Green", "Blue"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema for enum with multiple values") {
    enum Status:
      case Pending, Approved, Rejected, InProgress

    object Status:
      given JsonSchema[Status] = DeriveJsonSchema.derived

    val schema = JsonSchema[Status].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "string",
      "enum": ["Pending", "Approved", "Rejected", "InProgress"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }

  test("Derive schema for case class containing enum field") {
    enum Priority:
      case Low, Medium, High

    case class Task(name: String, priority: Priority)
    object Task:
      given JsonSchema[Task] = DeriveJsonSchema.derived

    val schema = JsonSchema[Task].schema
    val json = schema.toJson

    val expected = parse("""{
      "type": "object",
      "properties": {
        "name": {"type": "string"},
        "priority": {
          "type": "string",
          "enum": ["Low", "Medium", "High"]
        }
      },
      "required": ["name", "priority"]
    }""").getOrElse(io.circe.Json.Null)

    assertEquals(json, expected)
  }
