package com.fullwall.Citizens.Questers;

import com.fullwall.Citizens.Interfaces.Toggleable;
import com.fullwall.Citizens.Properties.PropertyManager;
import com.fullwall.resources.redecouverte.NPClib.HumanNPC;

public class QuesterNPC implements Toggleable {
	private HumanNPC npc;

	public QuesterNPC(HumanNPC npc) {
		this.npc = npc;
	}

	@Override
	public void toggle() {
		npc.setQuester(!npc.isQuester());
	}

	@Override
	public boolean getToggle() {
		return npc.isQuester();
	}

	@Override
	public String getName() {
		return npc.getStrippedName();
	}

	@Override
	public String getType() {
		return "quester";
	}

	@Override
	public void saveState() {
		PropertyManager.get(getType()).saveState(npc);
	}

	@Override
	public void registerState() {
		PropertyManager.get(getType()).register(npc);
	}
}