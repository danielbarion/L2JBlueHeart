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
package custom.RaidbossInfo;

import java.util.ArrayList;
import java.util.List;

import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Raidboss Info AI.<br>
 * Original Jython script Kerberos.
 * @author Nyaran
 */
public final class RaidbossInfo extends AbstractNpcAI
{
	// @formatter:off
	private static final int[] NPC =
	{
		31729, 31730, 31731, 31732, 31733, 31734, 31735, 31736, 31737, 31738,
		31739, 31740, 31741, 31742, 31743, 31744, 31745, 31746, 31747, 31748,
		31749, 31750, 31751, 31752, 31753, 31754, 31755, 31756, 31757, 31758,
		31759, 31760, 31761, 31762, 31763, 31764, 31765, 31766, 31767, 31768,
		31769, 31770, 31771, 31772, 31773, 31774, 31775, 31776, 31777, 31778,
		31779, 31780, 31781, 31782, 31783, 31784, 31785, 31786, 31787, 31788,
		31789, 31790, 31791, 31792, 31793, 31794, 31795, 31796, 31797, 31798,
		31799, 31800, 31801, 31802, 31803, 31804, 31805, 31806, 31807, 31808,
		31809, 31810, 31811, 31812, 31813, 31814, 31815, 31816, 31817, 31818,
		31819, 31820, 31821, 31822, 31823, 31824, 31825, 31826, 31827, 31828,
		31829, 31830, 31831, 31832, 31833, 31834, 31835, 31836, 31837, 31838,
		31839, 31840, 31841, 32337, 32338, 32339, 32340
	};
	// @formatter:on
	private static final List<Integer> RAIDS = new ArrayList<>();
	
	public RaidbossInfo()
	{
		super(RaidbossInfo.class.getSimpleName(), "custom");
		addStartNpc(NPC);
		addTalkId(NPC);
		
		// Add all Raid Bosses to RAIDS list
		for (L2NpcTemplate raid : NpcTable.getInstance().getAllNpcOfClassType("L2RaidBoss"))
		{
			RAIDS.add(raid.getId());
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		if (Util.isDigit(event))
		{
			htmltext = null;
			int bossId = Integer.parseInt(event);
			
			if (RAIDS.contains(bossId))
			{
				final L2Spawn spawn = SpawnTable.getInstance().findAny(bossId);
				if (spawn != null)
				{
					final Location loc = spawn.getLocation();
					player.getRadar().addMarker(loc.getX(), loc.getY(), loc.getZ());
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		return "info.htm";
	}
}