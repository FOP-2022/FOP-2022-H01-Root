repositories {
  mavenLocal()
}

dependencies {
  implementation(project(":solution"))
  implementation("org.sourcegrade:jagr-grader-api:0.1")
  implementation("org.sourcegrade:insn-replacer:0.1.0-SNAPSHOT")
}
