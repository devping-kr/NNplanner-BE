# 베이스 이미지로 OpenJDK 사용
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일 복사할 디렉토리 설정
VOLUME /tmp

# 빌드된 JAR 파일을 이미지로 복사
COPY build/libs/NNplanner-0.0.1-SNAPSHOT.jar app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
