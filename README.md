# Undockerizer
Undockerizer is a project that helps to convert a Docker image to an installer (shell script).

## Issue tracker: 
- [github](https://github.com/arielcarrera/undockerizer/issues)

## Build

### System Requirements
1. Java Jdk 8 or later
2. Maven 3.6.3 or later
3. optional: Graalvm 20.1.0 or later
4. Docker 19 or later
5. Checkout project

### Build jar:
Command line:
> mvn clean install

## Build native image:
Command line:
1. run:
> cd docker-graalvm
2. select centos, ubuntu or your custom image:
> cd centos
3. run once:
> docker-build.bat
4. compile a native image release:
> docker-compile.bat

## Usage:

Please note that interactive mode and compressed mode (tar.gz) are the suggested modes of use.

### Command line parameters:
´´´
Usage: Undockerizer [-cftv] [-de] [-fp] [-it] -i=<image> [-o=<outputfileStr>]
                    [-od=<outputDirPathStr>] [-sp=<shellPathStr>]
  -c, --cleanAll         Clean all temp data
      -de, --disableEscaping
                         Disable escaping of variables
                           Default: false
  -f, --force            Overwrite output file if exists
      -fp, --forcePull   Force to pull image.
                           Default: false
  -i, --image=<image>    The docker image.
      -it, --interactiveOutput
                         Generate output file with interactive mode
  -o, --output=<outputfileStr>
                         The output file name.
                           Default: null
      -od, --outputDir=<outputDirPathStr>
                         Sets the output directory path.
                           Default: undockerizer
      -sp, --shellPath=<shellPathStr>
                         Sets the shell path.
                           Default: /bin/sh
  -t, --tar              Create tar file.
                           Default: false
  -v, --verbose          Verbose mode.
´´´

### Run with JDK
Prerequisites:
- JDK and Docker is required.

Command line:
> java -jar ./target/undockerizer.jar [PARAMETERS]

### Run native
Prerequisites:
- Docker is required.

Command line:
> undockerizer-centos [PARAMETERS]
or
> undockerizer-ubuntu [PARAMETERS]


## How to Try an undockerizer script?
Prerequisites:
- Docker is not required
- Sudo is required

Command line:
1. Run a docker image with same base than your undockerized image (e.g. Centos 7) and mount your undockerizer tar.gz file (or your undockerizer target folder). For example.
> docker run -it -v ${WORKDIR}\undockerizer\undockerizer\:/home/undockerizer centos:7 /bin/bash

2. Untar file:
> cd /home/undockerizer
> tar -xvz $UNDOCKERIZER_FILE.tar.gz

3. Add execution attribute:
> chmod +x $UNDOCKERIZER_FILE.sh

4. Install sudo:
> yum install sudo -y

5. Run:
> ./$UNDOCKERIZER_FILE.sh

# Note

> Please note that this project is experimental and is offered without any guarantees or liability. Please note review the generated script and do not make illegal use of the tool or code.
