plugins {
    id("xyz.deftu.gradle.multiversion-root")
}

preprocess {
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val fabric11605 = createNode("1.16.5-fabric", 11605, "yarn")
    val fabric11502 = createNode("1.15.2-fabric", 11502, "yarn")
    val forge11502 = createNode("1.15.2-forge", 11502, "srg")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")

    fabric11903.link(fabric11902, file("versions/1.19.3-1.19.2.txt"))
    fabric11902.link(fabric11802)
    fabric11802.link(fabric11701)
    fabric11701.link(fabric11605, file("versions/1.17.1-1.16.5.txt"))
    fabric11605.link(fabric11502)
    fabric11502.link(forge11502)
    forge11502.link(forge11202, file("versions/1.15.2-1.12.2.txt"))
    forge11202.link(forge10809, file("versions/1.12.2-1.8.9.txt"))
}

listOf(
    "MavenLocalRepository",
    "DeftuReleasesRepository",
    "DeftuSnapshotsRepository"
).forEach { repo ->
    tasks.register("publishAllVersionsTo$repo") {
        group = "publishing"

        listOf(
            "1.19.3-fabric",
            "1.19.2-fabric",
            "1.18.2-fabric",
            "1.17.1-fabric",
            "1.16.5-fabric",
            "1.15.2-fabric",
            "1.15.2-forge",
            "1.12.2-forge",
            "1.8.9-forge"
        ).forEach { version ->
            dependsOn(":$version:publishAllPublicationsTo$repo")
        }
    }
}
