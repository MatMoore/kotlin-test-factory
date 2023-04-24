package testfactory

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

open class TestFactory<T : Any>(private val cls: KClass<T>) {
  fun build(): T {
    val constructor = cls.primaryConstructor ?: throw Exception("No primary constructor! This probably means you didn't pass a concrete class to TestFactory")
    val parameters = constructor.parameters

    val arglist = parameters.map { generateParameter(it) ?: generateDefault(it) }

    return constructor.call(*arglist.toTypedArray())
  }

  /**
   * Generate parameters based on factory members
   */
  private fun generateParameter(kparameter: KParameter): Any? {
    val property = this::class.declaredMemberProperties.find { it.name == kparameter.name }
    if(property == null) {
      println("No property ${kparameter.name}")
      return null
    }
    val value = property.getter.call(this)
    val valueType = property.returnType
    val type = kparameter.type
    if(type.isMarkedNullable && value == null) {
      return null
    }
    if(!type.isMarkedNullable && value == null) {
      throw Exception("Factory defined a null value for a non-nullable field")
    }
    if(valueType != type) {
      throw Exception("Factory defined ${valueType} for a field with type ${type}")
    }

    return value
  }

  // Maybe this behaviour is not worth having?
  // Should users be forced to provide defaults for every field?
  private fun generateDefault(kparameter: KParameter): Any {
    val ktype = kparameter.type
    return when {
      ktype.isSupertypeOf(typeOf<Int>()) -> 0
      ktype.isSupertypeOf(typeOf<String>()) -> ""
      else -> throw Exception("Blarg")
    }
  }
}