package configuration.dataConfigs

import jda.DCustomAPI
import jda.DiscordInteractionEnum
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

@Serializable
data class CustomDiscordConfig(
    val interactionType: DiscordInteractionEnum = DiscordInteractionEnum.ON_MESSAGE_RECEIVE,
    val custom: MutableMap<String, List<Custom>> = mutableMapOf()
)

@Serializable
sealed class Custom {

    @Serializable
    data class SendText(
        val text: String = "EMPTY_TEXT",
        val reply: Boolean = false,
        val ephemeral: Boolean = false
    ) : Action {
        override fun run(event: GenericEvent) {
            if (event is GenericInteractionCreateEvent) {
                event.messageChannel.sendMessage(text).queue()
            } else if (event is IReplyCallback) {
                event.reply(text).setEphemeral(ephemeral).queue()
            }
        }
    }

    @Serializable
    data class BotFilter(
        val botIds: List<Long>?,
        val whitelist: Boolean = false,
        override val denyId: String?,
    ) : Filter {
        override fun isCan(event: GenericEvent): Boolean {
            if (botIds == null) {
                return false
            }
            return botIds.contains(event.jda.selfUser.idLong) && whitelist
        }
    }

    @Serializable
    data class MessageFilter(
        val messageRegexPatterns: List<String>?,
        val whitelist: Boolean = false,
        val onlyChannel: List<ChannelType>?,
        override val denyId: String?,
    ) : Filter {
        override fun isCan(event: GenericEvent): Boolean {
            if (messageRegexPatterns.isNullOrEmpty()) {
                return false
            }

            if (event is MessageReceivedEvent && !onlyChannel.isNullOrEmpty() && !onlyChannel.contains(event.channelType)) {
                return messageRegexPatterns.parallelStream()
                    .map { pattern -> Regex(pattern) }
                    .anyMatch { regex -> regex.containsMatchIn(event.message.contentDisplay) } && whitelist
            }

            return false
        }
    }

}

interface Filter {
    val denyId: String?
    fun isCan(event: GenericEvent): Boolean
    fun denyRun(event: GenericEvent, interactionType: DiscordInteractionEnum) {
        if (denyId == null) {
            return
        }

        val denyCustoms = DCustomAPI.sortedMap[interactionType]
        if (!denyCustoms.isNullOrEmpty()) {
            val customs = denyCustoms[denyId]
            if (!customs.isNullOrEmpty()) {
                customs.forEach {
                    if (it is Filter) {
                        if (!it.isCan(event)) {
                            it.denyRun(event, interactionType)
                            return
                        }
                    } else if (it is Action) {
                        it.run(event)
                    }
                }
            }
        }
    }

}

fun interface Action {
    fun run(event: GenericEvent)
}