plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    signingConfigs {
        create("release") {
            keyAlias = "key0"
            keyPassword = "123456789"
            storePassword = "123456789"
            storeFile =
                file("/Users/h0neyhunter-m2/Desktop/U-SYSTEM_App/1_Github_Public_Test/AgoraWebRtcCallVideoExample/1_FilesSDKLinkInformation/keystore.jks")
        }
    }
    namespace = "com.usyssoft.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.usyssoft.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("release")
        //ndk {
            //abiFilters.addAll(arrayOf("arm64-v8a","x86_64","x86","armeabi-v7a"))
        //    abiFilters.addAll(arrayOf("arm64-v8a","x86_64"))
        //}
    }

    buildTypes {
        debug {
            isMinifyEnabled = true

        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            testProguardFiles ("test-proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true

    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }


}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //implementation("io.agora.rtc:full-sdk:4.2.6")
    implementation("io.agora.rtc:full-rtc-basic:4.2.6")

    implementation("commons-codec:commons-codec:1.11")



}