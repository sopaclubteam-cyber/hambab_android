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
        versionCode = 2
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        // Supabase 연결 정보 — local.properties 에서 주입 (gitignore 보호)
        // 키 누락 시 빈 문자열 → mock 모드 fall-through (offline-safe)
        buildConfigField("String", "SUPABASE_URL",
            "\"${localProps.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY",
            "\"${localProps.getProperty("SUPABASE_ANON_KEY", "")}\"")
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
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseKey) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
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
            // Ktor / Supabase 메타파일 중복 제거
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
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

    // Supabase Kotlin SDK (postgrest + auth) + Ktor OkHttp transport
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.okhttp)

    debugImplementation(libs.androidx.ui.tooling)
}
