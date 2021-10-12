package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;

public class StringNode extends Node {
    public final StringValue value;

    public StringNode(Token token, Context context) {
        super(token, context);
        this.value = new StringValue(StringUtils.unescapeString(token.content.substring(1, token.content.length() - 1)));
        this.value.setPos(this.startPos, this.endPos);
    }

    @Override
    public Value<?> visit() {
        return value.setContext(this.context);
    }
}
