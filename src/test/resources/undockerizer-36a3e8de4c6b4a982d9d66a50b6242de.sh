#!/bin/sh
UNDOCKERIZER_WORKDIR="$PWD"
read -p "Are you sure do you want to execute (yum update -y && yum -y install xmlstarlet saxon augeas bsdtar unzip && yum clean all)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root yum update -y && yum -y install xmlstarlet saxon augeas bsdtar unzip && yum clean all
[ $? -eq 0 ]  || exit 10
fi
read -p "Are you sure do you want to execute (groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c 'JBoss user' jboss &&     chmod 755 /opt/jboss)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c "JBoss user" jboss &&     chmod 755 /opt/jboss
[ $? -eq 0 ]  || exit 10
fi
read -p "Are you sure do you want to execute (mkdir -p /opt/jboss & cd /opt/jboss)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root mkdir -p /opt/jboss & cd /opt/jboss
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: jboss
# Changed user to: root
read -p "Are you sure do you want to execute (yum -y install java-11-openjdk-devel && yum clean all)? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root yum -y install java-11-openjdk-devel && yum clean all
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: jboss
export JAVA_HOME=/usr/lib/jvm/java
[ $? -eq 0 ]  || exit 20
export WILDFLY_VERSION=19.1.0.Final
[ $? -eq 0 ]  || exit 20
export WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955
[ $? -eq 0 ]  || exit 20
export JBOSS_HOME=/opt/jboss/wildfly
[ $? -eq 0 ]  || exit 20
# Changed user to: root
read -p "Are you sure do you want to execute (cd $HOME     && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz     && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1     && tar xf wildfly-$WILDFLY_VERSION.tar.gz     && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME     && rm wildfly-$WILDFLY_VERSION.tar.gz     && chown -R jboss:0 ${JBOSS_HOME}     && chmod -R g+rw ${JBOSS_HOME})? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root cd $HOME     && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz     && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1     && tar xf wildfly-$WILDFLY_VERSION.tar.gz     && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME     && rm wildfly-$WILDFLY_VERSION.tar.gz     && chown -R jboss:0 ${JBOSS_HOME}     && chmod -R g+rw ${JBOSS_HOME}
[ $? -eq 0 ]  || exit 10
fi
export LAUNCH_JBOSS_IN_BACKGROUND=true
[ $? -eq 0 ]  || exit 20
# Changed user to: jboss
# Expose Ports: 8080
