import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

val invertedIndexMap = ConcurrentHashMap<String, ArrayList<String>>()
val allFilesArray = ArrayList<String>()

const val startIndex = 200 //(12500 / 50) * (33 + 30 - 1)
const val THREAD_COUNT = 5
val countDownLatch = CountDownLatch(THREAD_COUNT)

fun main(args: Array<String>) {
    collectAllFiles()
    substringForAllIndexes()
    countDownLatch.await()

    println(search("Creative").joinToString { "$it\n" })
}

fun search(searchValue: String): List<String> =
    invertedIndexMap.filter { it.key.equals(searchValue, true) }.flatMap { it.value }

fun substringForAllIndexes() {
    val fileCountForEachThread: Int = allFilesArray.size / THREAD_COUNT

    for (i in 0 until THREAD_COUNT) {
        val thread = Thread {
            createInvertedIndexMap(
                allFilesArray.subList(
                    i * fileCountForEachThread,
                    i * fileCountForEachThread + fileCountForEachThread
                )
            )
            countDownLatch.countDown()
        }
        thread.start()
        thread.join()
    }

    val leftItems = allFilesArray.size % THREAD_COUNT
    if (leftItems != 0) {
        createInvertedIndexMap(allFilesArray.subList(allFilesArray.size - leftItems, allFilesArray.size))
    }
}

fun createInvertedIndexMap(filesList: List<String>) {
    for (dir: String in filesList) {
        val file = File(dir)
        val reader = BufferedReader(FileReader(file))
        var line = reader.readLine()
        while (line != null) {
            for (word in line.split("\\W+".toRegex()).toTypedArray()) {
                invertedIndexMap.putIfAbsent(word, arrayListOf())
                invertedIndexMap[word].apply {
                    this?.add(dir)
                }
            }
            line = reader.readLine()
        }
    }
}

fun collectAllFiles() {
    allFilesArray.addAll(FilesReader("res/test/neg/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/test/pos/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/pos/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/neg/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/unsup/", startIndex, 1000).readAllFiles())
}