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
package ai.npc.Teleports.OracleTeleport;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Oracle teleport AI.
 * @author Charus
 */
public final class OracleTeleport extends AbstractNpcAI
{
	// @formatter:off
	private final static int[] TOWN_DAWN =
	{
		31078, 31079, 31080, 31081, 31083, 31084, 31082, 31692, 31694, 31997, 31168
	};
	
	private final static int[] TOWN_DUSK =
	{
		31085, 31086, 31087, 31088, 31090, 31091, 31089, 31693, 31695, 31998, 31169
	};
	
	private final static int[] TEMPLE_PRIEST =
	{
		31127, 31128, 31129, 31130, 31131, 31137, 31138, 31139, 31140, 31141
	};
	
	private final static int[] RIFT_POSTERS =
	{
		31488, 31489, 31490, 31491, 31492, 31493
	};
	
	private final static int[] TELEPORTERS =
	{
		31078, 31079, 31080, 31081, 31082, 31083, 31084, 31692, 31694, 31997,
		31168, 31085, 31086, 31087, 31088, 31089, 31090, 31091, 31693, 31695,
		31998, 31169, 31494, 31495, 31496, 31497, 31498, 31499, 31500, 31501,
		31502, 31503, 31504, 31505, 31506, 31507, 31095, 31096, 31097, 31098,
		31099, 31100, 31101, 31102, 31103, 31104, 31105, 31106, 31107, 31108,
		31109, 31110, 31114, 31115, 31116, 31117, 31118, 31119, 31120, 31121,
		31122, 31123, 31124, 31125
	};
	// @formatter:on
	private final static Location[] RETURN_LOCS =
	{
		new Location(-80555, 150337, -3040),
		new Location(-13953, 121404, -2984),
		new Location(16354, 142820, -2696),
		new Location(83369, 149253, -3400),
		new Location(111386, 220858, -3544),
		new Location(83106, 53965, -1488),
		new Location(146983, 26595, -2200),
		new Location(148256, -55454, -2779),
		new Location(45664, -50318, -800),
		new Location(86795, -143078, -1341),
		new Location(115136, 74717, -2608),
		new Location(-82368, 151568, -3120),
		new Location(-14748, 123995, -3112),
		new Location(18482, 144576, -3056),
		new Location(81623, 148556, -3464),
		new Location(112486, 220123, -3592),
		new Location(82819, 54607, -1520),
		new Location(147570, 28877, -2264),
		new Location(149888, -56574, -2979),
		new Location(44528, -48370, -800),
		new Location(85129, -142103, -1542),
		new Location(116642, 77510, -2688),
		new Location(-41572, 209731, -5087),
		new Location(-52872, -250283, -7908),
		new Location(45256, 123906, -5411),
		new Location(46192, 170290, -4981),
		new Location(111273, 174015, -5437),
		new Location(-20604, -250789, -8165),
		new Location(-21726, 77385, -5171),
		new Location(140405, 79679, -5427),
		new Location(-52366, 79097, -4741),
		new Location(118311, 132797, -4829),
		new Location(172185, -17602, -4901),
		new Location(83000, 209213, -5439),
		new Location(-19500, 13508, -4901),
		new Location(12525, -248496, -9580),
		new Location(-41561, 209225, -5087),
		new Location(45242, 124466, -5413),
		new Location(110711, 174010, -5439),
		new Location(-22341, 77375, -5173),
		new Location(-52889, 79098, -4741),
		new Location(117760, 132794, -4831),
		new Location(171792, -17609, -4901),
		new Location(82564, 209207, -5439),
		new Location(-41565, 210048, -5085),
		new Location(45278, 123608, -5411),
		new Location(111510, 174013, -5437),
		new Location(-21489, 77372, -5171),
		new Location(-52016, 79103, -4739),
		new Location(118557, 132804, -4829),
		new Location(172570, -17605, -4899),
		new Location(83347, 209215, -5437),
		new Location(42495, 143944, -5381),
		new Location(45666, 170300, -4981),
		new Location(77138, 78389, -5125),
		new Location(139903, 79674, -5429),
		new Location(-20021, 13499, -4901),
		new Location(113418, 84535, -6541),
		new Location(-52940, -250272, -7907),
		new Location(46499, 170301, -4979),
		new Location(-20280, -250785, -8163),
		new Location(140673, 79680, -5437),
		new Location(-19182, 13503, -4899),
		new Location(12837, -248483, -9579)
	};
	
	// Item
	private static final int DIMENSIONAL_FRAGMENT = 7079;
	
