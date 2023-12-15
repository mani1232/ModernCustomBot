package jda

import api.discord.DCustomAPI
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.reflect.KClass


class DiscordListeners : ListenerAdapter() {

    override fun onGenericEvent(event: GenericEvent) {
        val eventEnum =
            DiscordInteractionEnum.entries.firstOrNull { it.kType.classifier as KClass<GenericEvent> == event::class }

        if (eventEnum != null) {
            runBlocking {
                DCustomAPI.runCustoms(eventEnum.name, event)
            }
        }
    }
}