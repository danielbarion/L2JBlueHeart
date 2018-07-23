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

import l2r.gameserver.data.xml.impl.AdminData;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;

/**
 * This class handles following admin commands: - gmchat text = sends text to all online GM's - gmchat_menu text = same as gmchat, displays the admin panel after chat
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGmChat implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gmchat",
		"admin_snoop",
		"admin_gmchat_menu"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_gmchat"))
		{
			handleGmChat(command, activeChar);
		}
		else if (command.startsWith("admin_snoop"))
		{
			snoop(command, activeChar);
		}
		if (command.startsWith("admin_gmchat_menu"))
		{
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		return true;
	}
	
	/**
	 * @param command
	 * @param activeChar
	 */
	private void snoop(String command, L2PcInstance activeChar)
	{
		L2Object target = null;
		if (command.length() > 12)
		{
			target = L2World.getInstance().getPlayer(command.substring(12));
		}
		if (target == null)
		{
			target = activeChar.getTarget();
		}
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.SELECT_TARGET);
			return;
		}
		if (!(target instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		L2PcInstance player = (L2PcInstance) target;
		player.addSnooper(activeChar);
		activeChar.addSnooped(player);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * @param command
	 * @param activeChar
	 */
	private void handleGmChat(String command, L2PcInstance activeChar)
	{
		try
		{
			int offset = 0;
			String text;
			if (command.startsWith("admin_gmchat_menu"))
			{
				offset = 18;
			}
			else
			{
				offset = 13;
			}
			text = command.substring(offset);
			CreatureSay cs = new CreatureSay(0, Say2.ALLIANCE, activeChar.getVar("namePrefix", "") + activeChar.getName(), text);
			AdminData.getInstance().broadcastToGMs(cs);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// Who cares?
		}
	}
}
