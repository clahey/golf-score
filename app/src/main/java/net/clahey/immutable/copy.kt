package net.clahey.immutable

fun <T> List<T>.copy(vararg updates: Pair<Int, T>): List<T> = buildList {
    addAll(this@copy)
    for (update in updates) {
        this[update.first] = update.second
    }
}

fun <K, V> Map<K, V>.copy(vararg updates: Pair<K, V>): Map<K, V> = buildMap {
    putAll(this@copy)
    for (update in updates) {
        this[update.first] = update.second
    }
}