package gomoku.server.domain

data class Author(
    val name: String,
    val email: String,
    val gitHub: String,
    val id: Int
)

data class ServerInfo(
    val version: String,
    val authors: List<Author>
)