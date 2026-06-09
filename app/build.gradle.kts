plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tarunmalkam.studentnotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tarunmalkam.studentnotes"
        minSdk = 23
        targetSdk = 35
        versionCode = 3
        versionName = "3.0"
    }

    sourceSets["main"].java.srcDirs("src/main/kotlin")
}
