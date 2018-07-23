/*
 * Copyright (C) 2004-2015 L2J DataPack
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
package hellbound.AI.NPC.Shadai;

import l2r.gameserver.GameTimeController;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Shadai AI.
 * @author GKR
 */
public final class Shadai extends AbstractNpcAI
{
	// NPCs
	private static final int SHADAI = 32347;
	// Locations
	private static final Location DAY_COORDS = new Location(16882, 238952, 9776);
	private static final Location NIGHT_COORDS = new Location(9064, 253037, -1928);
	
	public Shadai()
	{
		super(Shadai.class.getSimpleName(), "hellbound/AI/NPC");
		addSpawnId(SHADAI);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("VALIDATE_POS") && (npc != null))
		{
			Location coords = DAY_COORDS;
			boolean mustRevalidate = false;
			if ((npc.getX() != NIGHT_COORDS.getX()) && GameTimeController.getInstance().isNight())
			{
				coords = NIGHT_COORDS;
				mustRevalidate = true;
			}
			else if ((npc.getX() != DAY_COORDS.getX()) && !GameTimeController.getInstance().isNight())
			{
				mustRevalidate = true;
			}
			
			if (mustRevalidate)
			{
				npc.getSpawn().setLocation(coords);
				npc.teleToLocation(coords);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		startQuestTimer("VALIDATE_POS", 60000, npc, null, true);
		return super.onSpawn(npc);
	}
}
