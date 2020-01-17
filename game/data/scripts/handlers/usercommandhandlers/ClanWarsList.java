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
package handlers.usercommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2r.L2DatabaseFactory;
import l2r.gameserver.handler.IUserCommandHandler;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clan War Start, Under Attack List, War List user commands.
 * @author Tempy
 */
public class ClanWarsList implements IUserCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(ClanWarsList.class);
	private static final int[] COMMAND_IDS =
	{
		88,
		89,
		90
	};
	// SQL queries
	private static final String ATTACK_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 AND clan2 NOT IN (SELECT clan1 FROM clan_wars WHERE clan2=?)";
	private static final String UNDER_ATTACK_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan2=? AND clan_id=clan1 AND clan1 NOT IN (SELECT clan2 FROM clan_wars WHERE clan1=?)";
	private static final String WAR_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 AND clan2 IN (SELECT clan1 FROM clan_wars WHERE clan2=?)";
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if ((id != COMMAND_IDS[0]) && (id != COMMAND_IDS[1]) && (id != COMMAND_IDS[2]))
		{
			return false;
		}
		
		final L2Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(SystemMessageId.NOT_JOINED_IN_ANY_CLAN);
			return false;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String query;
			// Attack List
			if (id == 88)
			{
				activeChar.sendPacket(SystemMessageId.CLANS_YOU_DECLARED_WAR_ON);
				query = ATTACK_LIST;
			}
			// Under Attack List
			else if (id == 89)
			{
				activeChar.sendPacket(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
				query = UNDER_ATTACK_LIST;
			}
			// War List
			else
			{
				activeChar.sendPacket(SystemMessageId.WAR_LIST);
				query = WAR_LIST;
			}
			
			try (PreparedStatement ps = con.prepareStatement(query))
			{
				ps.setInt(1, clan.getId());
				ps.setInt(2, clan.getId());
				
				SystemMessage sm;
				try (ResultSet rs = ps.executeQuery())
				{
					String clanName;
					int ally_id;
					while (rs.next())
					{
						clanName = rs.getString("clan_name");
						ally_id = rs.getInt("ally_id");
						if (ally_id > 0)
						{
							// Target With Ally
							sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_ALLIANCE);
							sm.addString(clanName);
							sm.addString(rs.getString("ally_name"));
						}
						else
						{
							// Target Without Ally
							sm = SystemMessage.getSystemMessage(SystemMessageId.S1_NO_ALLI_EXISTS);
							sm.addString(clanName);
						}
						activeChar.sendPacket(sm);
					}
				}
			}
			activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
		}
		catch (Exception e)
		{
			_log.warn("", e);
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
