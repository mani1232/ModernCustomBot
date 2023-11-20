package configuration

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import configuration.dataConfigs.BotConfig
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import java.io.File

const val MainConfigName = "CustomConfig.yml"

class ConfigVault {

    companion object {

        lateinit var mainConfig: ConfigFile<BotConfig>
        lateinit var logger: Logger
        lateinit var path: String

        fun loadAll(pathString: String, log: Logger) {
            path = pathString
            logger = log
            mainConfig = ConfigFile(File(path, MainConfigName), SerializersModule {  })
        }

        fun reloadAll() {
            loadAll(path, logger)
        }

        fun updateAllFiles() {
            mainConfig.updateFile(mainConfig.data!!)
        }

        fun getConfiguredYaml(module: SerializersModule): Yaml {
            return Yaml(
                serializersModule = module,
                configuration = YamlConfiguration(
                    encodeDefaults = true,
                    strictMode = false,
                    polymorphismStyle = PolymorphismStyle.Tag,
                    allowAnchorsAndAliases = true
                )
            )
        }
    }
}