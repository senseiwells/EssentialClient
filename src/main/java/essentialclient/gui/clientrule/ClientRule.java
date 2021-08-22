package essentialclient.gui.clientrule;

import java.util.*;

public class ClientRule {

    public static Map<String, ClientRule> clientRulesMap = new HashMap<>();

    public final String name;
    public final String type;
    public final String description;
    public final String defaultValue;
    public String value;
    public final boolean isCommand;

    public ClientRule(String name, String type, String description, String defaultValue, String value, boolean isCommand) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = value;
        this.isCommand = isCommand;
    }

    public static List<ClientRule> getRules() {
        TreeMap<String, ClientRule> sortedMap = new TreeMap<>(clientRulesMap);
        ArrayList<ClientRule> sortedList = new ArrayList<>();
        for (Map.Entry<String, ClientRule> entry : sortedMap.entrySet()) {
            sortedList.add(entry.getValue());
        }
        return sortedList;
    }

    public void invertBoolean() {
        if (this.type.equalsIgnoreCase("boolean"))
            this.value = String.valueOf(!Boolean.parseBoolean(this.value));
    }

}
