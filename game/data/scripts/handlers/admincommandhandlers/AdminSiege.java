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
package handlers.admincommandhandlers;

import java.util.Calendar;
import java.util.StringTokenizer;

import l2r.gameserver.SevenSigns;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.instancemanager.AuctionManager;
import l2r.gameserver.instancemanager.CHSiegeManager;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.instancemanager.ClanHallManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.ClanHall;
import l2r.gameserver.model.entity.clanhall.SiegableHall;
import l2r.gameserver.model.zone.type.L2ClanHallZone;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Util;
import l2r.util.StringUtil;

/**
 * This class handles all siege commands.
 * @author Zoey76 (rework)
 */
public class AdminSiege implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		// Castle commands
		"admin_siege",
		"admin_add_attacker",
		"admin_add_defender",
		"admin_add_guard",
		"admin_list_siege_clans",
		"admin_clear_siege_list",
		"admin_move_defenders",
		"admin_spawn_doors",
		"admin_endsiege",
		"admin_startsiege",
		"admin_setsiegetime",
		"admin_setcastle",
		"admin_removecastle",
		// Clan hall commands
		"admin_clanhall",
		"admin_clanhallset",
		"admin_clanhalldel",
		"admin_clanhallopendoors",
		"admin_clanhallclosedoors",
		"admin_clanhallteleportself"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command
		
		// Get castle
		Castle castle = null;
		ClanHall clanhall = null;
		if (st.hasMoreTokens())
		{
			L2PcInstance player = null;
			if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
			{
				player = activeChar.getTarget().getActingPlayer();
			}
			
			String val = st.nextToken();
			if (command.startsWith("admin_clanhall"))
			{
				if (Util.isDigit(val))
				{
					clanhall = ClanHallManager.getInstance().getClanHallById(Integer.parseInt(val));
					L2Clan clan = null;
					switch (command)
					{
						case "admin_clanhallset":
							if ((player == null) || (player.getClan() == null))
							{
								activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
								return false;
							}
							
							if (clanhall.getOwnerId() > 0)
							{
								activeChar.sendMessage("This Clan Hall is not free!");
								return false;
							}
							
							clan = player.getClan();
							if (clan.getHideoutId() > 0)
							{
								activeChar.sendMessage("You have already a Clan Hall!");
								return false;
							}
							
							if (!clanhall.isSiegableHall())
							{
								ClanHallManager.getInstance().setOwner(clanhall.getId(), clan);
								if (AuctionManager.getInstance().getAuction(clanhall.getId()) != null)
								{
									AuctionManager.getInstance().getAuction(clanhall.getId()).deleteAuctionFromDB();
								}
							}
							else
							{
								clanhall.setOwner(clan);
								clan.setHideoutId(clanhall.getId());
							}
							break;
						case "admin_clanhalldel":
							
							if (!clanhall.isSiegableHall())
							{
								if (!ClanHallManager.getInstance().isFree(clanhall.getId()))
								{
									ClanHallManager.getInstance().setFree(clanhall.getId());
									AuctionManager.getInstance().initNPC(clanhall.getId());
								}
								else
								{
									activeChar.sendMessage("This Clan Hall is already free!");
								}
							}
							else
							{
								final int oldOwner = clanhall.getOwnerId();
								if (oldOwner > 0)
								{
									clanhall.free();
									clan = ClanTable.getInstance().getClan(oldOwner);
									if (clan != null)
									{
										clan.setHideoutId(0);
										clan.broadcastClanStatus();
									}
								}
							}
							break;
						case "admin_clanhallopendoors":
							clanhall.openCloseDoors(true);
							break;
						case "admin_clanhallclosedoors":
							clanhall.openCloseDoors(false);
							break;
						case "admin_clanhallteleportself":
							final L2ClanHallZone zone = clanhall.getZone();
							if (zone != null)
							{
								activeChar.teleToLocation(zone.getSpawnLoc(), true);
							}
							break;
						default:
							if (!clanhall.isSiegableHall())
							{
								showClanHallPage(activeChar, clanhall);
							}
							else
							{
								showSiegableHallPage(activeChar, (SiegableHall) clanhall);
							}
							break;
					}
				}
			}
			else
			{
				castle = CastleManager.getInstance().getCastle(val);
				switch (command)
				{
					case "admin_add_attacker":
						if (player == null)
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
						}
						else
						{
							castle.getSiege().registerAttacker(player, true);
						}
						break;
					case "admin_add_defender":
						if (player == null)
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
						}
						else
						{
							castle.getSiege().registerDefender(player, true);
						}
						break;
					case "admin_add_guard":
						if (st.hasMoreTokens())
						{
							val = st.nextToken();
							if (Util.isDigit(val))
							{
								castle.getSiege().getSiegeGuardManager().addSiegeGuard(activeChar, Integer.parseInt(val));
								break;
							}
						}
						// If doesn't have more tokens or token is not a number.
						activeChar.sendMessage("Usage: //add_guard castle npcId");
						break;
					case "admin_clear_siege_list":
						castle.getSiege().clearSiegeClan();
						break;
					case "admin_endsiege":
						castle.getSiege().endSiege();
						break;
					case "admin_list_siege_clans":
						castle.getSiege().listRegisterClan(activeChar);
						break;
					case "admin_move_defenders":
						activeChar.sendMessage("Not implemented yet.");
						break;
					case "admin_setcastle":
						if ((player == null) || (player.getClan() == null))
						{
							activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
						}
						else
						{
							castle.setOwner(player.getClan());
						}
						break;
					case "admin_removecastle":
						final L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
						if (clan != null)
						{
							castle.removeOwner(clan);
						}
						else
						{
							activeChar.sendMessage("Unable to remove castle.");
						}
						break;
					case "admin_setsiegetime":
						if (st.hasMoreTokens())
						{
							final Calendar cal = Calendar.getInstance();
							cal.setTimeInMillis(castle.getSiegeDate().getTimeInMillis());
							
							val = st.nextToken();
							
							if ("month".equals(val))
							{
								int month = cal.get(Calendar.MONTH) + Integer.parseInt(st.nextToken());
								if ((cal.getActualMinimum(Calendar.MONTH) > month) || (cal.getActualMaximum(Calendar.MONTH) < month))
								{
									activeChar.sendMessage("Unable to change Siege Date - Incorrect month value only " + cal.getActualMinimum(Calendar.MONTH) + "-" + cal.getActualMaximum(Calendar.MONTH) + " is accepted!");
									return false;
								}
								cal.set(Calendar.MONTH, month);
							}
							else if ("day".equals(val))
							{
								int day = Integer.parseInt(st.nextToken());
								if ((cal.getActualMinimum(Calendar.DAY_OF_MONTH) > day) || (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < day))
								{
									activeChar.sendMessage("Unable to change Siege Date - Incorrect day value only " + cal.getActualMinimum(Calendar.DAY_OF_MONTH) + "-" + cal.getActualMaximum(Calendar.DAY_OF_MONTH) + " is accepted!");
								}
								cal.set(Calendar.DAY_OF_MONTH, day);
							}
							else if ("hour".equals(val))
							{
								int hour = Integer.parseInt(st.nextToken());
								if ((cal.getActualMinimum(Calendar.HOUR_OF_DAY) > hour) || (cal.getActualMaximum(Calendar.HOUR_OF_DAY) < hour))
								{
									activeChar.sendMessage("Unable to change Siege Date - Incorrect hour value only " + cal.getActualMinimum(Calendar.HOUR_OF_DAY) + "-" + cal.getActualMaximum(Calendar.HOUR_OF_DAY) + " is accepted!");
									return false;
								}
								cal.set(Calendar.HOUR_OF_DAY, hour);
							}
							else if ("min".equals(val))
							{
								int min = Integer.parseInt(st.nextToken());
								if ((cal.getActualMinimum(Calendar.MINUTE) > min) || (cal.getActualMaximum(Calendar.MINUTE) < min))
								{
									activeChar.sendMessage("Unable to change Siege Date - Incorrect minute value only " + cal.getActualMinimum(Calendar.MINUTE) + "-" + cal.getActualMaximum(Calendar.MINUTE) + " is accepted!");
									return false;
								}
								cal.set(Calendar.MINUTE, min);
							}
							
							if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
							{
								activeChar.sendMessage("Unable to change Siege Date");
							}
							else if (cal.getTimeInMillis() != castle.getSiegeDate().getTimeInMillis())
							{
								castle.getSiegeDate().setTimeInMillis(cal.getTimeInMillis());
								castle.getSiege().saveSiegeDate();
								activeChar.sendMessage("Castle siege time for castle " + castle.getName() + " has been changed.");
							}
						}
						showSiegeTimePage(activeChar, castle);
						return true;
					case "admin_spawn_doors":
						castle.spawnDoor();
						break;
					case "admin_startsiege":
						castle.getSiege().startSiege();
						break;
				}
				
				showSiegePage(activeChar, castle.getName());
			}
		}
		else
		{
			showCastleSelectPage(activeChar);
		}
		return true;
	}
	
	/**
	 * Show castle select page.
	 * @param activeChar the active char
	 */
	private void showCastleSelectPage(L2PcInstance activeChar)
	{
		int i = 0;
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/castles.htm");
		final StringBuilder cList = new StringBuilder(500);
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (castle != null)
			{
				String name = castle.getName();
				StringUtil.append(cList, "<td fixwidth=90><a action=\"bypass -h admin_siege ", name, "\">", name, "</a></td>");
				i++;
			}
			if (i > 2)
			{
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%castles%", cList.toString());
		cList.setLength(0);
		i = 0;
		for (SiegableHall hall : CHSiegeManager.getInstance().getConquerableHalls().values())
		{
			if (hall != null)
			{
				StringUtil.append(cList, "<td fixwidth=90><a action=\"bypass -h admin_chsiege_siegablehall ", String.valueOf(hall.getId()), "\">", hall.getName(), "</a></td>");
				i++;
			}
			if (i > 1)
			{
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%siegableHalls%", cList.toString());
		cList.setLength(0);
		i = 0;
		for (ClanHall clanhall : ClanHallManager.getInstance().getClanHalls().values())
		{
			if (clanhall != null)
			{
				StringUtil.append(cList, "<td fixwidth=134><a action=\"bypass -h admin_clanhall ", String.valueOf(clanhall.getId()), "\">", clanhall.getName(), "</a></td>");
				i++;
			}
			if (i > 1)
			{
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%clanhalls%", cList.toString());
		cList.setLength(0);
		i = 0;
		for (ClanHall clanhall : ClanHallManager.getInstance().getFreeClanHalls().values())
		{
			if (clanhall != null)
			{
				StringUtil.append(cList, "<td fixwidth=134><a action=\"bypass -h admin_clanhall ", String.valueOf(clanhall.getId()), "\">", clanhall.getName(), "</a></td>");
				i++;
			}
			if (i > 1)
			{
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		adminReply.replace("%freeclanhalls%", cList.toString());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * Show the siege page.
	 * @param activeChar the active char
	 * @param castleName the castle name
	 */
	private void showSiegePage(L2PcInstance activeChar, String castleName)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/castle.htm");
		adminReply.replace("%castleName%", castleName);
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * Show the siege time page.
	 * @param activeChar the active char
	 * @param castle the castle
	 */
	private void showSiegeTimePage(L2PcInstance activeChar, Castle castle)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/castlesiegetime.htm");
		adminReply.replace("%castleName%", castle.getName());
		adminReply.replace("%time%", castle.getSiegeDate().getTime().toString());
		final Calendar newDay = Calendar.getInstance();
		boolean isSunday = false;
		if (newDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			isSunday = true;
		}
		else
		{
			newDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}
		
		if (!SevenSigns.getInstance().isDateInSealValidPeriod(newDay))
		{
			newDay.add(Calendar.DAY_OF_MONTH, 7);
		}
		
		if (isSunday)
		{
			adminReply.replace("%sundaylink%", String.valueOf(newDay.get(Calendar.DAY_OF_YEAR)));
			adminReply.replace("%sunday%", String.valueOf(newDay.get(Calendar.MONTH) + "/" + String.valueOf(newDay.get(Calendar.DAY_OF_MONTH))));
			newDay.add(Calendar.DAY_OF_MONTH, 13);
			adminReply.replace("%saturdaylink%", String.valueOf(newDay.get(Calendar.DAY_OF_YEAR)));
			adminReply.replace("%saturday%", String.valueOf(newDay.get(Calendar.MONTH) + "/" + String.valueOf(newDay.get(Calendar.DAY_OF_MONTH))));
		}
		else
		{
			adminReply.replace("%saturdaylink%", String.valueOf(newDay.get(Calendar.DAY_OF_YEAR)));
			adminReply.replace("%saturday%", String.valueOf(newDay.get(Calendar.MONTH) + "/" + String.valueOf(newDay.get(Calendar.DAY_OF_MONTH))));
			newDay.add(Calendar.DAY_OF_MONTH, 1);
			adminReply.replace("%sundaylink%", String.valueOf(newDay.get(Calendar.DAY_OF_YEAR)));
			adminReply.replace("%sunday%", String.valueOf(newDay.get(Calendar.MONTH) + "/" + String.valueOf(newDay.get(Calendar.DAY_OF_MONTH))));
		}
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * Show the clan hall page.
	 * @param activeChar the active char
	 * @param clanhall the clan hall
	 */
	private void showClanHallPage(L2PcInstance activeChar, ClanHall clanhall)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/clanhall.htm");
		adminReply.replace("%clanhallName%", clanhall.getName());
		adminReply.replace("%clanhallId%", String.valueOf(clanhall.getId()));
		final L2Clan owner = ClanTable.getInstance().getClan(clanhall.getOwnerId());
		adminReply.replace("%clanhallOwner%", (owner == null) ? "None" : owner.getName());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * Show the siegable hall page.
	 * @param activeChar the active char
	 * @param hall the siegable hall
	 */
	private void showSiegableHallPage(L2PcInstance activeChar, SiegableHall hall)
	{
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setFile(null, "data/html/admin/siegablehall.htm");
		msg.replace("%clanhallId%", String.valueOf(hall.getId()));
		msg.replace("%clanhallName%", hall.getName());
		if (hall.getOwnerId() > 0)
		{
			final L2Clan owner = ClanTable.getInstance().getClan(hall.getOwnerId());
			msg.replace("%clanhallOwner%", (owner != null) ? owner.getName() : "No Owner");
		}
		else
		{
			msg.replace("%clanhallOwner%", "No Owner");
		}
		activeChar.sendPacket(msg);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
