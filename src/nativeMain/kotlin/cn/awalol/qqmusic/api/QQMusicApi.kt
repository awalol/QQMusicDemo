package cn.awalol.qqmusic.api

import cn.awalol.qqmusic.api.bean.TrackInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

class QQMusicApi {
    private val client = HttpClient(WinHttp)

    suspend fun getSongDetail(songId: Int): TrackInfo {
        val response = client.post("http://u.y.qq.com/cgi-bin/musicu.fcg") {
            contentType(ContentType.Application.Json)
            setBody("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_id\":$songId}}}")
        }
        val jsonElement = Json.parseToJsonElement(response.body())
            .jsonObject["songinfo"]!!
            .jsonObject["data"]!!
            .jsonObject["track_info"]!!
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromJsonElement<TrackInfo>(jsonElement)
    }
}
