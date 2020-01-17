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
package hellbound.Instances.DemonPrinceFloor;

import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Util;

import instances.AbstractInstance;

/**
 * Demon Prince Floor instance zone.
 * @author GKR
 */
public final class DemonPrinceFloor extends AbstractInstance
{
	protected class DPFWorld extends InstanceWorld
	{
	
	}
	
	// NPCs
	private static final int GK_4 = 32748;
	private static final int CUBE = 32375;
	private static final int DEMON_PRINCE = 25540;
	// Item
	private static final int SEAL_BREAKER_5 = 15515;
	// Locations
	private static final Location ENTRY_POINT = new Location(-22208, 277056, -8239);
	private static final Location EXIT_POINT = new Location(-19024, 277122, -8256);
	// Misc
	private static final int TEMPLATE_ID = 142;
	private static final int MIN_LV = 78;
	
	public DemonPrinceFloor()
	{
		super(DemonPrinceFloor.class.getSimpleName(), "hellbound/Instances");
		addStartNpc(GK_4, CUBE);
		addTalkId(GK_4, CUBE);
		addKillId(DEMON_PRINCE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (npc.getId() == GK_4)
		{
			if (!player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS))
			{
				if (player.getParty() == null)
				{
					htmltext = "gk-noparty.htm";
				}
				else if (!player.getParty().isLeader(player))
				{
					htmltext = "gk-noleader.htm";
				}
			}
			
			if (htmltext == null)
			{
				enterInstance(player, new DPFWorld(), "DemonPrince.xml", TEMPLATE_ID);
			}
		}
		else if (npc.getId() == CUBE)
		{
			final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (world instanceof DPFWorld)
			{
				world.removeAllowed(player.getObjectId());
				teleportPlayer(player, EXIT_POINT, 0);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final int instanceId = npc.getInstanceId();
		if (instanceId > 0)
		{
			final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
			final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			inst.setExitLoc(EXIT_POINT);
			
			finishInstance(world);
			addSpawn(CUBE, -22144, 278744, -8239, 0, false, 0, false, instanceId);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	protected boolean checkConditions(L2PcInstance player)
	{
		if (player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS))
		{
			return true;
		}
		
		final L2Party party = player.getParty();
		
		if ((party == null) || !party.isLeader(player))
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		
		for (L2PcInstance partyMember : party.getMembers())
		{
			if (partyMember.getLevel() < MIN_LV)
			{
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addPcName(partyMember));
				return false;
			}
			
			if (!Util.checkIfInRange(500, player, partyMember, true))
			{
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addPcName(partyMember));
				return false;
			}
			
			if (InstanceManager.getInstance().getPlayerWorld(player) != null)
			{
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON).addPcName(partyMember));
				return false;
			}
			
			final Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), TEMPLATE_ID);
			if (System.currentTimeMillis() < reentertime)
			{
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET).addPcName(partyMember));
				return false;
			}
			
			if (partyMember.getInventory().getInventoryItemCount(SEAL_BREAKER_5, -1, false) < 1)
			{
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addPcName(partyMember));
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			if (player.getParty() == null)
			{
				teleportPlayer(player, ENTRY_POINT, world.getInstanceId());
				player.destroyItemByItemId("Quest", SEAL_BREAKER_5, 1, null, true);
				world.addAllowed(player.getObjectId());
			}
			else
			{
				for (L2PcInstance partyMember : player.getParty().getMembers())
				{
					teleportPlayer(partyMember, ENTRY_POINT, world.getInstanceId());
					partyMember.destroyItemByItemId("Quest", SEAL_BREAKER_5, 1, null, true);
					world.addAllowed(partyMember.getObjectId());
				}
			}
		}
		else
		{
			teleportPlayer(player, ENTRY_POINT, world.getInstanceId());
		}
	}
}