	public OracleTeleport()
	{
		super(OracleTeleport.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(RIFT_POSTERS);
		addStartNpc(TELEPORTERS);
		addStartNpc(TEMPLE_PRIEST);
		addStartNpc(TOWN_DAWN);
		addStartNpc(TOWN_DUSK);
		addTalkId(RIFT_POSTERS);
		addTalkId(TELEPORTERS);
		addTalkId(TEMPLE_PRIEST);
		addTalkId(TOWN_DAWN);
		addTalkId(TOWN_DUSK);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		if (event.equalsIgnoreCase("Return"))
		{
			if (Util.contains(TEMPLE_PRIEST, npcId) && (st.getState() == State.STARTED))
			{
				player.teleToLocation(RETURN_LOCS[st.getInt("id")]);
				player.setIsIn7sDungeon(false);
				st.exitQuest(true);
			}
			else if (Util.contains(RIFT_POSTERS, npcId) && (st.getState() == State.STARTED))
			{
				player.teleToLocation(RETURN_LOCS[st.getInt("id")]);
				htmltext = "rift_back.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("Festival"))
		{
			int id = st.getInt("id");
			if (Util.contains(TOWN_DAWN, id))
			{
				player.teleToLocation(new Location(-80157, 111344, -4901));
				player.setIsIn7sDungeon(true);
			}
			else if (Util.contains(TOWN_DUSK, id))
			{
				player.teleToLocation(new Location(-81261, 86531, -5157));
				player.setIsIn7sDungeon(true);
			}
			else
			{
				htmltext = "oracle1.htm";
			}
		}
		else if (event.equalsIgnoreCase("Dimensional"))
		{
			htmltext = "oracle.htm";
			player.teleToLocation(new Location(-114755, -179466, -6752));
		}
		else if (event.equalsIgnoreCase("5.htm"))
		{
			int id = st.getInt("id");
			if (id > -1)
			{
				htmltext = "5a.htm";
			}
			int i = 0;
			for (int id1 : TELEPORTERS)
			{
				if (id1 == npcId)
				{
					break;
				}
				i++;
			}
			st.set("id", Integer.toString(i));
			st.setState(State.STARTED);
			player.teleToLocation(new Location(-114755, -179466, -6752));
		}
		else if (event.equalsIgnoreCase("6.htm"))
		{
			htmltext = "6.htm";
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("zigurratDimensional"))
		{
			int playerLevel = player.getLevel();
			if ((playerLevel >= 20) && (playerLevel < 30))
			{
				takeItems(player, Inventory.ADENA_ID, 2000);
			}
			else if ((playerLevel >= 30) && (playerLevel < 40))
			{
				takeItems(player, Inventory.ADENA_ID, 4500);
			}
			else if ((playerLevel >= 40) && (playerLevel < 50))
			{
				takeItems(player, Inventory.ADENA_ID, 8000);
			}
			else if ((playerLevel >= 50) && (playerLevel < 60))
			{
				takeItems(player, Inventory.ADENA_ID, 12500);
			}
			else if ((playerLevel >= 60) && (playerLevel < 70))
			{
				takeItems(player, Inventory.ADENA_ID, 18000);
			}
			else if (playerLevel >= 70)
			{
				takeItems(player, Inventory.ADENA_ID, 24500);
			}
			int i = 0;
			for (int ziggurat : TELEPORTERS)
			{
				if (ziggurat == npcId)
				{
					break;
				}
				i++;
			}
			st.set("id", Integer.toString(i));
			st.setState(State.STARTED);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
			htmltext = "ziggurat_rift.htm";
			player.teleToLocation(new Location(-114755, -179466, -6752));
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = getQuestState(player, true);
		
		int npcId = npc.getId();
		if (Util.contains(TOWN_DAWN, npcId))
		{
			st.setState(State.STARTED);
			int i = 0;
			for (int dawn : TELEPORTERS)
			{
				if (dawn == npcId)
				{
					break;
				}
				i++;
			}
			st.set("id", Integer.toString(i));
			playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
			player.teleToLocation(new Location(-80157, 111344, -4901));
			player.setIsIn7sDungeon(true);
		}
		if (Util.contains(TOWN_DUSK, npcId))
		{
			st.setState(State.STARTED);
			int i = 0;
			for (int dusk : TELEPORTERS)
			{
				if (dusk == npcId)
				{
					break;
				}
				i++;
			}
			st.set("id", Integer.toString(i));
			playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
			player.teleToLocation(new Location(-81261, 86531, -5157));
			player.setIsIn7sDungeon(true);
		}
		else if ((npcId >= 31494) && (npcId <= 31507))
		{
			if (player.getLevel() < 20)
			{
				htmltext = "1.htm";
				st.exitQuest(true);
			}
			else if (player.getAllActiveQuests().size() > 23)
			{
				htmltext = "1a.htm";
				st.exitQuest(true);
			}
			else if (!hasQuestItems(player, DIMENSIONAL_FRAGMENT))
			{
				htmltext = "3.htm";
			}
			else
			{
				st.setState(State.CREATED);
				htmltext = "4.htm";
			}
		}
		else if (((npcId >= 31095) && (npcId <= 31111)) || ((npcId >= 31114) && (npcId <= 31126)))
		{
			int playerLevel = player.getLevel();
			if (playerLevel < 20)
			{
				htmltext = "ziggurat_lowlevel.htm";
				st.exitQuest(true);
			}
			else if (player.getAllActiveQuests().size() > 40)
			{
				player.sendPacket(SystemMessageId.TOO_MANY_QUESTS);
				st.exitQuest(true);
			}
			else if (!hasQuestItems(player, DIMENSIONAL_FRAGMENT))
			{
				htmltext = "ziggurat_nofrag.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 20) && (playerLevel < 30) && (player.getAdena() < 2000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 30) && (playerLevel < 40) && (player.getAdena() < 4500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 40) && (playerLevel < 50) && (player.getAdena() < 8000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 50) && (playerLevel < 60) && (player.getAdena() < 12500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 60) && (playerLevel < 70) && (player.getAdena() < 18000))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else if ((playerLevel >= 70) && (player.getAdena() < 24500))
			{
				htmltext = "ziggurat_noadena.htm";
				st.exitQuest(true);
			}
			else
			{
				htmltext = "ziggurat.htm";
			}
		}
		return htmltext;
	}
}
