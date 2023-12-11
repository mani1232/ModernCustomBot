package api.configuration

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.modules.SerializersModule

class ConfigDefaults {
    companion object {
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