pluginManagement {
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
        jcenter() {
            content {
                includeModule("com.theartofdev.edmodo", "android-image-cropper")
            }
        }

    }
}

rootProject.name = "Leave-Management-System"
include(":app")
