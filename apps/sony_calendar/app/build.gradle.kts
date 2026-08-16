plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sonymobile.calendar"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.sonymobile.calendar"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "26.8.C.8.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.core)
    implementation(libs.fragment)
    implementation(libs.preference)
    implementation(libs.work)
    implementation(libs.loader)
    implementation(libs.viewpager)
    implementation(libs.drawerlayout)
    implementation(libs.legacy.v4)
    implementation(libs.interpolator)
    implementation(libs.vectordrawable)
    implementation(libs.lifecycle.runtime)
    implementation(libs.guava)
    implementation(libs.volley)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.analytics)
    implementation(libs.play.services.tagmanager)
    implementation(libs.play.services.base)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}