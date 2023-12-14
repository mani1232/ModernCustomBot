package console

import api.discord.DCustomAPI
import configVault
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess

class ConsoleCommandProcessor {

    suspend fun enable() = coroutineScope {
        launch {
            val log = LoggerFactory.getLogger("Console")
            val sc = Scanner(System.`in`)
            while (true) {
                val command = sc.nextLine()
                when (command) {
                    "/help" -> {
                        log.info(
                            """
                             /reload - reload fully application
                             /stop - stop fully application
                         """.trimIndent()
                        )
                    }

                    "/reload" -> {
                        DCustomAPI.clearAll()
                        configVault.reloadAll()
                        DCustomAPI.sort(configVault.customDiscordConfig.dirConfigFiles)
                        log.info("All addons and configs reloaded (Not recommended use this command)")
                    }

                    "/stop" -> {
                        exitProcess(0)
                    }

                    else -> {
                        log.warn("Command not found. Try /help")
                    }
                }
            }
        }
    }

}