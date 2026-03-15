package jsonschema.derivation

import jsonschema.*

import scala.quoted.*

/**
 * Macro-based derivation for JsonSchema
 */
object DeriveJsonSchema:

  /**
   * Derive a JsonSchema for a product type (case class)
   */
  inline def derived[A]: JsonSchema[A] = ${ derivedImpl[A] }

  private def derivedImpl[A: Type](using Quotes): Expr[JsonSchema[A]] =
    import quotes.reflect.*

    val tpe = TypeRepr.of[A]
    val typeSymbol = tpe.typeSymbol

    // Check if it's a case class
    if !typeSymbol.flags.is(Flags.Case) then
      report.errorAndAbort(s"${typeSymbol.name} is not a case class")

    // Get the primary constructor
    val constructor = typeSymbol.primaryConstructor
    val params = constructor.paramSymss.flatten.filter(_.isValDef)

    if params.isEmpty then
      report.errorAndAbort(s"${typeSymbol.name} has no parameters")

    // Generate schema for each field
    val fieldSchemas = params.map { param =>
      val fieldName = param.name
      val fieldType = tpe.memberType(param)
      val annotations = param.annotations

      // Extract constraints from annotations
      val (schemaExpr, isRequired) = generateFieldSchema(fieldType, annotations, fieldName)

      (Expr(fieldName), schemaExpr, Expr(isRequired))
    }

    // Build the properties map
    val propertiesExprs = fieldSchemas.map { case (nameExpr, schemaExpr, _) =>
      '{ ($nameExpr, $schemaExpr) }
    }

    val propertiesMapExpr = Expr.ofList(propertiesExprs)

    // Build the required fields list
    val requiredExprs = fieldSchemas
      .collect { case (nameExpr, _, Expr(true)) =>
        nameExpr
      }
    val requiredListExpr = Expr.ofList(requiredExprs)

    '{
      JsonSchema.instance[A](
        Schema.ObjectSchema(
          properties = $propertiesMapExpr.toMap,
          required = $requiredListExpr
        )
      )
    }

  private def generateFieldSchema(using
      Quotes
  )(fieldType: quotes.reflect.TypeRepr, annotations: List[quotes.reflect.Term], fieldName: String)
      : (Expr[Schema], Boolean) =
    import quotes.reflect.*

    // Check if the field type is Option[T]
    val (actualType, isRequired) = fieldType match
      case AppliedType(tycon, List(innerType))
          if tycon.typeSymbol == TypeRepr.of[Option[?]].typeSymbol =>
        (innerType, false) // Option types are not required
      case _ =>
        (fieldType, true) // Non-Option types are required

    // Extract constraint values from annotations
    val minLength = extractIntAnnotation[MinLength](annotations)
    val maxLength = extractIntAnnotation[MaxLength](annotations)
    val pattern = extractStringAnnotation[Pattern](annotations)
    val minimumInt = extractIntAnnotation[MinimumInt](annotations)
    val maximumInt = extractIntAnnotation[MaximumInt](annotations)
    val minimum = extractDoubleAnnotation[Minimum](annotations)
    val maximum = extractDoubleAnnotation[Maximum](annotations)
    val exclusiveMinimumInt = extractIntAnnotation[ExclusiveMinimumInt](annotations)
    val exclusiveMaximumInt = extractIntAnnotation[ExclusiveMaximumInt](annotations)
    val exclusiveMinimum = extractDoubleAnnotation[ExclusiveMinimum](annotations)
    val exclusiveMaximum = extractDoubleAnnotation[ExclusiveMaximum](annotations)

    actualType.asType match
      case '[String] =>
        val schema = '{
          Schema.StringSchema(
            minLength = ${ Expr(minLength) },
            maxLength = ${ Expr(maxLength) },
            pattern = ${ Expr(pattern) }
          )
        }
        (schema, isRequired)

      case '[Int] =>
        val schema = '{
          Schema.IntegerSchema(
            minimum = ${ Expr(minimumInt) },
            maximum = ${ Expr(maximumInt) },
            exclusiveMinimum = ${ Expr(exclusiveMinimumInt) },
            exclusiveMaximum = ${ Expr(exclusiveMaximumInt) }
          )
        }
        (schema, isRequired)

      case '[Long] =>
        val schema = '{
          Schema.IntegerSchema(
            minimum = ${ Expr(minimumInt) },
            maximum = ${ Expr(maximumInt) },
            exclusiveMinimum = ${ Expr(exclusiveMinimumInt) },
            exclusiveMaximum = ${ Expr(exclusiveMaximumInt) }
          )
        }
        (schema, isRequired)

      case '[Double] =>
        val schema = '{
          Schema.NumberSchema(
            minimum = ${ Expr(minimum) },
            maximum = ${ Expr(maximum) },
            exclusiveMinimum = ${ Expr(exclusiveMinimum) },
            exclusiveMaximum = ${ Expr(exclusiveMaximum) }
          )
        }
        (schema, isRequired)

      case '[Float] =>
        val schema = '{
          Schema.NumberSchema(
            minimum = ${ Expr(minimum) },
            maximum = ${ Expr(maximum) },
            exclusiveMinimum = ${ Expr(exclusiveMinimum) },
            exclusiveMaximum = ${ Expr(exclusiveMaximum) }
          )
        }
        (schema, isRequired)

      case '[Boolean] =>
        val schema = '{ Schema.BooleanSchema() }
        (schema, isRequired)

      case _ =>
        report.errorAndAbort(
          s"Unsupported field type: ${fieldType.show} for field $fieldName"
        )

  private def extractIntAnnotation[A: Type](using
      Quotes
  )(annotations: List[quotes.reflect.Term]): Option[Int] =
    import quotes.reflect.*

    annotations.collectFirst {
      case Apply(Select(New(tpt), _), List(Literal(constant)))
          if tpt.tpe <:< TypeRepr.of[A] =>
        constant.value.asInstanceOf[Int]
    }

  private def extractDoubleAnnotation[A: Type](using
      Quotes
  )(annotations: List[quotes.reflect.Term]): Option[Double] =
    import quotes.reflect.*

    annotations.collectFirst {
      case Apply(Select(New(tpt), _), List(Literal(constant)))
          if tpt.tpe <:< TypeRepr.of[A] =>
        constant.value.asInstanceOf[Double]
    }

  private def extractStringAnnotation[A: Type](using
      Quotes
  )(annotations: List[quotes.reflect.Term]): Option[String] =
    import quotes.reflect.*

    annotations.collectFirst {
      case Apply(Select(New(tpt), _), List(Literal(constant)))
          if tpt.tpe <:< TypeRepr.of[A] =>
        constant.value.asInstanceOf[String]
    }
