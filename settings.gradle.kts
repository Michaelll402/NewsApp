pluginManagement {
    repositories {
<<<<<<< HEAD
        google()
=======
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
>>>>>>> cda0c7a (Updated News App with new API implementation and pagination)
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

<<<<<<< HEAD
rootProject.name = "News App"
=======
rootProject.name = "News_app"
>>>>>>> cda0c7a (Updated News App with new API implementation and pagination)
include(":app")
 