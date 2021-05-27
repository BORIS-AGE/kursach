import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class FilesReader(private val dir: String, private val fileStartIndex: Int, private val fileCount: Int) {

    fun readAllFiles(): ArrayList<String> {
        val fileList = arrayListOf<String>()
        val path = Paths.get(dir)
        try {
            Files.walk(path).skip(fileStartIndex.toLong()).limit(fileCount.toLong()).forEach { filePath ->
                filePath.toFile().let { file ->
                    if (!file.isDirectory) {
                        fileList.add(file.absolutePath)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileList
    }
}