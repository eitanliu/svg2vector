# Android Converts SVG to VectorDrawable's XML

## Use

Add `svg2vertor/bin` to Path.  

JVM
```shell
java -jar svg2vector-1.0.3.jar -d ./
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

[tools/base](https://android.googlesource.com/platform/tools/base/), [Branches/Tags](https://android.googlesource.com/platform/tools/base/+refs)

### Find the latest tag

```shell
curl -s "https://android.googlesource.com/platform/tools/base/+refs" \
  | grep -o 'studio-[0-9.]*\(-patch[0-9]*\)\?' | sort -u | tail -10
```

### Download by tag

Replace `{tag}` with the target tag (e.g. `studio-2025.3.3`):

```text
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/{tag}/sdk-common/src/main/java/com/android/ide/common/vectordrawable.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/{tag}/common/src/main/java/com/android/ide/common/blame.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/tags/{tag}/common/src/main/java/com/android/utils.tar.gz
https://android.googlesource.com/platform/tools/base/+/refs/tags/{tag}/sdk-common/src/main/java/com/android/ide/common/util/AssetUtil.java
https://android.googlesource.com/platform/tools/base/+/refs/tags/{tag}/common/src/main/java/com/android/SdkConstants.java
```

### Download from main branch

```text
https://android.googlesource.com/platform/tools/base/+archive/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/vectordrawable.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/heads/main/common/src/main/java/com/android/ide/common/blame.tar.gz
https://android.googlesource.com/platform/tools/base/+archive/refs/heads/main/common/src/main/java/com/android/utils.tar.gz
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/sdk-common/src/main/java/com/android/ide/common/util/AssetUtil.java
https://android.googlesource.com/platform/tools/base/+/refs/heads/main/common/src/main/java/com/android/SdkConstants.java
```

> Notes:
> - `SdkConstants.java`: comment out `import com.android.sdklib.AndroidVersion` and fields that depend on `AndroidVersion`/`Version`.
> - `SvgTree.java`: keep local `autoMirrored` customization, do not overwrite.

[Guava](https://android.googlesource.com/platform/external/guava/)