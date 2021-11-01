plugins {
  kotlin("jvm") version "1.5.31"
}

dependencies {
  implementation(project(":solution"))
  implementation("org.sourcegrade:insn-replacer:0.1.0-SNAPSHOT")
  implementation("org.sourcegrade:jagr-grader-api:0.1.0-SNAPSHOT")
  implementation("org.sourcegrade:fopbot:0.1.0-SNAPSHOT")
}
