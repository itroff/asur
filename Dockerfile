FROM openjdk:8-slim
ENV TZ="Europe/Moscow"
COPY build/libs/asur-0.0.1-SNAPSHOT.jar /asur-0.0.1-SNAPSHOT.jar

#ENTRYPOINT [ "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8666", "-jar", "asur-0.0.1-SNAPSHOT.jar" ]
ENTRYPOINT [ "java", "-jar", "asur-0.0.1-SNAPSHOT.jar" ]