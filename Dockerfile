# 플랫폼을 지정하여 빌드
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 및 소스 코드 복사
COPY . /app

# Gradle 빌드를 실행 (테스트 제외)
RUN ./gradlew build -x test

# 빌드 결과 파일을 그대로 사용
RUN chmod +x /app/build/libs/NNplanner-0.0.1-SNAPSHOT.jar

# 애플리케이션 실행
CMD ["java", "-jar", "/app/build/libs/NNplanner-0.0.1-SNAPSHOT.jar"]
