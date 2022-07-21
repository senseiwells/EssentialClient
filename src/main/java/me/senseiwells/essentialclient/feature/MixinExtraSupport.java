package me.senseiwells.essentialclient.feature;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

/**
 * <p>
 * The reason we have this class is to be able
 * to load MixinExtrasBootstrap at runtime.
 * </p>
 *
 * <p>
 * MixinExtras shadows to different package so
 * fabric's pre-launch entry point will not be
 * able to find it at runtime since shadow gradle
 * does not remap the {@code fabric.mod.json} but
 * will remap this class file to the right package.
 * </p>
 */
public class MixinExtraSupport {
	static {
		MixinExtrasBootstrap.init();
	}

	public static void load() { }
}
