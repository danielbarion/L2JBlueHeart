/*
 * Copyright (C) 2004-2016 L2J DataPack
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
package ai.npc.Teleports.ElrokiTeleporters;

import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Elroki teleport AI.
 * @author Plim
 */
public final class ElrokiTeleporters extends AbstractNpcAI
{
	// NPCs
	private static final int ORAHOCHIN = 32111;
	private static final int GARIACHIN = 32112;
	// Locations
	private static final Location TELEPORT_ORAHOCIN = new Location(5171, -1889, -3165);
	private static final Location TELEPORT_GARIACHIN = new Location(7651, -5416, -3155);
	
	public ElrokiTeleporters()
	{
		super(ElrokiTeleporters.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(ORAHOCHIN, GARIACHIN);
		addStartNpc(ORAHOCHIN, GARIACHIN);
		addTalkId(ORAHOCHIN, GARIACHIN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		if (!talker.isInCombat())
		{
			talker.teleToLocation((npc.getId() == ORAHOCHIN) ? TELEPORT_ORAHOCIN : TELEPORT_GARIACHIN);
		}
		else
		{
			return npc.getId() + "-no.html";
		}
		return super.onTalk(npc, talker);
	}
}