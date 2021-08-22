package essentialclient.gui.clientruleformat;

import java.util.*;

public class NumberClientRule {

    public static Map<String, NumberClientRule> clientNumberRulesMap = new HashMap<>();

    public final String name;
    public final String description;
    public final int defaultValue;
    public final String defaultAsString;
    public int value;
    public final boolean isCommand;

    public NumberClientRule(String name, String description, int defaultValue, int value, boolean isCommand) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.defaultAsString = String.valueOf(defaultValue);
        this.value = value;
        this.isCommand = false;
    }

    public NumberClientRule(String name, String description, boolean isCommand) {
        this.name = name;
        this.description = description;
        this.defaultValue = 0;
        this.defaultAsString = "0";
        this.value = 0;
        this.isCommand = false;
    }

    public static List<NumberClientRule> getRules() {
        TreeMap<String, NumberClientRule> sortedMap = new TreeMap<>(clientNumberRulesMap);
        ArrayList<NumberClientRule> sortedList = new ArrayList<>();
        for (Map.Entry<String, NumberClientRule> entry : sortedMap.entrySet()) {
            sortedList.add(entry.getValue());
        }
        return sortedList;
    }
}
