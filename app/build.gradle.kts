plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("app.cash.sqldelight") version "2.0.2"
    id("kotlin-kapt") // For Kotlin annotation processing
    id("dagger.hilt.android.plugin") // Apply Hilt plugin
}


kapt {
    //this block is to suppress some warnings.
    correctErrorTypes = true // Helps Hilt work with other libraries like Room
    arguments {
        // Enable Dagger's fastInit
        arg("dagger.fastInit", "enabled")
    }
}

android {
    namespace = "com.hich2000.tagcapella"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hich2000.tagcapella"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.session)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //material icons extended
    implementation(libs.androidx.material.icons.extended)

    //coroutines for async, await, etc.
    implementation(libs.kotlinx.coroutines.android)

    //implementation of viewmodels for compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)

    //sqldelight
    implementation(libs.sqldelight.android.driver)

    // Hilt dependencies
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt Compose Navigation dependency for hiltViewModel() in Composables
    implementation(libs.androidx.hilt.navigation.compose)
}

sqldelight {
    databases {
        create("TagcapellaDb") {
            packageName.set("com.hich200.tagcapella")
        }
    }
}