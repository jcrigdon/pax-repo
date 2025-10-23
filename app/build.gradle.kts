plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.paxrepo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.paxrepo"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug { isMinifyEnabled = false }
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        aidl = true
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*"
        }
    }
}

// hard-stop any attempt to pull androidx.core 1.17.0
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.core" && requested.name.startsWith("core")) {
            useVersion("1.13.1")
            because("AGP 8.7 + compileSdk 34; core 1.17.0 requires AGP 8.9.1 + SDK 36")
        }
    }
}

dependencies {
    implementation(project(":PAX-POSLink-Android"))

    // ✅ Explicit versions (no version catalog)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
