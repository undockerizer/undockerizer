#!/bin/sh
UNDOCKERIZER_WORKDIR="$PWD"
export JDBC_POSTGRES_VERSION=42.2.5
[ $? -eq 0 ]  || exit 20
export JDBC_MYSQL_VERSION=8.0.19
[ $? -eq 0 ]  || exit 20
export JDBC_MARIADB_VERSION=2.5.4
[ $? -eq 0 ]  || exit 20
export JDBC_MSSQL_VERSION=7.4.1.jre11
[ $? -eq 0 ]  || exit 20
export LAUNCH_JBOSS_IN_BACKGROUND=1
[ $? -eq 0 ]  || exit 20
export PROXY_ADDRESS_FORWARDING=false
[ $? -eq 0 ]  || exit 20
export JBOSS_HOME=/opt/jboss/keycloak
[ $? -eq 0 ]  || exit 20
export LANG=en_US.UTF-8
[ $? -eq 0 ]  || exit 20
# ARG var without default value: GIT_REPO
# ARG var without default value: GIT_BRANCH
KEYCLOAK_DIST=https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz
[ $? -eq 0 ]  || exit 20
# Changed user to: root
read -p "Are you sure do you want to execute ( GIT_BRANCH='10.0.2' GIT_REPO='' KEYCLOAK_DIST='https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz';microdnf update -y && microdnf install -y glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
GIT_BRANCH='10.0.2' GIT_REPO='' KEYCLOAK_DIST='https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz';sudo -E -u root /bin/sh -c 'microdnf update -y && microdnf install -y glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all'
[ $? -eq 0 ]  || exit 10
fi
read -p "Are you sure do you want to execute ( GIT_BRANCH='10.0.2' GIT_REPO='' KEYCLOAK_DIST='https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz';/opt/jboss/tools/build-keycloak.sh)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
GIT_BRANCH='10.0.2' GIT_REPO='' KEYCLOAK_DIST='https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz';sudo -E -u root /bin/sh -c '/opt/jboss/tools/build-keycloak.sh'
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: 1000
# Expose Ports: 8080
# Expose Ports: 8443
