pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.hq.hydraulic.software")
    }

    plugins {
        val kotlinVer = "1.9.23"
        val composeVer = "1.6.2"
        kotlin("jvm").version(kotlinVer)
        kotlin("multiplatform").version(kotlinVer)
        id("org.jetbrains.compose").version(composeVer)
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "CatVodSpider"

