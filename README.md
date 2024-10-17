# Android Converts SVG to VectorDrawable's XML

## Use

Add `svg2vertor/bin` to Path.  

JVM
```shell
java -jar svg2vector-1.0.1.jar -d ./
```

Linux/Mac
```shell
svg2vector -d ./input_dir -o ./output_dir
```

Windows
```cmd
svg2vector.bat -d .\input_dir -o .\output_dir
```

## Build

```shell
# build Tar and Zip
# build Tar ./gradlew distTar
# build Zip ./gradlew distZip
./gradlew assembleDist
# build jar
./gradlew jarPackage
```

## Sources

[tools/base](https://android.googlesource.com/platform/tools/base/)

```text
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/sdk-common/src/main/java/com/android/ide/common/vectordrawable
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/sdk-common/src/main/java/com/android/ide/common/util/
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/common/src/main/java/com/android/ide/common/blame/
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/common/src/main/java/com/android/utils
```

```text
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/vectordrawable
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/util/
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/ide/common/blame/
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/utils
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/SdkConstants.java
```

[Guava](https://android.googlesource.com/platform/external/guava/)