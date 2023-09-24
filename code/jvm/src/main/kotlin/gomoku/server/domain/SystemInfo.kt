package gomoku.server.domain

data class Author(
    val studentID: Int,
    val name: String,
    val email: String
)

data class ServerInfo(
    val version: String,
    val authors: List<Author>
)