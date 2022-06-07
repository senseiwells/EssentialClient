package me.senseiwells.essentialclient.utils.keyboard;

import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyboardHelper {

	private static final Map<String, Integer> stringToKeyMap = Util.make(new HashMap<>(), map -> {
		map.put("a", GLFW.GLFW_KEY_A);
		map.put("b", GLFW.GLFW_KEY_B);
		map.put("c", GLFW.GLFW_KEY_C);
		map.put("d", GLFW.GLFW_KEY_D);
		map.put("e", GLFW.GLFW_KEY_E);
		map.put("f", GLFW.GLFW_KEY_F);
		map.put("g", GLFW.GLFW_KEY_G);
		map.put("h", GLFW.GLFW_KEY_H);
		map.put("i", GLFW.GLFW_KEY_I);
		map.put("j", GLFW.GLFW_KEY_J);
		map.put("k", GLFW.GLFW_KEY_K);
		map.put("l", GLFW.GLFW_KEY_L);
		map.put("m", GLFW.GLFW_KEY_M);
		map.put("n", GLFW.GLFW_KEY_N);
		map.put("o", GLFW.GLFW_KEY_O);
		map.put("p", GLFW.GLFW_KEY_P);
		map.put("q", GLFW.GLFW_KEY_Q);
		map.put("r", GLFW.GLFW_KEY_R);
		map.put("s", GLFW.GLFW_KEY_S);
		map.put("t", GLFW.GLFW_KEY_T);
		map.put("u", GLFW.GLFW_KEY_U);
		map.put("v", GLFW.GLFW_KEY_V);
		map.put("w", GLFW.GLFW_KEY_W);
		map.put("x", GLFW.GLFW_KEY_X);
		map.put("y", GLFW.GLFW_KEY_Y);
		map.put("z", GLFW.GLFW_KEY_Z);
		map.put("`", GLFW.GLFW_KEY_GRAVE_ACCENT);
		map.put("1", GLFW.GLFW_KEY_1);
		map.put("2", GLFW.GLFW_KEY_2);
		map.put("3", GLFW.GLFW_KEY_3);
		map.put("4", GLFW.GLFW_KEY_4);
		map.put("5", GLFW.GLFW_KEY_5);
		map.put("6", GLFW.GLFW_KEY_6);
		map.put("7", GLFW.GLFW_KEY_7);
		map.put("8", GLFW.GLFW_KEY_8);
		map.put("9", GLFW.GLFW_KEY_9);
		map.put("0", GLFW.GLFW_KEY_0);
		map.put("-", GLFW.GLFW_KEY_MINUS);
		map.put("=", GLFW.GLFW_KEY_EQUAL);
		map.put("[", GLFW.GLFW_KEY_LEFT_BRACKET);
		map.put("]", GLFW.GLFW_KEY_RIGHT_BRACKET);
		map.put("\\", GLFW.GLFW_KEY_WORLD_2);
		map.put(",", GLFW.GLFW_KEY_COMMA);
		map.put(".", GLFW.GLFW_KEY_PERIOD);
		map.put("/", GLFW.GLFW_KEY_SLASH);
		map.put(";", GLFW.GLFW_KEY_SEMICOLON);
		map.put("'", GLFW.GLFW_KEY_APOSTROPHE);
		map.put("#", GLFW.GLFW_KEY_BACKSLASH);
		map.put("backslash", GLFW.GLFW_KEY_BACKSLASH);
	});

	private static final Map<Integer, String> keyToStringMap = Util.make(new HashMap<>(), map -> {
		map.put(GLFW.GLFW_KEY_SPACE, "space");
		map.put(GLFW.GLFW_KEY_LEFT_ALT, "left_alt");
		map.put(GLFW.GLFW_KEY_RIGHT_ALT, "right_alt");
		map.put(GLFW.GLFW_KEY_LEFT_SHIFT, "left_shift");
		map.put(GLFW.GLFW_KEY_RIGHT_SHIFT, "right_shift");
		map.put(GLFW.GLFW_KEY_LEFT_CONTROL, "left_control");
		map.put(GLFW.GLFW_KEY_RIGHT_CONTROL, "right_control");
		map.put(GLFW.GLFW_KEY_TAB, "tab");
		map.put(GLFW.GLFW_KEY_ESCAPE, "escape");
		map.put(GLFW.GLFW_KEY_BACKSPACE, "backspace");
		map.put(GLFW.GLFW_KEY_ENTER, "enter");
		map.put(GLFW.GLFW_KEY_NUM_LOCK, "num_lock");
		map.put(GLFW.GLFW_KEY_DELETE, "delete");
		map.put(GLFW.GLFW_KEY_INSERT, "insert");
		map.put(GLFW.GLFW_KEY_HOME, "home");
		map.put(GLFW.GLFW_KEY_END, "end");
		map.put(GLFW.GLFW_KEY_PAGE_UP, "page_up");
		map.put(GLFW.GLFW_KEY_PAGE_DOWN, "page_down");
		map.put(GLFW.GLFW_KEY_PRINT_SCREEN, "print_screen");
		map.put(GLFW.GLFW_KEY_SCROLL_LOCK, "scroll_lock");
		map.put(GLFW.GLFW_KEY_PAUSE, "pause");
		map.put(GLFW.GLFW_KEY_MENU, "menu");
		map.put(GLFW.GLFW_KEY_LEFT_SUPER, "left_windows");
		map.put(GLFW.GLFW_KEY_RIGHT_SUPER, "right_windows");
		map.put(GLFW.GLFW_KEY_CAPS_LOCK, "caps_lock");
		map.put(GLFW.GLFW_KEY_RIGHT, "right");
		map.put(GLFW.GLFW_KEY_LEFT, "left");
		map.put(GLFW.GLFW_KEY_UP, "up");
		map.put(GLFW.GLFW_KEY_DOWN, "down");
		map.put(GLFW.GLFW_KEY_KP_0, "keypad_0");
		map.put(GLFW.GLFW_KEY_KP_1, "keypad_1");
		map.put(GLFW.GLFW_KEY_KP_2, "keypad_2");
		map.put(GLFW.GLFW_KEY_KP_3, "keypad_3");
		map.put(GLFW.GLFW_KEY_KP_4, "keypad_4");
		map.put(GLFW.GLFW_KEY_KP_5, "keypad_5");
		map.put(GLFW.GLFW_KEY_KP_6, "keypad_6");
		map.put(GLFW.GLFW_KEY_KP_7, "keypad_7");
		map.put(GLFW.GLFW_KEY_KP_8, "keypad_8");
		map.put(GLFW.GLFW_KEY_KP_9, "keypad_9");
		map.put(GLFW.GLFW_KEY_KP_ADD, "keypad_+");
		map.put(GLFW.GLFW_KEY_KP_DECIMAL, "keypad_.");
		map.put(GLFW.GLFW_KEY_KP_DIVIDE, "keypad_/");
		map.put(GLFW.GLFW_KEY_KP_ENTER, "keypad_enter");
		map.put(GLFW.GLFW_KEY_KP_EQUAL, "keypad_=");
		map.put(GLFW.GLFW_KEY_KP_MULTIPLY, "keypad_*");
		map.put(GLFW.GLFW_KEY_KP_SUBTRACT, "keypad_-");
		map.put(GLFW.GLFW_KEY_F1, "f1");
		map.put(GLFW.GLFW_KEY_F2, "f2");
		map.put(GLFW.GLFW_KEY_F3, "f3");
		map.put(GLFW.GLFW_KEY_F4, "f4");
		map.put(GLFW.GLFW_KEY_F5, "f5");
		map.put(GLFW.GLFW_KEY_F6, "f6");
		map.put(GLFW.GLFW_KEY_F7, "f7");
		map.put(GLFW.GLFW_KEY_F8, "f8");
		map.put(GLFW.GLFW_KEY_F9, "f9");
		map.put(GLFW.GLFW_KEY_F10, "f10");
		map.put(GLFW.GLFW_KEY_F11, "f11");
		map.put(GLFW.GLFW_KEY_F12, "f12");
		map.put(GLFW.GLFW_KEY_F13, "f13");
		map.put(GLFW.GLFW_KEY_F14, "f14");
		map.put(GLFW.GLFW_KEY_F15, "f15");
		map.put(GLFW.GLFW_KEY_F16, "f16");
	});

	static {
		for (Map.Entry<Integer, String> entry : keyToStringMap.entrySet()) {
			stringToKeyMap.put(entry.getValue(), entry.getKey());
		}
	}

	public static String translateKeyToString(int code) {
		String keyName = keyToStringMap.get(code);
		return keyName == null ? GLFW.glfwGetKeyName(code, 0) : keyName;
	}

	public static int translateStringToKey(String string) {
		Integer keyCode = stringToKeyMap.get(string);
		return keyCode == null ? -1 : keyCode;
	}
}
