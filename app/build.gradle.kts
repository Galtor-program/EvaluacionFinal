plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")


}

android {
    namespace = "com.example.alkewalletevalacion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alkewalletevalacion"
        minSdk = 31
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
  
}

dependencies {


   // Room
    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)




    // retrofit
    implementation (libs.retrofit)


    //Convertor Gson
    implementation (libs.gson)


    //Factory Gson
    implementation (libs.converter.gson)

    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // picasso
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}