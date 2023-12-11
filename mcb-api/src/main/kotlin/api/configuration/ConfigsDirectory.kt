package api.configuration

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import java.io.File

open class ConfigsDirectory<T>(
    private val folder: File,
    private val module: SerializersModule,
    private val serializer: KSerializer<T>
) {

    val dirConfigFiles = mutableListOf<ConfigFile<T>>()
    private val logger = LoggerFactory.getLogger(this::class.java)


    companion object {
        inline fun <reified T> create(file: File, module: SerializersModule) =
            ConfigsDirectory(file, module, module.serializer<T>())
    }

    fun updateAllFiles() {
        dirConfigFiles.parallelStream().forEach { it.updateFile(it.data!!) }
    }


    fun loadDefaultFiles(files: MutableMap<String, T>) {
        if (!folder.exists()) {
            folder.mkdirs()
            files.forEach { (t, u) ->
                ConfigFile(folder.resolve(t), module, serializer).updateFile(u)
            }
        }
        loadFolderFiles()
    }

    fun loadFolderFiles() {
        if (!folder.exists()) {
            folder.mkdirs()
        }

        folder.listFiles()?.filter { it.extension == "yml" || it.extension == "yaml" }?.map { file ->
            try {
                val existedConfig = dirConfigFiles.parallelStream().filter { file.name == it.file.name }.findFirst()
                if (existedConfig.isPresent) {
                    existedConfig.get().loadFile()
                } else {
                    val tempCfg = ConfigFile(file, module, serializer)
                    tempCfg.loadFile()
                    dirConfigFiles.add(tempCfg)
                }
            } catch (e: SerializationException) {
                logger.error(
                    """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                )
            }
        }
    }
}