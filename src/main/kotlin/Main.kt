import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import configuration.ConfigVault
import configuration.dataConfigs.BotImpl
import jda.DCustomAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val configVault = ConfigVault("CustomBot")

private val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    try {
        logger.info("Loading configs")
        configVault.loadAll()
        startCustomBot()
    } catch (e: Exception) {
        logger.error(e.message, e)
    }

}

fun startCustomBot() {
    runBlocking {
        logger.info("Sorting configs")
        DCustomAPI.sort(configVault.customDiscordConfig.dirConfigFiles, true)
        logger.info("Starting bots")
        configVault.mainConfig.data!!.bots.forEach {
            async {
                (it as BotImpl<*>).init()
            }.await()
        }
        logger.info("Done!")
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