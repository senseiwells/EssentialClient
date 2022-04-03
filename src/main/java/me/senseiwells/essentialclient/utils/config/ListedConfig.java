package me.senseiwells.essentialclient.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ListedConfig<E> implements Config.CList {
	protected final List<E> list;

	public ListedConfig() {
		this.list = new ArrayList<>();
	}

	public void add(E element) {
		this.list.add(element);
	}

	public E get(int index) {
		return this.list.get(index);
	}

	public E remove(int index) {
		return this.list.remove(index);
	}

	protected abstract JsonElement elementToJson(E element);

	protected abstract E jsonToElement(JsonElement element);

	@Override
	public final JsonArray getSaveData() {
		JsonArray configList = new JsonArray();
		this.list.forEach(e -> configList.add(this.elementToJson(e)));
		return configList;
	}

	@Override
	public final void readConfig(JsonArray element) {
		element.forEach(jsonElement -> this.list.add(this.jsonToElement(element)));
	}

	@Override
	public final void readConfig() {
		Config.CList.super.readConfig();
	}

	@Override
	public final Path getConfigRootPath() {
		return Config.CList.super.getConfigRootPath();
	}

	@Override
	public final JsonArray getConfigData() {
		return Config.CList.super.getConfigData();
	}

	@Override
	public final void saveConfig() {
		Config.CList.super.saveConfig();
	}
}
