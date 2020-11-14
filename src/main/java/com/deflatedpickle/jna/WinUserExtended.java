/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.jna;

import com.sun.jna.platform.win32.WinUser;

public interface WinUserExtended extends WinUser {
  /* These values come from:
  https://referencesource.microsoft.com/#UIAutomationClient/MS/Win32/NativeMethods.cs,f66435563fb4ebdf,references
  */
  int EVENT_MIN = 0x00000001;
  int EVENT_MAX = 0x7FFFFFFF;

  int EVENT_SYSTEM_MENUSTART = 0x0004;
  int EVENT_SYSTEM_MENUEND = 0x0005;
  int EVENT_SYSTEM_MENUPOPUPSTART = 0x0006;
  int EVENT_SYSTEM_MENUPOPUPEND = 0x0007;
  int EVENT_SYSTEM_CAPTURESTART = 0x0008;
  int EVENT_SYSTEM_CAPTUREEND = 0x0009;
  int EVENT_SYSTEM_SWITCHSTART = 0x0014;
  int EVENT_SYSTEM_SWITCHEND = 0x0015;

  int EVENT_OBJECT_CREATE = 0x8000;
  int EVENT_OBJECT_DESTROY = 0x8001;
  int EVENT_OBJECT_SHOW = 0x8002;
  int EVENT_OBJECT_HIDE = 0x8003;
  int EVENT_OBJECT_FOCUS = 0x8005;
  int EVENT_OBJECT_STATECHANGE = 0x800A;
  int EVENT_OBJECT_LOCATIONCHANGE = 0x800B;

  int EVENT_SYSTEM_MOVESIZESTART = 0x000A;
  int EVENT_SYSTEM_MOVESIZEEND = 0x000B;

  int WINEVENT_OUTOFCONTEXT = 0x0000;
  int WINEVENT_SKIPOWNPROCESS = 0x0002;
}
