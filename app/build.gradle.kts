plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.leave_management_system"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.leave_management_system"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    project dependency (user define install)

    /**
     * Dependency Name : CircleImageView
     * Dependency Url : https://github.com/hdodenhof/CircleImageView
     * -------------------------------------------------------------------
     * Dependency Name : Volley
     * Dependency Url : https://google.github.io/volley/
     * -------------------------------------------------------------------
     * Dependency Name : Glide
     * Dependency Url : https://github.com/bumptech/glide
     * -------------------------------------------------------------------
     * Dependency name : Android-Image-Cropper
     * Dependency Url : https://github.com/ArthurHub/Android-Image-Cropper
     * -------------------------------------------------------------------
     * Dependency name : Lottie Animation
     * Dependency Url : https://github.com/airbnb/lottie-android
     * */

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation("com.airbnb.android:lottie:3.4.0")
}