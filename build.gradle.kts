plugins {
    id("java")
    kotlin("jvm")
}

java {
}
group = "org.heatdsert.catvod"
//version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.googlecode.juniversalchardet:juniversalchardet:1.0.3")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.lookfirst:sardine:5.10")
    implementation("cn.wanghaomiao:JsoupXpath:2.5.1")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("com.google.zxing:core:3.3.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.google.guava:guava:32.1.3-jre")
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20231013")
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation(kotlin("stdlib-jdk8"))


}

tasks.withType<Jar> {
    destinationDirectory = file("$rootDir/jar")
    archiveBaseName = "spider"
    manifest {
        attributes["Main-Class"] = "org.github.catvod.Main"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

allprojects {
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        maven("https://androidx.dev/storage/compose-compiler/repository")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}
kotlin {
    jvmToolchain(8)
}