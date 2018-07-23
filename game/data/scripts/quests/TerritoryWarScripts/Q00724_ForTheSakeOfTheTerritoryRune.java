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
 * For the Sake of the Territory - Rune (724)
 * @author Gigiikun
 */
public final class Q00724_ForTheSakeOfTheTerritoryRune extends TerritoryWarSuperClass
{
	public Q00724_ForTheSakeOfTheTerritoryRune()
	{
		super(724, Q00724_ForTheSakeOfTheTerritoryRune.class.getSimpleName(), "For the Sake of the Territory - Rune");
		CATAPULT_ID = 36506;
		TERRITORY_ID = 88;
		LEADER_IDS = new int[]
		{
			36550,
			36552,
			36555,
			36598
		};
		GUARD_IDS = new int[]
		{
			36551,
			36553,
			36554
		};
		npcString = new NpcStringId[]
		{
			NpcStringId.THE_CATAPULT_OF_RUNE_HAS_BEEN_DESTROYED
		};
		registerKillIds();
	}
}
