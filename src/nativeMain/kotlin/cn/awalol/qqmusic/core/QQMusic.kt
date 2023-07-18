package cn.awalol.qqmusic.core

import kotlinx.cinterop.*
import platform.windows.*

//  QQMusic Desktop Version:1942.12.34.15
//  curPos 0x57DC091C(QQMusic.dll+B7091C) or 0x57D520D0(QQMusic.dll+B020D0)
//  songLen 0x57D520D4 QQMusic.dll+B020D4
//  songId 0x57DC2878 QQMusic.dll+B72878
@OptIn(ExperimentalForeignApi::class)
class QQMusic {
    private val pid = getProcessID("QQMusic.exe")
    private var processHandle : HANDLE = OpenProcess(PROCESS_VM_READ.toUInt(),0,pid)!!
    private val baseAddress = getProcessModuleAddress("QQMusic.dll",pid)
    private var curPosition = baseAddress + 0xB020D0L
    private var songLength = baseAddress + 0xB020D4L
    private var songId = baseAddress + 0xB72878L

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

    private fun getProcessID(processName: String): UInt{
        memScoped {
            val snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS.toUInt(),0U)
            val processEntry = alloc<PROCESSENTRY32W>()
            processEntry.dwSize = sizeOf<PROCESSENTRY32W>().convert()
            if(Process32FirstW(snapshot,processEntry.ptr) != 0){
                do {
                    val name = processEntry.szExeFile.toKString()
                    if(processName == name){
                        CloseHandle(snapshot)
                        return processEntry.th32ProcessID
                    }
                }while (Process32NextW(snapshot,processEntry.ptr) != 0)
            }
            CloseHandle(snapshot)
            return 0U
        }
    }

    private fun getProcessModuleAddress(moduleName: String,pid: UInt): Long{
        memScoped {
            val snapshot = CreateToolhelp32Snapshot((TH32CS_SNAPMODULE or TH32CS_SNAPMODULE32).toUInt(),pid)
            val moduleEntry = alloc<MODULEENTRY32W>()
            moduleEntry.dwSize = sizeOf<MODULEENTRY32W>().convert()
            if(Module32FirstW(snapshot,moduleEntry.ptr) != 0){
                do {
                    val name = moduleEntry.szModule.toKString()
                    if(name == moduleName){
                        CloseHandle(snapshot)
                        return moduleEntry.modBaseAddr.toLong()
                    }
                }while (Module32NextW(snapshot,moduleEntry.ptr) != 0)
            }
            CloseHandle(snapshot)
            return 0
        }
    }
}