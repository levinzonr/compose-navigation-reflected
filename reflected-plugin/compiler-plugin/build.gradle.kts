@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    kotlin("kapt")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    `maven-publish`
}

group = "com.levinzonr.navigation.reflected"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            pom {
                name.set("compiler-plugin")
                description.set("Compiler Plugin")

            }
        }
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.compiler)
    compileOnly(libs.google.autoservice)
    kapt(libs.google.autoservice)

}