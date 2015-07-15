/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repeater;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import repeater.WinUserX.LowLevelMouseProc;
import repeater.WinUserX.MSLLHOOKSTRUCT;

/**
 *
 * @author Admin
 */
public class GlobalMouseListener extends Thread{
    private static volatile boolean quit;
    private static WinUser.HHOOK hhk;
    private static LowLevelMouseProc mouseHook;
	private static Method leftMouseClick;
	private static Object handler;
	
	public void addListener(Method leftMouseClick, Object handler) {
		GlobalMouseListener.leftMouseClick = leftMouseClick;
		GlobalMouseListener.handler = handler;
	}

	@Override
	public void run() {
        System.out.println("Press middle button to quit.");
        final User32 lib = User32.INSTANCE;
        WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        mouseHook = new LowLevelMouseProc() 
        {
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, MSLLHOOKSTRUCT info) 
            {
                if (nCode >= 0) 
                {
                    switch (wParam.intValue())
                    {
                        case WinUserX.WM_LBUTTONDOWN:
//                            System.out.println("Left button click at " + info.pt.x + ", " + info.pt.y);
							{
								try {
									GlobalMouseListener.leftMouseClick.invoke(GlobalMouseListener.handler, info.pt.x, info.pt.x);
								} catch (IllegalAccessException ex) {
									Logger.getLogger(GlobalMouseListener.class.getName()).log(Level.SEVERE, null, ex);
								} catch (IllegalArgumentException ex) {
									Logger.getLogger(GlobalMouseListener.class.getName()).log(Level.SEVERE, null, ex);
								} catch (InvocationTargetException ex) {
									Logger.getLogger(GlobalMouseListener.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
                            break;
                        case WinUserX.WM_LBUTTONUP:
//                            System.out.println("Left button release.");
                            break;
                        case WinUserX.WM_MBUTTONDOWN:
//                            System.out.println("Middle button.");
                            quit = true;
                            break;
                    }
                }
                return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
            }
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, hMod, 0);
        new Thread() 
        {
            public void run() 
            {
                while (!quit) 
                {
                    try { Thread.sleep(10); } catch(InterruptedException e) { }
                }
                System.err.println("unhook and exit");
                lib.UnhookWindowsHookEx(hhk);
                System.exit(0);
            }
        }.start();

        // This bit never returns from GetMessage
        int result;
        WinUser.MSG msg = new WinUser.MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) 
        {
            if (result == -1) 
            {
                System.err.println("error in get message");
                break;
            }
            else 
            {
                System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhk);
	}
}