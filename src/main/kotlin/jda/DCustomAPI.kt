package jda

import configuration.dataConfigs.Custom
import configuration.dataConfigs.CustomDiscordConfig

class DCustomAPI {

    companion object {
        lateinit var sortedMap: MutableMap<DiscordInteractionEnum, MutableMap<String, List<Custom>>>

        fun sort(configs: List<CustomDiscordConfig>, clear: Boolean) {
            if (clear) sortedMap.clear()
            configs.forEach {
                if (sortedMap.containsKey(it.interactionType) && sortedMap[it.interactionType] != null) {
                    sortedMap[it.interactionType]?.putAll(it.custom)
                } else {
                    sortedMap[it.interactionType] = it.custom
                }
            }
        }
    }

}