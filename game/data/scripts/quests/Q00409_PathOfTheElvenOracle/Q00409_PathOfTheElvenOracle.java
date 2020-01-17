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
package quests.Q00409_PathOfTheElvenOracle;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

/**
 * Path of the Elven Oracle (409)
 * @author ivantotov
 */
public final class Q00409_PathOfTheElvenOracle extends Quest
{
	// NPCs
	private static final int PRIEST_MANUEL = 30293;
	private static final int ALLANA = 30424;
	private static final int PERRIN = 30428;
	// Items
	private static final int CRYSTAL_MEDALLION = 1231;
	private static final int SWINDLERS_MONEY = 1232;
	private static final int ALLANA_OF_DAIRY = 1233;
	private static final int LIZARD_CAPTAIN_ORDER = 1234;
	private static final int HALF_OF_DAIRY = 1236;
	private static final int TAMIL_NECKLACE = 1275;
	// Reward
	private static final int LEAF_OF_ORACLE = 1235;
	// Misc
	private static final int MIN_LEVEL = 18;
	// Quest Monster
	private static final int lIZARDMAN_WARRIOR = 27032;
	private static final int LIZARDMAN_SCOUT = 27033;
	private static final int LIZARDMAN_SOLDIER = 27034;
	private static final int TAMIL = 27035;
	
