plugins {
    kotlin("jvm") version "2.0.0"
    id("java")
    jacoco
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
    implementation("com.google.code.gson:gson:2.10")

    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.google.flogger:flogger:0.8")
    implementation("com.google.flogger:flogger-system-backend:0.8")
    implementation("commons-cli:commons-cli:1.8.0")

    implementation("me.tongfei:progressbar:0.10.1")

    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("org.apache.commons:commons-math3:3.6")

    // Including the OpenCV jar provided by Homebrew
    implementation(files("/opt/homebrew/opt/opencv/share/java/opencv4/opencv-460.jar"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

jacoco {
    toolVersion = "0.8.11"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}


tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}