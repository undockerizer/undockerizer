#!/bin/sh
UNDOCKERIZER_WORKDIR="$PWD"
COMMIT_ID=unknown
[ $? -eq 0 ]  || exit 20
#  io.confluent.docker.git.id=918b4cd
BUILD_NUMBER=-1
[ $? -eq 0 ]  || exit 20
#  io.confluent.docker.build.number=1
CONFLUENT_PACKAGES_REPO=
[ $? -eq 0 ]  || exit 20
ALLOW_UNSIGNED=false
[ $? -eq 0 ]  || exit 20
export ALLOW_UNSIGNED=false
[ $? -eq 0 ]  || exit 20
# MAINTAINER partner-support@confluent.io
#  io.confluent.docker=true
export PYTHON_VERSION=2.7.9-1
[ $? -eq 0 ]  || exit 20
export PYTHON_PIP_VERSION=8.1.2
[ $? -eq 0 ]  || exit 20
export SCALA_VERSION=2.11
[ $? -eq 0 ]  || exit 20
KAFKA_VERSION=
[ $? -eq 0 ]  || exit 20
CONFLUENT_MAJOR_VERSION=
[ $? -eq 0 ]  || exit 20
CONFLUENT_MINOR_VERSION=
[ $? -eq 0 ]  || exit 20
CONFLUENT_PATCH_VERSION=
[ $? -eq 0 ]  || exit 20
CONFLUENT_MVN_LABEL=
[ $? -eq 0 ]  || exit 20
CONFLUENT_PLATFORM_LABEL=
[ $? -eq 0 ]  || exit 20
export KAFKA_VERSION=2.0.1cp8
[ $? -eq 0 ]  || exit 20
export CONFLUENT_MAJOR_VERSION=5
[ $? -eq 0 ]  || exit 20
export CONFLUENT_MINOR_VERSION=0
[ $? -eq 0 ]  || exit 20
export CONFLUENT_PATCH_VERSION=4
[ $? -eq 0 ]  || exit 20
export CONFLUENT_MVN_LABEL=
[ $? -eq 0 ]  || exit 20
export CONFLUENT_PLATFORM_LABEL=
[ $? -eq 0 ]  || exit 20
export CONFLUENT_VERSION=5.0.4
[ $? -eq 0 ]  || exit 20
export CONFLUENT_DEB_VERSION=1
[ $? -eq 0 ]  || exit 20
export ZULU_OPENJDK_VERSION=8=8.30.0.1
[ $? -eq 0 ]  || exit 20
export LANG=C.UTF-8
[ $? -eq 0 ]  || exit 20
BUILD_NUMBER='1' COMMIT_ID='918b4cd' CONFLUENT_PACKAGES_REPO='https://s3-us-west-2.amazonaws.com/staging-confluent-packages-5.0.4';sudo -E -u root /bin/sh -c 'echo "===> Updating debian ....."     && apt-get -qq update         && echo "===> Installing curl wget netcat python...."     && DEBIAN_FRONTEND=noninteractive apt-get install -y                 apt-transport-https                 curl                 git                 wget                 netcat                 python=${PYTHON_VERSION}     && echo "===> Installing python packages ..."      && curl -fSL "https://bootstrap.pypa.io/get-pip.py" | python     && pip install --no-cache-dir --upgrade pip==${PYTHON_PIP_VERSION}     && pip install --no-cache-dir git+https://github.com/confluentinc/confluent-docker-utils@v0.0.20     && apt remove --purge -y git     && echo "Installing Zulu OpenJDK ${ZULU_OPENJDK_VERSION}"     && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0x219BD9C9     && echo "deb http://repos.azulsystems.com/debian stable  main" >> /etc/apt/sources.list.d/zulu.list     && apt-get -qq update     && apt-get -y install zulu-${ZULU_OPENJDK_VERSION}     && echo "===> Installing Kerberos Patch ..."     && DEBIAN_FRONTEND=noninteractive apt-get -y install krb5-user     && rm -rf /var/lib/apt/lists/*     && echo "===> Adding confluent repository...${CONFLUENT_PACKAGES_REPO}/deb/${CONFLUENT_MAJOR_VERSION}.${CONFLUENT_MINOR_VERSION}"     && if [ "x$ALLOW_UNSIGNED" = "xtrue" ]; then echo "APT::Get::AllowUnauthenticated \"true\";" > /etc/apt/apt.conf.d/allow_unauthenticated; else curl -L ${CONFLUENT_PACKAGES_REPO}/deb/${CONFLUENT_MAJOR_VERSION}.${CONFLUENT_MINOR_VERSION}/archive.key | apt-key add - ; fi     && echo "deb [arch=amd64] ${CONFLUENT_PACKAGES_REPO}/deb/${CONFLUENT_MAJOR_VERSION}.${CONFLUENT_MINOR_VERSION} stable main" >> /etc/apt/sources.list'
[ $? -eq 0 ]  || exit 10
export CUB_CLASSPATH=/etc/confluent/docker/docker-utils.jar
[ $? -eq 0 ]  || exit 20
COMMIT_ID=unknown
[ $? -eq 0 ]  || exit 20
#  io.confluent.docker.git.id=918b4cd
BUILD_NUMBER=-1
[ $? -eq 0 ]  || exit 20
#  io.confluent.docker.build.number=1
# MAINTAINER partner-support@confluent.io
#  io.confluent.docker=true
# ARG var without default value: KAFKA_ZOOKEEPER_CONNECT
export KAFKA_ZOOKEEPER_CONNECT=
[ $? -eq 0 ]  || exit 20
# ARG var without default value: KAFKA_ADVERTISED_LISTENERS
export KAFKA_ADVERTISED_LISTENERS=
[ $? -eq 0 ]  || exit 20
export COMPONENT=kafka
[ $? -eq 0 ]  || exit 20
# Expose Ports: 9092
BUILD_NUMBER='1' COMMIT_ID='918b4cd';sudo -E -u root /bin/sh -c 'echo "===> installing ${COMPONENT}..."     && apt-get update && apt-get install -y confluent-kafka-${SCALA_VERSION}=${KAFKA_VERSION}${CONFLUENT_PLATFORM_LABEL}-${CONFLUENT_DEB_VERSION}         && echo "===> clean up ..."      && apt-get clean && rm -rf /tmp/* /var/lib/apt/lists/*         && echo "===> Setting up ${COMPONENT} dirs..."     && mkdir -p /var/lib/${COMPONENT}/data /etc/${COMPONENT}/secrets    && chmod -R ag+w /etc/${COMPONENT} /var/lib/${COMPONENT}/data /etc/${COMPONENT}/secrets     && chown -R root:root /var/log/kafka /var/log/confluent /var/lib/kafka /var/lib/zookeeper'
[ $? -eq 0 ]  || exit 10
# VOLUME [/var/lib/kafka/data /etc/kafka/secrets]
sudo -E -u root /bin/sh -c 'mkdir -p $UNDOCKERIZER_WORKDIR/93545591f1925bdc98103f5bbc846ff21e772b1fe66372226cffe19b4c9a7e01/525bb2aa9e1e555186a794f376d17a4ab9bd324874f9acb367b3e9b4d6cfb64b/layer.tar && tar -xvf $UNDOCKERIZER_WORKDIR/93545591f1925bdc98103f5bbc846ff2-content/525bb2aa9e1e555186a794f376d17a4ab9bd324874f9acb367b3e9b4d6cfb64b/layer.tar -C $UNDOCKERIZER_WORKDIR/93545591f1925bdc98103f5bbc846ff21e772b1fe66372226cffe19b4c9a7e01/525bb2aa9e1e555186a794f376d17a4ab9bd324874f9acb367b3e9b4d6cfb64b/layer.tar'
[ $? -eq 0 ]  || exit 10
sudo -E -u root /bin/sh -c 'cp -r $UNDOCKERIZER_WORKDIR/93545591f1925bdc98103f5bbc846ff21e772b1fe66372226cffe19b4c9a7e01/525bb2aa9e1e555186a794f376d17a4ab9bd324874f9acb367b3e9b4d6cfb64b/layer.tar/* / && rm -rf $UNDOCKERIZER_WORKDIR/93545591f1925bdc98103f5bbc846ff21e772b1fe66372226cffe19b4c9a7e01/525bb2aa9e1e555186a794f376d17a4ab9bd324874f9acb367b3e9b4d6cfb64b/layer.tar'
[ $? -eq 0 ]  || exit 10
echo Script executed successfully.
