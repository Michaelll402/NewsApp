// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
<<<<<<< HEAD
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
=======
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
>>>>>>> cda0c7a (Updated News App with new API implementation and pagination)
}