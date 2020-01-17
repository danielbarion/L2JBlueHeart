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
package gracia.AI.NPC.Nottingale;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.instancemanager.AirShipManager;
import l2r.gameserver.model.ClanPrivilege;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.serverpackets.RadarControl;

import ai.npc.AbstractNpcAI;
import quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;

/**
 * Nottingale AI
 * @author xban1x
 */
public final class Nottingale extends AbstractNpcAI
{
	// NPC
	private static final int NOTTINGALE = 32627;
	// Misc
	private static final Map<Integer, RadarControl> RADARS = new HashMap<>();
	
	static
	{
		RADARS.put(2, new RadarControl(0, -184545, 243120, 1581, 2));
		RADARS.put(5, new RadarControl(0, -192361, 254528, 3598, 1));
		RADARS.put(6, new RadarControl(0, -174600, 219711, 4424, 1));
		RADARS.put(7, new RadarControl(0, -181989, 208968, 4424, 1));
		RADARS.put(8, new RadarControl(0, -252898, 235845, 5343, 1));
		RADARS.put(9, new RadarControl(0, -212819, 209813, 4288, 1));
		RADARS.put(10, new RadarControl(0, -246899, 251918, 4352, 1));
	}
	
	public Nottingale()
	{
		super(Nottingale.class.getSimpleName(), "gracia/AI/NPC");
		addStartNpc(NOTTINGALE);
		addTalkId(NOTTINGALE);
		addFirstTalkId(NOTTINGALE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32627-02.html":
			case "32627-03.html":
			case "32627-04.html":
			{
				if (player.getClan() != null)
				{
					if (player.hasClanPrivilege(ClanPrivilege.CL_SUMMON_AIRSHIP) && AirShipManager.getInstance().hasAirShipLicense(player.getClanId()) && !AirShipManager.getInstance().hasAirShip(player.getClanId()))
					{
						htmltext = event;
					}
					else
					{
						final QuestState st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
						if ((st != null) && st.isCompleted())
						{
							htmltext = event;
						}
						else
						{
							player.sendPacket(RADARS.get(2));
							htmltext = "32627-01.html";
						}
					}
				}
				else
				{
					final QuestState st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
					if ((st != null) && st.isCompleted())
					{
						htmltext = event;
					}
					else
					{
						player.sendPacket(RADARS.get(2));
						htmltext = "32627-01.html";
					}
				}
				break;
			}
			case "32627-05.html":
			case "32627-06.html":
			case "32627-07.html":
			case "32627-08.html":
			case "32627-09.html":
			case "32627-10.html":
			{
				player.sendPacket(RADARS.get(Integer.valueOf(event.substring(6, 8))));
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
}
