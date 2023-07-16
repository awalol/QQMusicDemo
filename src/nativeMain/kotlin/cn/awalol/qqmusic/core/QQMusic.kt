package cn.awalol.qqmusic.core

import kotlinx.cinterop.*
import platform.windows.*

//  QQMusic Desktop Version:1942.12.34.15
//  curPos 0x57DC091C(QQMusic.dll+B7091C) or 0x57D520D0(QQMusic.dll+B020D0)
//  songLen 0x57D520D4 QQMusic.dll+B020D4
//  songId 0x57DC2878 QQMusic.dll+B72878
class QQMusic {
    private var processHandle : HANDLE
    private val curPosition = 0x57DC091CL
    private val songLength = 0x57D520D4L
    private val songId = 0x57DC2878L

    init {
        // TODO:使用进程名获取窗口句柄
        val windowHandle = FindWindowW(null,"桌面歌词")
        val processId = memScoped {
            val outBuffer = alloc<DWORDVar>()
            GetWindowThreadProcessId(windowHandle,outBuffer.ptr)
            outBuffer.value
        }
        processHandle = OpenProcess(PROCESS_VM_READ,0,processId)!!
    }

    fun readCurrentPosition() = readMemory(curPosition)
    fun readSongLength() = readMemory(songLength)
    fun readSongId() = readMemory(songId)

    private fun readMemory(address: Long): UInt{
        memScoped {
            val buff = alloc<UIntVar>()
            ReadProcessMemory(
                hProcess = processHandle,
                lpBaseAddress = address.toCPointer(),
                lpBuffer = buff.ptr,
                nSize = 4u,
                lpNumberOfBytesRead = null
            )
            return buff.value
        }
    }
}