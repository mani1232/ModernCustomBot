package api.addon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader

data class AddonData(
    val info: Info = Info("null", "null"),
    val manager: Manager = Manager(File("/"), URLClassLoader(arrayOf())),
    val logger: Logger = LoggerFactory.getLogger("null")
)

data class Info(
    val pluginVersion: String,
    val pluginName: String,
)

data class Manager(
    val addonDirectory: File,
    val urlClassLoader: URLClassLoader,
)