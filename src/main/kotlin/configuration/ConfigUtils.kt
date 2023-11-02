package configuration

import com.charleskorn.kaml.Yaml
import configuration.dataConfigs.BotConfig
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.slf4j.Logger
import java.io.File

const val MainConfigName = "CustomConfig.yml"

class ConfigVault {

    companion object {

        lateinit var mainConfig: BotConfig
        lateinit var logger: Logger
        private lateinit var path: String

        fun loadAll(pathString: String, log: Logger) {
            path = pathString
            logger = log
            mainConfig = ConfigVault().loadFile(File(path, MainConfigName), BotConfig())
        }

        fun reload() {
            mainConfig = ConfigVault().loadFile(File(path, MainConfigName), BotConfig())
        }

        fun updateAllFiles() {
            ConfigVault().updateFile(File(path, MainConfigName), mainConfig)
        }

        inline fun <reified T> loadCustomFiles(
            folderPath: String
        ): List<T> {
            val folder = File(folderPath)
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val list = mutableListOf<T>()

            folder.listFiles()?.filter { it.extension == "yml" }?.map { file ->
                try {
                    list.add(
                        Yaml.default.decodeFromString<T>(file.reader().use {
                            it.readLines().joinToString(separator = "\n")
                        })
                    )
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                    )
                }
            }
            return list
        }
    }

    private inline fun <reified T> updateFile(
        file: File, dataClass: T
    ) {
        try {
            file.writer(Charsets.UTF_8).use {
                it.write(Yaml.default.encodeToString(dataClass))
                it.close()
            }
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
            )
        }
    }

    private inline fun <reified T> loadFile(
        file: File, dataClass: T
    ): T {
        try {
            return if (file.createNewFile()) {
                file.writer(Charsets.UTF_8).use {
                    it.write(Yaml.default.encodeToString(dataClass))
                    it.close()
                }
                dataClass
            } else {
                Yaml.default.decodeFromString<T>(file.reader().use {
                    it.readLines().joinToString(separator = "\n")
                })
            }
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
            )
            return dataClass
        }
    }
}