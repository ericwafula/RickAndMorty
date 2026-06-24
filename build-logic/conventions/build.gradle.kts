plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.agp)
    compileOnly(libs.kgp)
}

gradlePlugin {
    plugins {
        fun createPlugin(
            name: String,
            id: String,
            implementationClass: String
        ) {
            create(name) {
                this.id = id
                this.implementationClass = implementationClass
            }
        }

        createPlugin(
            name = "RickAndMortyAndroidApplication",
            id = "rickandmorty.android.application",
            implementationClass = "plugins.AndroidApplicationConventionPlugin"
        )

        createPlugin(
            name = "RickAndMortyAndroidLibrary",
            id = "rickandmorty.android.library",
            implementationClass = "plugins.AndroidLibraryConventionPlugin"
        )

        createPlugin(
            name = "RickAndMortyAndroidLibraryCompose",
            id = "rickandmorty.android.library.compose",
            implementationClass = "plugins.AndroidLibraryComposeConventionPlugin"
        )
    }
}
