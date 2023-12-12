import addon.AddonManager
import api.discord.DCustomAPI
import configuration.ConfigVault
import configuration.dataConfigs.BotImpl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val mainFolder = "CustomBot"

val configVault = ConfigVault(mainFolder)
val addonManager = AddonManager(mainFolder)

private val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) = runBlocking {
    try {
        logger.info("Starting ModernCustomBot...")
        startCustomBot()
        logger.info("Done!")
    } catch (e: Exception) {
        logger.error(e.message, e)
    }
}

suspend fun startCustomBot() = coroutineScope {
    logger.info("Loading addons")
    addonManager.initAddons()
    addonManager.enableAddons()
    logger.info("Loading configs")
    configVault.loadAll()
    logger.info("Sorting configs")
    DCustomAPI.clear()
    DCustomAPI.sort(configVault.customDiscordConfig.dirConfigFiles)
    logger.info("Starting bots")
    configVault.mainConfig.data.await()?.bots?.forEach {
        (it as BotImpl<*>).init()
    }
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run(): Unit = runBlocking {
            logger.info("Shutdown bot manager")
            addonManager.disableAddons()
            configVault.mainConfig.data.await()?.bots?.forEach {
                (it as BotImpl<*>).shutdown()
            }
            logger.info("Bye!")
        }
    })
}