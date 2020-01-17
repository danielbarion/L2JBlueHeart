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
package events.CharacterBirthday;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.util.Util;

/**
 * Character Birthday event AI.<br>
 * Updated to H5 by Nyaran.
 * @author Gnacik
 */
public final class CharacterBirthday extends Quest
{
	private static final int ALEGRIA = 32600;
	private static int SPAWNS = 0;
	
	private final static int[] GK =
	{
		30006,
		30059,
		30080,
		30134,
		30146,
		30177,
		30233,
		30256,
		30320,
		30540,
		30576,
		30836,
		30848,
		30878,
		30899,
		31275,
		31320,
		31964,
		32163
	};
	
	public CharacterBirthday()
	{
		super(-1, CharacterBirthday.class.getSimpleName(), "events");
		addStartNpc(ALEGRIA);
		addStartNpc(GK);
		addTalkId(ALEGRIA);
		addTalkId(GK);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("despawn_npc"))
		{
			npc.doDie(player);
			SPAWNS--;
			
			htmltext = null;
		}
		else if (event.equalsIgnoreCase("change"))
		{
			// Change Hat
			if (hasQuestItems(player, 10250))
			{
				takeItems(player, 10250, 1); // Adventurer Hat (Event)
				giveItems(player, 21594, 1); // Birthday Hat
				htmltext = null; // FIXME: Probably has html
				// Despawn npc
				npc.doDie(player);
				SPAWNS--;
			}
			else
			{
				htmltext = "32600-nohat.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (SPAWNS >= 3)
		{
			return "busy.htm";
		}
		
		if (!Util.checkIfInRange(10, npc, player, true))
		{
			L2Npc spawned = addSpawn(32600, player.getX() + 10, player.getY() + 10, player.getZ() + 10, 0, false, 0, true);
			startQuestTimer("despawn_npc", 180000, spawned, player);
			SPAWNS++;
		}
		else
		{
			return "tooclose.htm";
		}
		return null;
	}
}
