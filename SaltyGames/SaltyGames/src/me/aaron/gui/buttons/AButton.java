package me.aaron.gui.buttons;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.aaron.gui.guis.AGui;
import me.aaron.utils.ClickAction;

public class AButton extends ItemStack {
	
	private ClickAction action;
	private AGui gui;
	private String[] args;
	
	public AButton(MaterialData mat, int count) {
		super(mat.getItemType());
		this.setData(mat);
		this.setDurability(mat.getData());
		this.setAmount(count);
	}
	
	public AButton(ItemStack itm) {
		super(itm);
	}

	public ClickAction getAction() {
		return action;
	}

	public void setAction(ClickAction action) {
		this.action = action;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String... args) {
		this.args = args;
	}
	
	public AButton setActionAndArgs(ClickAction action, String... args) {
		this.action = action;
		this.args = args;
		return this;
	}
}
