import configuration.ConfigVault
import configuration.dataConfigs.BotImpl
import jda.DCustomAPI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

val configVault = ConfigVault("CustomBot")

private val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) = runBlocking() {
    try {
        logger.info("Loading configs")
        configVault.loadAll()
        startCustomBot()
    } catch (e: Exception) {
        logger.error(e.message, e)
    }
}

suspend fun startCustomBot() = coroutineScope {
    logger.info("Sorting configs")
    DCustomAPI.sort(configVault.customDiscordConfig.dirConfigFiles, true)
    logger.info("Starting bots")
    configVault.mainConfig.data!!.bots.forEach {
        thread {
            (it as BotImpl<*>).init()
        }
    }
    logger.info("Done!")

}