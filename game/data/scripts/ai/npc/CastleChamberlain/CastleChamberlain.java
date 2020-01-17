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
package ai.npc.CastleChamberlain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2r.Config;
import l2r.gameserver.SevenSigns;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.sql.TeleportLocationTable;
import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.instancemanager.CastleManorManager;
import l2r.gameserver.instancemanager.FortManager;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.ClanPrivilege;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2TeleportLocation;
import l2r.gameserver.model.SeedProduction;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2DoorInstance;
import l2r.gameserver.model.actor.instance.L2MerchantInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.Castle.CastleFunction;
import l2r.gameserver.model.entity.Fort;
import l2r.gameserver.model.events.EventType;
import l2r.gameserver.model.events.ListenerRegisterType;
import l2r.gameserver.model.events.annotations.Id;
import l2r.gameserver.model.events.annotations.RegisterEvent;
import l2r.gameserver.model.events.annotations.RegisterType;
import l2r.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExShowCropInfo;
import l2r.gameserver.network.serverpackets.ExShowCropSetting;
import l2r.gameserver.network.serverpackets.ExShowDominionRegistry;
import l2r.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import l2r.gameserver.network.serverpackets.ExShowSeedInfo;
import l2r.gameserver.network.serverpackets.ExShowSeedSetting;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Castle Chamberlain AI.
 * @author malyelfik
 */
