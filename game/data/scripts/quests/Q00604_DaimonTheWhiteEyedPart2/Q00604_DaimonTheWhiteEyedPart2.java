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
package quests.Q00604_DaimonTheWhiteEyedPart2;

import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.util.Util;

/**
 * Daimon the White-Eyed - Part 2 (604)
 * @author Adry_85
 */
public final class Q00604_DaimonTheWhiteEyedPart2 extends Quest
{
	// NPCs
	private static final int DAIMONS_ALTAR = 31541;
	private static final int EYE_OF_ARGOS = 31683;
	// Raid Boss
	private static final int DAIMON_THE_WHITE_EYED = 25290;
	// Items
	private static final int UNFINISHED_SUMMON_CRYSTAL = 7192;
	private static final int SUMMON_CRYSTAL = 7193;
	private static final int ESSENCE_OF_DAIMON = 7194;
	// Misc
	private static final int MIN_LEVEL = 73;
	// Location
	private static final Location DAIMON_THE_WHITE_EYED_LOC = new Location(186320, -43904, -3175);
	// Rewards
	private static final int DYE_I2M2_C = 4595; // Greater Dye of INT <Int+2 Men-2>
	private static final int DYE_I2W2_C = 4596; // Greater Dye of INT <Int+2 Wit-2>
	private static final int DYE_M2I2_C = 4597; // Greater Dye of MEN <Men+2 Int-2>
	private static final int DYE_M2W2_C = 4598; // Greater Dye of MEN <Men+2 Wit-2>
	private static final int DYE_W2I2_C = 4599; // Greater Dye of WIT <Wit+2 Int-2>
	private static final int DYE_W2M2_C = 4600; // Greater Dye of WIT <Wit+2 Men-2>
	
	public Q00604_DaimonTheWhiteEyedPart2()
	{
		super(604, Q00604_DaimonTheWhiteEyedPart2.class.getSimpleName(), "Daimon the White-Eyed - Part 2");
		addStartNpc(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS, DAIMONS_ALTAR);
		addSpawnId(DAIMON_THE_WHITE_EYED);
		addKillId(DAIMON_THE_WHITE_EYED);
		registerQuestItems(SUMMON_CRYSTAL, ESSENCE_OF_DAIMON);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.getMemoState() >= 11) && (qs.getMemoState() <= 21))
		{
			if (Util.checkIfInRange(1500, npc, player, false))
			{
				if (hasQuestItems(player, ESSENCE_OF_DAIMON))
				{
					qs.setCond(3, true);
					qs.setMemoState(22);
				}
				
				giveItems(player, ESSENCE_OF_DAIMON, 1);
				qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ("DESPAWN".equals(event))
		{
			if (isDaimonSpawned())
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getTemplate().getIdTemplate(), NpcStringId.CAN_LIGHT_EXIST_WITHOUT_DARKNESS));
				npc.deleteMe();
			}
			return super.onAdvEvent(event, npc, player);
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31683-04.htm":
			{
				takeItems(player, UNFINISHED_SUMMON_CRYSTAL, 1);
				qs.startQuest();
				qs.setMemoState(11);
				giveItems(player, SUMMON_CRYSTAL, 1);
				htmltext = event;
				break;
			}
			case "31683-07.html":
			{
				if (hasQuestItems(player, ESSENCE_OF_DAIMON))
				{
					final int reward;
					final int random = getRandom(1000);
					takeItems(player, ESSENCE_OF_DAIMON, 1);
					if (random < 167)
					{
						reward = DYE_I2M2_C;
					}
					else if (random < 334)
					{
						reward = DYE_I2W2_C;
					}
					else if (random < 501)
					{
						reward = DYE_M2I2_C;
					}
					else if (random < 668)
					{
						reward = DYE_M2W2_C;
					}
					else if (random < 835)
					{
						reward = DYE_W2I2_C;
					}
					else
					{
						reward = DYE_W2M2_C;
					}
					
					rewardItems(player, reward, 5);
					qs.exitQuest(true, true);
					htmltext = event;
				}
				else
				{
					htmltext = "31683-08.html";
				}
				break;
			}
			case "31541-02.html":
			{
				if (hasQuestItems(player, SUMMON_CRYSTAL))
				{
					if (!isDaimonSpawned())
					{
						takeItems(player, SUMMON_CRYSTAL, 1);
						htmltext = event;
						addSpawn(DAIMON_THE_WHITE_EYED, DAIMON_THE_WHITE_EYED_LOC);
						npc.deleteMe();
						qs.setMemoState(21);
						qs.setCond(2, true);
					}
					else
					{
						htmltext = "31541-03.html";
					}
				}
				else
				{
					htmltext = "31541-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("DESPAWN", 1200000, npc, null);
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getTemplate().getIdTemplate(), NpcStringId.WHO_IS_CALLING_ME));
		return super.onSpawn(npc);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				htmltext = "31683-01.htm";
			}
			else if (!hasQuestItems(player, UNFINISHED_SUMMON_CRYSTAL))
			{
				htmltext = "31683-02.htm";
			}
			else
			{
				htmltext = "31683-03.htm";
			}
		}
		else if (qs.isStarted())
		{
			if (npc.getId() == EYE_OF_ARGOS)
			{
				if (qs.isMemoState(11))
				{
					htmltext = "31683-05.html";
				}
				else if (qs.getMemoState() >= 22)
				{
					htmltext = (hasQuestItems(player, ESSENCE_OF_DAIMON)) ? "31683-06.html" : "31683-09.html";
				}
			}
			else
			{
				if (qs.isMemoState(11))
				{
					if (hasQuestItems(player, SUMMON_CRYSTAL))
					{
						htmltext = "31541-01.html";
					}
				}
				else if (qs.isMemoState(21))
				{
					if (!isDaimonSpawned())
					{
						addSpawn(DAIMON_THE_WHITE_EYED, DAIMON_THE_WHITE_EYED_LOC);
						npc.deleteMe();
						htmltext = "31541-02.html";
					}
					else
					{
						htmltext = "31541-03.html";
					}
				}
				else if (qs.getMemoState() >= 22)
				{
					htmltext = "31541-05.html";
				}
			}
		}
		return htmltext;
	}
	
	private static boolean isDaimonSpawned()
	{
		return SpawnTable.getInstance().findAny(DAIMON_THE_WHITE_EYED) != null;
	}
}
