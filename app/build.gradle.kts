plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")  // Add this line
}

android {
    namespace = "com.smart.garbage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smart.garbage"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // ... rest of your android config
}

dependencies {
    // Add the Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Your existing dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)

    // Update Firebase dependencies to use BOM
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-common")
    implementation(libs.firebase.crashlytics.buildtools)


    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}