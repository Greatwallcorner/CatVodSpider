import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import java.io.FileInputStream
import java.io.FileReader
import java.security.MessageDigest


plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.compose")
}

java {
}
group = "org.heatdesert.catvod"
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
    implementation("cn.hutool:hutool-all:5.8.27")
    implementation("com.google.guava:guava:32.1.3-jre")
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20231013")
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation(kotlin("stdlib-jdk8"))

    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.desktop.common)
    implementation(compose.ui)
    implementation(compose.uiUtil)
    implementation(compose.uiTooling)
}

tasks.withType<Jar> {
    destinationDirectory = file("$rootDir/jar")
    archiveBaseName = "spider"
    manifest {
        attributes["Main-Class"] = "org.github.catvod.Main"
    }
    doLast {
        modJson()
    }
}

fun modJson() {
    val files = listOf("$rootDir/json/config.json", "$rootDir/json/configAll.json")
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    for (file in files) {
        if (!File(file).exists()) return
        val parseReader = JsonParser.parseReader(JsonReader(FileReader(File(file))))
        var spiderPath = parseReader.asJsonObject.get("spider").asString
        if (spiderPath.contains("md5")) {
            spiderPath = spiderPath.split(";")[0]
        }
        val md5 = calculateMD5(File("$rootDir/jar/spider.jar"))
        spiderPath += ";md5;$md5"
        println("new spider path $spiderPath")
        parseReader.asJsonObject.addProperty("spider", spiderPath)
        println("new json ${parseReader.toString()}")
        File(file).writeText(gson.toJson(parseReader))
    }
}

fun calculateMD5(file: File): String {
    val digest = MessageDigest.getInstance("MD5")
    val fis = FileInputStream(file)
    val bytes = ByteArray(4096)
    var count: Int
    while ((fis.read(bytes).also { count = it }) != -1) digest.update(bytes, 0, count)
    fis.close()
    val sb = StringBuilder()
    for (b in digest.digest()) sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
    return sb.toString()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
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
    jvmToolchain(11)
}