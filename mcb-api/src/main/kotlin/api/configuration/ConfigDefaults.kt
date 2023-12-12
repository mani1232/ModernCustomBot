package api.configuration

import api.configuration.configType.Custom
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

class ConfigDefaults {
    companion object {

        private val customsTypesList: MutableList<KType> = mutableListOf()

        fun registerCustoms(list: List<KType>) {
            customsTypesList.addAll(list)
            customsModule = SerializersModule {
                polymorphic(Custom::class) {
                    customsTypesList.forEach {
                        subclass(it.classifier as KClass<Custom>, serializer(it) as KSerializer<Custom>)
                    }
                }
            }
        }

        fun getCustoms(): MutableList<KType> {
            return customsTypesList
        }

        fun getCustomsModule(): SerializersModule {
            return customsModule
        }

        private var customsModule = SerializersModule {
            polymorphic(Custom::class) {
                customsTypesList.forEach {
                    subclass(it.classifier as KClass<Custom>, serializer(it) as KSerializer<Custom>)
                }
            }
        }

        fun getConfiguredYaml(module: SerializersModule): Yaml {
            return Yaml(
                serializersModule = module, configuration = YamlConfiguration(
                    encodeDefaults = true,
                    strictMode = false,
                    polymorphismStyle = PolymorphismStyle.Tag,
                    allowAnchorsAndAliases = true
                )
            )
        }
    }
}