plugins {
  java
}
allprojects {
  apply(plugin = "java")
  repositories {
    mavenLocal()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }
  dependencies {
    implementation("org.sourcegrade:jagr-grader-api:0.1.0-SNAPSHOT")
    implementation("org.sourcegrade:fopbot:0.1.0-SNAPSHOT")
    implementation("org.junit.jupiter:junit-jupiter:5.7.1")
    implementation("org.sourcegrade:fopbot:0.1.0-SNAPSHOT")
  }
  java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
