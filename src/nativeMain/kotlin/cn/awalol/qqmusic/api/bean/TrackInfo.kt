package cn.awalol.qqmusic.api.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackInfo(
    @SerialName("album")
    val album: Album?,
    @SerialName("id")
    val id: Int?,
    @SerialName("mid")
    val mid: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("singer")
    val singer: List<Singer?>?,
    @SerialName("title")
    val title: String?
) {
    @Serializable
    data class Album(
        @SerialName("id")
        val id: Int?,
        @SerialName("mid")
        val mid: String?,
        @SerialName("name")
        val name: String?,
        @SerialName("pmid")
        val pmid: String?,
        @SerialName("subtitle")
        val subtitle: String?,
        @SerialName("time_public")
        val timePublic: String?,
        @SerialName("title")
        val title: String?
    )

    @Serializable
    data class Singer(
        @SerialName("id")
        val id: Int?,
        @SerialName("mid")
        val mid: String?,
        @SerialName("name")
        val name: String?,
        @SerialName("title")
        val title: String?,
        @SerialName("type")
        val type: Int?,
        @SerialName("uin")
        val uin: Int?
    )
}