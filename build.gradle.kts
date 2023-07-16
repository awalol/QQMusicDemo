import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

plugins {
    kotlin("multiplatform") version "1.8.21"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "cn.awalol"
version = "1.0-SNAPSHOT"
val ktor_version = "2.3.2"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                windowsResources("${project.projectDir}/src/nativeMain/resources/samples.rc")
                entryPoint = "cn.awalol.qqmusic.main"
            }
        }
    }


    sourceSets {
        val nativeMain by getting{
            dependencies {
                implementation("com.github.msink:libui:0.1.8")
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-winhttp:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            }
        }
        val nativeTest by getting
    }
}

fun Executable.windowsResources(rcFileName: String) {
    val taskName = linkTaskName.replaceFirst("link", "windres")
    val inFile = File(rcFileName)
    val outFile = buildDir.resolve("processedResources/$taskName.res")

    val windresTask = tasks.create<Exec>(taskName) {
        val konanDataDir = System.getenv("KONAN_DATA_DIR") ?: "${System.getProperty("user.home")}/.konan"
        val toolchainBinDir = when (target.konanTarget.architecture.bitness) {
            32 -> File("$konanDataDir/dependencies/msys2-mingw-w64-i686-2/bin").invariantSeparatorsPath
            64 -> File("$konanDataDir/dependencies/msys2-mingw-w64-x86_64-2/bin").invariantSeparatorsPath
            else -> error("Unsupported architecture")
        }

        inputs.file(inFile)
        outputs.file(outFile)
        commandLine("$toolchainBinDir/windres", inFile, "-D_${buildType.name}", "-O", "coff", "-o", outFile)
        environment("PATH", "$toolchainBinDir;${System.getenv("PATH")}")

        dependsOn(compilation.compileKotlinTask)
    }

    linkTask.dependsOn(windresTask)
    linkerOpts(outFile.toString())
}
