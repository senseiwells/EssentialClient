package essentialclient.utils.keyboard;

import org.lwjgl.glfw.GLFW;

public class KeyboardHelper {
    // This is perfectly fine :P
    public static String translate(int code) {
        return switch (code) {
            case GLFW.GLFW_KEY_SPACE -> "space";
            case GLFW.GLFW_KEY_LEFT_ALT -> "left_alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "right_alt";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "left_shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "right_shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "left_control";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "right_control";
            case GLFW.GLFW_KEY_TAB -> "tab";
            case GLFW.GLFW_KEY_ESCAPE -> "escape";
            case GLFW.GLFW_KEY_ENTER -> "enter";
            case GLFW.GLFW_KEY_NUM_LOCK -> "num_lock";
            case GLFW.GLFW_KEY_DELETE -> "delete";
            case GLFW.GLFW_KEY_INSERT -> "insert";
            case GLFW.GLFW_KEY_HOME -> "home";
            case GLFW.GLFW_KEY_END -> "end";
            case GLFW.GLFW_KEY_PAGE_UP -> "page_up";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "page_down";
            case GLFW.GLFW_KEY_PRINT_SCREEN -> "print_screen";
            case GLFW.GLFW_KEY_SCROLL_LOCK -> "scroll_lock";
            case GLFW.GLFW_KEY_PAUSE -> "pause";
            case GLFW.GLFW_KEY_MENU -> "menu";
            case GLFW.GLFW_KEY_LEFT_SUPER -> "left_windows";
            case GLFW.GLFW_KEY_RIGHT_SUPER -> "right_windows";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "caps_lock";
            case GLFW.GLFW_KEY_RIGHT -> "right";
            case GLFW.GLFW_KEY_LEFT -> "left";
            case GLFW.GLFW_KEY_UP -> "up";
            case GLFW.GLFW_KEY_DOWN -> "down";
            case GLFW.GLFW_KEY_KP_0 -> "keypad_0";
            case GLFW.GLFW_KEY_KP_1 -> "keypad_1";
            case GLFW.GLFW_KEY_KP_2 -> "keypad_2";
            case GLFW.GLFW_KEY_KP_3 -> "keypad_3";
            case GLFW.GLFW_KEY_KP_4 -> "keypad_4";
            case GLFW.GLFW_KEY_KP_5 -> "keypad_5";
            case GLFW.GLFW_KEY_KP_6 -> "keypad_6";
            case GLFW.GLFW_KEY_KP_7 -> "keypad_7";
            case GLFW.GLFW_KEY_KP_8 -> "keypad_8";
            case GLFW.GLFW_KEY_KP_9 -> "keypad_9";
            case GLFW.GLFW_KEY_KP_ADD -> "keypad_+";
            case GLFW.GLFW_KEY_KP_DECIMAL -> "keypad_.";
            case GLFW.GLFW_KEY_KP_DIVIDE -> "keypad_/";
            case GLFW.GLFW_KEY_KP_ENTER -> "keypad_enter";
            case GLFW.GLFW_KEY_KP_EQUAL -> "keypad_=";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "keypad_*";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "keypad_-";
            case GLFW.GLFW_KEY_F1 -> "f1";
            case GLFW.GLFW_KEY_F2 -> "f2";
            case GLFW.GLFW_KEY_F3 -> "f3";
            case GLFW.GLFW_KEY_F4 -> "f4";
            case GLFW.GLFW_KEY_F5 -> "f5";
            case GLFW.GLFW_KEY_F6 -> "f6";
            case GLFW.GLFW_KEY_F7 -> "f7";
            case GLFW.GLFW_KEY_F8 -> "f8";
            case GLFW.GLFW_KEY_F9 -> "f9";
            case GLFW.GLFW_KEY_F10 -> "f10";
            case GLFW.GLFW_KEY_F11 -> "f11";
            case GLFW.GLFW_KEY_F12 -> "f12";
            case GLFW.GLFW_KEY_F13 -> "f13";
            case GLFW.GLFW_KEY_F14 -> "f14";
            case GLFW.GLFW_KEY_F15 -> "f15";
            case GLFW.GLFW_KEY_F16 -> "f16";
            default -> GLFW.glfwGetKeyName(code, 0);
        };
    }
}
