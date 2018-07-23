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
package quests.TerritoryWarScripts;

import l2r.gameserver.network.NpcStringId;

/**
 * Pierce through a Shield! (734)
 * @author Gigiikun
 */
public final class Q00734_PierceThroughAShield extends TerritoryWarSuperClass
{
	public Q00734_PierceThroughAShield()
	{
		super(734, Q00734_PierceThroughAShield.class.getSimpleName(), "Pierce through a Shield");
		CLASS_IDS = new int[]
		{
			6,
			91,
			5,
			90,
			20,
			99,
			33,
			106
		};
		RANDOM_MIN = 10;
		RANDOM_MAX = 15;
		npcString = new NpcStringId[]
		{
			NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS,
			NpcStringId.YOU_WEAKENED_THE_ENEMYS_DEFENSE
		};
	}
}
