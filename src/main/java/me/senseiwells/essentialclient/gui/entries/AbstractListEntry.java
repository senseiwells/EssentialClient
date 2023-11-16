package me.senseiwells.essentialclient.gui.entries;

import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ElementListWidget;

public abstract class AbstractListEntry<E extends AbstractListEntry<E>> extends ElementListWidget.Entry<E> implements ParentElement {

}
