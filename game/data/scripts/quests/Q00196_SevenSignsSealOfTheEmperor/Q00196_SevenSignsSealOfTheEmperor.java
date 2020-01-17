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
package quests.Q00196_SevenSignsSealOfTheEmperor;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;

import quests.Q00195_SevenSignsSecretRitualOfThePriests.Q00195_SevenSignsSecretRitualOfThePriests;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class Q00196_SevenSignsSealOfTheEmperor extends Quest
{
	// NPCs
	private static final int IASON_HEINE = 30969;
	private static final int MERCHANT_OF_MAMMON = 32584;
	private static final int SHUNAIMAN = 32586;
	private static final int WOOD = 32593;
	private static final int COURT_MAGICIAN = 32598;
	// Items
	private static final int ELMOREDEN_HOLY_WATER = 13808;
	private static final int COURT_MAGICIANS_MAGIC_STAFF = 13809;
	private static final int SEAL_OF_BINDING = 13846;
	private static final int SACRED_SWORD_OF_EINHASAD = 15310;
	// Misc
	private static final int MIN_LEVEL = 79;
	private static Map<Integer, L2Npc> spawns = new HashMap<>();
	
	public Q00196_SevenSignsSealOfTheEmperor()
	{
		super(196, Q00196_SevenSignsSealOfTheEmperor.class.getSimpleName(), "Seven Signs, Seal of the Emperor");
		addFirstTalkId(MERCHANT_OF_MAMMON);
		addStartNpc(IASON_HEINE);
		addTalkId(IASON_HEINE, MERCHANT_OF_MAMMON, SHUNAIMAN, WOOD, COURT_MAGICIAN);
		registerQuestItems(ELMOREDEN_HOLY_WATER, COURT_MAGICIANS_MAGIC_STAFF, SEAL_OF_BINDING, SACRED_SWORD_OF_EINHASAD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc.getId() == MERCHANT_OF_MAMMON) && "DESPAWN".equals(event))
		{
			final L2Npc merchant = spawns.get(player.getObjectId());
			if ((merchant != null) && (merchant.getObjectId() == npc.getObjectId()))
			{
				spawns.remove(player.getObjectId());
			}
			
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.THE_ANCIENT_PROMISE_TO_THE_EMPEROR_HAS_BEEN_FULFILLED));
			npc.deleteMe();
			return super.onAdvEvent(event, npc, player);
		}
		
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30969-02.htm":
			case "30969-03.htm":
			case "30969-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30969-05.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "ssq_mammon":
			{
				if (st.isCond(1))
				{
					final L2Npc monster = spawns.get(player.getObjectId());
					if ((monster != null) && !monster.isDead())
					{
						htmltext = "30969-07.html";
					}
					else
					{
						npc.setScriptValue(1);
						final L2Npc merchant = addSpawn(MERCHANT_OF_MAMMON, 109743, 219975, -3512, 0, false, 0, false);
						spawns.put(player.getObjectId(), merchant);
						merchant.broadcastPacket(new NpcSay(merchant.getObjectId(), Say2.NPC_ALL, merchant.getId(), NpcStringId.WHO_DARES_SUMMON_THE_MERCHANT_OF_MAMMON));
						htmltext = "30969-06.html";
						startQuestTimer("DESPAWN", 120000, merchant, player);
					}
				}
				break;
			}
			case "30969-13.html":
			{
				if (st.isCond(5))
				{
					htmltext = event;
				}
				break;
			}
			case "30969-14.html":
			{
				if (st.isCond(5))
				{
					st.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "32584-02.html":
			case "32584-03.html":
			case "32584-04.html":
			{
				if (st.isCond(1))
				{
					htmltext = event;
				}
				break;
			}
			case "32584-05.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					htmltext = event;
					cancelQuestTimers("DESPAWN");
					npc.deleteMe();
					final L2Npc merchant = spawns.get(player.getObjectId());
					if ((merchant != null) && (merchant.getObjectId() == npc.getObjectId()))
					{
						spawns.remove(player.getObjectId());
					}
				}
				break;
			}
			case "32586-02.html":
			case "32586-03.html":
			case "32586-04.html":
			case "32586-06.html":
			{
				if (st.isCond(3))
				{
					htmltext = event;
				}
				break;
			}
			case "32586-07.html":
			{
				if (st.isCond(3))
				{
					giveItems(player, ELMOREDEN_HOLY_WATER, 1);
					giveItems(player, SACRED_SWORD_OF_EINHASAD, 1);
					st.setCond(4, true);
					player.sendPacket(SystemMessageId.BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS);
					player.sendPacket(SystemMessageId.USING_EINHASAD_HOLY_WATER_TO_OPEN_DOOR);
					htmltext = event;
				}
				break;
			}
			case "32586-11.html":
			case "32586-12.html":
			case "32586-13.html":
			{
				if (st.isCond(4) && (getQuestItemsCount(player, SEAL_OF_BINDING) >= 4))
				{
					htmltext = event;
				}
				break;
			}
			case "32586-14.html":
			{
				if (st.isCond(4) && (getQuestItemsCount(player, SEAL_OF_BINDING) >= 4))
				{
					takeItems(player, -1, ELMOREDEN_HOLY_WATER, COURT_MAGICIANS_MAGIC_STAFF, SEAL_OF_BINDING, SACRED_SWORD_OF_EINHASAD);
					st.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "finish":
			{
				if (st.isCond(6))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						st.addExpAndSp(25000000, 2500000);
						st.exitQuest(false, true);
						htmltext = "32593-02.html";
					}
					else
					{
						htmltext = "level_check.html";
					}
				}
				break;
			}
			case "32598-02.html":
			{
				if (st.isCond(3) || st.isCond(4))
				{
					giveItems(player, COURT_MAGICIANS_MAGIC_STAFF, 1);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "32584.htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
			case State.CREATED:
			{
				if (npc.getId() == IASON_HEINE)
				{
					st = player.getQuestState(Q00195_SevenSignsSecretRitualOfThePriests.class.getSimpleName());
					htmltext = ((player.getLevel() >= MIN_LEVEL) && (st != null) && (st.isCompleted())) ? "30969-01.htm" : "30969-08.html";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case IASON_HEINE:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "30969-09.html";
								break;
							}
							case 2:
							{
								st.setCond(3, true);
								npc.setScriptValue(0);
								htmltext = "30969-10.html";
								break;
							}
							case 3:
							case 4:
							{
								htmltext = "30969-11.html";
								break;
							}
							case 5:
							{
								htmltext = "30969-12.html";
								break;
							}
							case 6:
							{
								htmltext = "30969-15.html";
								break;
							}
						}
						break;
					}
					case MERCHANT_OF_MAMMON:
					{
						if (st.isCond(1))
						{
							if (npc.isScriptValue(0))
							{
								npc.setScriptValue(player.getObjectId());
							}
							htmltext = (npc.isScriptValue(player.getObjectId())) ? "32584-01.html" : "32584-06.html";
						}
						break;
					}
					case SHUNAIMAN:
					{
						switch (st.getCond())
						{
							case 3:
							{
								htmltext = "32586-01.html";
								break;
							}
							case 4:
							{
								if (getQuestItemsCount(player, SEAL_OF_BINDING) < 4)
								{
									if (hasQuestItems(player, ELMOREDEN_HOLY_WATER, SACRED_SWORD_OF_EINHASAD))
									{
										htmltext = "32586-08.html";
									}
									else if (!hasQuestItems(player, ELMOREDEN_HOLY_WATER) && hasQuestItems(player, SACRED_SWORD_OF_EINHASAD))
									{
										htmltext = "32586-09.html";
										giveItems(player, ELMOREDEN_HOLY_WATER, 1);
									}
									else if (hasQuestItems(player, ELMOREDEN_HOLY_WATER) && !hasQuestItems(player, SACRED_SWORD_OF_EINHASAD))
									{
										htmltext = "32586-09.html";
										giveItems(player, SACRED_SWORD_OF_EINHASAD, 1);
									}
									player.sendPacket(SystemMessageId.BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS);
									player.sendPacket(SystemMessageId.USING_EINHASAD_HOLY_WATER_TO_OPEN_DOOR);
								}
								else
								{
									htmltext = "32586-10.html";
								}
								break;
							}
							case 5:
							{
								htmltext = "32586-15.html";
								break;
							}
						}
						break;
					}
					case WOOD:
					{
						if (st.isCond(6))
						{
							htmltext = "32593-01.html";
						}
						break;
					}
					case COURT_MAGICIAN:
					{
						if (st.isCond(3) || st.isCond(4))
						{
							htmltext = (!hasQuestItems(player, COURT_MAGICIANS_MAGIC_STAFF)) ? "32598-01.html" : "32598-03.html";
							player.sendPacket(SystemMessageId.USING_COURT_MAGICIANS_STAFF_TO_OPEN_DOOR);
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
