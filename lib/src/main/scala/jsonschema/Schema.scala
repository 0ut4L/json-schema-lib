package jsonschema

import io.circe.Encoder
import io.circe.Json
import io.circe.JsonObject

/**
 * Represents a JSON Schema
 */
sealed trait Schema:
  def toJson: Json

object Schema:
  /**
   * A string type schema
   */
  case class StringSchema(
      minLength: Option[Int] = None,
      maxLength: Option[Int] = None,
      pattern: Option[String] = None
  ) extends Schema:
    def toJson: Json =
      val base = JsonObject("type" -> Json.fromString("string"))
      val withMinLength = minLength.fold(base)(min => base.add("minLength", Json.fromInt(min)))
      val withMaxLength =
        maxLength.fold(withMinLength)(max => withMinLength.add("maxLength", Json.fromInt(max)))
      val withPattern =
        pattern.fold(withMaxLength)(p => withMaxLength.add("pattern", Json.fromString(p)))
      Json.fromJsonObject(withPattern)

  /**
   * An integer type schema
   */
  case class IntegerSchema(
      minimum: Option[Int] = None,
      maximum: Option[Int] = None,
      exclusiveMinimum: Option[Int] = None,
      exclusiveMaximum: Option[Int] = None
  ) extends Schema:
    def toJson: Json =
      val base = JsonObject("type" -> Json.fromString("integer"))
      val withMinimum = minimum.fold(base)(min => base.add("minimum", Json.fromInt(min)))
      val withMaximum =
        maximum.fold(withMinimum)(max => withMinimum.add("maximum", Json.fromInt(max)))
      val withExclusiveMin = exclusiveMinimum.fold(withMaximum)(min =>
        withMaximum.add("exclusiveMinimum", Json.fromInt(min))
      )
      val withExclusiveMax = exclusiveMaximum.fold(withExclusiveMin)(max =>
        withExclusiveMin.add("exclusiveMaximum", Json.fromInt(max))
      )
      Json.fromJsonObject(withExclusiveMax)

  /**
   * A number type schema (for floating point numbers)
   */
  case class NumberSchema(
      minimum: Option[Double] = None,
      maximum: Option[Double] = None,
      exclusiveMinimum: Option[Double] = None,
      exclusiveMaximum: Option[Double] = None
  ) extends Schema:
    def toJson: Json =
      val base = JsonObject("type" -> Json.fromString("number"))
      val withMinimum = minimum.fold(base)(min => base.add("minimum", Json.fromDoubleOrNull(min)))
      val withMaximum =
        maximum.fold(withMinimum)(max => withMinimum.add("maximum", Json.fromDoubleOrNull(max)))
      val withExclusiveMin = exclusiveMinimum.fold(withMaximum)(min =>
        withMaximum.add("exclusiveMinimum", Json.fromDoubleOrNull(min))
      )
      val withExclusiveMax = exclusiveMaximum.fold(withExclusiveMin)(max =>
        withExclusiveMin.add("exclusiveMaximum", Json.fromDoubleOrNull(max))
      )
      Json.fromJsonObject(withExclusiveMax)

  /**
   * A boolean type schema
   */
  case class BooleanSchema() extends Schema:
    def toJson: Json =
      Json.fromJsonObject(JsonObject("type" -> Json.fromString("boolean")))

  /**
   * An object type schema
   */
  case class ObjectSchema(
      properties: Map[String, Schema],
      required: List[String] = Nil
  ) extends Schema:
    def toJson: Json =
      val propsJson = JsonObject.fromMap(properties.view.mapValues(_.toJson).toMap)
      val base = JsonObject(
        "type" -> Json.fromString("object"),
        "properties" -> Json.fromJsonObject(propsJson)
      )
      val withRequired =
        if required.isEmpty then base
        else base.add("required", Json.fromValues(required.map(Json.fromString)))
      Json.fromJsonObject(withRequired)

given Encoder[Schema] = Encoder.instance(_.toJson)
