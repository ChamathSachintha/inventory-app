plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.inventoryapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.inventoryapp"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    // Core Android
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // RecyclerView (IMPORTANT for your inventory)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView (for modern product UI)
    implementation("androidx.cardview:cardview:1.0.0")

    // Volley (API calls for Market Insights page)
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.material:material:1.12.0")

    // Testing
    testImplementation(libs.junit)

    implementation("com.github.bumptech.glide:glide:4.16.0")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}