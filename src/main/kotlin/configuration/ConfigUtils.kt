package configuration

import configuration.dataConfigs.*
import configuration.dataConfigs.discord.*
import jda.DiscordInteractionEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import net.dv8tion.jda.api.entities.channel.ChannelType
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

const val MainConfigName = "CustomConfig.yml"

class ConfigVault(path: String) {

    init {
        File(path).mkdirs()
    }


    private val customsTypesList: MutableList<KType> = mutableListOf(
        typeOf<SendText>(),
        typeOf<BotFilter>(),
        typeOf<MessageFilter>(),
        typeOf<GuildFilter>(),
        typeOf<RegisterCommand>(),
        typeOf<DeleteCommand>(),
        typeOf<InteractionFilter>(),
    )

    fun registerCustoms(list: List<KType>) {
        customsTypesList.addAll(list)
    }

    private var customsModule = SerializersModule {
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
        mainConfig.loadDefaultFile(BotConfig(mutableListOf(DiscordBot("ENTER_IT_HERE"), TelegramBot("TODO"))))
        customDiscordConfig.loadDefaultFiles(
            true,
            mutableMapOf(
                "PingPongExample.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_MESSAGE_RECEIVE,
                    mutableMapOf("pingPong" to listOf(MessageFilter(onlyChannel = mutableListOf(ChannelType.PRIVATE), whitelist = true, messageRegexPatterns = mutableListOf("ping")),
                        SendText(text = "pong", reply = true)
                    ))
                ),
                "RegisterCommands.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_READY,
                    mutableMapOf("helpCommand" to listOf(RegisterCommand("help", "send help info")))
                ),
                "ProcessCommands.yml" to CustomDiscordConfig(
                    DiscordInteractionEnum.ON_COMMAND,
                    mutableMapOf("helpCommandCheck" to listOf(InteractionFilter(mutableListOf("help"), whitelist = true), SendText(text = "This is example help", ephemeral = true)))
                )
            )
        )
    }

    fun reloadAll() {
        mainConfig.loadFile()
        customDiscordConfig.loadFolderFiles(true)
    }

    fun updateAllFiles() {
        mainConfig.updateFile(mainConfig.data!!)
        customDiscordConfig.updateAllFiles()
    }
}