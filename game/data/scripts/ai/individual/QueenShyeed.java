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
package ai.individual;

import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.zone.type.L2EffectZone;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;

import ai.npc.AbstractNpcAI;

/**
 * Queen Shyeed AI
 * @author malyelfik
 */
public class QueenShyeed extends AbstractNpcAI
{
	// NPC
	private static final int SHYEED = 25671;
	private static final Location SHYEED_LOC = new Location(79634, -55428, -6104, 0);
	
	// Respawn
	private static final int RESPAWN = 86400000; // 24 h
	private static final int RANDOM_RESPAWN = 43200000; // 12 h
	
	// Zones
	private static final L2EffectZone MOB_BUFF_ZONE = ZoneManager.getInstance().getZoneById(200103, L2EffectZone.class);
	private static final L2EffectZone MOB_BUFF_DISPLAY_ZONE = ZoneManager.getInstance().getZoneById(200104, L2EffectZone.class);
	private static final L2EffectZone PC_BUFF_ZONE = ZoneManager.getInstance().getZoneById(200105, L2EffectZone.class);
	
	public QueenShyeed()
	{
		super(QueenShyeed.class.getSimpleName(), "ai");
		addKillId(SHYEED);
		spawnShyeed();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "respawn":
				spawnShyeed();
				break;
			case "despawn":
				if (!npc.isDead())
				{
					npc.deleteMe();
					startRespawn();
				}
				break;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.SHYEEDS_CRY_IS_STEADILY_DYING_DOWN);
		startRespawn();
		PC_BUFF_ZONE.setEnabled(true);
		return super.onKill(npc, killer, isSummon);
	}
	
	private void spawnShyeed()
	{
		String respawn = loadGlobalQuestVar("Respawn");
		long remain = (!respawn.isEmpty()) ? Long.parseLong(respawn) - System.currentTimeMillis() : 0;
		if (remain > 0)
		{
			startQuestTimer("respawn", remain, null, null);
			PC_BUFF_ZONE.setEnabled(true);
			return;
		}
		
		final L2Npc npc = addSpawn(SHYEED, SHYEED_LOC, false, 0);
		startQuestTimer("despawn", 10800000, npc, null);
		MOB_BUFF_ZONE.setEnabled(true);
		MOB_BUFF_DISPLAY_ZONE.setEnabled(true);
	}
	
	private void startRespawn()
	{
		int respawnTime = RESPAWN - getRandom(RANDOM_RESPAWN);
		saveGlobalQuestVar("Respawn", Long.toString(System.currentTimeMillis() + respawnTime));
		startQuestTimer("respawn", respawnTime, null, null);
		MOB_BUFF_ZONE.setEnabled(false);
		MOB_BUFF_DISPLAY_ZONE.setEnabled(false);
	}
}