package me.senseiwells.arucas.core;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerContext {
	private final List<LexerRule> rules;
	
	public LexerContext() {
		rules = new ArrayList<>();
	}
	
	public LexerContext addRule(Token.Type type, Consumer<LexerRule> consumer) {
		LexerRule rule = new LexerRule(type);
		consumer.accept(rule);
		rules.add(rule);
		return this;
	}
	
	public LexerToken nextToken(String input) {
		LexerRule selectedRule = null;
		int longestRule = 1;
		for (LexerRule rule : rules) {
			int length = rule.getMatchLength(input);
			
			if (length >= longestRule) {
				longestRule = length;
				selectedRule = rule;
			}
		}
		
		return selectedRule == null ? null : new LexerToken(selectedRule.type, input.substring(0, longestRule));
	}
	
	public static class LexerToken {
		public final Token.Type type;
		public final String content;
		public final int length;
		
		public LexerToken(Token.Type type, String content) {
			this.type = type;
			this.content = content;
			this.length = content.length();
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public static class LexerRule {
		public final List<Pattern> matches;
		public final Token.Type type;
		
		public LexerRule(Token.Type type) {
			this.matches = new ArrayList<>();
			this.type = type;
		}
		
		public LexerRule addString(String value) {
			this.matches.add(Pattern.compile(StringUtils.regexEscape(value)));
			return this;
		}
		
		public LexerRule addStrings(String... values) {
			for(String value : values)
				addString(value);
			return this;
		}
		
		public LexerRule addRegex(String regex) {
			this.matches.add(Pattern.compile(regex));
			return this;
		}
		
		public LexerRule addRegexes(String... regexes) {
			for(String regex : regexes)
				addRegex(regex);
			return this;
		}
		
		public LexerRule addMultiline(String open, String close) {
			return addMultiline(open, "", close);
		}
		
		public LexerRule addMultiline(String open, String escape, String close) {
			String s = StringUtils.regexEscape(open);
			String c = StringUtils.regexEscape(close);
			
			String regex;
			if(escape.isEmpty()) {
				regex = s + ".*?" + c;
			} else {
				String e = StringUtils.regexEscape(escape);
				regex = s + "(?:" + e + "(?:" + e + "|" + c + "|(?!" + c + ").)|(?!" + e + "|" + c + ").)*" + c;
			}
			
			this.matches.add(Pattern.compile(regex, Pattern.DOTALL));
			return this;
		}
		
		public int getMatchLength(String string) {
			int length = 0;
			for(Pattern pattern : matches) {
				Matcher matcher = pattern.matcher(string);
				if(matcher.lookingAt()) {
					length = Math.max(length, matcher.end());
				}
			}
			
			return length < 1 ? -1:length;
		}
	}
}
