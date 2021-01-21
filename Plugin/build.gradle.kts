plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.google.zxing:javase:3.3.0")
    implementation("io.socket:socket.io-client:1.0.0")
    implementation("dev.onvoid.webrtc:webrtc-java:0.2.0")
    implementation("dev.onvoid.webrtc:webrtc-java-windows-x86_64:0.1.0")
    implementation("org.scijava:native-lib-loader:2.3.4")
    implementation(files("libs/PluginHelper.jar"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.2.3"
}
tasks.getByName("patchPluginXml") {
}

