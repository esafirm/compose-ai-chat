import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)

    alias(libs.plugins.buildKonfig)
}

buildkonfig {
    packageName = "nolambda.aichat"

    // default config is required
    defaultConfigs {
        buildConfigField(STRING, "OPEN_AI_TOKEN", System.getenv("OPEN_AI_TOKEN") ?: "")
    }
}

kotlin {
    jvm("desktop")
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    ios()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)
                implementation("org.jetbrains.compose.components:components-resources:1.4.0-alpha01-dev942")
                implementation("io.github.qdsfdhvh:image-loader:1.2.10")

                implementation(libs.ktor.core)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.auth)
                implementation(libs.ktor.logging)

                implementation(libs.voyager.navigator)

                implementation("org.jetbrains:markdown:0.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "nolambda.aichat.common"
    compileSdk = 33
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDir("src/commonMain/resources")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
