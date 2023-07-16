import kotlinx.cinterop.*
import platform.windows.*

fun main() {
    val windowHandle = FindWindowW(null,"桌面歌词")
    val processId = memScoped {
        val outBuffer = alloc<DWORDVar>()
        GetWindowThreadProcessId(windowHandle,outBuffer.ptr)
        outBuffer.value
    }
    val processHandle : HANDLE? = OpenProcess(PROCESS_VM_READ,0,processId)

//  QQMusic Desktop Version:1942.12.34.15
//  curPos 0x57DC091C(QQMusic.dll+B7091C) or 0x57D520D0(QQMusic.dll+B020D0)
//  songLen 0x57D520D4 QQMusic.dll+B020D4
//  songId 0x57DC2878 QQMusic.dll+B72878

    memScoped {
        val buff = alloc<UIntVar>()
        ReadProcessMemory(
            hProcess = processHandle,
            lpBaseAddress = 0x57DC2878L.toCPointer(),
            lpBuffer = buff.ptr,
            nSize = 4u,
            lpNumberOfBytesRead = null
        )
        println(buff.value)
    }
}