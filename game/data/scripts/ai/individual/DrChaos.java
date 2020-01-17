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
package ai.individual;

import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.SpecialCamera;

import ai.npc.AbstractNpcAI;

/**
 * DrChaos' AI.
 * @author Kerberos
 */
public final class DrChaos extends AbstractNpcAI
{
	private static final int DR_CHAOS = 32033;
	private static final int STRANGE_MACHINE = 32032;
	private static final int CHAOS_GOLEM = 25703;
	private static boolean _IsGolemSpawned;
	
	private static final Location PLAYER_TELEPORT = new Location(94832, -112624, -3304);
	private static final Location NPC_LOCATION = new Location(-113091, -243942, -15536);
	
	public DrChaos()
	{
		super(DrChaos.class.getSimpleName(), "ai/individual");
		addFirstTalkId(DR_CHAOS);
		_IsGolemSpawned = false;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "1":
			{
				L2Npc machine = null;
				for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(STRANGE_MACHINE))
				{
					if (spawn != null)
					{
						machine = spawn.getLastSpawn();
					}
				}
				if (machine != null)
				{
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, machine);
					machine.broadcastPacket(new SpecialCamera(machine, 1, -200, 15, 10000, 1000, 20000, 0, 0, 0, 0, 0));
				}
				else
				{
					startQuestTimer("2", 2000, npc, player);
				}
				startQuestTimer("3", 10000, npc, player);
				break;
			}
			case "2":
			{
				npc.broadcastSocialAction(3);
				break;
			}
			case "3":
			{
				npc.broadcastPacket(new SpecialCamera(npc, 1, -150, 10, 3000, 1000, 20000, 0, 0, 0, 0, 0));
				startQuestTimer("4", 2500, npc, player);
				break;
			}
			case "4":
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(96055, -110759, -3312, 0));
				startQuestTimer("5", 2000, npc, player);
				break;
			}
			case "5":
			{
				player.teleToLocation(PLAYER_TELEPORT);
				npc.teleToLocation(NPC_LOCATION);
				if (!_IsGolemSpawned)
				{
					L2Npc golem = addSpawn(CHAOS_GOLEM, 94640, -112496, -3336, 0, false, 0);
					_IsGolemSpawned = true;
					startQuestTimer("6", 1000, golem, player);
				}
				break;
			}
			case "6":
			{
				npc.broadcastPacket(new SpecialCamera(npc, 30, -200, 20, 6000, 700, 8000, 0, 0, 0, 0, 0));
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getId() == DR_CHAOS)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(96323, -110914, -3328, 0));
			startQuestTimer("1", 3000, npc, player);
		}
		return "";
	}
}
