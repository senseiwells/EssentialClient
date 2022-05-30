package me.senseiwells.essentialclient.rule.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class ListClientRule extends ClientRule<List<String>> implements Rule.ListRule {
    private static final Gson GSON = new Gson();
    private int maxLength;

    public ListClientRule(String name, String description, List<String> defaultValue, RuleListener<List<String>> ruleListener) {
        super(name, description, defaultValue);
        this.addListener(ruleListener);
        this.maxLength = 32;
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

    @Override
    public JsonObject serialise() {
        JsonObject object = super.serialise();
        object.addProperty("max_length", this.getMaxLength());
        return object;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength <= 0 ? 32 : maxLength;
    }
}
