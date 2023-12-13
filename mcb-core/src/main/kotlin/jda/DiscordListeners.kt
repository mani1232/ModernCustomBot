package jda

import api.configuration.configType.Action
import api.configuration.configType.Filter
import api.discord.DCustomAPI
import api.discord.DiscordInteractionEnum
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
                var tempData = mutableMapOf<String, Any>()

                val customs = DCustomAPI.getBundle(eventEnum)
                if (!customs.isNullOrEmpty()) {
                    customs.forEach { map ->
                        map.value.forEach {
                            it.tempData = tempData
                            if (it is Filter) {
                                if (!it.isCan(event) == it.whitelist) {
                                    it.denyRun(event)
                                    return@forEach
                                }
                            } else if (it is Action) {
                                it.run(event)
                            }
                            tempData = it.tempData
                        }
                    }
                }
            }
        }
    }
}