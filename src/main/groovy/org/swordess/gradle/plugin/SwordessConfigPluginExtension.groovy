package org.swordess.gradle.plugin

import org.gradle.testing.jacoco.plugins.JacocoPlugin

class SwordessConfigPluginExtension {

    def String vcsUrl

    def kotlinVersion = '1.3.50'
    def jacocoToolVersion = JacocoPlugin.DEFAULT_JACOCO_VERSION
    def junitVersion = '4.12'

}