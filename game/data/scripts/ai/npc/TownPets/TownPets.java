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
package ai.npc.TownPets;

import l2r.Config;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Town Pets AI
 * @author malyelfik
 */
public class TownPets extends AbstractNpcAI
{
	// Pet IDs
	private static final int[] PETS =
	{
		31202, // Maximus
		31203, // Moon Dancer
		31204, // Georgio
		31205, // Katz
		31206, // Ten Ten
		31207, // Sardinia
		31208, // La Grange
		31209, // Misty Rain
		31266, // Kaiser
		31593, // Dorothy
		31758, // Rafi
		31955, // Ruby
	};
	
	public TownPets()
	{
		super(TownPets.class.getSimpleName(), "ai/npc");
		
		if (Config.ALLOW_PET_WALKERS)
		{
			addSpawnId(PETS);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("move"))
		{
			final int locX = (npc.getSpawn().getX() - 50) + getRandom(100);
			final int locY = (npc.getSpawn().getY() - 50) + getRandom(100);
			npc.setRunning();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(locX, locY, npc.getZ(), 0));
			startQuestTimer("move", 5000, npc, null);
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("move", 3000, npc, null);
		return super.onSpawn(npc);
	}
}
