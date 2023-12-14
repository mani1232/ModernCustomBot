package api.addon

import api.configuration.ConfigFile
import api.discord.DCustomAPI
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import java.io.File

abstract class ModernAddon {

    private lateinit var data: AddonData

    lateinit var manager: Manager

    lateinit var info: Info

    lateinit var logger: Logger

    lateinit var api: DCustomAPI

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

    fun initDefaultValue(data: AddonData) {
        this.data = data
        this.api = DCustomAPI(data)
        this.manager = data.manager
        this.info = data.info
        this.logger = data.logger
    }

}