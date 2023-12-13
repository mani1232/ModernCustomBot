import api.addon.ModernAddon
import api.configuration.configType.Action
import api.configuration.configType.Custom
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.typeOf

class MusicAddon : ModernAddon() {
    override suspend fun enableAddon() = coroutineScope {
        getDefaultConfig<MusicAddonConfig>().loadDefaultFile(MusicAddonConfig("example")).await()
        logger.info("Addon enabled!")
        api.registerAddonCustoms(typeOf<ExampleAction>())
    }

    override suspend fun disableAddon() = coroutineScope {

    }

    override suspend fun reloadAddon() = coroutineScope {

    }

    @Serializable
    data class MusicAddonConfig(
        val testString: String
    )
}

@Serializable
@SerialName("ModernMusicAddon_exampleAction")
data class ExampleAction(
    val text: String
): Custom(), Action {
    override fun run(event: GenericEvent) {
        if (event is MessageReceivedEvent && !event.message.author.isBot) {
            event.channel.sendMessage(text).queue()
        }
    }
}