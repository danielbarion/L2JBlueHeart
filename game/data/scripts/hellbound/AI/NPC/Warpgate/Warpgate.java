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
package hellbound.AI.NPC.Warpgate;

import l2r.Config;
import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.zone.L2ZoneType;

import ai.npc.AbstractNpcAI;
import hellbound.HellboundEngine;
import quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;

/**
 * Warpgate teleport AI.
 * @author _DS_
 */
public final class Warpgate extends AbstractNpcAI
{
	// NPCs
	private static final int[] WARPGATES =
	{
		32314,
		32315,
		32316,
		32317,
		32318,
		32319,
	};
	// Locations
	private static final Location ENTER_LOC = new Location(-11272, 236464, -3248);
	private static final Location REMOVE_LOC = new Location(-16555, 209375, -3670);
	// Item
	private static final int MAP = 9994;
	// Misc
	private static final int ZONE = 40101;
	
	public Warpgate()
	{
		super(Warpgate.class.getSimpleName(), "hellbound/AI/NPC");
		addStartNpc(WARPGATES);
		addFirstTalkId(WARPGATES);
		addTalkId(WARPGATES);
		addEnterZoneId(ZONE);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("enter"))
		{
			if (canEnter(player))
			{
				player.teleToLocation(ENTER_LOC, true);
			}
			else
			{
				return "Warpgate-03.html";
			}
		}
		else if (event.equals("TELEPORT"))
		{
			player.teleToLocation(REMOVE_LOC, true);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (!canEnter(player))
		{
			if (HellboundEngine.getInstance().isLocked())
			{
				return "Warpgate-01.html";
			}
		}
		
		return "Warpgate-02.html";
	}
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character.isPlayer())
		{
			final L2PcInstance player = character.getActingPlayer();
			
			if (!canEnter(player) && !player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isOnEvent())
			{
				startQuestTimer("TELEPORT", 1000, null, player);
			}
			else if (!player.isMinimapAllowed() && hasAtLeastOneQuestItem(player, MAP))
			{
				player.setMinimapAllowed(true);
			}
		}
		return super.onEnterZone(character, zone);
	}
	
	private static boolean canEnter(L2PcInstance player)
	{
		if (player.isFlying())
		{
			return false;
		}
		
		if (Config.HELLBOUND_WITHOUT_QUEST)
		{
			return true;
		}
		
		final QuestState path_to_hellbound_st = player.getQuestState(Q00130_PathToHellbound.class.getSimpleName());
		final QuestState thats_bloody_hot_st = player.getQuestState(Q00133_ThatsBloodyHot.class.getSimpleName());
		
		return (((path_to_hellbound_st != null) && path_to_hellbound_st.isCompleted()) || ((thats_bloody_hot_st != null) && thats_bloody_hot_st.isCompleted()));
	}
}