package configuration.dataConfigs.discord

import configuration.dataConfigs.Action
import configuration.dataConfigs.Custom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData


@Serializable
data class DeleteCommand(
    val id: String,
): Custom(), Action {
    override fun run(event: GenericEvent) {
        event.jda.deleteCommandById(id).queue()
    }
}

@Serializable
data class RegisterCommand(
    val id: String,
    val description: String,
    val options: List<COptionData> = mutableListOf(),
    val guildOnly: Boolean = false,
    val nsfwOnly: Boolean = false,
): Custom(), Action {
    override fun run(event: GenericEvent) {
        event.jda.upsertCommand(Commands.slash(id, description).addOptions(options.map { OptionData(it.type, it.name, it.description) }).setGuildOnly(guildOnly).setNSFW(nsfwOnly)).queue()
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
    val ephemeral: Boolean = false
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        if (event is GenericInteractionCreateEvent) {
            event.messageChannel.sendMessage(text).queue()
        } else if (event is IReplyCallback) {
            event.reply(text).setEphemeral(ephemeral).queue()
        } else if (event is GenericMessageEvent) {
            if (reply && event is MessageReceivedEvent) {
                event.message.reply(text).queue()
            } else {
                event.channel.sendMessage(text).queue()
            }
        }
    }
}