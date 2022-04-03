package me.senseiwells.essentialclient.clientrule.entries;

import java.util.List;
import java.util.function.Consumer;

public class CommandClientRule extends CycleClientRule {
	public CommandClientRule(String name, String description, String defaultValue, Consumer<String> consumer) {
		super(name, description, List.of("true", "false", "ops"), defaultValue, consumer);
	}
}
