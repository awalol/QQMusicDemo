package cn.awalol.qqmusic.ui

import cn.awalol.qqmusic.api.QQMusicApi
import cn.awalol.qqmusic.api.bean.TrackInfo
import cn.awalol.qqmusic.core.QQMusic
import kotlinx.coroutines.runBlocking
import libui.ktx.*
class GUI {
    lateinit var scroll: TextArea
    private var songId: Int = 0
    private lateinit var trackInfo: TrackInfo
    private val qqMusicApi = QQMusicApi()
    fun launch() = appWindow(
        title = "QQMusicHelper",
        width = 320,
        height = 240
    ){
        vbox {
            button("currentPosition"){
                action {
                    val qqMusic = QQMusic()
                    scroll.append(qqMusic.readCurrentPosition().toString() + "\n")
                }
            }

            scroll = textarea {
                readonly = true
                stretchy = true
            }

            onTimer(1000){
                val qqMusic = QQMusic()
                val position = qqMusic.readCurrentPosition()
                val songLength = qqMusic.readSongLength()
                val nowPlayingSongId = qqMusic.readSongId()
                if (songId != nowPlayingSongId.toInt()){
                    songId = nowPlayingSongId.toInt()
                    runBlocking {
                        trackInfo = qqMusicApi.getSongDetail(songId)
                    }
                }
                scroll.append("""
                    |NowPlaying:
                    |SongId: $songId
                    |SongName: ${trackInfo.name}
                    |Singer: ${trackInfo.singer!!.map { it!!.name }.joinToString("/") }
                    |$position/$songLength
                    |
                    |""".trimMargin())
                true
            }
        }
    }
}