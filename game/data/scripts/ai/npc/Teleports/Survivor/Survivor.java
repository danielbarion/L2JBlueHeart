/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.npc.Teleports.Survivor;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;

import ai.npc.AbstractNpcAI;

/**
 * Gracia Survivor teleport AI.<br>
 * Original Jython script by Kerberos.
 * @author Plim
 */
public class Survivor extends AbstractNpcAI
{
	// NPC
	private static final int SURVIVOR = 32632;
	// Misc
	private static final int MIN_LEVEL = 75;
	
	public Survivor()
	{
		super(Survivor.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(SURVIVOR);
		addTalkId(SURVIVOR);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ("32632-2.htm".equals(event))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				event = "32632-3.htm";
			}
			else if (player.getAdena() < 150000)
			{
				return event;
			}
			else
			{
				takeItems(player, Inventory.ADENA_ID, 150000);
				player.teleToLocation(-149406, 255247, -80);
				return null;
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		return "32632-1.htm";
	}
}
