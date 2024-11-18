plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.prototype"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prototype"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.picasso)
    implementation(libs.viewpager2)
    implementation("com.google.firebase:firebase-auth:21.0.7")
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")

}

apply(plugin = "com.google.gms.google-services")

