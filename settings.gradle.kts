// D:/NetraMeds/settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // <-- ADD THIS LINE
    }
}
dependencyResolutionManagement {    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NetraMeds"
include(":app")
