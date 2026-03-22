package jsonschema

import scala.collection.mutable.*

/**
 * Type class for generating JSON schemas at compile time
 */
trait JsonSchema[A]:
  def schema: Schema

object JsonSchema:

  /**
   * Summon a JsonSchema instance
   */
  def apply[A](using js: JsonSchema[A]): JsonSchema[A] = js

  /**
   * Create a JsonSchema instance from a schema value
   */
  def instance[A](s: Schema): JsonSchema[A] = new JsonSchema[A]:
    def schema: Schema = s

  /**
   * Given instance for String
   */
  given JsonSchema[String] = instance(Schema.StringSchema())

  /**
   * Given instance for Int
   */
  given JsonSchema[Int] = instance(Schema.IntegerSchema())

  /**
   * Given instance for Double
   */
  given JsonSchema[Double] = instance(Schema.NumberSchema())

  /**
   * Given instance for Long
   */
  given JsonSchema[Long] = instance(Schema.IntegerSchema())

  /**
   * Given instance for Float
   */
  given JsonSchema[Float] = instance(Schema.NumberSchema())

  /**
   * Given instance for Boolean
   */
  given JsonSchema[Boolean] = instance(Schema.BooleanSchema())

  /**
   * Given instance for List
   */
  given [A](using js: JsonSchema[A]): JsonSchema[List[A]] = instance(Schema.ArraySchema(js.schema))

  /**
   *  Given instrance for Seq
   */
  given [A](using js: JsonSchema[A]): JsonSchema[Seq[A]] = instance(Schema.ArraySchema(js.schema))

  /**
   * Given instance for Vector
   */
  given [A](using js: JsonSchema[A]): JsonSchema[Vector[A]] = instance(Schema.ArraySchema(js.schema))

  /**
   * Given instance for Set
   */
  given [A](using js: JsonSchema[A]): JsonSchema[Set[A]] = instance(Schema.ArraySchema(js.schema))

  /**
   * Given instance for Buffer
   */
  given [A](using js: JsonSchema[A]): JsonSchema[Buffer[A]] = instance(Schema.ArraySchema(js.schema))

  /**
   * Given instance or ListBuffer
   */
  given [A](using js: JsonSchema[A]): JsonSchema[ListBuffer[A]] =
    instance(Schema.ArraySchema(js.schema))

  /**
   * Given instance for ArrayBuffer
   */
  given [A](using js: JsonSchema[A]): JsonSchema[ArrayBuffer[A]] =
    instance(Schema.ArraySchema(js.schema))

  /**
   * Extension method to generate schema
   */
  extension [A](value: A)
    def jsonSchema(using js: JsonSchema[A]): Schema = js.schema

  /**
   * Extension on types to generate schema
   */
  inline def schemaFor[A](using js: JsonSchema[A]): Schema = js.schema
