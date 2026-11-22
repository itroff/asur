FROM eclipse-temurin:25-jre
ENV TZ="Europe/Moscow"
COPY build/libs/asur-0.0.1-SNAPSHOT.jar /asur-0.0.1-SNAPSHOT.jar

#ENTRYPOINT [ "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8666", "-jar", "asur-0.0.1-SNAPSHOT.jar" ]
ENTRYPOINT [ "java", "-Djava.compiler=NONE" ,"-jar", "asur-0.0.1-SNAPSHOT.jar" ]