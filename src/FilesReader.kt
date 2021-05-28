import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FilesReader(private val dir: String, private val fileStartIndex: Int, private val fileCount: Int) {

    fun readAllFiles(): ArrayList<String> {
        val path = Paths.get(dir)
        val filesForEachThread: Int = fileCount / THREAD_COUNT
        val resList = ArrayList<String>()
        for (i in 1..THREAD_COUNT) {
            var temp = 0
            val thread = Thread {
                resList.addAll(getFileList(path, fileStartIndex + temp, filesForEachThread))
            }
            thread.start()
            thread.join()
            temp += filesForEachThread
        }

        val leftItems = fileCount % THREAD_COUNT
        if (leftItems != 0) {
            resList.addAll(getFileList(path, fileStartIndex + fileCount - leftItems, leftItems))
        }
        firstCountDownLatch.countDown()

        return resList
    }

    private fun getFileList(path: Path, startIndex: Int, limit: Int): ArrayList<String> {
        val fileList = arrayListOf<String>()
        try {
            Files.walk(path).skip(startIndex.toLong()).limit(limit.toLong()).forEach { filePath ->
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