# Android Converts SVG to VectorDrawable's XML

## Use

Add `svg2vertor/bin` to Path.  

Linux/Mac
```shell
svg2vector -d ./input_dir -o ./output_dir
```

Windows
```cmd
svg2vector.bat -d .\input_dir -o .\output_dir
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