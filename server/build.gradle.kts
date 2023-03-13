val ktor_version: String by extra
val logback_version: String by extra
val http4k_version: String by extra
val kotlin_version: String by extra

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.10"
    id("org.graalvm.buildtools.native") version "0.9.4"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(kotlin("bom:$kotlin_version"))

    implementation(platform("org.http4k:http4k-bom:$http4k_version"))
    implementation("org.http4k:http4k-core:$http4k_version")
    implementation("org.http4k:http4k-server-netty:$http4k_version")
    implementation("org.http4k:http4k-server-undertow:$http4k_version")
    implementation("org.http4k:http4k-client-apache:$http4k_version")
    implementation("org.http4k:http4k-security-oauth:$http4k_version")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4k_version")
    implementation("org.http4k:http4k-format-jackson:$http4k_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")

    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.5.1")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation(project(":common"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    // Attempt to remove the --allow-incomplete-classpath (almost)
    implementation("com.github.jnr:jnr-unixsocket:0.38.17")
    implementation("org.xerial.snappy:snappy-java:1.1.8.4")
    implementation("com.github.luben:zstd-jni:1.5.2-2")
    implementation("org.mongodb:mongodb-crypt:1.3.0")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")

}

application {
    // Define the main class for the application.
    mainClass.set("kelegram.server.AppKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestJava {
        targetCompatibility = "11"
    }
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "kelegram.server.AppKt"))
        }
    }
}

nativeBuild {
    buildArgs.add("--no-fallback")
    buildArgs.add("-H:+ReportExceptionStackTraces")
    buildArgs.add("--initialize-at-run-time=io.netty.util.internal.logging.Log4JLogger")
    buildArgs.add("--initialize-at-run-time=com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor")
    buildArgs.add("--initialize-at-build-time=ch.qos.logback,org.slf4j,javax.xml,jdk.xml")
    // I tried not to use this one, but with "sun.reflect.Reflection" and "org.apache.log4j.Logger" I don't think it's possible
    buildArgs.add("--allow-incomplete-classpath")

    // https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/Agent.md#agent-advanced-usage
    // configurationFileDirectories.from(file("./config"))
}