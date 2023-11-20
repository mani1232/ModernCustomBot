import configuration.ConfigVault
import configuration.dataConfigs.BotImpl
import jda.DCustomAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    ConfigVault.loadAll("", LoggerFactory.getLogger("Config"))
    startCustomBot()
}

fun startCustomBot() {
    runBlocking {
        ConfigVault.mainConfig.data!!.bots.forEach {
            launch {
                DCustomAPI.sort(ConfigVault.customDiscordConfig.dirConfigFiles, false)
                (it as BotImpl<*>).init()
            }
        }
    }
}