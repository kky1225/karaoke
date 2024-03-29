plugins {
    java
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //lombok
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    //Jsoup
    implementation("org.jsoup:jsoup:1.14.3")

    //Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    //MySQL
    implementation("mysql:mysql-connector-java:8.0.32")

    //mybatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
