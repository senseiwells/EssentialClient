package essentialclient.utils.keyboard;

import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class KeyboardHelper {
    private static final Map<Integer, String> keyMap = Map.<Integer, String>ofEntries(
            Map.entry(GLFW.GLFW_KEY_SPACE, "space"),
            Map.entry(GLFW.GLFW_KEY_LEFT_ALT, "left_alt"),
            Map.entry(GLFW.GLFW_KEY_RIGHT_ALT, "right_alt"),
            Map.entry(GLFW.GLFW_KEY_LEFT_SHIFT, "left_shift"),
            Map.entry(GLFW.GLFW_KEY_RIGHT_SHIFT, "right_shift"),
            Map.entry(GLFW.GLFW_KEY_LEFT_CONTROL, "left_control"),
            Map.entry(GLFW.GLFW_KEY_RIGHT_CONTROL, "right_control"),
            Map.entry(GLFW.GLFW_KEY_TAB, "tab"),
            Map.entry(GLFW.GLFW_KEY_ESCAPE, "escape"),
            Map.entry(GLFW.GLFW_KEY_ENTER, "enter"),
            Map.entry(GLFW.GLFW_KEY_NUM_LOCK, "num_lock"),
            Map.entry(GLFW.GLFW_KEY_DELETE, "delete"),
            Map.entry(GLFW.GLFW_KEY_INSERT, "insert"),
            Map.entry(GLFW.GLFW_KEY_HOME, "home"),
            Map.entry(GLFW.GLFW_KEY_END, "end"),
            Map.entry(GLFW.GLFW_KEY_PAGE_UP, "page_up"),
            Map.entry(GLFW.GLFW_KEY_PAGE_DOWN, "page_down"),
            Map.entry(GLFW.GLFW_KEY_PRINT_SCREEN, "print_screen"),
            Map.entry(GLFW.GLFW_KEY_SCROLL_LOCK, "scroll_lock"),
            Map.entry(GLFW.GLFW_KEY_PAUSE, "pause"),
            Map.entry(GLFW.GLFW_KEY_MENU, "menu"),
            Map.entry(GLFW.GLFW_KEY_LEFT_SUPER, "left_windows"),
            Map.entry(GLFW.GLFW_KEY_RIGHT_SUPER, "right_windows"),
            Map.entry(GLFW.GLFW_KEY_CAPS_LOCK, "caps_lock"),
            Map.entry(GLFW.GLFW_KEY_RIGHT, "right"),
            Map.entry(GLFW.GLFW_KEY_LEFT, "left"),
            Map.entry(GLFW.GLFW_KEY_UP, "up"),
            Map.entry(GLFW.GLFW_KEY_DOWN, "down"),
            Map.entry(GLFW.GLFW_KEY_KP_0, "keypad_0"),
            Map.entry(GLFW.GLFW_KEY_KP_1, "keypad_1"),
            Map.entry(GLFW.GLFW_KEY_KP_2, "keypad_2"),
            Map.entry(GLFW.GLFW_KEY_KP_3, "keypad_3"),
            Map.entry(GLFW.GLFW_KEY_KP_4, "keypad_4"),
            Map.entry(GLFW.GLFW_KEY_KP_5, "keypad_5"),
            Map.entry(GLFW.GLFW_KEY_KP_6, "keypad_6"),
            Map.entry(GLFW.GLFW_KEY_KP_7, "keypad_7"),
            Map.entry(GLFW.GLFW_KEY_KP_8, "keypad_8"),
            Map.entry(GLFW.GLFW_KEY_KP_9, "keypad_9"),
            Map.entry(GLFW.GLFW_KEY_KP_ADD, "keypad_+"),
            Map.entry(GLFW.GLFW_KEY_KP_DECIMAL, "keypad_."),
            Map.entry(GLFW.GLFW_KEY_KP_DIVIDE, "keypad_/"),
            Map.entry(GLFW.GLFW_KEY_KP_ENTER, "keypad_enter"),
            Map.entry(GLFW.GLFW_KEY_KP_EQUAL, "keypad_="),
            Map.entry(GLFW.GLFW_KEY_KP_MULTIPLY, "keypad_*"),
            Map.entry(GLFW.GLFW_KEY_KP_SUBTRACT, "keypad_-"),
            Map.entry(GLFW.GLFW_KEY_F1, "f1"),
            Map.entry(GLFW.GLFW_KEY_F2, "f2"),
            Map.entry(GLFW.GLFW_KEY_F3, "f3"),
            Map.entry(GLFW.GLFW_KEY_F4, "f4"),
            Map.entry(GLFW.GLFW_KEY_F5, "f5"),
            Map.entry(GLFW.GLFW_KEY_F6, "f6"),
            Map.entry(GLFW.GLFW_KEY_F7, "f7"),
            Map.entry(GLFW.GLFW_KEY_F8, "f8"),
            Map.entry(GLFW.GLFW_KEY_F9, "f9"),
            Map.entry(GLFW.GLFW_KEY_F10, "f10"),
            Map.entry(GLFW.GLFW_KEY_F11, "f11"),
            Map.entry(GLFW.GLFW_KEY_F12, "f12"),
            Map.entry(GLFW.GLFW_KEY_F13, "f13"),
            Map.entry(GLFW.GLFW_KEY_F14, "f14"),
            Map.entry(GLFW.GLFW_KEY_F15, "f15"),
            Map.entry(GLFW.GLFW_KEY_F16, "f16")
    );
    
    public static String translate(int code) {
        String keyName = keyMap.get(code);
        return keyName == null ? GLFW.glfwGetKeyName(code, 0) : keyName;
    }
}
