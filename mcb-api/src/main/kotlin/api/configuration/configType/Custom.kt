package api.configuration.configType

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Custom {

    @Transient
    var tempData = mutableMapOf<String, Any>()

}