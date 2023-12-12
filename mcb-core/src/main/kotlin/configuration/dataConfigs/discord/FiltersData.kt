package configuration.dataConfigs.discord

import api.configuration.configType.Custom
import api.configuration.configType.Filter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


@Serializable
@SerialName("interactionFilter")
data class InteractionFilter(
    val allowIdPatterns: List<String>,
    override val denyId: String? = null, override val whitelist: Boolean = false
) : Custom(), Filter {
    override fun isCan(event: GenericEvent): Boolean {
        val id = when (event) {
            is SlashCommandInteractionEvent -> event.fullCommandName
            is ButtonInteractionEvent -> event.componentId
            is StringSelectInteractionEvent -> event.componentId
            is ModalInteractionEvent -> event.modalId
            else -> null
        }
        return if (id == null) {
            false
        } else {
            allowIdPatterns.parallelStream()
                .map { pattern -> Regex(pattern) }
                .anyMatch { regex -> regex.containsMatchIn(id) }
        }
    }
}

@Serializable
@SerialName("guildFilter")
data class GuildFilter(
    val longIds: List<Long>? = null,
    val stringNames: List<String>? = null,
    override val denyId: String? = null,
    override val whitelist: Boolean = false
) : Custom(), Filter {
    override fun isCan(event: GenericEvent): Boolean {
        if (event is GenericGuildEvent) {
            if (!longIds.isNullOrEmpty()) {
                return longIds.contains(event.guild.idLong)
            }
            if (!stringNames.isNullOrEmpty()) {
                return stringNames.contains(event.guild.name)
            }
        }
        return false
    }
}

@Serializable
@SerialName("botFilter")
data class BotFilter(
    val botIds: List<Long>?,
    override val whitelist: Boolean = false,
    override val denyId: String? = null,
) : Custom(), Filter {
    override fun isCan(event: GenericEvent): Boolean {
        if (botIds == null) {
            return false
        }
        return botIds.contains(event.jda.selfUser.idLong)
    }
}

@Serializable
@SerialName("messageFilter")
data class MessageFilter(
    val messageRegexPatterns: List<String>? = listOf(),
    override val whitelist: Boolean = false,
    val onlyChannel: List<ChannelType>? = listOf(ChannelType.PRIVATE),
    override val denyId: String? = null,
) : Custom(), Filter {
    override fun isCan(event: GenericEvent): Boolean {
        if (event is MessageReceivedEvent && !onlyChannel.isNullOrEmpty() && onlyChannel.contains(event.channelType)) {
            if (messageRegexPatterns.isNullOrEmpty()) {
                return true
            }

            return messageRegexPatterns.parallelStream()
                .map { pattern -> Regex(pattern) }
                .anyMatch { regex -> regex.containsMatchIn(event.message.contentDisplay) }
        }

        return false
    }
}