package api.configuration

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import java.io.File


open class ConfigFile<T>(val file: File, module: SerializersModule, private val serializer: KSerializer<T>) {

    companion object {
        inline fun <reified T> create(file: File, module: SerializersModule) =
            ConfigFile(file, module, module.serializer<T>())
    }

    private val yaml = ConfigDefaults.getConfiguredYaml(module)

    var data: T? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun updateFile(
        dataClass: T
    ): T? {
        data = try {
            yaml.encodeToStream(serializer, dataClass, file.outputStream())
            dataClass
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
            )
            null
        }
        return data
    }

    fun loadDefaultFile(
        dataClass: T
    ): T? {
        data = try {
            if (file.createNewFile()) {
                updateFile(dataClass)
                dataClass
            } else {
                loadFile()
            }
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                                ${e.message}""".trimIndent()
            )
            null
        }
        return data
    }

    fun loadFile(
    ): T? {
        data = try {
            yaml.decodeFromStream(serializer, file.inputStream())
        } catch (e: SerializationException) {
            logger.error(
                """Error in file: ${file.name}
                                ${e.message}""".trimIndent()
            )
            null
        }
        return data
    }

}
