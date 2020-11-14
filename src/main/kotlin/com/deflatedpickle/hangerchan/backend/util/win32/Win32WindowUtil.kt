/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.hangerchan.backend.util.win32

import com.deflatedpickle.jna.TITLEBARINFO
import com.deflatedpickle.jna.User32Extended
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.StringArray
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference

/**
 * A utility object for working with windows on-screen
 */
object Win32WindowUtil {
    /**
     * Returns a list of all the current windows that are shown
     */
    fun getAllWindows(monitor: Int): List<HWND> {
        val windows: MutableList<HWND> = mutableListOf()

        User32.INSTANCE.EnumWindows({ hWnd, _ ->
            if (isWindow(hWnd)) {
                // println(getTitle(hwnd))

                val monitorHandle = User32.INSTANCE.MonitorFromWindow(hWnd, User32.MONITOR_DEFAULTTONEAREST)
                if (getMonitorFromIndex(monitor) == monitorHandle) {
                    windows.add(hWnd)
                }
            }

            true
        }, null)

        return windows
    }

    // Credit: https://stackoverflow.com/a/3238193
    fun getAllWindowsByTop(monitor: Int): List<HWND> {
        val list = mutableListOf<HWND>()

        var top = User32Extended.INSTANCE.GetTopWindow(HWND(Pointer(0L)))
        for (i in 0..100) {
            if (isWindow(top) &&
                    User32.INSTANCE.MonitorFromWindow(top, User32.MONITOR_DEFAULTTONEAREST) == getMonitorFromIndex(monitor)) {
                list.add(top)
            }
            top = User32Extended.INSTANCE.GetWindow(top, User32.GW_HWNDNEXT)
        }

        return list
    }

    // TODO: Specify what monitor to look for windows on
    fun getAllWindowRects(monitor: Int): List<WinDef.RECT> {
        val rectList: MutableList<WinDef.RECT> = mutableListOf()

        for (w in getAllWindows(monitor)) {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetWindowRect(w, rect)

            rectList.add(rect)
        }

        return rectList
    }

    /**
     * Gets a window's HWND from its title
     */
    fun getWindowFromTitle(name: String, partial: Boolean = false): HWND? {
        var window: HWND? = null

        User32.INSTANCE.EnumWindows({ hwnd, pntr ->
            val wText = getTitle(hwnd)

            if (wText == pntr.getStringArray(0L)[0]) {
                window = hwnd
                return@EnumWindows false
            } else {
                if (partial) {
                    if (wText.contains(pntr.getStringArray(0L)[0])) {
                        window = hwnd
                        return@EnumWindows false
                    }
                }
            }

            true
        }, StringArray(arrayOf(name)))

        return window
    }

    /**
     * Gets the owner window from a process ID
     */
    fun getWindowFromProcess(process: IntByReference): HWND? {
        var window: HWND? = null

        User32.INSTANCE.EnumWindows({ hwnd, pntr ->
            val currentProcess = IntByReference(0)
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, currentProcess)

            if (currentProcess.value == process.value) {
                window = hwnd
                return@EnumWindows false
            }

            true
        }, null)

        return User32.INSTANCE.GetWindow(window, WinDef.DWORD(User32.GW_OWNER.toLong()))
    }

    /**
     * Gets the title of a window as a string
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getTitle(hWnd: HWND): String {
        val length = User32.INSTANCE.GetWindowTextLength(hWnd) + 1
        val windowText = CharArray(length)
        User32.INSTANCE.GetWindowText(hWnd, windowText, length)

        return Native.toString(windowText)
    }

    /**
     * Gets the class of the window
     */
    fun getClass(hWnd: HWND): String {
        val className = CharArray(80)
        User32.INSTANCE.GetClassName(hWnd, className, 80)

        return Native.toString(className)
    }

    /**
     * Determines whether or not a window is minimized
     */
    @Suppress("MemberVisibilityCanBePrivate")
    // https://stackoverflow.com/a/7292674
    fun isIconic(hWnd: HWND): Boolean {
        val info = WinUser.WINDOWINFO()
        User32.INSTANCE.GetWindowInfo(hWnd, info)

        return info.dwStyle and WinUser.WS_MINIMIZE == WinUser.WS_MINIMIZE
    }

    /**
     * Determines whether or not the given HWND is a window
     */
    // TODO: Check if the program is running in the background, if so, return false
    // TODO: Move visibility checks to a different function
    @Suppress("MemberVisibilityCanBePrivate")
    fun isWindow(hWnd: HWND): Boolean {
        if (getTitle(hWnd).isEmpty() ||
                !User32.INSTANCE.IsWindowVisible(hWnd) ||
                !User32.INSTANCE.IsWindowEnabled(hWnd) ||
                User32Extended.INSTANCE.IsIconic(hWnd) ||
                User32.INSTANCE.GetWindowLong(hWnd, User32.GWL_EXSTYLE) and User32Extended.WS_EX_TOOLWINDOW != 0 ||
                User32.INSTANCE.GetWindowLong(hWnd, User32.GWL_EXSTYLE) and User32Extended.WS_EX_APPWINDOW != 0) {
            return false
        }

        val titleBarInfo = TITLEBARINFO()
        User32Extended.INSTANCE.GetTitleBarInfo(hWnd, titleBarInfo)

        if (titleBarInfo.rgstate[TITLEBARINFO.TITLE_BAR] and User32Extended.STATE_SYSTEM_INVISIBLE != 0) {
            return false
        }

        var hwndTry = User32.INSTANCE.GetAncestor(hWnd, User32.GA_ROOTOWNER)
        var hwndWalk: HWND? = null

        while (hwndTry != hwndWalk) {
            hwndWalk = hwndTry
            hwndTry = User32Extended.INSTANCE.GetLastActivePopup(hwndWalk)

            if (User32.INSTANCE.IsWindowVisible(hwndTry)) {
                break
            }
        }
        if (hwndWalk != hWnd) {
            return false
        }

        return true
    }

    /**
     * Retrieves a monitor handle from an index
     */
    fun getMonitorFromIndex(index: Int): WinUser.HMONITOR? {
        var monitor: WinUser.HMONITOR? = null

        var counter = 0
        User32.INSTANCE.EnumDisplayMonitors(null, null, { hMonitor, hdcMonitor, lprcMonitor, dwData ->
            if (counter == index) {
                monitor = hMonitor
                return@EnumDisplayMonitors 0
            }
            counter++

            1
        }, null)

        return monitor
    }

    fun getRect(hWnd: HWND): WinDef.RECT {
        val rect = WinDef.RECT()
        User32.INSTANCE.GetWindowRect(hWnd, rect)

        return rect
    }
}
