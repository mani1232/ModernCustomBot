package configuration

import addonManager
import api.configuration.ConfigDefaults
import api.configuration.ConfigFile
import api.configuration.ConfigsDirectory
import api.discord.DiscordInteractionEnum
import api.discord.dataConfigs.CustomDiscordConfig
import configuration.dataConfigs.BotConfig
import configuration.dataConfigs.DiscordBot
import configuration.dataConfigs.TelegramBot
import configuration.dataConfigs.discord.*
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.modules.SerializersModule
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.io.File
import kotlin.reflect.typeOf

const val MainConfigName = "CustomConfig.yml"

class ConfigVault(path: String) {

    init {
        File(path).mkdirs()
        ConfigDefaults.registerCustoms(
            listOf(
                typeOf<SendText>(),
                typeOf<BotFilter>(),
                typeOf<MessageFilter>(),
                typeOf<GuildFilter>(),
                typeOf<RegisterCommand>(),
                typeOf<DeleteCommand>(),
                typeOf<InteractionFilter>(),
            )
        )
    }

    val mainConfig: ConfigFile<BotConfig> = ConfigFile.create(File(path, MainConfigName), SerializersModule { })
    val customDiscordConfig: ConfigsDirectory<CustomDiscordConfig> =
        ConfigsDirectory.create(File(path, "custom/"), ConfigDefaults.getCustomsModule())

    suspend fun loadAll() {
        mainConfig.loadDefaultFile(BotConfig(mutableListOf(DiscordBot("ENTER_IT_HERE"), TelegramBot("TODO"))))
        customDiscordConfig.loadDefaultFiles(
            mutableMapOf(
                "PingPongExample.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_MESSAGE_RECEIVE, mutableMapOf(
                        "pingPong" to mutableListOf(
                            MessageFilter(
                                onlyChannel = mutableListOf(ChannelType.PRIVATE),
                                whitelist = true,
                                messageRegexPatterns = mutableListOf("ping")
                            ), SendText(
                                text = "pong", reply = true, actionRows = mutableListOf(
                                    mutableListOf(
                                        ButtonConfig(
                                            ButtonStyle.DANGER, "new", emoji = "😃"
                                        )
                                    ), mutableListOf(ButtonConfig(ButtonStyle.SECONDARY, "new_other", label = "text"))
                                )
                            )
                        )
                    )
                ), "RegisterCommands.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_READY,
                    mutableMapOf("helpCommand" to mutableListOf(RegisterCommand("help", "send help info")))
                ), "ProcessCommands.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_COMMAND, mutableMapOf(
                        "helpCommandCheck" to mutableListOf(
                            InteractionFilter(
                                mutableListOf("help"), whitelist = true
                            ), SendText(text = "This is example help", ephemeral = true)
                        )
                    )
                )
            )
        )
    }

    suspend fun reloadAll() {
        mainConfig.loadFile()
        customDiscordConfig.loadFolderFiles()
        addonManager.reloadAddons()
    }

    suspend fun updateAllFiles() = coroutineScope {
        mainConfig.updateFile(mainConfig.data.await()!!)
        customDiscordConfig.updateAllFiles()
    }
}