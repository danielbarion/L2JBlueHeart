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
package handlers.bypasshandlers;

import l2r.Config;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.ClanPrivilege;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2ClanHallManagerInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2WarehouseInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList.WarehouseListType;
import l2r.gameserver.network.serverpackets.WareHouseDepositList;
import l2r.gameserver.network.serverpackets.WareHouseWithdrawalList;

public class ClanWarehouse implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"withdrawc",
		"withdrawsortedc",
		"depositc"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2WarehouseInstance) && !(target instanceof L2ClanHallManagerInstance))
		{
			return false;
		}
		
		if (activeChar.isEnchanting())
		{
			return false;
		}
		
		if (activeChar.getClan() == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
			return false;
		}
		
		if (activeChar.getClan().getLevel() == 0)
		{
			activeChar.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
			return false;
		}
		
		try
		{
			activeChar.setIsUsingAioWh(false);
			if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawC
			{
				if (Config.L2JMOD_ENABLE_WAREHOUSESORTING_CLAN)
				{
					NpcHtmlMessage msg = new NpcHtmlMessage(((L2Npc) target).getObjectId());
					msg.setFile(activeChar.getHtmlPrefix(), "data/html/mods/WhSortedC.htm");
					msg.replace("%objectId%", String.valueOf(((L2Npc) target).getObjectId()));
					activeChar.sendPacket(msg);
				}
				else
				{
					showWithdrawWindow(activeChar, null, (byte) 0);
				}
				return true;
			}
			else if (command.toLowerCase().startsWith(COMMANDS[1])) // WithdrawSortedC
			{
				final String param[] = command.split(" ");
				
				if (param.length > 2)
				{
					showWithdrawWindow(activeChar, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.getOrder(param[2]));
				}
				else if (param.length > 1)
				{
					showWithdrawWindow(activeChar, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.A2Z);
				}
				else
				{
					showWithdrawWindow(activeChar, WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z);
				}
				return true;
			}
			else if (command.toLowerCase().startsWith(COMMANDS[2])) // DepositC
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				activeChar.setActiveWarehouse(activeChar.getClan().getWarehouse());
				activeChar.tempInventoryDisable();
				
				if (Config.DEBUG)
				{
					_log.info("Source: L2WarehouseInstance.java; Player: " + activeChar.getName() + "; Command: showDepositWindowClan; Message: Showing items to deposit.");
				}
				
				activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.CLAN));
				return true;
			}
			
			return false;
		}
		catch (Exception e)
		{
			_log.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	private static final void showWithdrawWindow(L2PcInstance player, WarehouseListType itemtype, byte sortorder)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		if (!player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
			return;
		}
		
		player.setActiveWarehouse(player.getClan().getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
		
		for (L2ItemInstance i : player.getActiveWarehouse().getItems())
		{
			if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0))
			{
				player.getActiveWarehouse().destroyItem("L2ItemInstance", i, player, null);
			}
		}
		
		if (itemtype != null)
		{
			player.sendPacket(new SortedWareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN, itemtype, sortorder));
		}
		else
		{
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN));
		}
		
		if (Config.DEBUG)
		{
			_log.info("Source: L2WarehouseInstance.java; Player: " + player.getName() + "; Command: showRetrieveWindowClan; Message: Showing stored items.");
		}
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
