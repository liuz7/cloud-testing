if ( java -version 2>&1 | grep "1.8" >/dev/null ); then
    echo "java 1.8 installed."
    exit 0
else
    echo "java 1.8 is not find, try to install ..."
fi

[ "$(id -u)" != "0" ] && {
  echo "Use root to execute this script or install java 1.8+ manually."
  exit 1
}

wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u31-b13/jdk-8u31-linux-x64.tar.gz"
tar xzf jdk-8u31-linux-x64.tar.gz

JDK_NAME=jdk1.8.0_31

# Create a location to save the JDK, and move it there
mkdir /usr/local/java
mv $JDK_NAME /usr/local/java
JAVA_HOME=/usr/local/java/$JDK_NAME

# Place links to java commands in /usr/bin, and set preferred sources
#ls -l /usr/local/java/
#ls -l $JAVA_HOME
#ls -l $JAVA_HOME/bin/
update-alternatives --install "/usr/bin/java" "java" "$JAVA_HOME/bin/java" 1
update-alternatives --set "java" "$JAVA_HOME/bin/java"

update-alternatives --install "/usr/bin/javac" "javac" "$JAVA_HOME/bin/javac" 1
update-alternatives --set "javac" "$JAVA_HOME/bin/javac"

update-alternatives --install "/usr/bin/javaws" "javaws" "$JAVA_HOME/bin/javaws" 1
update-alternatives --set "javaws" "$JAVA_HOME/bin/javaws"

update-alternatives --install "/usr/bin/jar" "jar" "$JAVA_HOME/bin/jar" 1
update-alternatives --set "jar" "$JAVA_HOME/bin/jar"

# Affirm completion, optionally delete archive, and exit
echo "Java Development Kit version $JDK_NAME successfully installed!"
exit 0