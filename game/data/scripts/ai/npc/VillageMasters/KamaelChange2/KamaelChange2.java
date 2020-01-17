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
package ai.npc.VillageMasters.KamaelChange2;

import l2r.gameserver.data.xml.impl.CategoryData;
import l2r.gameserver.enums.CategoryType;
import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;
import quests.Q00064_CertifiedBerserker.Q00064_CertifiedBerserker;
import quests.Q00065_CertifiedSoulBreaker.Q00065_CertifiedSoulBreaker;
import quests.Q00066_CertifiedArbalester.Q00066_CertifiedArbalester;

/**
 * Kamael class transfer AI.
 * @author Adry_85
 */
public final class KamaelChange2 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS_MALE =
	{
		32146, // Valpor
		32205, // Aetonic
		32209, // Ferdinand
		32213, // Vitus
		32217, // Barta
		32221, // Brome
		32225, // Taine
		32229, // Hagel
		32233, // Zoldart
	};
	private static int[] NPCS_FEMALE =
	{
		32145, // Maynard
		32206, // Pieche
		32210, // Eddy
		32214, // Meldina
		32218, // Miya
		32222, // Liane
		32226, // Raula
		32230, // Ceci
		32234, // Nizer
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int ORKURUS_RECOMMENDATION = 9760;
	private static final int KAMAEL_INQUISITOR_MARK = 9782;
	private static final int SOUL_BREAKER_CERTIFICATE = 9806;
	
	public KamaelChange2()
	{
		super(KamaelChange2.class.getSimpleName(), "ai/npc/VillageMasters");
		addStartNpc(NPCS_MALE);
		addStartNpc(NPCS_FEMALE);
		addTalkId(NPCS_MALE);
		addTalkId(NPCS_FEMALE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32145-05.htm": // master_all_kamael003t
			case "32145-06.htm": // master_all_kamael006ta
			case "32145-07.htm": // master_all_kamael007ta
			case "32145-08.htm": // master_all_kamael006msa
			case "32145-09.htm": // master_all_kamael007msa
			case "32145-11.htm": // master_all_kamael003w
			case "32145-12.htm": // master_all_kamael006wa
			case "32145-13.htm": // master_all_kamael007wa
			case "32145-14.htm": // master_all_kamael006fsa
			case "32145-15.htm": // master_all_kamael007fsa
			{
				htmltext = event;
				break;
			}
			case "127":
			case "128":
			case "129":
			case "130":
			{
				htmltext = ClassChangeRequested(player, npc, Integer.valueOf(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, L2Npc npc, int classId)
	{
		String htmltext = null;
		if (CategoryData.getInstance().isInCategory(CategoryType.KAMAEL_THIRD_CLASS_GROUP, classId))
		{
			if (player.isInCategory(CategoryType.KAMAEL_FIRST_CLASS_GROUP))
			{
				if (Util.contains(NPCS_MALE, npc.getId()))
				{
					htmltext = "32145-02.htm"; // master_all_kamael012b
				}
				else
				{
					htmltext = "32145-03.htm"; // master_all_kamael012c
				}
			}
			else if (player.isInCategory(CategoryType.KAMAEL_THIRD_CLASS_GROUP))
			{
				if (Util.contains(NPCS_MALE, npc.getId()))
				{
					htmltext = "32145-16.htm"; // master_all_kamael005b
				}
				else
				{
					htmltext = "32145-17.htm"; // master_all_kamael005c
				}
			}
			else if (player.isInCategory(CategoryType.KAMAEL_FOURTH_CLASS_GROUP))
			{
				if (Util.contains(NPCS_MALE, npc.getId()))
				{
					htmltext = "32145-18.htm"; // master_all_kamael100b
				}
				else
				{
					htmltext = "32145-19.htm"; // master_all_kamael100c
				}
			}
			else if (player.getClassId() == ClassId.trooper)
			{
				if (Util.contains(NPCS_MALE, npc.getId()))
				{
					if (classId == 127)
					{
						QuestState qs = player.getQuestState(Q00064_CertifiedBerserker.class.getSimpleName());
						if (player.getLevel() < 40)
						{
							if ((qs != null) && qs.isCompleted())
							{
								htmltext = "32145-20.htm"; // master_all_kamael008ta
							}
							else
							{
								htmltext = "32145-21.htm"; // master_all_kamael009ta
							}
						}
						else if ((qs == null) || !qs.isCompleted())
						{
							htmltext = "32145-22.htm"; // master_all_kamael010ta
						}
						else
						{
							takeItems(player, ORKURUS_RECOMMENDATION, -1);
							player.setClassId(127);
							player.setBaseClass(127);
							// SystemMessage and cast skill is done by setClassId
							player.broadcastUserInfo();
							giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
							htmltext = "32145-23.htm"; // master_all_kamael011ta
						}
					}
					else if (classId == 128)
					{
						QuestState qs = player.getQuestState(Q00065_CertifiedSoulBreaker.class.getSimpleName());
						if (player.getLevel() < 40)
						{
							if ((qs != null) && qs.isCompleted())
							{
								htmltext = "32145-24.htm"; // master_all_kamael008msa
							}
							else
							{
								htmltext = "32145-25.htm"; // master_all_kamael009msa
							}
						}
						else if ((qs == null) || !qs.isCompleted())
						{
							htmltext = "32145-26.htm"; // master_all_kamael010msa
						}
						else
						{
							takeItems(player, SOUL_BREAKER_CERTIFICATE, -1);
							player.setClassId(128);
							player.setBaseClass(128);
							// SystemMessage and cast skill is done by setClassId
							player.broadcastUserInfo();
							giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
							htmltext = "32145-27.htm"; // master_all_kamael011msa
						}
					}
				}
				else
				{
					htmltext = "32145-10.htm"; // master_all_kamael002c
				}
			}
			else if (player.getClassId() == ClassId.warder)
			{
				if (Util.contains(NPCS_MALE, npc.getId()))
				{
					htmltext = "32145-04.htm"; // master_all_kamael002b
				}
				else
				{
					if (classId == 129)
					{
						QuestState qs = player.getQuestState(Q00065_CertifiedSoulBreaker.class.getSimpleName());
						if (player.getLevel() < 40)
						{
							if ((qs != null) && qs.isCompleted())
							{
								htmltext = "32145-28.htm"; // master_all_kamael008fsa
							}
							else
							{
								htmltext = "32145-29.htm"; // master_all_kamael009fsa
							}
						}
						else if ((qs == null) || !qs.isCompleted())
						{
							htmltext = "32145-30.htm"; // master_all_kamael010fsa
						}
						else
						{
							takeItems(player, SOUL_BREAKER_CERTIFICATE, -1);
							player.setClassId(129);
							player.setBaseClass(129);
							// SystemMessage and cast skill is done by setClassId
							player.broadcastUserInfo();
							giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
							htmltext = "32145-31.htm"; // master_all_kamael011fsa
						}
					}
					else if (classId == 130)
					{
						QuestState qs = player.getQuestState(Q00066_CertifiedArbalester.class.getSimpleName());
						if (player.getLevel() < 40)
						{
							if ((qs != null) && qs.isCompleted())
							{
								htmltext = "32145-32.htm"; // master_all_kamael008wa
							}
							else
							{
								htmltext = "32145-33.htm"; // master_all_kamael009wa
							}
						}
						else if ((qs == null) || !qs.isCompleted())
						{
							htmltext = "32145-34.htm"; // master_all_kamael010wa
						}
						else
						{
							takeItems(player, KAMAEL_INQUISITOR_MARK, -1);
							player.setClassId(130);
							player.setBaseClass(130);
							// SystemMessage and cast skill is done by setClassId
							player.broadcastUserInfo();
							giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
							htmltext = "32145-35.htm"; // master_all_kamael011wa
						}
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (player.getRace() != Race.KAMAEL)
		{
			htmltext = "32145-01.htm"; // master_all_kamael002a
		}
		else if (player.isInCategory(CategoryType.KAMAEL_FIRST_CLASS_GROUP))
		{
			if (player.getClassId() == ClassId.maleSoldier)
			{
				htmltext = "32145-02.htm"; // master_all_kamael012b
			}
			else if (player.getClassId() == ClassId.femaleSoldier)
			{
				htmltext = "32145-03.htm"; // master_all_kamael012c
			}
		}
		else if (player.isInCategory(CategoryType.KAMAEL_SECOND_CLASS_GROUP))
		{
			if (Util.contains(NPCS_MALE, npc.getId()))
			{
				if (player.isInCategory(CategoryType.KAMAEL_FEMALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-04.htm"; // master_all_kamael002b
					return htmltext;
				}
				
				if (player.getClassId() == ClassId.trooper)
				{
					htmltext = "32145-05.htm"; // master_all_kamael003t
				}
				else if (player.getClassId() == ClassId.warder)
				{
					htmltext = "32145-02.htm"; // master_all_kamael012b
				}
			}
			else
			{
				if (player.isInCategory(CategoryType.KAMAEL_MALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-10.htm"; // master_all_kamael002c
					return htmltext;
				}
				
				if (player.getClassId() == ClassId.trooper)
				{
					htmltext = "32145-03.htm"; // master_all_kamael012c
				}
				else if (player.getClassId() == ClassId.warder)
				{
					htmltext = "32145-11.htm"; // master_all_kamael003w
				}
			}
		}
		else if (player.isInCategory(CategoryType.KAMAEL_THIRD_CLASS_GROUP))
		{
			if (Util.contains(NPCS_MALE, npc.getId()))
			{
				if (player.isInCategory(CategoryType.KAMAEL_MALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-16.htm"; // master_all_kamael005b
				}
				else
				{
					htmltext = "32145-04.htm"; // master_all_kamael002b
				}
			}
			else
			{
				if (player.isInCategory(CategoryType.KAMAEL_FEMALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-17.htm"; // master_all_kamael005c
				}
				else
				{
					htmltext = "32145-10.htm"; // master_all_kamael002c
				}
			}
		}
		else if (player.isInCategory(CategoryType.KAMAEL_FOURTH_CLASS_GROUP))
		{
			if (Util.contains(NPCS_MALE, npc.getId()))
			{
				if (player.isInCategory(CategoryType.KAMAEL_MALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-18.htm"; // master_all_kamael100b
				}
				else
				{
					htmltext = "32145-04.htm"; // master_all_kamael002b
				}
			}
			else
			{
				if (player.isInCategory(CategoryType.KAMAEL_FEMALE_MAIN_OCCUPATION))
				{
					htmltext = "32145-19.htm"; // master_all_kamael100c
				}
				else
				{
					htmltext = "32145-10.htm"; // master_all_kamael002c
				}
			}
		}
		return htmltext;
	}
}
