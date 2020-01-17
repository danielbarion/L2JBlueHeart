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
package gracia.AI.NPC.GeneralDilios;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;

import ai.npc.AbstractNpcAI;

/**
 * Dilios AI
 * @author JIV, Sephiroth, Apocalipce
 */
public final class GeneralDilios extends AbstractNpcAI
{
	private static final int GENERAL_ID = 32549;
	private static final int GUARD_ID = 32619;
	
	private L2Npc _general = null;
	private final Set<L2Spawn> _guards = Collections.newSetFromMap(new ConcurrentHashMap<L2Spawn, Boolean>());
	
	private static final NpcStringId[] DILIOS_TEXT =
	{
		NpcStringId.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_WERE_GATHERING_BRAVE_ADVENTURERS_TO_ATTACK_TIATS_MOUNTED_TROOP_THATS_ROOTED_IN_THE_SEED_OF_DESTRUCTION,
		// NpcStringId.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_DESTRUCTION_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE,
		// NpcStringId.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_TIATS_MOUNTED_TROOP_IS_CURRENTLY_TRYING_TO_RETAKE_SEED_OF_DESTRUCTION_COMMIT_ALL_THE_AVAILABLE_REINFORCEMENTS_INTO_SEED_OF_DESTRUCTION,
		NpcStringId.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_BRAVE_ADVENTURERS_WHO_HAVE_CHALLENGED_THE_SEED_OF_INFINITY_ARE_CURRENTLY_INFILTRATING_THE_HALL_OF_EROSION_THROUGH_THE_DEFENSIVELY_WEAK_HALL_OF_SUFFERING,
		// NpcStringId.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_SWEEPING_THE_SEED_OF_INFINITY_IS_CURRENTLY_COMPLETE_TO_THE_HEART_OF_THE_SEED_EKIMUS_IS_BEING_DIRECTLY_ATTACKED_AND_THE_UNDEAD_REMAINING_IN_THE_HALL_OF_SUFFERING_ARE_BEING_ERADICATED,
		NpcStringId.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_INFINITY_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE
		// NpcStringId.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_RESURRECTED_UNDEAD_IN_THE_SEED_OF_INFINITY_ARE_POURING_INTO_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION
		// NpcStringId.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_EKIMUS_IS_ABOUT_TO_BE_REVIVED_BY_THE_RESURRECTED_UNDEAD_IN_SEED_OF_INFINITY_SEND_ALL_REINFORCEMENTS_TO_THE_HEART_AND_THE_HALL_OF_SUFFERING
	};
	
	public GeneralDilios()
	{
		super(GeneralDilios.class.getSimpleName(), "gracia/AI/NPC");
		
		addSpawnId(GENERAL_ID, GUARD_ID);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.startsWith("command_"))
		{
			int value = Integer.parseInt(event.substring(8));
			if (value < 6)
			{
				_general.broadcastPacket(new NpcSay(_general.getObjectId(), Say2.NPC_ALL, GENERAL_ID, NpcStringId.STABBING_THREE_TIMES));
				startQuestTimer("guard_animation_0", 3400, null, null);
			}
			else
			{
				value = -1;
				_general.broadcastPacket(new NpcSay(_general.getObjectId(), Say2.NPC_SHOUT, GENERAL_ID, DILIOS_TEXT[getRandom(DILIOS_TEXT.length)]));
			}
			startQuestTimer("command_" + (value + 1), 60000, null, null);
		}
		else if (event.startsWith("guard_animation_"))
		{
			int value = Integer.parseInt(event.substring(16));
			for (L2Spawn guard : _guards)
			{
				guard.getLastSpawn().broadcastSocialAction(4);
			}
			if (value < 2)
			{
				startQuestTimer("guard_animation_" + (value + 1), 1500, null, null);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == GENERAL_ID)
		{
			startQuestTimer("command_0", 60000, null, null);
			_general = npc;
		}
		else if (npc.getId() == GUARD_ID)
		{
			_guards.add(npc.getSpawn());
		}
		return super.onSpawn(npc);
	}
}
