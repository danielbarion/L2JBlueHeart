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
package ai.npc.Teleports.SteelCitadelTeleport;

import l2r.Config;
import l2r.gameserver.instancemanager.GrandBossManager;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.L2CommandChannel;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.zone.L2ZoneType;

import ai.npc.AbstractNpcAI;

/**
 * Steel Citadel teleport AI.
 * @author GKR
 */
public class SteelCitadelTeleport extends AbstractNpcAI
{
	// NPCs
	private static final int BELETH = 29118;
	private static final int NAIA_CUBE = 32376;
	
	public SteelCitadelTeleport()
	{
		super(SteelCitadelTeleport.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(NAIA_CUBE);
		addTalkId(NAIA_CUBE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final int belethStatus = GrandBossManager.getInstance().getBossStatus(BELETH);
		if (belethStatus == 3)
		{
			return "32376-02.htm";
		}
		
		if (belethStatus > 0)
		{
			return "32376-03.htm";
		}
		
		final L2CommandChannel channel = player.getParty() == null ? null : player.getParty().getCommandChannel();
		if ((channel == null) || (channel.getLeader().getObjectId() != player.getObjectId()) || (channel.getMemberCount() < Config.BELETH_MIN_PLAYERS))
		{
			return "32376-02a.htm";
		}
		
		final L2ZoneType zone = ZoneManager.getInstance().getZoneById(70053);
		if (zone != null)
		{
			GrandBossManager.getInstance().setBossStatus(BELETH, 1);
			
			for (L2Party party : channel.getPartys())
			{
				if (party == null)
				{
					continue;
				}
				
				for (L2PcInstance pl : party.getMembers())
				{
					if (pl.isInsideRadius(npc.getX(), npc.getY(), npc.getZ(), 3000, true, false))
					{
						pl.teleToLocation(16342, 209557, -9352, true);
					}
				}
			}
		}
		return null;
	}
}
