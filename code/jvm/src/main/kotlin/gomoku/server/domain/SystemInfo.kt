package gomoku.server.domain

// TODO: Change this and send more info like git, etc.

/**
 * Represents an author of the code
 * @property studentID the student id of the author
 * @property name the name of the author
 * @property email the email of the author
 */
data class Author(
    val studentID: Int,
    val name: String,
    val email: String,
    val socials: List<Socials>
)

/**
 * Represents the server info
 * @property version the version of the server
 * @property authors the authors of the code
 */
data class ServerInfo(
    val version: String,
    val authors: List<Author>
)

/**
 * Represents a social media link
 * @property name the name of the social media
 * @property url the url of the social media
 */
data class Socials(
    val name: String,
    val url: String
)