	public Q00409_PathOfTheElvenOracle()
	{
		super(409, Q00409_PathOfTheElvenOracle.class.getSimpleName(), "Path of the Elven Oracle");
		addStartNpc(PRIEST_MANUEL);
		addTalkId(PRIEST_MANUEL, ALLANA, PERRIN);
		addKillId(TAMIL, lIZARDMAN_WARRIOR, LIZARDMAN_SCOUT, LIZARDMAN_SOLDIER);
		addAttackId(TAMIL, lIZARDMAN_WARRIOR, LIZARDMAN_SCOUT, LIZARDMAN_SOLDIER);
		registerQuestItems(CRYSTAL_MEDALLION, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY, TAMIL_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.elvenMage)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, LEAF_OF_ORACLE))
						{
							htmltext = "30293-04.htm";
						}
						else
						{
							qs.startQuest();
							qs.setMemoState(1);
							giveItems(player, CRYSTAL_MEDALLION, 1);
							htmltext = "30293-05.htm";
						}
					}
					else
					{
						htmltext = "30293-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.oracle)
				{
					htmltext = "30293-02a.htm";
				}
				else
				{
					htmltext = "30293-02.htm";
				}
				break;
			}
			case "30424-08.html":
			case "30424-09.html":
			{
				htmltext = event;
				break;
			}
			case "30424-07.html":
			{
				if (qs.isMemoState(1))
				{
					htmltext = event;
				}
				break;
			}
			case "replay_1":
			{
				qs.setMemoState(2);
				final L2Attackable monster1 = (L2Attackable) addSpawn(lIZARDMAN_WARRIOR, npc, true, 0, false);
				final L2Attackable monster2 = (L2Attackable) addSpawn(LIZARDMAN_SCOUT, npc, true, 0, false);
				final L2Attackable monster3 = (L2Attackable) addSpawn(LIZARDMAN_SOLDIER, npc, true, 0, false);
				attackPlayer(monster1, player);
				attackPlayer(monster2, player);
				attackPlayer(monster3, player);
				break;
			}
			case "30428-02.html":
			case "30428-03.html":
			{
				if (qs.isMemoState(2))
				{
					htmltext = event;
				}
				break;
			}
			case "replay_2":
			{
				if (qs.isMemoState(2))
				{
					qs.setMemoState(3);
					final L2Attackable monster = (L2Attackable) addSpawn(TAMIL, npc, true, 0, true);
					attackPlayer(monster, player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getQuestState(attacker, false) != null)
		{
			switch (npc.getScriptValue())
			{
				case 0:
				{
					switch (npc.getId())
					{
						case lIZARDMAN_WARRIOR:
						{
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case LIZARDMAN_SCOUT:
						{
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case LIZARDMAN_SOLDIER:
						{
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.THE_SACRED_FLAME_IS_OURS));
							break;
						}
						case TAMIL:
						{
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.AS_YOU_WISH_MASTER));
							break;
						}
					}
					
					npc.setScriptValue(1);
					npc.getVariables().set("firstAttacker", attacker.getObjectId());
					break;
				}
				case 1:
				{
					if (npc.getVariables().getInt("firstAttacker") != attacker.getObjectId())
					{
						npc.setScriptValue(2);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && npc.isScriptValue(1) && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case lIZARDMAN_WARRIOR:
				{
					if (!hasQuestItems(killer, LIZARD_CAPTAIN_ORDER))
					{
						npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.ARRGHHWE_SHALL_NEVER_SURRENDER));
						giveItems(killer, LIZARD_CAPTAIN_ORDER, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case LIZARDMAN_SCOUT:
				case LIZARDMAN_SOLDIER:
				{
					if (!hasQuestItems(killer, LIZARD_CAPTAIN_ORDER))
					{
						giveItems(killer, LIZARD_CAPTAIN_ORDER, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case TAMIL:
				{
					if (!hasQuestItems(killer, TAMIL_NECKLACE))
					{
						giveItems(killer, TAMIL_NECKLACE, 1);
						qs.setCond(5, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated() || qs.isCompleted())
		{
			if (npc.getId() == PRIEST_MANUEL)
			{
				if (!hasQuestItems(player, LEAF_OF_ORACLE))
				{
					htmltext = "30293-01.htm";
				}
				else
				{
					htmltext = "30293-04.htm";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PRIEST_MANUEL:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION))
					{
						if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if (qs.isMemoState(2))
							{
								qs.setMemoState(1);
								qs.setCond(8);
								htmltext = "30293-09.html";
							}
							else
							{
								qs.setMemoState(1);
								htmltext = "30293-06.html";
							}
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER))
						{
							if (!hasQuestItems(player, HALF_OF_DAIRY))
							{
								giveAdena(player, 163800, true);
								giveItems(player, LEAF_OF_ORACLE, 1);
								final int level = player.getLevel();
								if (level >= 20)
								{
									addExpAndSp(player, 320534, 20392);
								}
								else if (level == 19)
								{
									addExpAndSp(player, 456128, 27090);
								}
								else
								{
									addExpAndSp(player, 591724, 33788);
								}
								qs.exitQuest(false, true);
								player.sendPacket(new SocialAction(player.getObjectId(), 3));
								qs.saveGlobalQuestVar("1ClassQuestFinished", "1");
								htmltext = "30293-08.html";
							}
						}
						else
						{
							htmltext = "30293-07.html";
						}
					}
					break;
				}
				case ALLANA:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION))
					{
						if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if (qs.isMemoState(2))
							{
								htmltext = "30424-05.html";
							}
							else if (qs.isMemoState(1))
							{
								qs.setCond(2, true);
								htmltext = "30424-01.html";
							}
						}
						else if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY, HALF_OF_DAIRY) && hasQuestItems(player, LIZARD_CAPTAIN_ORDER))
						{
							qs.setMemoState(2);
							giveItems(player, HALF_OF_DAIRY, 1);
							qs.setCond(4, true);
							htmltext = "30424-02.html";
						}
						else if (!hasAtLeastOneQuestItem(player, SWINDLERS_MONEY, ALLANA_OF_DAIRY) && hasQuestItems(player, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
						{
							if ((qs.isMemoState(3)) && !hasQuestItems(player, TAMIL_NECKLACE))
							{
								qs.setMemoState(2);
								qs.setCond(4, true);
								htmltext = "30424-06.html";
							}
							else
							{
								htmltext = "30424-03.html";
							}
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY) && !hasQuestItems(player, ALLANA_OF_DAIRY))
						{
							giveItems(player, ALLANA_OF_DAIRY, 1);
							takeItems(player, HALF_OF_DAIRY, 1);
							qs.setCond(9, true);
							htmltext = "30424-04.html";
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY, LIZARD_CAPTAIN_ORDER, ALLANA_OF_DAIRY))
						{
							qs.setCond(7, true);
							htmltext = "30424-05.html";
						}
					}
					break;
				}
				case PERRIN:
				{
					if (hasQuestItems(player, CRYSTAL_MEDALLION, LIZARD_CAPTAIN_ORDER, HALF_OF_DAIRY))
					{
						if (hasQuestItems(player, TAMIL_NECKLACE))
						{
							giveItems(player, SWINDLERS_MONEY, 1);
							takeItems(player, TAMIL_NECKLACE, 1);
							qs.setCond(6, true);
							htmltext = "30428-04.html";
						}
						else if (hasQuestItems(player, SWINDLERS_MONEY))
						{
							htmltext = "30428-05.html";
						}
						else if (qs.isMemoState(3))
						{
							htmltext = "30428-06.html";
						}
						else
						{
							htmltext = "30428-01.html";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	private static void attackPlayer(L2Attackable npc, L2PcInstance player)
	{
		if ((npc != null) && (player != null))
		{
			npc.setIsRunning(true);
			npc.addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
	}
}