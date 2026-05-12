rootProject.name = "AuraRoll"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":shared")
include(":feature:onboarding:api")
include(":feature:onboarding:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:detail:api")
include(":feature:detail:impl")
include(":core:designsystem")
include(":androidApp")
include(":common")
