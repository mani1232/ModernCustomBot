package configuration.dataConfigs.discord

import api.configuration.configType.Action
import api.configuration.configType.Custom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
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

data class StringSelectMenuConfig(
    val id: String
)

@Serializable
abstract class ComponentImpl {
    abstract fun getComponent(): ItemComponent
}