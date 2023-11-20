package configuration

import configuration.dataConfigs.GenericData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import java.io.File

open class ConfigsDirectory<T: GenericData>(private val folder: File, private val module: SerializersModule) {

    val dirConfigFiles = mutableListOf<ConfigFile<T>>()

    fun loadFolderFiles(clearConfigs: Boolean) {

        if (clearConfigs) dirConfigFiles.clear()

        if (!folder.exists()) {
            folder.mkdirs()
        }

        folder.listFiles()?.filter { it.extension == "yml" || it.extension == "yaml" }?.map { file ->
            try {
                val tempCfg = ConfigFile<T>(file, module)
                tempCfg.loadFile()
                dirConfigFiles.add(tempCfg)
            } catch (e: SerializationException) {
                ConfigVault.logger.error(
                    """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                )
            }
        }
    }
}