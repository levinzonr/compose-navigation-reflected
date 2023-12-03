import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class NavigationReflectedPlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.create("navigationReflected", NavigationReflectedExtension::class.java)
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>,
    ): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider {
            listOf()
        }
    }

    override fun getCompilerPluginId(): String = "reflected"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.levinzonr.navigation.reflected",
        artifactId = "compiler-plugin",
        version = "1.0.0",
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}

open class NavigationReflectedExtension {
    var enabled: Boolean = true
}
