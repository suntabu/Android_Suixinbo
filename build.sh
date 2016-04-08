export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK7
export PATH=$JDK7/bin:$GRADLE_HOME/bin:$PATH
gradle build
cp app/build/outputs/apk/*.apk bin
