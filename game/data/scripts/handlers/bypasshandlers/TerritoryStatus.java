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

import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

public class TerritoryStatus implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"TerritoryStatus"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		final L2Npc npc = (L2Npc) target;
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		{
			if (npc.getCastle().getOwnerId() > 0)
			{
				html.setFile(activeChar.getHtmlPrefix(), "data/html/territorystatus.htm");
				L2Clan clan = ClanTable.getInstance().getClan(npc.getCastle().getOwnerId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
			}
			else
			{
				html.setFile(activeChar.getHtmlPrefix(), "data/html/territorynoclan.htm");
			}
		}
		html.replace("%castlename%", npc.getCastle().getName());
		html.replace("%taxpercent%", "" + npc.getCastle().getTaxPercent());
		html.replace("%objectId%", String.valueOf(npc.getObjectId()));
		{
			if (npc.getCastle().getResidenceId() > 6)
			{
				html.replace("%territory%", "The Kingdom of Elmore");
			}
			else
			{
				html.replace("%territory%", "The Kingdom of Aden");
			}
		}
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