public final class CastleChamberlain extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPC =
	{
		35100, // Sayres
		35142, // Crosby
		35184, // Saul
		35226, // Brasseur
		35274, // Logan
		35316, // Neurath
		35363, // Alfred
		35509, // Frederick
		35555, // August
	};
	// Item
	private static final int CROWN = 6841;
	// Fortress
	private static final Map<Integer, List<Integer>> FORTRESS = new HashMap<>();
	
	static
	{
		FORTRESS.put(1, Arrays.asList(101, 102, 112, 113)); // Gludio Castle
		FORTRESS.put(2, Arrays.asList(103, 112, 114, 115)); // Dion Castle
		FORTRESS.put(3, Arrays.asList(104, 114, 116, 118, 119)); // Giran Castle
		FORTRESS.put(4, Arrays.asList(105, 113, 115, 116, 117)); // Oren Castle
		FORTRESS.put(5, Arrays.asList(106, 107, 117, 118)); // Aden Castle
		FORTRESS.put(6, Arrays.asList(108, 119)); // Innadril Castle
		FORTRESS.put(7, Arrays.asList(109, 117, 120)); // Goddard Castle
		FORTRESS.put(8, Arrays.asList(110, 120, 121)); // Rune Castle
		FORTRESS.put(9, Arrays.asList(111, 121)); // Schuttgart Castle
	}
	
	// Buffs
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(4342, 2), // Wind Walk Lv.2
		new SkillHolder(4343, 3), // Decrease Weight Lv.3
		new SkillHolder(4344, 3), // Shield Lv.3
		new SkillHolder(4346, 4), // Mental Shield Lv.4
		new SkillHolder(4345, 3), // Might Lv.3
		new SkillHolder(4347, 2), // Bless the Body Lv.2
		new SkillHolder(4349, 1), // Magic Barrier Lv.1
		new SkillHolder(4350, 1), // Resist Shock Lv.1
		new SkillHolder(4348, 2), // Bless the Soul Lv.2
		new SkillHolder(4351, 2), // Concentration Lv.2
		new SkillHolder(4352, 1), // Berserker Spirit Lv.1
		new SkillHolder(4353, 2), // Bless Shield Lv.2
		new SkillHolder(4358, 1), // Guidance Lv.1
		new SkillHolder(4354, 1), // Vampiric Rage Lv.1
		new SkillHolder(4347, 6), // Bless the Body Lv.6
		new SkillHolder(4349, 2), // Magic Barrier Lv.2
		new SkillHolder(4350, 4), // Resist Shock Lv.4
		new SkillHolder(4348, 6), // Bless the Soul Lv.6
		new SkillHolder(4351, 6), // Concentration Lv.6
		new SkillHolder(4352, 2), // Berserker Spirit Lv.2
		new SkillHolder(4353, 6), // Bless Shield Lv.6
		new SkillHolder(4358, 3), // Guidance Lv.3
		new SkillHolder(4354, 4), // Vampiric Rage Lv.4
		new SkillHolder(4355, 1), // Acumen Lv.1
		new SkillHolder(4356, 1), // Empower Lv.1
		new SkillHolder(4357, 1), // Haste Lv.1
		new SkillHolder(4359, 1), // Focus Lv.1
		new SkillHolder(4360, 1), // Death Whisper Lv.1
	};
	
	public CastleChamberlain()
	{
		super(CastleChamberlain.class.getSimpleName(), "ai/npc");
		addStartNpc(NPC);
		addTalkId(NPC);
		addFirstTalkId(NPC);
	}
	
	private NpcHtmlMessage getHtmlPacket(L2PcInstance player, L2Npc npc, String htmlFile)
	{
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player.getHtmlPrefix(), htmlFile));
		return packet;
	}
	
	private final String funcConfirmHtml(final L2PcInstance player, final L2Npc npc, final Castle castle, final int func, final int level)
	{
		if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
		{
			final NpcHtmlMessage html;
			final String fstring = (func == Castle.FUNC_TELEPORT) ? "9" : "10";
			if (level == 0)
			{
				html = getHtmlPacket(player, npc, "castleresetdeco.html");
				html.replace("%AgitDecoSubmit%", Integer.toString(func));
			}
			else if ((castle.getFunction(func) != null) && (castle.getFunction(func).getLvl() == level))
			{
				html = getHtmlPacket(player, npc, "castledecoalreadyset.html");
				html.replace("%AgitDecoEffect%", "<fstring p1=\"" + level + "\">" + fstring + "</fstring>");
			}
			else
			{
				html = getHtmlPacket(player, npc, "castledeco-0" + func + ".html");
				html.replace("%AgitDecoCost%", "<fstring p1=\"" + getFunctionFee(func, level) + "\" p2=\"" + (getFunctionRatio(func) / 86400000) + "\">6</fstring>");
				html.replace("%AgitDecoEffect%", "<fstring p1=\"" + level + "\">" + fstring + "</fstring>");
				html.replace("%AgitDecoSubmit%", func + " " + level);
			}
			player.sendPacket(html);
			return null;
		}
		return "chamberlain-21.html";
	}
	
	private final void funcReplace(final Castle castle, final NpcHtmlMessage html, final int func, final String str)
	{
		final CastleFunction function = castle.getFunction(func);
		if (function == null)
		{
			html.replace("%" + str + "Depth%", "<fstring>4</fstring>");
			html.replace("%" + str + "Cost%", "");
			html.replace("%" + str + "Expire%", "<fstring>4</fstring>");
			html.replace("%" + str + "Reset%", "");
		}
		else
		{
			final String fstring = ((func == Castle.FUNC_SUPPORT) || (func == Castle.FUNC_TELEPORT)) ? "9" : "10";
			final Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(function.getEndTime());
			html.replace("%" + str + "Depth%", "<fstring p1=\"" + function.getLvl() + "\">" + fstring + "</fstring>");
			html.replace("%" + str + "Cost%", "<fstring p1=\"" + function.getLease() + "\" p2=\"" + (function.getRate() / 86400000) + "\">6</fstring>");
			html.replace("%" + str + "Expire%", "<fstring p1=\"" + calendar.get(Calendar.DATE) + "\" p2=\"" + (calendar.get(Calendar.MONTH) + 1) + "\" p3=\"" + calendar.get(Calendar.YEAR) + "\">5</fstring>");
			html.replace("%" + str + "Reset%", "[<a action=\"bypass -h Quest CastleChamberlain " + str + " 0\">Deactivate</a>]");
		}
	}
	
	private final int getFunctionFee(final int func, final int level)
	{
		int fee = 0;
		switch (func)
		{
			case Castle.FUNC_RESTORE_EXP:
				fee = (level == 45) ? Config.CS_EXPREG1_FEE : Config.CS_EXPREG2_FEE;
				break;
			case Castle.FUNC_RESTORE_HP:
				fee = (level == 300) ? Config.CS_HPREG1_FEE : Config.CS_HPREG2_FEE;
				break;
			case Castle.FUNC_RESTORE_MP:
				fee = (level == 40) ? Config.CS_MPREG1_FEE : Config.CS_MPREG2_FEE;
				break;
			case Castle.FUNC_SUPPORT:
				fee = (level == 5) ? Config.CS_SUPPORT1_FEE : Config.CS_SUPPORT2_FEE;
				break;
			case Castle.FUNC_TELEPORT:
				fee = (level == 1) ? Config.CS_TELE1_FEE : Config.CS_TELE2_FEE;
				break;
		}
		return fee;
	}
	
	private final long getFunctionRatio(final int func)
	{
		long ratio = 0;
		switch (func)
		{
			case Castle.FUNC_RESTORE_EXP:
				ratio = Config.CS_EXPREG_FEE_RATIO;
				break;
			case Castle.FUNC_RESTORE_HP:
				ratio = Config.CS_HPREG_FEE_RATIO;
				break;
			case Castle.FUNC_RESTORE_MP:
				ratio = Config.CS_MPREG_FEE_RATIO;
				break;
			case Castle.FUNC_SUPPORT:
				ratio = Config.CS_SUPPORT_FEE_RATIO;
				break;
			case Castle.FUNC_TELEPORT:
				ratio = Config.CS_TELE_FEE_RATIO;
				break;
		}
		return ratio;
	}
	
	private final int getDoorUpgradePrice(final int type, final int level)
	{
		int price = 0;
		switch (type)
		{
			case 1: // Outer Door
			{
				switch (level)
				{
					case 2:
						price = Config.OUTER_DOOR_UPGRADE_PRICE2;
						break;
					case 3:
						price = Config.OUTER_DOOR_UPGRADE_PRICE3;
						break;
					case 5:
						price = Config.OUTER_DOOR_UPGRADE_PRICE5;
						break;
				}
				break;
			}
			case 2: // Inner Door
			{
				switch (level)
				{
					case 2:
						price = Config.INNER_DOOR_UPGRADE_PRICE2;
						break;
					case 3:
						price = Config.INNER_DOOR_UPGRADE_PRICE3;
						break;
					case 5:
						price = Config.INNER_DOOR_UPGRADE_PRICE5;
						break;
				}
				break;
			}
			case 3: // Wall
			{
				switch (level)
				{
					case 2:
						price = Config.WALL_UPGRADE_PRICE2;
						break;
					case 3:
						price = Config.WALL_UPGRADE_PRICE3;
						break;
					case 5:
						price = Config.WALL_UPGRADE_PRICE5;
						break;
				}
				break;
			}
		}
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DUSK:
				price *= 3;
				break;
			case SevenSigns.CABAL_DAWN:
				price *= 0.8;
				break;
		}
		return price;
	}
	
	private final String getSealOwner(final int seal)
	{
		String npcString;
		switch (SevenSigns.getInstance().getSealOwner(seal))
		{
			case SevenSigns.CABAL_DAWN:
				npcString = "1000511";
				break;
			case SevenSigns.CABAL_DUSK:
				npcString = "1000510";
				break;
			default:
				npcString = "1000512";
				break;
		}
		return npcString;
	}
	
	private final int getTaxLimit()
	{
		final int taxLimit;
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				taxLimit = 25;
				break;
			case SevenSigns.CABAL_DUSK:
				taxLimit = 5;
				break;
			default:
				taxLimit = 15;
				break;
		}
		return taxLimit;
	}
	
	private final int getTrapUpgradePrice(final int level)
	{
		int price = 0;
		switch (level)
		{
			case 1:
				price = Config.TRAP_UPGRADE_PRICE1;
				break;
			case 2:
				price = Config.TRAP_UPGRADE_PRICE2;
				break;
			case 3:
				price = Config.TRAP_UPGRADE_PRICE3;
				break;
			case 4:
				price = Config.TRAP_UPGRADE_PRICE4;
				break;
		}
		
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DUSK:
				price *= 3;
				break;
			case SevenSigns.CABAL_DAWN:
				price *= 0.8;
				break;
		}
		return price;
	}
	
	private final boolean isDomainFortressInContractStatus(final int castleId)
	{
		final int numFort = ((castleId == 1) || (castleId == 5)) ? 2 : 1;
		final List<Integer> fortList = FORTRESS.get(castleId);
		for (int i = 0; i < numFort; i++)
		{
			final Fort fortress = FortManager.getInstance().getFortById(fortList.get(i));
			if (fortress.getFortState() == 2)
			{
				return true;
			}
		}
		return false;
	}
	
	private final boolean isOwner(final L2PcInstance player, final L2Npc npc)
	{
		return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || ((player.getClan() != null) && (player.getClanId() == npc.getCastle().getOwnerId()));
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final Castle castle = npc.getCastle();
		final StringTokenizer st = new StringTokenizer(event, " ");
		String htmltext = null;
		switch (st.nextToken())
		{
			case "chamberlain-01.html":
			case "manor-help-01.html":
			case "manor-help-02.html":
			case "manor-help-03.html":
			case "manor-help-04.html":
			{
				htmltext = event;
				break;
			}
			case "fort_status":
			{
				if (npc.isMyLord(player))
				{
					final StringBuilder sb = new StringBuilder();
					final List<Integer> fort = FORTRESS.get(castle.getResidenceId());
					for (int id : fort)
					{
						final Fort fortress = FortManager.getInstance().getFortById(id);
						final int fortId = fortress.getResidenceId();
						final String fortType = (fortId < 112) ? "1300133" : "1300134";
						final String fortStatus;
						switch (fortress.getFortState())
						{
							case 1:
								fortStatus = "1300122";
								break;
							case 2:
								fortStatus = "1300124";
								break;
							default:
								fortStatus = "1300123";
								break;
						}
						sb.append("<fstring>1300" + fortId + "</fstring>");
						sb.append(" (<fstring>" + fortType + "</fstring>)");
						sb.append(" : <font color=\"00FFFF\"><fstring>" + fortStatus + "</fstring></font><br>");
					}
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-28.html");
					html.replace("%list%", sb.toString());
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "siege_functions":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else if (!isDomainFortressInContractStatus(castle.getResidenceId()))
					{
						htmltext = "chamberlain-27.html";
					}
					else if (!SevenSigns.getInstance().isCompResultsPeriod())
					{
						htmltext = "chamberlain-26.html";
					}
					else
					{
						htmltext = "chamberlain-12.html";
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manage_doors":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					if (st.hasMoreTokens())
					{
						final StringBuilder sb = new StringBuilder();
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-13.html");
						html.replace("%type%", st.nextToken());
						while (st.hasMoreTokens())
						{
							sb.append(" " + st.nextToken());
						}
						html.replace("%doors%", sb.toString());
						player.sendPacket(html);
					}
					else
					{
						htmltext = npc.getId() + "-du.html";
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "upgrade_doors":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					final int type = Integer.parseInt(st.nextToken());
					final int level = Integer.parseInt(st.nextToken());
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-14.html");
					html.replace("%gate_price%", Integer.toString(getDoorUpgradePrice(type, level)));
					html.replace("%event%", event.substring("upgrade_doors".length() + 1));
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "upgrade_doors_confirm":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						final int type = Integer.parseInt(st.nextToken());
						final int level = Integer.parseInt(st.nextToken());
						final int price = getDoorUpgradePrice(type, level);
						final int[] doors = new int[2];
						for (int i = 0; i <= st.countTokens(); i++)
						{
							doors[i] = Integer.parseInt(st.nextToken());
						}
						
						final L2DoorInstance door = castle.getDoor(doors[0]);
						if (door != null)
						{
							final int currentLevel = door.getStat().getUpgradeHpRatio();
							if (currentLevel >= level)
							{
								final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-15.html");
								html.replace("%doorlevel%", Integer.toString(currentLevel));
								player.sendPacket(html);
							}
							else if (player.getAdena() >= price)
							{
								takeItems(player, Inventory.ADENA_ID, price);
								for (int doorId : doors)
								{
									castle.setDoorUpgrade(doorId, level, true);
								}
								htmltext = "chamberlain-16.html";
							}
							else
							{
								htmltext = "chamberlain-09.html";
							}
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manage_trap":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					if (st.hasMoreTokens())
					{
						final NpcHtmlMessage html;
						if (castle.getName().equalsIgnoreCase("aden"))
						{
							html = getHtmlPacket(player, npc, "chamberlain-17a.html");
						}
						else
						{
							html = getHtmlPacket(player, npc, "chamberlain-17.html");
						}
						html.replace("%trapIndex%", st.nextToken());
						player.sendPacket(html);
					}
					else
					{
						htmltext = npc.getId() + "-tu.html";
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "upgrade_trap":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					final String trapIndex = st.nextToken();
					final int level = Integer.parseInt(st.nextToken());
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-18.html");
					html.replace("%trapIndex%", trapIndex);
					html.replace("%level%", Integer.toString(level));
					html.replace("%dmgzone_price%", Integer.toString(getTrapUpgradePrice(level)));
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "upgrade_trap_confirm":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						final int trapIndex = Integer.parseInt(st.nextToken());
						final int level = Integer.parseInt(st.nextToken());
						final int price = getTrapUpgradePrice(level);
						final int currentLevel = castle.getTrapUpgradeLevel(trapIndex);
						
						if (currentLevel >= level)
						{
							final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-19.html");
							html.replace("%dmglevel%", Integer.toString(currentLevel));
							player.sendPacket(html);
						}
						else if (player.getAdena() >= price)
						{
							takeItems(player, Inventory.ADENA_ID, price);
							castle.setTrapUpgrade(trapIndex, level, true);
							htmltext = "chamberlain-20.html";
						}
						else
						{
							htmltext = "chamberlain-09.html";
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "receive_report":
			{
				if (npc.isMyLord(player))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-07.html";
					}
					else
					{
						final L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-02.html");
						html.replace("%clanleadername%", clan.getLeaderName());
						html.replace("%clanname%", clan.getName());
						html.replace("%castlename%", String.valueOf(1001000 + castle.getResidenceId()));
						
						switch (SevenSigns.getInstance().getCurrentPeriod())
						{
							case SevenSigns.PERIOD_COMP_RECRUITING:
								html.replace("%ss_event%", "1000509");
								break;
							case SevenSigns.PERIOD_COMPETITION:
								html.replace("%ss_event%", "1000507");
								break;
							case SevenSigns.PERIOD_SEAL_VALIDATION:
							case SevenSigns.PERIOD_COMP_RESULTS:
								html.replace("%ss_event%", "1000508");
								break;
						}
						html.replace("%ss_avarice%", getSealOwner(1));
						html.replace("%ss_gnosis%", getSealOwner(2));
						html.replace("%ss_strife%", getSealOwner(3));
						player.sendPacket(html);
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manage_tax":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlesettaxrate.html");
						html.replace("%tax_rate%", Integer.toString(castle.getTaxPercent()));
						html.replace("%next_tax_rate%", "0"); // TODO: Implement me!
						html.replace("%tax_limit%", Integer.toString(getTaxLimit()));
						player.sendPacket(html);
					}
				}
				else if (isOwner(player, npc))
				{
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-03.html");
					html.replace("%tax_rate%", Integer.toString(castle.getTaxPercent()));
					html.replace("%next_tax_rate%", "0"); // TODO: Implement me!
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "set_tax":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						final NpcHtmlMessage html;
						final int tax = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 0;
						final int taxLimit = getTaxLimit();
						if (tax > taxLimit)
						{
							html = getHtmlPacket(player, npc, "castletoohightaxrate.html");
							html.replace("%tax_limit%", Integer.toString(taxLimit));
						}
						else
						{
							castle.setTaxPercent(tax);
							html = getHtmlPacket(player, npc, "castleaftersettaxrate.html");
							html.replace("%next_tax_rate%", Integer.toString(tax));
						}
						player.sendPacket(html);
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manage_vault":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES))
				{
					long seedIncome = 0;
					if (Config.ALLOW_MANOR)
					{
						for (SeedProduction sp : CastleManorManager.getInstance().getSeedProduction(castle.getResidenceId(), false))
						{
							final long diff = sp.getStartAmount() - sp.getAmount();
							if (diff != 0)
							{
								seedIncome += diff * sp.getPrice();
							}
						}
					}
					
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlemanagevault.html");
					html.replace("%tax_income%", Util.formatAdena(castle.getTreasury()));
					html.replace("%tax_income_reserved%", "0"); // TODO: Implement me!
					html.replace("%seed_income%", Util.formatAdena(seedIncome));
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "deposit":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES))
				{
					final long amount = (st.hasMoreTokens()) ? Long.parseLong(st.nextToken()) : 0;
					if ((amount > 0) && (amount < Inventory.MAX_ADENA))
					{
						if (player.getAdena() >= amount)
						{
							takeItems(player, Inventory.ADENA_ID, amount);
							castle.addToTreasuryNoTax(amount);
						}
						else
						{
							player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
						}
					}
					htmltext = "chamberlain-01.html";
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "withdraw":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES))
				{
					final long amount = (st.hasMoreTokens()) ? Long.parseLong(st.nextToken()) : 0;
					if (amount <= castle.getTreasury())
					{
						castle.addToTreasuryNoTax((-1) * amount);
						giveAdena(player, amount, false);
						htmltext = "chamberlain-01.html";
					}
					else
					{
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlenotenoughbalance.html");
						html.replace("%tax_income%", Util.formatAdena(castle.getTreasury()));
						html.replace("%withdraw_amount%", Util.formatAdena(amount));
						player.sendPacket(html);
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manage_functions":
			{
				if (!isOwner(player, npc))
				{
					htmltext = "chamberlain-21.html";
				}
				else if (castle.getSiege().isInProgress())
				{
					htmltext = "chamberlain-08.html";
				}
				else
				{
					htmltext = "chamberlain-23.html";
				}
				break;
			}
			case "banish_foreigner_show":
			{
				if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_DISMISS))
				{
					htmltext = "chamberlain-21.html";
				}
				else if (castle.getSiege().isInProgress())
				{
					htmltext = "chamberlain-08.html";
				}
				else
				{
					htmltext = "chamberlain-10.html";
				}
				break;
			}
			case "banish_foreigner":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_DISMISS))
				{
					if (castle.getSiege().isInProgress() || TerritoryWarManager.getInstance().isTWInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						castle.banishForeigners();
						htmltext = "chamberlain-11.html";
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "doors":
			{
				if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR))
				{
					htmltext = "chamberlain-21.html";
				}
				else if (castle.getSiege().isInProgress())
				{
					htmltext = "chamberlain-08.html";
				}
				else
				{
					htmltext = npc.getId() + "-d.html";
				}
				break;
			}
			case "operate_door":
			{
				if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR))
				{
					htmltext = "chamberlain-21.html";
				}
				else if (castle.getSiege().isInProgress())
				{
					htmltext = "chamberlain-08.html";
				}
				else
				{
					final boolean open = (Integer.parseInt(st.nextToken()) == 1);
					while (st.hasMoreTokens())
					{
						castle.openCloseDoor(player, Integer.parseInt(st.nextToken()), open);
					}
					htmltext = (open ? "chamberlain-05.html" : "chamberlain-06.html");
				}
				break;
			}
			case "additional_functions":
			{
				htmltext = (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) ? "castletdecomanage.html" : "chamberlain-21.html";
				break;
			}
			case "recovery":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledeco-AR01.html");
					funcReplace(castle, html, Castle.FUNC_RESTORE_HP, "HP");
					funcReplace(castle, html, Castle.FUNC_RESTORE_MP, "MP");
					funcReplace(castle, html, Castle.FUNC_RESTORE_EXP, "XP");
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "other":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledeco-AE01.html");
					funcReplace(castle, html, Castle.FUNC_TELEPORT, "TP");
					funcReplace(castle, html, Castle.FUNC_SUPPORT, "BF");
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "HP":
			{
				final int level = Integer.parseInt(st.nextToken());
				htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_HP, level);
				break;
			}
			case "MP":
			{
				final int level = Integer.parseInt(st.nextToken());
				htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_MP, level);
				break;
			}
			case "XP":
			{
				final int level = Integer.parseInt(st.nextToken());
				htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_RESTORE_EXP, level);
				break;
			}
			case "TP":
			{
				final int level = Integer.parseInt(st.nextToken());
				htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_TELEPORT, level);
				break;
			}
			case "BF":
			{
				final int level = Integer.parseInt(st.nextToken());
				htmltext = funcConfirmHtml(player, npc, castle, Castle.FUNC_SUPPORT, level);
				break;
			}
			case "set_func":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS))
				{
					final int func = Integer.parseInt(st.nextToken());
					final int level = Integer.parseInt(st.nextToken());
					if (level == 0)
					{
						castle.updateFunctions(player, func, level, 0, 0, false);
					}
					else if (!castle.updateFunctions(player, func, level, getFunctionFee(func, level), getFunctionRatio(func), castle.getFunction(func) == null))
					{
						htmltext = "chamberlain-09.html";
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "functions":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					final CastleFunction HP = castle.getFunction(Castle.FUNC_RESTORE_HP);
					final CastleFunction MP = castle.getFunction(Castle.FUNC_RESTORE_MP);
					final CastleFunction XP = castle.getFunction(Castle.FUNC_RESTORE_EXP);
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "castledecofunction.html");
					html.replace("%HPDepth%", (HP == null) ? "0" : Integer.toString(HP.getLvl()));
					html.replace("%MPDepth%", (MP == null) ? "0" : Integer.toString(MP.getLvl()));
					html.replace("%XPDepth%", (XP == null) ? "0" : Integer.toString(XP.getLvl()));
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "teleport":
			{
				if (!isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					htmltext = "chamberlain-21.html";
				}
				else if (castle.getFunction(Castle.FUNC_TELEPORT) == null)
				{
					htmltext = "castlefuncdisabled.html";
				}
				else
				{
					htmltext = npc.getId() + "-t" + castle.getFunction(Castle.FUNC_TELEPORT).getLvl() + ".html";
				}
				break;
			}
			case "goto":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					final int locId = Integer.parseInt(st.nextToken());
					final L2TeleportLocation list = TeleportLocationTable.getInstance().getTemplate(locId);
					if (list != null)
					{
						if (takeItems(player, list.getId(), list.getPrice()))
						{
							player.teleToLocation(list.getX(), list.getY(), list.getZ());
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "buffer":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					if (castle.getFunction(Castle.FUNC_SUPPORT) == null)
					{
						htmltext = "castlefuncdisabled.html";
					}
					else
					{
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "castlebuff-0" + castle.getFunction(Castle.FUNC_SUPPORT).getLvl() + ".html");
						html.replace("%MPLeft%", Integer.toString((int) npc.getCurrentMp()));
						player.sendPacket(html);
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "cast_buff":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					if (castle.getFunction(Castle.FUNC_SUPPORT) == null)
					{
						htmltext = "castlefuncdisabled.html";
					}
					else
					{
						final int index = Integer.parseInt(st.nextToken());
						if (BUFFS.length > index)
						{
							final NpcHtmlMessage html;
							final SkillHolder holder = BUFFS[index];
							if (holder.getSkill().getMpConsume() < npc.getCurrentMp())
							{
								npc.setTarget(player);
								npc.doCast(holder.getSkill());
								html = getHtmlPacket(player, npc, "castleafterbuff.html");
							}
							else
							{
								html = getHtmlPacket(player, npc, "castlenotenoughmp.html");
							}
							
							html.replace("%MPLeft%", Integer.toString((int) npc.getCurrentMp()));
							player.sendPacket(html);
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "list_siege_clans":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE))
				{
					castle.getSiege().listRegisterClan(player);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "list_territory_clans":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE))
				{
					player.sendPacket(new ExShowDominionRegistry(castle.getResidenceId(), player));
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manor":
			{
				if (Config.ALLOW_MANOR)
				{
					htmltext = (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN)) ? "manor.html" : "chamberlain-21.html";
				}
				else
				{
					player.sendMessage("Manor system is deactivated.");
				}
				break;
			}
			case "products":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-22.html");
					html.replace("%npcId%", Integer.toString(npc.getId()));
					player.sendPacket(html);
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "buy":
			{
				if (isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS))
				{
					((L2MerchantInstance) npc).showBuyWindow(player, Integer.parseInt(st.nextToken()));
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "give_crown":
			{
				if (castle.getSiege().isInProgress())
				{
					htmltext = "chamberlain-08.html";
				}
				else if (npc.isMyLord(player))
				{
					if (hasQuestItems(player, CROWN))
					{
						htmltext = "chamberlain-24.html";
					}
					else
					{
						final NpcHtmlMessage html = getHtmlPacket(player, npc, "chamberlain-25.html");
						html.replace("%owner_name%", String.valueOf(player.getName()));
						html.replace("%feud_name%", String.valueOf(String.valueOf(1001000 + castle.getResidenceId())));
						player.sendPacket(html);
						giveItems(player, CROWN, 1);
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manors_cert":
			{
				if (npc.isMyLord(player))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						if ((SevenSigns.getInstance().getPlayerCabal(player.getObjectId()) == SevenSigns.CABAL_DAWN) && SevenSigns.getInstance().isCompetitionPeriod())
						{
							final int ticketCount = castle.getTicketBuyCount();
							if (ticketCount < (Config.SSQ_DAWN_TICKET_QUANTITY / Config.SSQ_DAWN_TICKET_BUNDLE))
							{
								final NpcHtmlMessage html = getHtmlPacket(player, npc, "ssq_selldawnticket.html");
								html.replace("%DawnTicketLeft%", String.valueOf(Config.SSQ_DAWN_TICKET_QUANTITY - (ticketCount * Config.SSQ_DAWN_TICKET_BUNDLE)));
								html.replace("%DawnTicketBundle%", String.valueOf(Config.SSQ_DAWN_TICKET_BUNDLE));
								html.replace("%DawnTicketPrice%", String.valueOf(Config.SSQ_DAWN_TICKET_PRICE * Config.SSQ_DAWN_TICKET_BUNDLE));
								player.sendPacket(html);
							}
							else
							{
								htmltext = "ssq_notenoughticket.html";
							}
						}
						else
						{
							htmltext = "ssq_notdawnorevent.html";
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
			case "manors_cert_confirm":
			{
				if (npc.isMyLord(player))
				{
					if (castle.getSiege().isInProgress())
					{
						htmltext = "chamberlain-08.html";
					}
					else
					{
						if ((SevenSigns.getInstance().getPlayerCabal(player.getObjectId()) == SevenSigns.CABAL_DAWN) && SevenSigns.getInstance().isCompetitionPeriod())
						{
							final int ticketCount = castle.getTicketBuyCount();
							if (ticketCount < (Config.SSQ_DAWN_TICKET_QUANTITY / Config.SSQ_DAWN_TICKET_BUNDLE))
							{
								final long totalCost = Config.SSQ_DAWN_TICKET_PRICE * Config.SSQ_DAWN_TICKET_BUNDLE;
								if (player.getAdena() >= totalCost)
								{
									takeItems(player, Inventory.ADENA_ID, totalCost);
									giveItems(player, Config.SSQ_MANORS_AGREEMENT_ID, Config.SSQ_DAWN_TICKET_BUNDLE);
									castle.setTicketBuyCount(ticketCount + 1);
								}
								else
								{
									htmltext = "chamberlain-09.html";
								}
							}
							else
							{
								htmltext = "ssq_notenoughticket.html";
							}
						}
						else
						{
							htmltext = "ssq_notdawnorevent.html";
						}
					}
				}
				else
				{
					htmltext = "chamberlain-21.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return (isOwner(player, npc)) ? "chamberlain-01.html" : "chamberlain-04.html";
	}
	
	// @formatter:off
	@RegisterEvent(EventType.ON_NPC_MANOR_BYPASS)
	@RegisterType(ListenerRegisterType.NPC)
	@Id({35100, 35142, 35184, 35226, 35274,	35316, 35363, 35509, 35555})
	// @formatter:on
	public final void onNpcManorBypass(OnNpcManorBypass evt)
	{
		final L2PcInstance player = evt.getActiveChar();
		final L2Npc npc = evt.getTarget();
		if (isOwner(player, npc))
		{
			final CastleManorManager manor = CastleManorManager.getInstance();
			if (manor.isUnderMaintenance())
			{
				player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				return;
			}
			
			final int castleId = (evt.getManorId() == -1) ? npc.getCastle().getResidenceId() : evt.getManorId();
			switch (evt.getRequest())
			{
				case 3: // Seed info
					player.sendPacket(new ExShowSeedInfo(castleId, evt.isNextPeriod(), true));
					break;
				case 4: // Crop info
					player.sendPacket(new ExShowCropInfo(castleId, evt.isNextPeriod(), true));
					break;
				case 5: // Basic info
					player.sendPacket(new ExShowManorDefaultInfo(true));
					break;
				case 7: // Seed settings
					if (manor.isManorApproved())
					{
						player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_4_30_AM_AND_8_PM);
						return;
					}
					player.sendPacket(new ExShowSeedSetting(castleId));
					break;
				case 8: // Crop settings
					if (manor.isManorApproved())
					{
						player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_4_30_AM_AND_8_PM);
						return;
					}
					player.sendPacket(new ExShowCropSetting(castleId));
					break;
				default:
					_log.warn(getClass().getSimpleName() + ": Player " + player.getName() + " (" + player.getObjectId() + ") send unknown request id " + evt.getRequest() + "!");
			}
		}
	}
}