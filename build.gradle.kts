plugins {
  java
}
allprojects {
  apply(plugin = "java")
  repositories {
    mavenCentral()
  }
  dependencies {
    implementation("org.sourcegrade:fopbot:0.1.0")
  }
  java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
  }
  tasks {
    withType<JavaCompile> {
      options.encoding = "UTF-8"
    }
    jar {
      archiveFileName.set("${rootProject.name}-${project.name}.jar")
    }
    named<Jar>("sourcesJar") {
      archiveFileName.set("${rootProject.name}-${project.name}-sources.jar")
    }
  }
}
