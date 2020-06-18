#!/bin/sh
export UNDOCKERIZER_WORKDIR="$PWD"
read -p "WARN: The image could be generated with docker build secrets.
Press a key to continue." -n 1 -r
printf "\n"
read -p "Docker build secret detected: /run/secrets/credenciales -> make sure you have the file in the detailed location
Press a key to continue." -n 1 -r
printf "\n"
if [ -f /run/secrets/credenciales ]; then echo "File: /run/secrets/credenciales found.
"; else echo "File: /run/secrets/credenciales Not found!
" & exit; fi
#  maintainer=Ariel Carrera <cesar.carrera@pjn.gov.ar>
export TZ=America/Argentina/Buenos_Aires
[ $? -eq 0 ]  || exit 20
read -p "Line: sudo -E -u root /bin/sh -c 'ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone'
[ $? -eq 0 ]  || exit 10
fi
export JAVA_HOME=/usr/lib/jvm/java
[ $? -eq 0 ]  || exit 20
export PJN_HOME=/opt/pjn
[ $? -eq 0 ]  || exit 20
# Changed user to: root
read -p "Line: sudo -E -u root /bin/sh -c 'groupadd -r pjn -g 1000 && useradd -u 1000 -r -g pjn -m -d $PJN_HOME -s /sbin/nologin -c 'Pjn user' pjn &&     chmod 755 $PJN_HOME'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'groupadd -r pjn -g 1000 && useradd -u 1000 -r -g pjn -m -d $PJN_HOME -s /sbin/nologin -c "Pjn user" pjn &&     chmod 755 $PJN_HOME'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'yum update -y && yum -y install xmlstarlet saxon augeas bsdtar unzip && yum clean all'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'yum update -y && yum -y install xmlstarlet saxon augeas bsdtar unzip && yum clean all'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'yum -y install java-11-openjdk-devel && yum clean all'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'yum -y install java-11-openjdk-devel && yum clean all'
[ $? -eq 0 ]  || exit 10
fi
export JAVA_HOME=/usr/lib/jvm/java
[ $? -eq 0 ]  || exit 20
read -p "Line: sudo -E -u root /bin/sh -c 'mkdir -p /opt/pjn & cd /opt/pjn'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'mkdir -p /opt/pjn & cd /opt/pjn'
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: pjn
#  maintainer=Ariel Carrera <cesar.carrera@pjn.gov.ar>
export WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955
[ $? -eq 0 ]  || exit 20
export WILDFLY_VERSION=19.1.0.Final
[ $? -eq 0 ]  || exit 20
export KEYCLOAK_VERSION=10.0.1
[ $? -eq 0 ]  || exit 20
export ORACLE_OJDBC_VERSION=18.3.0.0.0
[ $? -eq 0 ]  || exit 20
# ARG var without default value: NEXUS_USER
# ARG var without default value: NEXUS_PASS
export JBOSS_HOME=/opt/pjn/wildfly-19.1.0.Final
[ $? -eq 0 ]  || exit 20
export TZ=America/Argentina/Buenos_Aires
[ $? -eq 0 ]  || exit 20
# Changed user to: root
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/static/content/wildfly/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.tar.gz -o wildfly-${WILDFLY_VERSION}.tar.gz     && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1     && tar -C $PJN_HOME -xf wildfly-$WILDFLY_VERSION.tar.gz     && rm wildfly-$WILDFLY_VERSION.tar.gz     && chown -R pjn:pjn ${JBOSS_HOME}     && chmod -R g+rw ${JBOSS_HOME} # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/static/content/wildfly/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.tar.gz -o wildfly-${WILDFLY_VERSION}.tar.gz     && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1     && tar -C $PJN_HOME -xf wildfly-$WILDFLY_VERSION.tar.gz     && rm wildfly-$WILDFLY_VERSION.tar.gz     && chown -R pjn:pjn ${JBOSS_HOME}     && chmod -R g+rw ${JBOSS_HOME} # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/static/content/keycloak/${KEYCLOAK_VERSION}/adapters/keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz -o keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& tar -C $JBOSS_HOME -xf keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& rm keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& chown -R pjn:pjn $JBOSS_HOME 	&& chmod -R g+rw ${JBOSS_HOME} # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/static/content/keycloak/${KEYCLOAK_VERSION}/adapters/keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz -o keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& tar -C $JBOSS_HOME -xf keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& rm keycloak-wildfly-adapter-dist-${KEYCLOAK_VERSION}.tar.gz 	&& chown -R pjn:pjn $JBOSS_HOME 	&& chmod -R g+rw ${JBOSS_HOME} # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-elytron-install-offline.cli 	&& rm -rf ${JBOSS_HOME}/standalone/data 	&& rm -rf ${JBOSS_HOME}/standalone/tmp 	&& rm -rf ${JBOSS_HOME}/standalone/log 	&& rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-elytron-install-offline.cli 	&& rm -rf ${JBOSS_HOME}/standalone/data 	&& rm -rf ${JBOSS_HOME}/standalone/tmp 	&& rm -rf ${JBOSS_HOME}/standalone/log 	&& rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/bd5df1f7919b3b3c40b852bfcb9ff8ab19d7987570e5c299283176ae015eed7f/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;chown -R pjn:pjn $JBOSS_HOME/modules/layers.conf 	&& chmod g+rw ${JBOSS_HOME}/modules/layers.conf # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;chown -R pjn:pjn $JBOSS_HOME/modules/layers.conf 	&& chmod g+rw ${JBOSS_HOME}/modules/layers.conf # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/e130836e595ea303c322552fe132d4961a22fc7b0b39ee9a023022905f1efa87/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/maven-releases/com/oracle/ojdbc8/${ORACLE_OJDBC_VERSION}/ojdbc8-${ORACLE_OJDBC_VERSION}.jar -o ojdbc8.jar 	&& mv ojdbc8.jar $JBOSS_HOME/modules/system/layers/pjn/com/oracle/ojdbc8/main/ojdbc8.jar 	&& chown -R pjn:pjn $JBOSS_HOME/modules/system/layers/pjn 	&& chmod -R g+rw ${JBOSS_HOME}/modules/system/layers/pjn # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;cd $HOME 	&& cat /run/secrets/credenciales | curl -L -K - https://nexus.pjn.gov.ar/repository/maven-releases/com/oracle/ojdbc8/${ORACLE_OJDBC_VERSION}/ojdbc8-${ORACLE_OJDBC_VERSION}.jar -o ojdbc8.jar 	&& mv ojdbc8.jar $JBOSS_HOME/modules/system/layers/pjn/com/oracle/ojdbc8/main/ojdbc8.jar 	&& chown -R pjn:pjn $JBOSS_HOME/modules/system/layers/pjn 	&& chmod -R g+rw ${JBOSS_HOME}/modules/system/layers/pjn # buildkit'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;sed -i -e '''''s/<drivers>/&\n                        <driver name='oracle18' module='com.oracle.ojdbc8'><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name='oracle18xa' module='com.oracle.ojdbc8'><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/''''' $JBOSS_HOME/standalone/configuration/standalone.xml && 	sed -i -e '''''s/<drivers>/&\n                        <driver name='oracle18' module='com.oracle.ojdbc8'><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name='oracle18xa' module='com.oracle.ojdbc8'><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/''''' $JBOSS_HOME/standalone/configuration/standalone-full.xml && 	sed -i -e '''''s/<drivers>/&\n                        <driver name='oracle18' module='com.oracle.ojdbc8'><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name='oracle18xa' module='com.oracle.ojdbc8'><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/''''' $JBOSS_HOME/standalone/configuration/standalone-full-ha.xml # buildkit'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'WILDFLY_SHA1=6883125745a62b598659ea351f5b1706aff53955 WILDFLY_VERSION=19.1.0.Final KEYCLOAK_VERSION=10.0.1 ORACLE_OJDBC_VERSION=18.3.0.0.0 NEXUS_USER= NEXUS_PASS= ;sed -i -e '"'"'s/<drivers>/&\n                        <driver name="oracle18" module="com.oracle.ojdbc8"><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name="oracle18xa" module="com.oracle.ojdbc8"><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/'"'"' $JBOSS_HOME/standalone/configuration/standalone.xml && 	sed -i -e '"'"'s/<drivers>/&\n                        <driver name="oracle18" module="com.oracle.ojdbc8"><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name="oracle18xa" module="com.oracle.ojdbc8"><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/'"'"' $JBOSS_HOME/standalone/configuration/standalone-full.xml && 	sed -i -e '"'"'s/<drivers>/&\n                        <driver name="oracle18" module="com.oracle.ojdbc8"><driver-class>oracle.jdbc.driver.OracleDriver<\/driver-class><\/driver><driver name="oracle18xa" module="com.oracle.ojdbc8"><xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource<\/xa-datasource-class><\/driver>/'"'"' $JBOSS_HOME/standalone/configuration/standalone-full-ha.xml # buildkit'
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: pjn
# Expose Ports: map[8787/tcp:{} 9990/tcp:{} 8080/tcp:{}]
#  maintainer=Ariel Carrera <cesar.carrera@pjn.gov.ar>
export SCW_API_VERSION=3.0.3
[ $? -eq 0 ]  || exit 20
# Changed user to: root
read -p "Line: sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/2b4572cb42ae856ab7d42e00790a0e53cdea82b015e7106d26ce94216361c3f9/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'SCW_API_VERSION=3.0.3 ;${JBOSS_HOME}/bin/jboss-cli.sh --file=${JBOSS_HOME}/bin/config-script.cli 	&& rm -rf ${JBOSS_HOME}/standalone/data 	&& rm -rf ${JBOSS_HOME}/standalone/tmp 	&& rm -rf ${JBOSS_HOME}/standalone/log 	&& rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'SCW_API_VERSION=3.0.3 ;${JBOSS_HOME}/bin/jboss-cli.sh --file=${JBOSS_HOME}/bin/config-script.cli 	&& rm -rf ${JBOSS_HOME}/standalone/data 	&& rm -rf ${JBOSS_HOME}/standalone/tmp 	&& rm -rf ${JBOSS_HOME}/standalone/log 	&& rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4c-content/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar -C $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/604f40911a1bbfa192201bd98b7d5a4caac72cdf89ba78769a8efeaadfb6abc7/952cb0210973e2842a89fcaa54d2913cff099a9f4a8a8164449d0ad7822cf05c/layer.tar'
[ $? -eq 0 ]  || exit 10
fi
read -p "Line: sudo -E -u root /bin/sh -c 'SCW_API_VERSION=3.0.3 ;chown -R pjn:pjn ${JBOSS_HOME}/standalone/deployments 	&& chmod -R g+rw ${JBOSS_HOME}/standalone/deployments'
Are you sure do you want to execute? " -n 1 -r
printf "\n"
if [[ $REPLY = "" || $REPLY =~ ^[Yy]$ ]]
then
sudo -E -u root /bin/sh -c 'SCW_API_VERSION=3.0.3 ;chown -R pjn:pjn ${JBOSS_HOME}/standalone/deployments 	&& chmod -R g+rw ${JBOSS_HOME}/standalone/deployments'
[ $? -eq 0 ]  || exit 10
fi
# Changed user to: pjn
echo Script executed successfully.
