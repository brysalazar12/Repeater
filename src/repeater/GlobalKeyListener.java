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
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class GlobalKeyListener extends Thread{
	private User32 lib;
	private WinUser.LowLevelKeyboardProc keyboardHook;
	private static WinUser.HHOOK hhk;
	private static Method keypress;
	private static Method keyrelease;
	private static Object handler;
	
	public void addListener(Method keypress, Method keyrelease, Object handler) {
		GlobalKeyListener.keypress = keypress;
		GlobalKeyListener.keyrelease = keyrelease;
		GlobalKeyListener.handler = handler;
	}
	
	@Override
	public void run() {
		lib = User32.INSTANCE;
		WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		keyboardHook = new WinUser.LowLevelKeyboardProc() {
			public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT info) {

					if (nCode >= 0) {   
						//To unhook press 'esc' key
						if(info.vkCode == 0x1B) {
							User32.INSTANCE.UnhookWindowsHookEx(hhk);
						}
						if(wParam.intValue() == WinUser.WM_KEYDOWN) {
//							System.out.println("Key Pressed : "+info.vkCode);
							try {
								GlobalKeyListener.keypress.invoke(GlobalKeyListener.handler, info.vkCode);
							} catch (IllegalAccessException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							} catch (IllegalArgumentException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							} catch (InvocationTargetException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							}
						} else {
							try {
								GlobalKeyListener.keyrelease.invoke(GlobalKeyListener.handler, info.vkCode);
							} catch (IllegalAccessException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							} catch (IllegalArgumentException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							} catch (InvocationTargetException ex) {
								Logger.getLogger(GlobalKeyListener.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
//						switch (info.vkCode){   
//							case 0x5B:System.err.println("l win"); return new WinDef.LRESULT(1);
//							case 0x5C:System.err.println("r win"); return new WinDef.LRESULT(1);
//							case 0xA2:System.err.println("l ctrl"); return new WinDef.LRESULT(1);
//							case 0xA3:System.err.println("r ctrl"); return new WinDef.LRESULT(1);
//							case 0xA4:System.err.println("l alt"); return new WinDef.LRESULT(1);
//							case 0xA5:System.err.println("r alt"); return new WinDef.LRESULT(1); 
//							default: System.out.println("Key Pressed : "+info.vkCode);//do nothing
//						}
//						System.out.println("Key Pressed : "+info.vkCode);
					}  return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
				}

		};
		hhk = lib.SetWindowsHookEx(13, keyboardHook, hMod, 0);
		// This bit never returns from GetMessage
		int result;
		WinUser.MSG msg = new WinUser.MSG();

		while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
			if (result == -1) {
				break;
			} else {
				lib.TranslateMessage(msg);
				lib.DispatchMessage(msg);
			}
		}

		lib.UnhookWindowsHookEx(hhk);

	}
}
