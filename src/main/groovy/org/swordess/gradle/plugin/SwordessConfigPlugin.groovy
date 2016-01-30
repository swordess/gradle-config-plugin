package org.swordess.gradle.plugin

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.kt3k.gradle.plugin.CoverallsPlugin

class SwordessConfigPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("swordess", SwordessConfigPluginExtension)

        project.plugins.apply KotlinPluginWrapper

        // Unit Test Coverage
        project.plugins.apply JacocoPlugin
        project.plugins.apply CoverallsPlugin

        // Packaging and Publishing
        project.plugins.apply MavenPublishPlugin
        project.plugins.apply BintrayPlugin

        project.afterEvaluate {
            project.repositories {
                jcenter()
            }
            project.dependencies {
                compile "org.jetbrains.kotlin:kotlin-stdlib:${project.swordess.kotlinVersion}"

                testCompile "junit:junit:${project.swordess.junitVersion}"
                testCompile "org.jetbrains.kotlin:kotlin-test:${project.swordess.kotlinVersion}"
            }

            project.sourceSets {
                main.java.srcDirs += 'src/main/kotlin'
            }

            project.tasks.create('sourceJar', Jar) {
                dependsOn project.tasks.getByName('classes')
                from project.sourceSets.main.allSource
                classifier 'sources'
                extension 'jar'
                group 'build'
            }

            project.jacoco {
                toolVersion = project.swordess.jacocoToolVersion
            }

            project.jacocoTestReport {
                reports {
                    xml.enabled = true // coveralls plugin depends on xml format report
                    html.enabled = true
                }
            }

            project.publishing {
                publications {
                    swordess(MavenPublication) {
                        from project.components.java
                        artifact project.tasks.sourceJar
                    }
                }
            }

            project.bintray {
                user = project.hasProperty('bintrayUser') ? project.property('bintrayUser') : System.getenv('BINTRAY_USER')
                key = project.hasProperty('bintrayApiKey') ? project.property('bintrayApiKey') : System.getenv('BINTRAY_API_KEY')
                publications = ['swordess']
                pkg {
                    repo = 'maven'
                    name = project.name
                    licenses = ['MIT']
                    vcsUrl = project.swordess.vcsUrl
                    version {
                        name = project.version
                        released = new Date()
                    }
                }
            }
        }
    }

}
