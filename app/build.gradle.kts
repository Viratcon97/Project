plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.capstone"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.capstone"
        minSdk = 30
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Kotlin
    val fragmentVersion = "1.6.2"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.android.material:material:1.9.0")

    // Add the dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage-ktx")

    //Display picture
    implementation("com.squareup.picasso:picasso:2.71828")

    //Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation ("com.squareup.picasso:picasso:2.8")

    //Picture Compressor
    implementation("id.zelory:compressor:3.0.1")
}