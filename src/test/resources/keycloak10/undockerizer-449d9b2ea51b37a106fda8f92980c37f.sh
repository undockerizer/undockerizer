#!/bin/sh
export UNDOCKERIZER_WORKDIR="$PWD"
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
export KEYCLOAK_DIST=https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz
[ $? -eq 0 ]  || exit 20
# Changed user to: root
sudo -E -u root /bin/sh -c 'GIT_BRANCH=10.0.2 GIT_REPO= KEYCLOAK_DIST=https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz ;microdnf update -y && microdnf install -y glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all'
[ $? -eq 0 ]  || exit 10
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/449d9b2ea51b37a106fda8f92980c37f6cb2122f00b355913448760527ba3f87/5fd998d971f674158f6827d23832fcc4e084b48384aa5067c0bbc160d2f60d04/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/449d9b2ea51b37a106fda8f92980c37f-content/5fd998d971f674158f6827d23832fcc4e084b48384aa5067c0bbc160d2f60d04/layer.tar -C $UNDOCKERIZER_WORKDIR/449d9b2ea51b37a106fda8f92980c37f6cb2122f00b355913448760527ba3f87/5fd998d971f674158f6827d23832fcc4e084b48384aa5067c0bbc160d2f60d04/layer.tar'
[ $? -eq 0 ]  || exit 10
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/449d9b2ea51b37a106fda8f92980c37f6cb2122f00b355913448760527ba3f87/5fd998d971f674158f6827d23832fcc4e084b48384aa5067c0bbc160d2f60d04/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/449d9b2ea51b37a106fda8f92980c37f6cb2122f00b355913448760527ba3f87/5fd998d971f674158f6827d23832fcc4e084b48384aa5067c0bbc160d2f60d04/layer.tar'
[ $? -eq 0 ]  || exit 10
sudo -E -u root /bin/sh -c 'GIT_BRANCH=10.0.2 GIT_REPO= KEYCLOAK_DIST=https://downloads.jboss.org/keycloak/10.0.2/keycloak-10.0.2.tar.gz ;/opt/jboss/tools/build-keycloak.sh'
[ $? -eq 0 ]  || exit 10
# Changed user to: 1000
# Expose Ports: 8080
# Expose Ports: 8443
