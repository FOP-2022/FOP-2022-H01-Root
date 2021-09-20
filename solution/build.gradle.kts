plugins {
  java
}

allprojects {
  apply(plugin = "java")
  apply(plugin = "application")
  repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }
  dependencies {
    implementation("org.jagrkt:jagrkt-api:0.1.0-SNAPSHOT")
    implementation("org.junit.jupiter:junit-jupiter:5.7.1")
    implementation(files("lib/fopbot.jar"))
  }
  tasks {
    create<Jar>("prepareSubmission") {
      group = "submit"
      from(sourceSets.main.get().allSource)
      archiveFileName.set("${project.name}-submission.jar")
    }
    test {
      useJUnitPlatform()
    }
  }
}
