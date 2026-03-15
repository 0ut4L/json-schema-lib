package jsonschema

import scala.annotation.StaticAnnotation

/**
 * Constraint annotations for JSON Schema generation
 */

/**
 * Minimum length for strings
 */
case class MinLength(value: Int) extends StaticAnnotation

/**
 * Maximum length for strings
 */
case class MaxLength(value: Int) extends StaticAnnotation

/**
 * Pattern (regex) for strings
 */
case class Pattern(value: String) extends StaticAnnotation

/**
 * Minimum value for numbers
 */
case class Minimum(value: Double) extends StaticAnnotation

/**
 * Maximum value for numbers
 */
case class Maximum(value: Double) extends StaticAnnotation

/**
 * Exclusive minimum value for numbers
 */
case class ExclusiveMinimum(value: Double) extends StaticAnnotation

/**
 * Exclusive maximum value for numbers
 */
case class ExclusiveMaximum(value: Double) extends StaticAnnotation

/**
 * Minimum value for integers
 */
case class MinimumInt(value: Int) extends StaticAnnotation

/**
 * Maximum value for integers
 */
case class MaximumInt(value: Int) extends StaticAnnotation

/**
 * Exclusive minimum value for integers
 */
case class ExclusiveMinimumInt(value: Int) extends StaticAnnotation

/**
 * Exclusive maximum value for integers
 */
case class ExclusiveMaximumInt(value: Int) extends StaticAnnotation
