package configuration

import configuration.dataConfigs.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

const val MainConfigName = "CustomConfig.yml"

class ConfigVault(path: String) {

    init {
        File(path).mkdirs()
    }


    val customsTypesList: MutableList<KType> = mutableListOf(
        typeOf<SendText>(),
        typeOf<BotFilter>(),
        typeOf<MessageFilter>(),
    )

    private val customsModule = SerializersModule {
        polymorphic(Custom::class) {
            customsTypesList.forEach {
                subclass(it.classifier as KClass<Custom>, serializer(it) as KSerializer<Custom>)
            }
        }
    }

    val mainConfig: ConfigFile<BotConfig> = ConfigFile.create(File(path, MainConfigName), SerializersModule { })
    val customDiscordConfig: ConfigsDirectory<CustomDiscordConfig> =
        ConfigsDirectory.create(File(path, "custom/"), customsModule)

    fun loadAll() {
        mainConfig.loadDefaultFile(BotConfig())
        customDiscordConfig.loadFolderFiles(true)
    }

    fun reloadAll() {
        loadAll()
        customDiscordConfig.loadFolderFiles(true)
    }

    fun updateAllFiles() {
        mainConfig.updateFile(mainConfig.data!!)
    }
}