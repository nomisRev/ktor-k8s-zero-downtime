FROM ubuntu
RUN apt-get update && apt-get install -y libpq-dev
COPY build/bin/linuxX64/releaseExecutable/ktor-native-server.kexe /ktor-native-server
EXPOSE 8080
CMD ["/ktor-native-server"]
