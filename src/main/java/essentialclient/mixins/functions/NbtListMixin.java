package essentialclient.mixins.functions;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(NbtList.class)
public interface NbtListMixin {
    @Invoker
    @SuppressWarnings("unused")
    static NbtList createNbtList(List<NbtElement> list, byte type) {
        throw new AssertionError();
    }
}
