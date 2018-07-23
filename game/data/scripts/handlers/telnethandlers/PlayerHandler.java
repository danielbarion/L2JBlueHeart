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
package handlers.telnethandlers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import l2r.Config;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.handler.ITelnetHandler;
import l2r.gameserver.instancemanager.PunishmentManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.punishment.PunishmentAffect;
import l2r.gameserver.model.punishment.PunishmentTask;
import l2r.gameserver.model.punishment.PunishmentType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.GMAudit;
import l2r.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class PlayerHandler implements ITelnetHandler
{
	private final String[] _commands =
	{
		"kick",
		"give",
		"enchant",
		"jail",
		"unjail"
	};
	
	@Override
	public boolean useCommand(String command, PrintWriter _print, Socket _cSocket, int _uptime)
	{
		if (command.startsWith("kick"))
		{
			try
			{
				command = command.substring(5);
				L2PcInstance player = L2World.getInstance().getPlayer(command);
				if (player != null)
				{
					player.sendMessage("You are kicked by gm");
					player.logout();
					_print.println("Player kicked");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				_print.println("Please enter player name to kick");
			}
		}
		else if (command.startsWith("give"))
		{
			StringTokenizer st = new StringTokenizer(command.substring(5));
			
			try
			{
				L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
				int itemId = Integer.parseInt(st.nextToken());
				int amount = Integer.parseInt(st.nextToken());
				
				if (player != null)
				{
					L2ItemInstance item = player.getInventory().addItem("Status-Give", itemId, amount, null, null);
					InventoryUpdate iu = new InventoryUpdate();
					iu.addItem(item);
					player.sendPacket(iu);
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1_S2);
					sm.addItemName(itemId);
					sm.addLong(amount);
					player.sendPacket(sm);
					_print.println("ok");
					GMAudit.auditGMAction("Telnet Admin", "Give Item", player.getName(), "item: " + itemId + " amount: " + amount);
				}
				else
				{
					_print.println("Player not found");
				}
			}
			catch (Exception e)
			{
			
			}
		}
		else if (command.startsWith("enchant"))
		{
			StringTokenizer st = new StringTokenizer(command.substring(8), " ");
			int enchant = 0, itemType = 0;
			
			try
			{
				L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
				itemType = Integer.parseInt(st.nextToken());
				enchant = Integer.parseInt(st.nextToken());
				
				switch (itemType)
				{
					case 1:
						itemType = Inventory.PAPERDOLL_HEAD;
						break;
					case 2:
						itemType = Inventory.PAPERDOLL_CHEST;
						break;
					case 3:
						itemType = Inventory.PAPERDOLL_GLOVES;
						break;
					case 4:
						itemType = Inventory.PAPERDOLL_FEET;
						break;
					case 5:
						itemType = Inventory.PAPERDOLL_LEGS;
						break;
					case 6:
						itemType = Inventory.PAPERDOLL_RHAND;
						break;
					case 7:
						itemType = Inventory.PAPERDOLL_LHAND;
						break;
					case 8:
						itemType = Inventory.PAPERDOLL_LEAR;
						break;
					case 9:
						itemType = Inventory.PAPERDOLL_REAR;
						break;
					case 10:
						itemType = Inventory.PAPERDOLL_LFINGER;
						break;
					case 11:
						itemType = Inventory.PAPERDOLL_RFINGER;
						break;
					case 12:
						itemType = Inventory.PAPERDOLL_NECK;
						break;
					case 13:
						itemType = Inventory.PAPERDOLL_UNDER;
						break;
					case 14:
						itemType = Inventory.PAPERDOLL_CLOAK;
						break;
					case 15:
						itemType = Inventory.PAPERDOLL_BELT;
						break;
					default:
						itemType = 0;
				}
				
				if (enchant > 65535)
				{
					enchant = 65535;
				}
				else if (enchant < 0)
				{
					enchant = 0;
				}
				
				boolean success = false;
				
				if ((player != null) && (itemType > 0))
				{
					success = setEnchant(player, enchant, itemType);
					if (success)
					{
						_print.println("Item enchanted successfully.");
					}
				}
				else if (!success)
				{
					_print.println("Item failed to enchant.");
				}
			}
			catch (Exception e)
			{
			
			}
		}
		else if (command.startsWith("jail"))
		{
			StringTokenizer st = new StringTokenizer(command.substring(5));
			try
			{
				String name = st.nextToken();
				int charId = CharNameTable.getInstance().getIdByName(name);
				int delay = 0;
				String reason = "";
				if (st.hasMoreTokens())
				{
					String token = st.nextToken();
					if (Util.isDigit(token))
					{
						delay = Integer.parseInt(token);
					}
					while (st.hasMoreTokens())
					{
						reason += st.nextToken() + " ";
					}
					if (!reason.isEmpty())
					{
						reason = reason.substring(0, reason.length() - 1);
					}
				}
				
				if (charId > 0)
				{
					long expirationTime = delay > 0 ? System.currentTimeMillis() + (delay * 60 * 1000) : -1;
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(charId, PunishmentAffect.CHARACTER, PunishmentType.JAIL, expirationTime, reason, "Telnet Admin: " + _cSocket.getInetAddress().getHostAddress()));
					_print.println("Character " + name + " jailed for " + (delay > 0 ? delay + " minutes." : "ever!"));
				}
				else
				{
					_print.println("Character with name: " + name + " was not found!");
				}
			}
			catch (NoSuchElementException nsee)
			{
				_print.println("Specify a character name.");
			}
			catch (Exception e)
			{
				if (Config.DEBUG)
				{
					e.printStackTrace();
				}
			}
		}
		else if (command.startsWith("unjail"))
		{
			StringTokenizer st = new StringTokenizer(command.substring(7));
			try
			{
				String name = st.nextToken();
				int charId = CharNameTable.getInstance().getIdByName(name);
				
				if (charId > 0)
				{
					PunishmentManager.getInstance().stopPunishment(charId, PunishmentAffect.CHARACTER, PunishmentType.JAIL);
					_print.println("Character " + name + " have been unjailed");
				}
				else
				{
					_print.println("Character with name: " + name + " was not found!");
				}
			}
			catch (NoSuchElementException nsee)
			{
				_print.println("Specify a character name.");
			}
			catch (Exception e)
			{
				if (Config.DEBUG)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private boolean setEnchant(L2PcInstance activeChar, int ench, int armorType)
	{
		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		L2ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		L2ItemInstance parmorInstance = activeChar.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		else
		{
			// for bows/crossbows and double handed weapons
			parmorInstance = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == Inventory.PAPERDOLL_RHAND))
			{
				itemInstance = parmorInstance;
			}
		}
		
		if (itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();
			
			// set enchant value
			activeChar.getInventory().unEquipItemInSlot(armorType);
			itemInstance.setEnchantLevel(ench);
			activeChar.getInventory().equipItem(itemInstance);
			
			// send packets
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
			
			// informations
			activeChar.sendMessage("Changed enchantment of " + activeChar.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			activeChar.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			
			// log
			GMAudit.auditGMAction("TelnetAdministrator", "enchant", activeChar.getName(), itemInstance.getItem().getName() + "(" + itemInstance.getObjectId() + ")" + " from " + curEnchant + " to " + ench);
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getCommandList()
	{
		return _commands;
	}
}
