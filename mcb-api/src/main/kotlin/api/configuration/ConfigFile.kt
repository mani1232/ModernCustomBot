package api.configuration

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    lateinit var data: Deferred<T?>

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun updateFile(
        dataClass: T
    ): Deferred<T?> {
        coroutineScope {
            data = async {
                try {
                    yaml.encodeToStream(serializer, dataClass, file.outputStream())
                    dataClass
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                    )
                    null
                }
            }
        }
        return data

    }

    suspend fun loadDefaultFile(
        dataClass: T
    ): Deferred<T?> {
        coroutineScope {
            data = async {
                try {
                    if (file.createNewFile()) {
                        updateFile(dataClass).await()
                    } else {
                        loadFile().await()
                    }
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                                        ${e.message}""".trimIndent()
                    )
                    null
                }
            }
        }
        return data
    }

    suspend fun loadFile(
    ): Deferred<T?> {
        coroutineScope {
            data = async {
                try {
                    yaml.decodeFromStream(serializer, file.inputStream())
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                                ${e.message}""".trimIndent()
                    )
                    null
                }
            }
        }
        return data
    }

}
