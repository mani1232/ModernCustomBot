package api.configuration.configType

import net.dv8tion.jda.api.events.GenericEvent

fun interface Action {
    fun run(event: GenericEvent)
}