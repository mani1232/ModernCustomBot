package configuration

import configuration.dataConfigs.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

val customsTypesList : MutableList<KType> = mutableListOf(
    typeOf<SendText>(),
    typeOf<BotFilter>(),
    typeOf<MessageFilter>(),
)

val customsModule = SerializersModule {
    polymorphic(Custom::class) {
        customsTypesList.forEach {
            subclass(it.classifier as KClass<Custom>, serializer(it) as KSerializer<Custom>)
        }
    }
}
class CustomDiscordConfigDir: ConfigsDirectory<CustomDiscordConfig>(File("DiscordCustomConfigs/"), customsModule)