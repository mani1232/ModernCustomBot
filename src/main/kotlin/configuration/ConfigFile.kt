package configuration

import configuration.ConfigVault.Companion.getConfiguredYaml
import configuration.dataConfigs.GenericData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.LoggerFactory
import java.io.File

open class ConfigFile<T : GenericData>(private val file: File, module: SerializersModule) {

    private val yaml = getConfiguredYaml(module)

    var data: T? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun updateFile(dataClass: T) {
        data = updateFile(dataClass as GenericData) as T?
    }

    fun loadDefaultFile(dataClass: T) {
        data = loadDefaultFile(dataClass as GenericData) as T?
    }

    fun loadFile() {
        data = loadFile<GenericData>() as T?
    }


    private inline fun <reified T: GenericData> updateFile(
        dataClass: T
    ) : T? {
        return try {
            file.writer(Charsets.UTF_8).use {
                it.write(yaml.encodeToString(dataClass))
                it.close()
            }
            dataClass
        } catch (e: SerializationException) {
             logger.error(
                """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
            )
            null
        }
    }

    private inline fun <reified T: GenericData> loadDefaultFile(
        dataClass: T
    ) : T?{
        try {
            return if (file.createNewFile()) {
                file.writer(Charsets.UTF_8).use {
                    it.write(yaml.encodeToString(dataClass))
                    it.close()
                }
                dataClass
            } else {
                yaml.decodeFromString(file.reader().use {
                    it.readLines().joinToString(separator = "\n")
                })
            }
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
            )
            return null
        }
    }

    private inline fun <reified T: GenericData> loadFile(
    ): T? {
        return try {
            yaml.decodeFromString(file.reader().use {
                it.readLines().joinToString(separator = "\n")
            })
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                                ${e.message}""".trimIndent()
            )
            null
        }
    }

}