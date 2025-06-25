# HOW TO COMPILE AND RUN THE PROGRAM
Our program is written in Java, and must therefore be compiled to a JAR file. We have written a script to automate the process of building the JAR file and the ZIP package containing the JAR file along with assets.

## Prerequisite
This script is currently only supported under **Linux** operating systems. The output package can run under ANY operating system, but the build script must be run from Linux.

If you do not already have a JDK installed, you should install it from your distribution's package manager, or alternatively obtain the [Adoptium Eclipse Temurin](https://adoptium.net/temurin/) OpenJDK distribution, which provides prebuilt JDK packages, and can be downloaded from [this link](https://adoptium.net/temurin/releases/?os=linux&package=jdk) for Linux.

If you downloaded the standalone JDK package, instead of installing a distribution package, you should extract the tarball and add the binary directory to your PATH, for example:
```
# Extract distribution to 'java' directory.
mkdir -p java
tar -xf OpenJDK21U-jdk_x64_linux_hotspot_21.0.6_7.tar.gz -C java --strip-components=1
# Add the distribution to your PATH.
export PATH="$PATH:$PWD/java/bin"
```

You can verify that it is in your PATH successfully by running the following from the terminal:
```
javac --version
```

## Downloading the source
You can check out the source code for our project by running the following command:
```
git clone https://cseegit.essex.ac.uk/24-25-ce201-col/24-25_CE201-col_team33
cd 24-25_CE201-col_team33
```

## Building on Linux
After the prerequisite is satisifed and a suitable JDK is in your path, you are able to build the package by running our build script. Run it as follows, from the source tree:

```
./build-jar.sh
```

If the shell script finishes successfully, it will produce an output file named **BabyTracker.jar**, and a distribution containing the JAR file + assets named **BabyTracker-JAR.zip**. The JAR file can be run from the command-line using the following command:
```
java -jar BabyTracker.jar
```
