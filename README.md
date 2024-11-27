# Android Converts SVG to VectorDrawable's XML

## Use

Add `svg2vertor/bin` to Path.  

JVM
```shell
java -jar svg2vector-1.0.2.jar -d ./
```

Linux/Mac
```shell
svg2vector -d ./input_dir -o ./output_dir
```

Windows
```cmd
svg2vector.bat -d .\input_dir -o .\output_dir
```

### Options
```text
 -d,--dir <arg>            the target svg directory
 -f,--file <arg>           the target svg file
 -o,--output <arg>         the output vector file or directory
 -w,--width <arg>          the width needs to be overridden.
 -h,--height <arg>         the height needs to be overridden.
 -a,--alpha <arg>          the alpha needs to be overridden. (0.0 ~ 1.0)
 -t,--tint <arg>           the RGB value of the tint. (000000 ~ ffffff)
 -m,--autoMirrored <arg>   auto mirroring for RTL layout (default ture)
```

## Build

```shell
./gradlew installDist
# build Tar and Zip
# build Tar ./gradlew distTar
# build Zip ./gradlew distZip
./gradlew assembleDist
# build jar
./gradlew jarPackage
```

## Sources

[tools/base](https://android.googlesource.com/platform/tools/base/), [Branches](https://android.googlesource.com/platform/tools/base/+refs)

### studio-2024.2.1-patch02
```text
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/studio-2024.2.1-patch02/sdk-common/src/main/java/com/android/ide/common/vectordrawable.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/studio-2024.2.1-patch02/sdk-common/src/main/java/com/android/ide/common/blame.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/studio-2024.2.1-patch02/common/src/main/java/com/android/utils.tar.gz
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.2.1-patch02/sdk-common/src/main/java/com/android/ide/common/util/AssetUtil.java
```
### studio-2024.1.2-canary1
```text
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/sdk-common/src/main/java/com/android/ide/common/vectordrawable
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/sdk-common/src/main/java/com/android/ide/common/util/
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/common/src/main/java/com/android/ide/common/blame/
https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-2024.1.2-canary1/common/src/main/java/com/android/utils
```
### main
```text
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/vectordrawable
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/util/
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/ide/common/blame/
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/utils
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/SdkConstants.java
```

[Guava](https://android.googlesource.com/platform/external/guava/)