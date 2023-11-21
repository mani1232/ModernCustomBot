package jda

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.KType
import kotlin.reflect.typeOf

enum class DiscordInteractionEnum(val kType: KType) {
    ON_COMMAND(typeOf<SlashCommandInteractionEvent>()),
    ON_MESSAGE_RECEIVE(typeOf<MessageReceivedEvent>()),
}