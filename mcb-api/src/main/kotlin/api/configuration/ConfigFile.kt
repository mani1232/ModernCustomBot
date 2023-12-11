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
import java.util.Optional


open class ConfigFile<T : Any>(val file: File, module: SerializersModule, private val serializer: KSerializer<T>) {

    companion object {
        inline fun <reified T : Any> create(file: File, module: SerializersModule) =
            ConfigFile(file, module, module.serializer<T>())
    }

    private val yaml = ConfigDefaults.getConfiguredYaml(module)

    lateinit var data: Deferred<Optional<T>>

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun updateFile(
        dataClass: T
    ): Deferred<Optional<T>> {
        coroutineScope {
            data = async {
                try {
                    yaml.encodeToStream(serializer, dataClass, file.outputStream())
                    Optional.of(dataClass)
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                            ${e.message}""".trimIndent()
                    )
                    Optional.empty<T>()
                }
            }
        }
        return data

    }

    suspend fun loadDefaultFile(
        dataClass: T
    ): Deferred<Optional<T>> {
        coroutineScope {
            data = async {
                try {
                    if (file.createNewFile()) {
                        Optional.of(updateFile(dataClass))
                    } else {
                        Optional.of(loadFile())
                    }
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                                        ${e.message}""".trimIndent()
                    )
                    Optional.empty<T>()
                }
            } as Deferred<Optional<T>>
        }
        return data
    }

    suspend fun loadFile(
    ): Deferred<Optional<T>> {
        coroutineScope {
            data = async {
                try {
                    Optional.of(yaml.decodeFromStream(serializer, file.inputStream()))
                } catch (e: SerializationException) {
                    logger.error(
                        """Error in file: ${file.name}
                                ${e.message}""".trimIndent()
                    )
                    Optional.empty<T>()
                }
            }
        }
        return data
    }

}
