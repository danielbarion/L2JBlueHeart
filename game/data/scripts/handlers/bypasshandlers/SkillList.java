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

import java.util.List;

import l2r.Config;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2NpcInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

public class SkillList implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"SkillList"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2NpcInstance))
		{
			return false;
		}
		
		if (Config.ALT_GAME_SKILL_LEARN)
		{
			try
			{
				String id = command.substring(9).trim();
				if (id.length() != 0)
				{
					L2NpcInstance.showSkillList(activeChar, (L2Npc) target, ClassId.getClassId(Integer.parseInt(id)));
				}
				else
				{
					boolean own_class = false;
					
					final List<ClassId> classesToTeach = ((L2NpcInstance) target).getClassesToTeach();
					for (ClassId cid : classesToTeach)
					{
						if (cid.equalsOrChildOf(activeChar.getClassId()))
						{
							own_class = true;
							break;
						}
					}
					
					String text = "<html><body><center>Skill learning:</center><br>";
					
					if (!own_class)
					{
						String charType = activeChar.getClassId().isMage() ? "fighter" : "mage";
						text += "Skills of your class are the easiest to learn.<br>" + "Skills of another class of your race are a little harder.<br>" + "Skills for classes of another race are extremely difficult.<br>" + "But the hardest of all to learn are the  " + charType + "skills!<br>";
					}
					
					// make a list of classes
					if (!classesToTeach.isEmpty())
					{
						int count = 0;
						ClassId classCheck = activeChar.getClassId();
						
						while ((count == 0) && (classCheck != null))
						{
							for (ClassId cid : classesToTeach)
							{
								if (cid.level() > classCheck.level())
								{
									continue;
								}
								
								if (SkillTreesData.getInstance().getAvailableSkills(activeChar, cid, false, false).isEmpty())
								{
									continue;
								}
								
								text += "<a action=\"bypass -h npc_%objectId%_SkillList " + cid.getId() + "\">Learn " + cid + "'s class Skills</a><br>\n";
								count++;
							}
							classCheck = classCheck.getParent();
						}
						classCheck = null;
					}
					else
					{
						text += "No Skills.<br>";
					}
					text += "</body></html>";
					
					NpcHtmlMessage html = new NpcHtmlMessage(((L2Npc) target).getObjectId());
					html.setHtml(text);
					html.replace("%objectId%", String.valueOf(((L2Npc) target).getObjectId()));
					activeChar.sendPacket(html);
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
			}
			catch (Exception e)
			{
				_log.warn("Exception in " + getClass().getSimpleName(), e);
			}
		}
		else
		{
			L2NpcInstance.showSkillList(activeChar, (L2Npc) target, activeChar.getClassId());
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
