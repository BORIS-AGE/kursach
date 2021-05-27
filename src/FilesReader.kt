import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class FilesReader(private val dir: String, private var fileStartIndex: Int) {

    fun readAllFiles(): ArrayList<String> {
        val fileList = arrayListOf<String>()
        val path = Paths.get(dir)
        try {
            Files.walk(path).forEach {
                path.toFile().let { file ->
                    if (!file.isDirectory && fileStartIndex-- == 0) {
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