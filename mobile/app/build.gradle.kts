plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobile"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"
}

val room_version = "2.6.1"
val hilt_version = "2.44"

// Принудительно используем JavaPoet версии 1.13.0
configurations.all {
    resolutionStrategy {
        force("com.squareup:javapoet:1.13.0")
        force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.3.0")
    }
}

dependencies {
    // Явные зависимости на нужные версии библиотек для предотвращения конфликтов
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.3.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Navigation Component - использую стабильную версию
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    
    // Cardview
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    
    // Glide для загрузки изображений
    implementation("com.github.bumptech.glide:glide:4.15.1")
    
    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    
    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:$hilt_version")
    ksp("com.google.dagger:hilt-compiler:$hilt_version")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}