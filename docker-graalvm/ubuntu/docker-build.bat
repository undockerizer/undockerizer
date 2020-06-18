@echo Off
docker build . -f Dockerfile -t tools/graalvm-jdk8-ubuntu:20.1.0
