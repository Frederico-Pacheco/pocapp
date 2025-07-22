import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val wifiSsid: String = localProperties.getProperty("wifi.ssid")
val wifiPsk: String = localProperties.getProperty("wifi.pks")
val appPkg: String = localProperties.getProperty("app.pkg")
val appCompany: String = localProperties.getProperty("app.company")
val phoneNumber: String = localProperties.getProperty("phone.number")

android {
    namespace = "br.org.cesar.wificonnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.org.cesar.wificonnect"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "WIFI_SSID", "\"$wifiSsid\"")
        buildConfigField("String", "WIFI_PKS", "\"$wifiPsk\"")
        buildConfigField("String", "APP_PKG", "\"$appPkg\"")
        buildConfigField("String", "APP_COMPANY", "\"$appCompany\"")
        buildConfigField("String", "PHONE_NUMBER", "\"$phoneNumber\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation(compose.materialIconsExtended)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.window)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}