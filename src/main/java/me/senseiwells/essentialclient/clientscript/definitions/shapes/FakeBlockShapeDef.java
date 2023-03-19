package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.annotations.*;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.NullDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.ConstructorFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.definitions.BlockDef;
import me.senseiwells.essentialclient.clientscript.definitions.MaterialDef;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptFakeBlock;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = FAKE_BLOCK,
	desc = "This class can be used to create fake blocks which can be rendered in the world.",
	superclass = ShapeDef.class,
	language = Language.Java
)
public class FakeBlockShapeDef extends CreatableDefinition<ScriptFakeBlock> {
	public FakeBlockShapeDef(Interpreter interpreter) {
		super(FAKE_BLOCK, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptFakeBlock> superclass() {
		return this.getPrimitiveDef(ShapeDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(2, this::construct)
		);
	}

	@ConstructorDoc(
		desc = "Creates a fake block with the given block and position",
		params = {
			@ParameterDoc(type = BlockDef.class, name = "block", desc = "The block to use"),
			@ParameterDoc(type = PosDef.class, name = "pos", desc = "The position of the block")
		},
		examples = "new FakeBlock(Material.BEDROCK.asBlock(), new Pos(0, 0, 0));"
	)
	private Unit construct(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		ScriptFakeBlock block = new ScriptFakeBlock(arguments.getInterpreter(), pos.getVec3d(), material.asBlockState());
		instance.setPrimitive(this, block);
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setBlock", 1, this::setBlock),
			MemberFunction.of("setPos", 1, this::setPos),
			MemberFunction.of("getBlock", this::getBlock),
			MemberFunction.of("getPos", this::getPos),
			MemberFunction.of("setCull", 1, this::setCull),
			MemberFunction.of("shouldCull", this::shouldCull),
			MemberFunction.of("setDirection", 1, this::setDirection),
			MemberFunction.of("getDirection", this::getDirection)
		);
	}

	@FunctionDoc(
		name = "setBlock",
		desc = "Sets the block type to render of the fake block",
		params = {@ParameterDoc(type = BlockDef.class, name = "block", desc = "The block to render")},
		examples = "fakeBlock.setBlock(Material.BEDROCK.asBlock());"
	)
	private Void setBlock(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
		block.setState(material.asBlockState());
		return null;
	}

	@FunctionDoc(
		name = "setPos",
		desc = "Sets the position of the fake block",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "The position of the fake block")},
		examples = "fakeBlock.setPos(new Pos(0, 0, 0));"
	)
	private Void setPos(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		block.setPosition(pos.getVec3d());
		return null;
	}

	@FunctionDoc(
		name = "getBlock",
		desc = "Gets the current block type of the fake block",
		returns = @ReturnDoc(type = BlockDef.class, desc = "The block type of the fake block"),
		examples = "fakeBlock.getBlock();"
	)
	private ScriptMaterial getBlock(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		return new ScriptBlockState(block.getState(), EssentialUtils.vec3dToBlockPos(block.getPosition()));
	}

	@FunctionDoc(
		name = "getPos",
		desc = "Gets the position of the fake block",
		returns = @ReturnDoc(type = PosDef.class, desc = "The position of the fake block"),
		examples = "fakeBlock.getPos();"
	)
	private ScriptPos getPos(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		return new ScriptPos(block.getPosition());
	}

	@FunctionDoc(
		name = "setCull",
		desc = "Sets whether the fake block should be culled",
		params = {@ParameterDoc(type = BooleanDef.class, name = "cull", desc = "Whether the fake block should be culled")},
		examples = "fakeBlock.setCull(true);"
	)
	private Void setCull(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		block.setCull(arguments.nextPrimitive(BooleanDef.class));
		return null;
	}

	@FunctionDoc(
		name = "shouldCull",
		desc = "Gets whether the fake block should be culled",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "Whether the fake block should be culled"),
		examples = "fakeBlock.shouldCull();"
	)
	private boolean shouldCull(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		return block.shouldCull();
	}

	@FunctionDoc(
		name = "setDirection",
		desc = {
			"Sets the direction of the fake block,",
			"this may be null in which case the block will face the player"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "direction", desc = "The direction of the fake block")},
		examples = "fakeBlock.setDirection(Direction.UP);"
	)
	private Void setDirection(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		if (arguments.isNext(NullDef.class)) {
			block.setDirection(null);
		} else {
			Direction direction = ClientScriptUtils.stringToDirection(arguments.nextConstant(), null);
			block.setDirection(direction);
		}
		return null;
	}

	@FunctionDoc(
		name = "getDirection",
		desc = "Gets the direction of the fake block",
		returns = @ReturnDoc(type = StringDef.class, desc = "The direction of the fake block, may be null"),
		examples = "fakeBlock.getDirection();"
	)
	private String getDirection(Arguments arguments) {
		ScriptFakeBlock block = arguments.nextPrimitive(this);
		Direction direction = block.getDirection();
		return direction == null ? null : direction.getName();
	}
}
