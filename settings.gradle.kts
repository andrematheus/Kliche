rootProject.name = "Kliche"

include("backend")
include("frontend")

buildCache {
    local {
        directory = File(rootDir, ".build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}