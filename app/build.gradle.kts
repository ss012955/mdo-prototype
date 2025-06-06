plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.prototype"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prototype"
        minSdk = 26
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

    // Add this packaging block to resolve the duplicate META-INF/DEPENDENCIES issue
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    buildFeatures{
        dataBinding = true;
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.picasso)
    implementation(libs.viewpager2)
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.firebase:firebase-auth:21.0.7")
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-auth:21.1.0")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation ("androidx.activity:activity:1.7.2")

    // External dependencies
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")

}

apply(plugin = "com.google.gms.google-services")

