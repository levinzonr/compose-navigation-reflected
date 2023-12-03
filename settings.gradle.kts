pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()

    }
}

rootProject.name = "Reflected"
include(":app")
include(":reflected-core")
include(":reflected-navigation")
include(":reflected-navigation-material")
include(":reflected-plugin:gradle-plugin")
include(":reflected-plugin:compiler-plugin")
