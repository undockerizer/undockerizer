@echo Off
docker build . -f Dockerfile -t tools/graalvm-jdk8-centos:20.1.0
