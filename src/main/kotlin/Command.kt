import com.android.ConfigConstant
import com.android.ide.common.vectordrawable.Svg2Vector
import com.android.ide.common.vectordrawable.VdOverrideInfo
import com.android.ide.common.vectordrawable.VdPreview
import com.android.utils.XmlUtils
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.GZIPInputStream


object Command {
    private const val HELPER_INFO = "-f d:/svg/a.svg -o d:/vector/a.xml"

    val noOverrideInfo = VdOverrideInfo(0.0, 0.0, null, -1.0, false)

    @JvmStatic
    fun parse(args: Array<String>) {
        val opt = Options().apply {
            addOption("d", "dir", true, "the target svg directory")
            addOption("f", "file", true, "the target svg file")
            addOption("o", "output", true, "the output vector file or directory")
            addOption("w", "width", true, "the width needs to be overridden.")
            addOption("h", "height", true, "the width needs to be overridden. ")
            addOption("a", "alpha", true, "the alpha needs to be overridden. (0.0 ~ 1.0)")
            addOption("t", "tint", true, "the RGB value of the tint. (000000 ~ ffffff)")
            addOption("m", "autoMirrored", true, "auto mirroring for RTL layout (default ture)")
        }

        val formatter = HelpFormatter()
        val parser: CommandLineParser = DefaultParser()

        val cl: CommandLine = try {
            parser.parse(opt, args)
        } catch (e: ParseException) {
            formatter.printHelp(HELPER_INFO, opt)
            return
        }

        var overrideInfo = noOverrideInfo
        if (cl.hasOption("w")) {
            try {
                val value = cl.getOptionValue("w").toDouble()
                overrideInfo = overrideInfo.copy(width = value)
            } catch (_: Throwable) {
            }
        }
        if (cl.hasOption("h")) {
            try {
                val value = cl.getOptionValue("h").toDouble()
                overrideInfo = overrideInfo.copy(height = value)
            } catch (_: Throwable) {
            }
        }
        if (cl.hasOption("a")) {
            try {
                val value = cl.getOptionValue("a").toDouble()
                overrideInfo = overrideInfo.copy(alpha = value)
            } catch (_: Throwable) {
            }
        }
        if (cl.hasOption("t")) {
            try {
                val value = cl.getOptionValue("t")
                    .substringAfter("0x")
                    .substringAfter("#")
                    .toInt(16)
                overrideInfo = overrideInfo.copy(tint = Color(value))
            } catch (_: Throwable) {
            }
        }
        if (cl.hasOption("m")) {
            try {
                val value = cl.getOptionValue("m").toBoolean()
                overrideInfo = overrideInfo.copy(autoMirrored = value)
            } catch (_: Throwable) {
                overrideInfo = overrideInfo.copy(autoMirrored = true)
            }
        } else {
            overrideInfo = overrideInfo.copy(autoMirrored = true)
        }
        if (overrideInfo != noOverrideInfo) {
            ConfigConstant.overrideInfo = overrideInfo
        }

        var dir: String? = null
        var file: String? = null
        if (cl.hasOption("d")) {
            dir = cl.getOptionValue("d")
        } else if (cl.hasOption("f")) {
            file = cl.getOptionValue("f")
        }

        var output: String? = null
        if (cl.hasOption("o")) {
            output = cl.getOptionValue("o")
        }

        if (output == null) {
            if (dir != null) {
                output = dir
            }
            if (file != null) {

                output = file.substringBeforeLast(".").fixFileName() + ".xml"
            }
        }

        if (dir == null && file == null) {
            formatter.printHelp(HELPER_INFO, opt)
            throw RuntimeException("You must input the target svg file or directory")
        }

        if (dir != null) {
            val inputDir = File(dir)
            if (!inputDir.exists() || !inputDir.isDirectory) {
                throw RuntimeException("The path [$dir] is not exist or valid directory")
            }
            val outputDir = output?.let { File(it) }
                ?: throw RuntimeException("The output path is null")
            if (outputDir.exists() || outputDir.mkdirs()) {
                svg2vectorForDirectory(inputDir, outputDir)
            } else {
                throw RuntimeException("The path [$outputDir] is not a valid directory")
            }
        }

        if (file != null) {
            val inputFile = File(file)
            if (!inputFile.exists() || !inputFile.isFile) {
                throw RuntimeException("The path [$file] is not exist or valid file")
            }
            val outputFile = output?.let { File(it) }
                ?: throw RuntimeException("The output path is null")
            svg2vectorForFile(inputFile, outputFile)
        }
    }

    private fun svg2vectorForDirectory(inputDir: File, outputDir: File) {
        val childFiles = inputDir.listFiles()
        if (childFiles != null) {
            for (childFile in childFiles) {
                if (childFile.isFile && childFile.length() > 0) {
                    svg2vectorForFile(
                        childFile, File(outputDir, childFile.fixFileName() + ".xml"),
                    )
                }
            }
        }
    }

    private fun svg2vectorForFile(inputFile: File, outputFile: File) {
        if (inputFile.name.endsWith(".svgz")) {
            val tempUnzipFile = File(inputFile.parent, inputFile.fixFileName() + ".svg")
            try {
                unZipGzipFile(inputFile, tempUnzipFile)
                svg2vectorForFile(tempUnzipFile, outputFile)
            } catch (e: IOException) {
                throw RuntimeException("Unzip file occur an error: " + e.message)
            } finally {
                tempUnzipFile.delete()
            }
        } else if (inputFile.name.endsWith(".svg")) {
            println("${inputFile.name} → ${outputFile.name}")
            outputFile.outputStream().use { out ->
                val tempStream = ByteArrayOutputStream()
                Svg2Vector.parseSvgToXml(inputFile.toPath(), tempStream)
                if (ConfigConstant.overrideInfo != null) {
                    try {
                        val doc = XmlUtils.parseDocument(
                            tempStream.toString(), true
                        )
                        val docString = VdPreview.overrideXmlContent(
                            doc, ConfigConstant.overrideInfo, null
                        )
                        if (docString != null) {
                            out.write(docString.toByteArray())
                        } else {
                            throw NullPointerException("Override Content Error")
                        }
                    } catch (e: Throwable) {
                        out.write(tempStream.toByteArray())
                        e.printStackTrace()
                    }
                } else {
                    out.write(tempStream.toByteArray())
                }
            }
        }
    }

    private fun unZipGzipFile(source: File, destination: File) {
        if (destination.parentFile.exists() || destination.parentFile.mkdirs()) {
            source.inputStream().use { fis ->
                GZIPInputStream(fis).use { gis ->
                    destination.outputStream().use { fos ->
                        var count: Int
                        val data = ByteArray(1024)
                        while ((gis.read(data).also { count = it }) != -1) {
                            fos.write(data, 0, count)
                        }
                        fos.flush()
                    }
                }
            }
        }
    }

    private fun File.fixFileName() = nameWithoutExtension.fixFileName()

    private fun String.fixFileName(replacement: String = "_"): String {
        val regex = Regex("[^a-zA-Z0-9]+")
        return replace(regex, replacement)
    }
}