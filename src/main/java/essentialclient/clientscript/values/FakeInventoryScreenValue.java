package essentialclient.clientscript.values;

import essentialclient.utils.render.FakeInventoryScreen;

public class FakeInventoryScreenValue extends ScreenValue {
    public FakeInventoryScreenValue(FakeInventoryScreen value) {
        super(value);
    }

    public FakeInventoryScreen getValue() {
        return (FakeInventoryScreen) this.value;
    }

    @Override
    public ScreenValue copy() {
        return this;
    }
}
