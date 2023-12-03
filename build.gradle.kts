
// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.hilt) apply false
    alias(libs.plugins.spotless)
}
true // Needed to make the Suppress annotation work for the plugins block

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.10"))
    }
}
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-receivers")

        }
    }

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("**/RateReminderActions.kt")
            ktlint("1.0.1")
                .editorConfigOverride(
                    mapOf(
                        "indent_size" to 4,
                        "indent_style" to "space",
                        "ij_kotlin_imports_layout" to "*,java.**,javax.**,kotlin.**,kotlinx.**,^",
                        "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                        "ij_kotlin_allow_trailing_comma" to "true",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                        "ktlint_code_style" to "android_studio",
                    )
                )
        }
    }
}