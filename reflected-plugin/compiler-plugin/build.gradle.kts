import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    kotlin("kapt")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.compiler)
    compileOnly(libs.google.autoservice)
    kapt(libs.google.autoservice)

}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    pomFromGradleProperties()
    configure(KotlinJvm())
    signAllPublications()
}