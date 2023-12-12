package api.addon

import api.configuration.ConfigFile
import kotlinx.serialization.modules.SerializersModule
import java.io.File

abstract class ModernAddon {

    private lateinit var data: AddonData

    val manager = data.manager
    val info = data.info

    abstract suspend fun enableAddon()
    abstract suspend fun disableAddon()
    abstract suspend fun reloadAddon()

    inline fun <reified T> getDefaultConfig(): ConfigFile<T> {
        return ConfigFile.create<T>(File(manager.addonDirectory, "/${info.pluginName}.yml"), SerializersModule { })
    }

    fun setData(data: AddonData) {
        this.data = data
    }

}