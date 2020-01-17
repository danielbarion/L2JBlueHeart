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

import l2r.Config;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.stat.PcStat;

/**
 * @author Psychokiller1888
 */
public class AdminVitality implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_set_vitality",
		"admin_set_vitality_level",
		"admin_full_vitality",
		"admin_empty_vitality",
		"admin_get_vitality"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!Config.ENABLE_VITALITY)
		{
			activeChar.sendMessage("Vitality is not enabled on the server!");
			return false;
		}
		
		int level = 0;
		int vitality = 0;
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String cmd = st.nextToken();
		
		if (activeChar.getTarget() instanceof L2PcInstance)
		{
			L2PcInstance target;
			target = (L2PcInstance) activeChar.getTarget();
			
			if (cmd.equals("admin_set_vitality"))
			{
				try
				{
					vitality = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Incorrect vitality");
				}
				
				target.setVitalityPoints(vitality, true);
				target.sendMessage("Admin set your Vitality points to " + vitality);
			}
			else if (cmd.equals("admin_set_vitality_level"))
			{
				try
				{
					level = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Incorrect vitality level (0-4)");
				}
				
				if ((level >= 0) && (level <= 4))
				{
					if (level == 0)
					{
						vitality = PcStat.MIN_VITALITY_POINTS;
					}
					else
					{
						vitality = PcStat.VITALITY_LEVELS[level - 1];
					}
					target.setVitalityPoints(vitality, true);
					target.sendMessage("Admin set your Vitality level to " + level);
				}
				else
				{
					activeChar.sendMessage("Incorrect vitality level (0-4)");
				}
			}
			else if (cmd.equals("admin_full_vitality"))
			{
				target.setVitalityPoints(PcStat.MAX_VITALITY_POINTS, true);
				target.sendMessage("Admin completly recharged your Vitality");
			}
			else if (cmd.equals("admin_empty_vitality"))
			{
				target.setVitalityPoints(PcStat.MIN_VITALITY_POINTS, true);
				target.sendMessage("Admin completly emptied your Vitality");
			}
			else if (cmd.equals("admin_get_vitality"))
			{
				level = target.getVitalityLevel();
				vitality = target.getVitalityPoints();
				
				activeChar.sendMessage("Player vitality level: " + level);
				activeChar.sendMessage("Player vitality points: " + vitality);
			}
			return true;
		}
		activeChar.sendMessage("Target not found or not a player");
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
