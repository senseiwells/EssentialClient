package me.senseiwells.essentialclient.utils.misc;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.rule.impl.SimpleRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class WikiParser {
	public static String generateWiki() {
		StringBuilder builder = new StringBuilder(getWikiBase());
		addClientRules(builder);
		return builder.toString();
	}

	private static String getWikiBase() {
		Path wikiPath = Path.of("../docs/HomeBase.md");
		// We should only really be running this code in a dev environment (actions)
		return Files.exists(wikiPath) ? EssentialUtils.throwAsUnchecked(() -> Files.readString(wikiPath)) : "# EssentialClient\n";
	}

	private static void addClientRules(StringBuilder builder) {
		builder.append("\n## Client Rules:\n");
		ClientRules.getClientRules().stream().sorted(Comparator.comparing(SimpleRule::getName)).forEach(r -> {
			builder.append("# ").append(r.getName()).append("\n");
			builder.append(r.getDescription()).append("\n");
			builder.append("- Category: ").append(r.getCategory()).append("\n");
			builder.append("- Type: ").append(r.getType().toString()).append("\n");
			builder.append("- Default Value: ").append(r.getDefaultValue()).append("\n\n");
		});
	}
}
