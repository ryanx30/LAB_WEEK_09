pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "2.2.21-2.0.0"
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LAB_WEEK_09"
include(":app")