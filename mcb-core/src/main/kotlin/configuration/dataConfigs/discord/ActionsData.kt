package configuration.dataConfigs.discord

import api.configuration.configType.Action
import api.configuration.configType.Custom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle


@Serializable
@SerialName("deleteCommand")
data class DeleteCommand(
    val id: String,
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        event.jda.deleteCommandById(id).queue()
    }
}

@Serializable
@SerialName("registerCommand")
data class RegisterCommand(
    val id: String,
    val description: String,
    val options: List<COptionData> = mutableListOf(),
    val guildOnly: Boolean = false,
    val nsfwOnly: Boolean = false,
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        event.jda.upsertCommand(
            Commands.slash(id, description).addOptions(options.map { OptionData(it.type, it.name, it.description) })
                .setGuildOnly(guildOnly).setNSFW(nsfwOnly)
        ).queue()
    }
}

@Serializable
data class COptionData(
    val type: OptionType,
    val name: String,
    val description: String,
)

@Serializable
@SerialName("sendText")
data class SendText(
    val text: String = "EMPTY_TEXT",
    val reply: Boolean = false,
    val ephemeral: Boolean = false,
    val fromBot: Boolean = false,
    val actionRows: List<List<ComponentConfig>>? = mutableListOf()
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        if (event is MessageReceivedEvent && event.message.author.isBot || fromBot) {
            return
        }

        val components = mutableListOf<LayoutComponent>()
        if (!actionRows.isNullOrEmpty()) {
            actionRows.forEach {
                components.add(ActionRow.of(it.map { comp -> comp.getComponent() }))
            }
        }

        if (event is GenericInteractionCreateEvent && !reply && !ephemeral) {
            event.messageChannel.sendMessage(text)
                .addComponents(components).queue()
        } else if (event is IReplyCallback) {
            event.reply(text).setEphemeral(ephemeral).addComponents(components).queue()
        } else if (event is GenericMessageEvent) {
            if (reply && event is MessageReceivedEvent) {
                event.message.reply(text).addComponents(components).queue()
            } else {
                event.channel.sendMessage(text).addComponents(components).queue()
            }
        }
    }
}

@Serializable
sealed class ComponentConfig : ComponentImpl()

@Serializable
@SerialName("button")
data class ButtonConfig(
    val buttonStyle: ButtonStyle,
    val idOrUrl: String,
    val label: String? = null,
    val emoji: String? = null
) : ComponentConfig() {
    override fun getComponent(): ItemComponent {
        return if (emoji == null) {
            Button.of(buttonStyle, idOrUrl, label, null)
        } else {
            Button.of(buttonStyle, idOrUrl, label, Emoji.fromFormatted(emoji))
        }
    }
}

@Serializable
@SerialName("connectVoiceChannel")
data class ConnectVoiceChannel(
    val idOfVoiceChannelData: String
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        val channel = tempData[idOfVoiceChannelData]

        if (channel == null || channel !is VoiceChannel) {
            logger.warn("$idOfVoiceChannelData is not found or not VoiceChannel")
            return
        }

        event.jda.directAudioController.connect(channel)
    }
}

@Serializable
@SerialName("addCommandOptions")
data class AddCommandOptions(
    val resetTempData: Boolean = false
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        if (resetTempData) {
            tempData.clear()
        }

        if (event is GenericCommandInteractionEvent) {
            event.options.forEach {
                tempData[it.name] = when (it.type) {
                    OptionType.UNKNOWN -> "empty"
                    OptionType.SUB_COMMAND -> it.asString
                    OptionType.SUB_COMMAND_GROUP -> it.asString
                    OptionType.STRING -> it.asString
                    OptionType.INTEGER -> it.asInt
                    OptionType.BOOLEAN -> it.asBoolean
                    OptionType.USER -> it.asUser
                    OptionType.CHANNEL -> it.asChannel
                    OptionType.ROLE -> it.asRole
                    OptionType.MENTIONABLE -> it.asMentionable
                    OptionType.NUMBER -> it.asDouble
                    OptionType.ATTACHMENT -> it.asAttachment
                }
            }
        }
    }
}

@Serializable
@SerialName("disconnectVoiceChannel")
data class DisconnectVoiceChannel(
    val idOfVoiceChannelData: String
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        val channel = tempData[idOfVoiceChannelData]

        if (channel == null || channel !is VoiceChannel) {
            logger.warn("$idOfVoiceChannelData is not found or not VoiceChannel")
            return
        }

        if (event !is GenericGuildEvent) {
            logger.warn("$idOfVoiceChannelData is not found or not VoiceChannel")
            return
        }

        event.jda.directAudioController.disconnect(event.guild)
    }
}

data class StringSelectMenuConfig(
    val id: String
)

@Serializable
abstract class ComponentImpl {
    abstract fun getComponent(): ItemComponent
}