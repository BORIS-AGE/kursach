import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.io.File
import java.io.FileReader
import java.io.BufferedReader

val invertedIndexMap = ConcurrentHashMap<String, ArrayList<String>>()

fun main(args: Array<String>) {
    val allFilesArray = ArrayList<String>()
    val startIndex = 200 //(12500 / 50) * (33 + 30 - 1)
    allFilesArray.addAll(FilesReader("res/test/neg/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/test/pos/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/pos/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/neg/", startIndex, 250).readAllFiles())
    allFilesArray.addAll(FilesReader("res/train/unsup/", startIndex, 1000).readAllFiles())

    for (fileDir: String in allFilesArray) {
        substringForAllIndexes(fileDir)
    }
    println(search("Creative").joinToString { "$it\n" })
}

fun search(searchValue: String): List<String> =
    invertedIndexMap.filter { it.key.equals(searchValue, true) }.flatMap { it.value }

fun substringForAllIndexes(dir: String) {
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