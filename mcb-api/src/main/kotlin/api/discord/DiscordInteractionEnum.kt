package api.discord

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import kotlin.reflect.KType
import kotlin.reflect.typeOf

enum class DiscordInteractionEnum(val kType: KType) {
    ON_COMMAND(typeOf<SlashCommandInteractionEvent>()),
    ON_MESSAGE_RECEIVE(typeOf<MessageReceivedEvent>()),
    ON_READY(typeOf<ReadyEvent>()),
}