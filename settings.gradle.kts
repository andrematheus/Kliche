rootProject.name = "Kliche"

buildCache {
    local {
        directory = File(rootDir, ".build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}