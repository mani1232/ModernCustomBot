package api.configuration.configType

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.slf4j.LoggerFactory

@Serializable
abstract class Custom {

    @Transient
    var tempData = mutableMapOf<String, Any>()

    @Transient
    var logger = LoggerFactory.getLogger("Custom-Logger")

}