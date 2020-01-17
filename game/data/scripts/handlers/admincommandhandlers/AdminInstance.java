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

import java.util.StringTokenizer;

import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;

/**
 * @author evill33t, GodKratos
 */
public class AdminInstance implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_setinstance",
		"admin_ghoston",
		"admin_ghostoff",
		"admin_createinstance",
		"admin_destroyinstance",
		"admin_listinstances"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		// create new instance
		if (command.startsWith("admin_createinstance"))
		{
			String[] parts = command.split(" ");
			if (parts.length != 3)
			{
				activeChar.sendMessage("Example: //createinstance <id> <templatefile> - ids => 300000 are reserved for dynamic instances");
			}
			else
			{
				try
				{
					final int id = Integer.parseInt(parts[1]);
					if ((id < 300000) && InstanceManager.getInstance().createInstanceFromTemplate(id, parts[2]))
					{
						activeChar.sendMessage("Instance created.");
					}
					else
					{
						activeChar.sendMessage("Failed to create instance.");
					}
					return true;
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Failed loading: " + parts[1] + " " + parts[2]);
					return false;
				}
			}
		}
		else if (command.startsWith("admin_listinstances"))
		{
			for (Instance temp : InstanceManager.getInstance().getInstances().values())
			{
				activeChar.sendMessage("Id: " + temp.getId() + " Name: " + temp.getName());
			}
		}
		else if (command.startsWith("admin_setinstance"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				if (InstanceManager.getInstance().getInstance(val) == null)
				{
					activeChar.sendMessage("Instance " + val + " doesnt exist.");
					return false;
				}
				
				L2Object target = activeChar.getTarget();
				if ((target == null) || (target instanceof L2Summon)) // Don't separate summons from masters
				{
					activeChar.sendMessage("Incorrect target.");
					return false;
				}
				target.setInstanceId(val);
				if (target instanceof L2PcInstance)
				{
					L2PcInstance player = (L2PcInstance) target;
					player.sendMessage("Admin set your instance to:" + val);
					player.teleToLocation(player.getX(), player.getY(), player.getZ());
				}
				activeChar.sendMessage("Moved " + target.getName() + " to instance " + target.getInstanceId() + ".");
				return true;
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use //setinstance id");
			}
		}
		else if (command.startsWith("admin_destroyinstance"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				InstanceManager.getInstance().destroyInstance(val);
				activeChar.sendMessage("Instance destroyed");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use //destroyinstance id");
			}
		}
		
		// set ghost mode on aka not appearing on any knownlist
		// you will be invis to all players but you also dont get update packets ;)
		// you will see snapshots (knownlist echoes?) if you port
		// so kinda useless atm
		// TODO: enable broadcast packets for ghosts
		else if (command.startsWith("admin_ghoston"))
		{
			activeChar.getAppearance().setGhostMode(true);
			activeChar.sendMessage("Ghost mode enabled");
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
		}
		// ghost mode off
		else if (command.startsWith("admin_ghostoff"))
		{
			activeChar.getAppearance().setGhostMode(false);
			activeChar.sendMessage("Ghost mode disabled");
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}