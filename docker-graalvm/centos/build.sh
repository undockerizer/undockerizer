#!/bin/bash

$JAVA_HOME/bin/native-image --no-server \
         --class-path /project/target/undockerizer.jar \
	     -H:Name=undockerizer-centos \
	     -H:Class=com.github.arielcarrera.undockerizer.Undockerizer \
	     -H:+ReportUnsupportedElementsAtRuntime \
	     -H:+AllowVMInspection 
		 #\
		 #-H:ReflectionConfigurationFiles=/project/docker-graalvm/reflect.json
