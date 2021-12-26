package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.FakeInventoryScreenValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Set;

public class ArucasFakeInventoryScreenMembers implements IArucasValueExtension {

    @Override
    public Set<MemberFunction> getDefinedFunctions() {
        return this.fakeInventoryScreenFunctions;
    }

    @Override
    public Class<FakeInventoryScreenValue> getValueType() {
        return FakeInventoryScreenValue.class;
    }

    @Override
    public String getName() {
        return "FakeInventoryScreenMemberFunctions";
    }

    private final Set<MemberFunction> fakeInventoryScreenFunctions = Set.of(
        new MemberFunction("onClick", "function", this::onClick),
        new MemberFunction("setStackForSlot", List.of("slotNum", "stack"), this::setStackForSlot),
        new MemberFunction("getStackForSlot", "slotNum", this::getStackForSlot)
    );

    private Value<?> onClick(Context context, MemberFunction function) throws CodeError {
        FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
        FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
        fakeScreen.getValue().setFunctionValue(functionValue);
        return NullValue.NULL;
    }

    private Value<?> setStackForSlot(Context context, MemberFunction function) throws CodeError {
        FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
        NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
        ItemStackValue stackValue = function.getParameterValueOfType(context, ItemStackValue.class, 2);
        fakeScreen.getValue().setStack(slot.value.intValue(), stackValue.value);
        return NullValue.NULL;
    }

    private Value<?> getStackForSlot(Context context, MemberFunction function) throws CodeError {
        FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
        NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
        return new ItemStackValue(fakeScreen.getValue().getScreenHandler().slots.get(slot.value.intValue()).getStack());
    }

    private FakeInventoryScreenValue getFakeScreen(Context context, MemberFunction function) throws CodeError {
        return function.getParameterValueOfType(context, FakeInventoryScreenValue.class, 0);
    }
}
