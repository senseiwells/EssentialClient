package me.senseiwells.essential_client.ducks;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.Nullable;

public interface TranslucentLiquids {
	void essentialclient$setTranslucentConsumer(@Nullable VertexConsumer consumer);

	@Nullable VertexConsumer essentialclient$getTranslucentConsumer();
}
