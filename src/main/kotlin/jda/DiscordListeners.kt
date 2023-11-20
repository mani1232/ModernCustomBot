package jda

import configuration.dataConfigs.Action
import configuration.dataConfigs.Filter
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


class DiscordListeners : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }

        val customs = DCustomAPI.sortedMap[DiscordInteractionEnum.ON_MESSAGE_RECEIVE]
        if (!customs.isNullOrEmpty()) {
            customs.forEach { map ->
                map.value.forEach {
                    if (it is Filter) {
                        if (!it.isCan(event)) {
                            it.denyRun(event, DiscordInteractionEnum.ON_MESSAGE_RECEIVE)
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