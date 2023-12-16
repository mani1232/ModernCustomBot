package api.addon

import api.configuration.ConfigFile
import api.discord.DCustomAPI
import kotlinx.serialization.modules.SerializersModule
import java.io.File

abstract class ModernAddon(data: AddonData) {

    val manager = data.manager

    val info = data.info

    val logger = data.logger

    val api = DCustomAPI(data)

    abstract suspend fun enableAddon()
    abstract suspend fun disableAddon()
    open suspend fun reloadAddon() {
        disableAddon()
        enableAddon()
    }

    open suspend fun botBuilder(token: String, botBuilder: Any): Any {
        return botBuilder
    }

    inline fun <reified T> getDefaultConfig(): ConfigFile<T> {
        return ConfigFile.create<T>(File(manager.addonDirectory, "/${info.pluginName}.yml"), SerializersModule { })
    }

}