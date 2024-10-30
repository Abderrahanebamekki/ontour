plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ontour"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ontour"
        minSdk = 24
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    implementation("com.mapbox.maps:android:11.6.0")
    implementation("com.mapbox.extension:maps-compose:11.6.0")





    implementation ("com.mapbox.plugin:maps-locationcomponent:11.6.0")
    implementation ("com.mapbox.navigationcore:navigation:3.2.1")
    implementation ("com.mapbox.navigationcore:copilot:3.2.1")
    implementation ("com.mapbox.navigationcore:ui-maps:3.2.1")
    implementation ("com.mapbox.navigationcore:voice:3.2.1")
    implementation ("com.mapbox.navigationcore:tripdata:3.2.1")
    implementation ("com.mapbox.navigationcore:android:3.2.1")
    implementation ("com.mapbox.navigationcore:ui-components:3.2.1")


    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-storage:20.1.0")


    implementation("io.coil-kt:coil-compose:2.0.0")  // Update to the latest version if necessary

    implementation ("androidx.compose.material:material-icons-extended:1.0.5")
    implementation("androidx.datastore:datastore-preferences:1.0.0")



}