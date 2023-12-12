package api.addon

import org.slf4j.Logger
import java.io.File
import java.net.URLClassLoader

data class AddonData(
    val info: Info,
    val manager: Manager,
    val logger: Logger
)

data class Info(
    val pluginVersion: String,
    val pluginName: String,
)

data class Manager(
    val addonDirectory: File,
    val urlClassLoader: URLClassLoader,
)