package jda

import configuration.dataConfigs.Action
import configuration.dataConfigs.Filter
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.reflect.KClass


class DiscordListeners : ListenerAdapter() {

    override fun onGenericEvent(event: GenericEvent) {
        val eventEnum = DiscordInteractionEnum.entries.parallelStream().filter { it.kType.classifier as KClass<GenericEvent> == event::class }.findFirst()

        if (eventEnum.isPresent) {
            val customs = DCustomAPI.sortedMap[eventEnum.get()]
            if (!customs.isNullOrEmpty()) {
                customs.forEach { map ->
                    map.value.forEach {
                        if (it is Filter) {
                            if (!it.isCan(event) == it.whitelist) {
                                it.denyRun(event, eventEnum.get())
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
}