plugins {
    kotlin("jvm") version "2.0.0"
    id("java")
}

group = "net.bluevine"
version = "0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.auto.value:auto-value-annotations:1.11.0")
    implementation("com.google.auto.value:auto-value:1.11.0")
    annotationProcessor("com.google.auto.value:auto-value:1.11.0")

    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.google.flogger:flogger:0.8")
    implementation("com.google.flogger:flogger-system-backend:0.8")
    implementation("commons-cli:commons-cli:1.8.0")

    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("org.apache.commons:commons-math3:3.6")

    // Including the OpenCV jar provided by Homebrew
    implementation(files("/opt/homebrew/opt/opencv/share/java/opencv4/opencv-460.jar"))

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}