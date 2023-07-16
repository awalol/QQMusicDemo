package cn.awalol.qqmusic.ui

import cn.awalol.qqmusic.core.QQMusic
import libui.ktx.*
class GUI {
    lateinit var scroll: TextArea
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
                val songId = qqMusic.readSongId()
                scroll.append("""
                    |NowPlaying:
                    |SongId: $songId
                    |$position/$songLength
                    |
                    |""".trimMargin())
                true
            }
        }
    }
}