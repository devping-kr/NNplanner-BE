# 베이스 이미지로 OpenJDK 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드 전체 복사
COPY . .

# Gradle 빌드 실행 (테스트 건너뛰기)
RUN ./gradlew build -x test

# 빌드된 JAR 파일을 이미지로 복사
COPY build/libs/NNplanner-0.0.1-SNAPSHOT.jar /app/app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
