package jda

import configuration.ConfigFile
import configuration.dataConfigs.Custom
import configuration.dataConfigs.CustomDiscordConfig

class DCustomAPI {

    companion object {
        val sortedMap: MutableMap<DiscordInteractionEnum, MutableMap<String, List<Custom>>> = mutableMapOf()

        fun sort(configs: MutableList<ConfigFile<CustomDiscordConfig>>, clear: Boolean) {
            if (clear) sortedMap.clear()
            configs.mapNotNull { it.data }.forEach {
                if (sortedMap.containsKey(it.interactionType) && sortedMap[it.interactionType] != null) {
                    sortedMap[it.interactionType]?.putAll(it.custom)
                } else {
                    sortedMap[it.interactionType] = it.custom
                }
            }
        }
    }

}