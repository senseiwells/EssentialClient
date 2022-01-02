package essentialclient.clientscript.values;

import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.util.math.*;

public class Vec3iValue extends Value<Vec3i> {
	public final Vec3i vec3i;

	public Vec3iValue(Vec3i vec3i) {
		super(vec3i);
		this.vec3i = vec3i;
	}
	public Vec3iValue(Vec3f vec3f){
		this(new Vec3i((double) vec3f.getX(), (double) vec3f.getY(),(double) vec3f.getZ()));
	}
	public Vec3iValue(Vec3d vec3d){
		this(new Vec3i((double) vec3d.getX(), (double) vec3d.getY(),(double) vec3d.getZ()));
	}
	public static Vec3iValue fromString(String string){
		Direction direction = Direction.byName(string);
		if(direction == null){
			return new Vec3iValue(Vec3i.ZERO);
		}
		return new Vec3iValue(direction.getVector());
	}
	public static Vec3iValue fromDirection(Direction direction){
		return new Vec3iValue(direction.getVector());
	}
	public Value<?> add(Vec3iValue vec3dValue){
		return new Vec3iValue(this.vec3i.add(vec3dValue.vec3i));
	}
	public Value<?> subtract(Vec3iValue vec3iValue){
		return new Vec3iValue(this.vec3i.subtract(vec3iValue.vec3i));
	}
	public Value<?> rotateYClockwise(){
		Direction newDirection = Direction.fromVector(new BlockPos(this.vec3i.getX(), this.vec3i.getY(), this.vec3i.getZ()));
		if (newDirection == null) {return new Vec3iValue(Vec3i.ZERO);}
		return new Vec3iValue(newDirection.rotateYClockwise().getVector());
	}
	public Value<?> offset(Direction direction, int distance){
		return new Vec3iValue(this.vec3i.offset(direction,distance));
	}
	public Value<?> up(){
		return new Vec3iValue(this.vec3i.up());
	}
	public Value<?> down(){
		return new Vec3iValue(this.vec3i.down());
	}
	public Value<?> east(){
		return new Vec3iValue(this.vec3i.east());
	}
	public Value<?> west(){
		return new Vec3iValue(this.vec3i.west());
	}
	public Value<?> north(){
		return new Vec3iValue(this.vec3i.north());
	}
	public Value<?> south(){
		return new Vec3iValue(this.vec3i.south());
	}
	public Value<?> getX() {
		return new NumberValue(this.vec3i.getX());
	}
	public Value<?> getY() {
		return new NumberValue(this.vec3i.getY());
	}
	public Value<?> getZ() {
		return new NumberValue(this.vec3i.getZ());
	}
	public Value<?> getXYZ(){
		ArucasValueList arucasValueList = new ArucasValueList();
		arucasValueList.add(new NumberValue(this.vec3i.getX()));
		arucasValueList.add(new NumberValue(this.vec3i.getY()));
		arucasValueList.add(new NumberValue(this.vec3i.getZ()));
		return new ListValue(arucasValueList);
	}
	public Value<?> getSquaredDistance(Vec3iValue vec3iValue){
		return new NumberValue(this.vec3i.getSquaredDistance(vec3iValue.vec3i));
	}
 	public BlockPos getBlockPos() {
		return new BlockPos(this.vec3i);
	}
	public Value<?> multiply(int scale){
		return new Vec3iValue(this.vec3i.multiply(scale));
	}
	@Override
	public Value<Vec3i> copy() {
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vec3iValue otherValue)) {
			return false;
		}
		return this.getBlockPos().asLong() == otherValue.getBlockPos().asLong();
	}

	@Override
	public String toString() {
		return this.vec3i.toString();
	}
	}