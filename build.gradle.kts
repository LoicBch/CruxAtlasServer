val ktor_version: String by project
val logback_version: String by project
val kotlin_version: String by project


plugins {
    id("application")
    id("io.ktor.plugin") version "2.3.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    id("com.google.cloud.tools.appengine") version "2.4.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.0"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

ktor {
    fatJar {
        archiveFileName.set("crux-atlas-$version-all.jar")
    }
//        archiveFileName.set("fat.jar")

    docker {
        jreVersion.set(JavaVersion.VERSION_17)
        localImageName.set("sample-docker-image")
        imageTag.set("0.0.6")
        portMappings.set(
            listOf(
                io.ktor.plugin.features.DockerPortMapping(
                    80,
                    8080,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "vpscruxatlas" },
                username = provider { "cruxatlas" },
                password = provider { "Cruxatlas2024!" }
            )
        )
    }
}

dependencies {

    //Payments
    implementation("com.stripe:stripe-java:25.0.0")

    //SMTP Mailing
    implementation("org.apache.commons:commons-email:1.5")

    //orm
    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("org.ktorm:ktorm-core:3.4.1")
    implementation("org.ktorm:ktorm-support-mysql:3.4.1")
    implementation("org.ktorm:ktorm-jackson:3.4.1")

//    serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
    implementation("com.google.code.gson:gson:2.8.8")

//    Security
    implementation("org.owasp.encoder:encoder:1.2.3")

    //validation
    implementation("io.konform:konform-jvm:0.4.0")

    //connection pooling & caching
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.ehcache:ehcache:3.10.8")

    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation(kotlin("stdlib-jdk8"))
}


//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
