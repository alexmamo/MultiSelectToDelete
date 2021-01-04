package ro.alexmamo.multiselecttodelete.data

data class DataOrException<T, E : Exception?>(
    var data: T? = null,
    var e: E? = null
)