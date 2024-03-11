plugins {
//    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    kotlin("multiplatform") version "1.9.23"
}

group = "me.chardskarth"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val okioVersion = "3.8.0"

        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:$okioVersion")
            }
        }

        val nativeMain by getting
        val nativeTest by getting
    }
}



//dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib")
//    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.21")
//    implementation("io.github.kscripting:kscript-annotations:1.5.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
//    implementation("com.github.pgreze:kotlin-process:1.4.1")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
//    implementation("org.jetbrains.kotlin:kotlin-serialization-compiler-plugin:1.9.0")
//    implementation("com.charleskorn.kaml:kaml-jvm:0.56.0")
//    implementation("de.m3y.kformat:kformat:0.10")
//}
//
//sourceSets.getByName("main").java.srcDirs("src")
//sourceSets.getByName("test").java.srcDirs("src")
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions {
//
//        freeCompilerArgs = listOf("-Xplugin=$(echo ~)/.m2/repository/org/jetbrains/kotlin/kotlin-serialization-compiler-plugin/1.9.0/kotlin-serialization-compiler-plugin-1.9.0.jar")
//    }
//}
