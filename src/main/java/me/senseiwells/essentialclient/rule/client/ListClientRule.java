package me.senseiwells.essentialclient.rule.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class ListClientRule extends ClientRule<List<String>> implements Rule.ListRule {
    private static final Gson GSON = new Gson();

    public ListClientRule(String name, String description, List<String> defaultValue, RuleListener<List<String>> ruleListener) {
        super(name, description, defaultValue);
        this.addListener(ruleListener);
    }

    public ListClientRule(String name, String description, List<String> listValues) {
        this(name, description, listValues, null);
    }

    @Override
    public String getTypeAsString() {
        return "list";
    }

    @Override
    public void setValueFromString(String value) {
        JsonArray array = ExceptionUtils.catchAsNull(() -> GSON.fromJson(value, JsonArray.class));
        if (array == null) {
            this.logCannotSet(value);
            return;
        }
        List<String> configs = this.fromJson(array);
        this.setValue(configs);
    }

    @Override
    public ListClientRule shallowCopy() {
        ListClientRule rule = new ListClientRule(this.getName(), this.getDescription(), this.getValue());
        if (this.getListeners() != null) {
            for (RuleListener<List<String>> listener : this.getListeners()) {
                rule.addListener(listener);
            }
        }
        return rule;
    }
}
