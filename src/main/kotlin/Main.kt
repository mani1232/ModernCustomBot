import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import configuration.ConfigVault
import configuration.dataConfigs.BotImpl
import jda.DCustomAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.modules.SerializersModule

val configVault = ConfigVault("CustomBot")

fun main(args: Array<String>) {
    configVault.loadAll()
    startCustomBot()
}

fun startCustomBot() {
    runBlocking {
        configVault.mainConfig.data!!.bots.forEach {
            launch {
                DCustomAPI.sort(configVault.customDiscordConfig.dirConfigFiles, false)
                (it as BotImpl<*>).init()
            }
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