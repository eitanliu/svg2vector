import com.android.ide.common.vectordrawable.Svg2Vector
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import java.io.File
import java.io.IOException
import java.util.zip.GZIPInputStream


object Command {
    private const val HELPER_INFO = "-f d:/svg/a.svg -o d:/vector/a.xml"

    @JvmStatic
    fun parse(args: Array<String>) {
        val opt: Options = Options()
        opt.addOption("d", "dir", true, "the target svg directory")
        opt.addOption("f", "file", true, "the target svg file")
        opt.addOption("o", "output", true, "the output vector file or directory")

        val formatter = HelpFormatter()
        val parser: CommandLineParser = PosixParser()

        val cl: CommandLine
        try {
            cl = parser.parse(opt, args)
        } catch (e: ParseException) {
            formatter.printHelp(HELPER_INFO, opt)
            return
        }

        if (cl == null) {
            formatter.printHelp(HELPER_INFO, opt)
            return
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
            val outputDir = File(output)
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
            svg2vectorForFile(inputFile, File(output))
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
            val tempUnzipFile =
                File(inputFile.parent, inputFile.fixFileName() + ".svg")
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

                Svg2Vector.parseSvgToXml(inputFile.toPath(), out)
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