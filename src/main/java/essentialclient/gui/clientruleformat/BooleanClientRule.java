package essentialclient.gui.clientruleformat;

import java.util.*;

public class BooleanClientRule {

    public static Map<String, BooleanClientRule> clientBooleanRulesMap = new HashMap<>();

    public final String name;
    public final String description;
    public final boolean defaultValue;
    public final String defaultAsString;
    public boolean value;
    public final boolean isCommand;

    public BooleanClientRule(String name, String description, boolean defaultValue, boolean value, boolean isCommand) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.defaultAsString = String.valueOf(defaultValue);
        this.value = value;
        this.isCommand = isCommand;
    }

    public BooleanClientRule(String name, String description, boolean isCommand) {
        this.name = name;
        this.description = description;
        this.defaultValue = false;
        this.defaultAsString = "false";
        this.value = false;
        this.isCommand = isCommand;
    }

    public static List<BooleanClientRule> getRules() {
        TreeMap<String, BooleanClientRule> sortedMap = new TreeMap<>(clientBooleanRulesMap);
        ArrayList<BooleanClientRule> sortedList = new ArrayList<>();
        for (Map.Entry<String, BooleanClientRule> entry : sortedMap.entrySet()) {
            sortedList.add(entry.getValue());
        }
        return sortedList;
    }

    public void invertBoolean() {
        this.value = !this.value;
    }

}
