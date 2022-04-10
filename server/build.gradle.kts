val ktor_version: String by extra
val logback_version: String by extra
val http4k_version: String by extra

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.31"
    id("org.graalvm.buildtools.native") version "0.9.4"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(kotlin("bom"))

    implementation(platform("org.http4k:http4k-bom:$http4k_version"))
    implementation("org.http4k:http4k-core:$http4k_version")
    implementation("org.http4k:http4k-server-netty:$http4k_version")
    implementation("org.http4k:http4k-server-undertow:$http4k_version")
    implementation("org.http4k:http4k-client-apache:$http4k_version")
    implementation("org.http4k:http4k-security-oauth:$http4k_version")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4k_version")
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.ktor:ktor-serialization:$ktor_version")
    // implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.litote.kmongo:kmongo-coroutine:4.3.0")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.3.0")

    // testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    // testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    // implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    implementation(project(":common"))
}

application {
    // Define the main class for the application.
    mainClass.set("kelegram.server.AppKt")

    // Dev mode
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestJava {
        targetCompatibility = "1.8"
    }
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "kelegram.server.AppKt"))
        }
    }
}


nativeBuild {
    buildArgs.add("--initialize-at-build-time=ch.qos.logback,org.slf4j")
    buildArgs.add("--allow-incomplete-classpath")
    buildArgs.add("-H:+ReportExceptionStackTraces")
    //buildArgs.add("--initialize-at-run-time=io.netty.util.internal.logging,io.netty.channel.MultithreadEventLoopGroup,io.netty.bootstrap.ServerBootstrap,io.netty.util.internal.SystemPropertyUtil")
    buildArgs.add("--trace-class-initialization=io.netty.util.internal.logging.LocationAwareSlf4JLogger, ")
    // https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/Agent.md#agent-advanced-usage
    configurationFileDirectories.from(file("./config"))
}