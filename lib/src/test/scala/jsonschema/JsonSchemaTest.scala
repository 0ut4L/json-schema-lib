package jsonschema

import io.circe.parser.*
import munit.FunSuite

class JsonSchemaTest extends FunSuite:

  test("String schema generates correct JSON") {
    val schema = JsonSchema[String].schema
    val json = schema.toJson
    val expected = parse("""{"type": "string"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Int schema generates correct JSON") {
    val schema = JsonSchema[Int].schema
    val json = schema.toJson
    val expected = parse("""{"type": "integer"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Double schema generates correct JSON") {
    val schema = JsonSchema[Double].schema
    val json = schema.toJson
    val expected = parse("""{"type": "number"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Long schema generates correct JSON") {
    val schema = JsonSchema[Long].schema
    val json = schema.toJson
    val expected = parse("""{"type": "integer"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Float schema generates correct JSON") {
    val schema = JsonSchema[Float].schema
    val json = schema.toJson
    val expected = parse("""{"type": "number"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Boolean schema generates correct JSON") {
    val schema = JsonSchema[Boolean].schema
    val json = schema.toJson
    val expected = parse("""{"type": "boolean"}""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("String schema with constraints") {
    val schema = Schema.StringSchema(
      minLength = Some(5),
      maxLength = Some(50),
      pattern = Some("^[a-z]+$")
    )
    val json = schema.toJson
    val expected = parse("""{
      "type": "string",
      "minLength": 5,
      "maxLength": 50,
      "pattern": "^[a-z]+$"
    }""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Integer schema with constraints") {
    val schema = Schema.IntegerSchema(
      minimum = Some(0),
      maximum = Some(100)
    )
    val json = schema.toJson
    val expected = parse("""{
      "type": "integer",
      "minimum": 0,
      "maximum": 100
    }""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Number schema with constraints") {
    val schema = Schema.NumberSchema(
      minimum = Some(0.0),
      maximum = Some(1.0)
    )
    val json = schema.toJson
    val expected = parse("""{
      "type": "number",
      "minimum": 0.0,
      "maximum": 1.0
    }""").getOrElse(io.circe.Json.Null)
    assertEquals(json, expected)
  }

  test("Object schema with properties") {
    val schema = Schema.ObjectSchema(
      properties = Map(
        "name" -> Schema.StringSchema(),
        "age" -> Schema.IntegerSchema()
      ),
      required = List("name", "age")
    )
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
