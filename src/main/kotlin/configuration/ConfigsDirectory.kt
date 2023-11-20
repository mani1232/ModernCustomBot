package configuration

import configuration.dataConfigs.GenericData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import java.io.File

open class ConfigsDirectory<T: GenericData>(private val folder: File, private val module: SerializersModule) {

    val dirConfigFiles = mutableSetOf<ConfigFile<T>>()

    fun loadFolderFiles(): List<T> {
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val list = mutableListOf<T>()

        folder.listFiles()?.filter { it.extension == "yml" || it.extension == "yaml" }?.map { file ->
            try {
                val tempCfg = ConfigFile<T>(file, module)
                dirConfigFiles.add(tempCfg)
                tempCfg.loadFile()
                val data = tempCfg.data
                if (data != null) {
                    list.add(data)
                }
            } catch (e: SerializationException) {
                ConfigVault.logger.error(
                    """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                )
            }
        }
        return list
    }
}