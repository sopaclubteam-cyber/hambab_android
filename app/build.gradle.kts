import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// local.properties 에서 keystore 비밀번호 로드 (gitignore 보호)
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(FileInputStream(f))
}
val ksPath: String? = localProps.getProperty("hambab.keystore.path")
val ksStorePassword: String? = localProps.getProperty("hambab.keystore.password")
val ksAlias: String? = localProps.getProperty("hambab.key.alias")
val ksKeyPassword: String? = localProps.getProperty("hambab.key.password")
val hasReleaseKey = !ksPath.isNullOrBlank()
        && !ksStorePassword.isNullOrBlank()
        && !ksAlias.isNullOrBlank()
        && !ksKeyPassword.isNullOrBlank()
        && rootProject.file(ksPath!!).exists()

android {
    namespace = "duckring.hambab.com"
    compileSdk = 35

    defaultConfig {
        applicationId = "duckring.hambab.com"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (hasReleaseKey) {
            create("release") {
                storeFile = rootProject.file(ksPath!!)
                storePassword = ksStorePassword
                keyAlias = ksAlias
                keyPassword = ksKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseKey) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.foundation)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Material (XML themes for Activity)
    implementation(libs.material.android)

    debugImplementation(libs.androidx.ui.tooling)
}
