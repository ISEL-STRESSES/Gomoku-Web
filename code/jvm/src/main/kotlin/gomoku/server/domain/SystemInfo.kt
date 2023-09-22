package gomoku.server.domain

data class Author(
    val id: Int,
    val name: String,
    val email: String,
)

data class ServerInfo(
    val version: String,
    val authors: List<Author>
)