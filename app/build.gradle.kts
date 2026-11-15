plugins {
    id("com.android.application") version "8.8.2"
}

android {
    namespace = "io.freshcabin.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.freshcabin.demo"
        minSdk = 28
        targetSdk = 35
        versionCode = 4
        versionName = "1.3"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    flavorDimensions += "platform"
    productFlavors {
        create("standalone") { dimension = "platform" }
        create("pleos") {
            dimension = "platform"
            applicationIdSuffix = ".pleos"
            versionNameSuffix = "-pleos"
        }
    }

    sourceSets["main"].assets.srcDir(rootProject.file("docs"))
}

dependencies {
    "pleosImplementation"("ai.pleos.playground:Vehicle:2.3.2")
}
