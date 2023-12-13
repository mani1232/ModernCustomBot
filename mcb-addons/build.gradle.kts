subprojects {
    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-Beta1")

        // Other
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
        compileOnly("ch.qos.logback:logback-classic:1.4.14")
        compileOnly("net.dv8tion:JDA:5.0.0-beta.18")
        compileOnly(project(path = ":mcb-api", configuration = "shadow"))
    }
}