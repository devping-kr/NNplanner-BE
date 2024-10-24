# 플랫폼을 지정하여 빌드
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 및 소스 코드 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN ./gradlew build -x test

# 빌드된 JAR 파일을 복사
COPY build/libs/NNplanner-0.0.1-SNAPSHOT.jar /app/app.jar

# JAR 파일에 실행 권한 추가
RUN chmod +x /app/app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "/app/app.jar"]
