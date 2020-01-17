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
package handlers.actionshifthandlers;

import l2r.Config;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.handler.IActionShiftHandler;
import l2r.gameserver.instancemanager.WalkingManager;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.L2DropCategory;
import l2r.gameserver.model.L2DropData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.stats.BaseStats;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.util.StringUtil;

public class L2NpcActionShift implements IActionShiftHandler
{
	/**
	 * Manage and Display the GM console to modify the L2NpcInstance (GM only).<BR>
	 * <BR>
	 * <B><U> Actions (If the L2PcInstance is a GM only)</U> :</B><BR>
	 * <BR>
	 * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li>
	 * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
	 * <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar</li>
	 * <li>Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid that client wait an other packet</B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Client packet : Action</li><BR>
	 * <BR>
	 */
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact)
	{
		// Check if the L2PcInstance is a GM
		if (activeChar.getAccessLevel().isGm())
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			final NpcHtmlMessage html = new NpcHtmlMessage();
			html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/npcinfo.htm");
			
			html.replace("%objid%", String.valueOf(target.getObjectId()));
			html.replace("%class%", target.getClass().getSimpleName());
			html.replace("%id%", String.valueOf(((L2Npc) target).getTemplate().getId()));
			html.replace("%lvl%", String.valueOf(((L2Npc) target).getTemplate().getLevel()));
			html.replace("%name%", String.valueOf(((L2Npc) target).getTemplate().getName()));
			html.replace("%tmplid%", String.valueOf(((L2Npc) target).getTemplate().getId()));
			html.replace("%aggro%", String.valueOf((target instanceof L2Attackable) ? ((L2Attackable) target).getAggroRange() : 0));
			html.replace("%hp%", String.valueOf((int) ((L2Character) target).getCurrentHp()));
			html.replace("%hpmax%", String.valueOf(((L2Character) target).getMaxHp()));
			html.replace("%mp%", String.valueOf((int) ((L2Character) target).getCurrentMp()));
			html.replace("%mpmax%", String.valueOf(((L2Character) target).getMaxMp()));
			
			html.replace("%patk%", String.valueOf((int) ((L2Character) target).getPAtk(null)));
			html.replace("%matk%", String.valueOf((int) ((L2Character) target).getMAtk(null, null)));
			html.replace("%pdef%", String.valueOf((int) ((L2Character) target).getPDef(null)));
			html.replace("%mdef%", String.valueOf((int) ((L2Character) target).getMDef(null, null)));
			html.replace("%accu%", String.valueOf(((L2Character) target).getAccuracy()));
			html.replace("%evas%", String.valueOf(((L2Character) target).getEvasionRate(null)));
			html.replace("%crit%", String.valueOf(((L2Character) target).getCriticalHit(null, null)));
			html.replace("%rspd%", String.valueOf(((L2Character) target).getRunSpeed()));
			html.replace("%aspd%", String.valueOf(((L2Character) target).getPAtkSpd()));
			html.replace("%cspd%", String.valueOf(((L2Character) target).getMAtkSpd()));
			html.replace("%str%", String.valueOf(((L2Character) target).getSTR()));
			html.replace("%dex%", String.valueOf(((L2Character) target).getDEX()));
			html.replace("%con%", String.valueOf(((L2Character) target).getCON()));
			html.replace("%int%", String.valueOf(((L2Character) target).getINT()));
			html.replace("%wit%", String.valueOf(((L2Character) target).getWIT()));
			html.replace("%men%", String.valueOf(((L2Character) target).getMEN()));
			html.replace("%loc%", String.valueOf(target.getX() + " " + target.getY() + " " + target.getZ()));
			html.replace("%heading%", String.valueOf(((L2Character) target).getHeading()));
			html.replace("%collision_radius%", String.valueOf(((L2Character) target).getTemplate().getfCollisionRadius()));
			html.replace("%collision_height%", String.valueOf(((L2Character) target).getTemplate().getfCollisionHeight()));
			html.replace("%dist%", String.valueOf((int) activeChar.calculateDistance(target, true, false)));
			
			byte attackAttribute = ((L2Character) target).getAttackElement();
			html.replace("%ele_atk%", Elementals.getElementName(attackAttribute));
			html.replace("%ele_atk_value%", String.valueOf(((L2Character) target).getAttackElementValue(attackAttribute)));
			html.replace("%ele_dfire%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.FIRE)));
			html.replace("%ele_dwater%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.WATER)));
			html.replace("%ele_dwind%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.WIND)));
			html.replace("%ele_dearth%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.EARTH)));
			html.replace("%ele_dholy%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.HOLY)));
			html.replace("%ele_ddark%", String.valueOf(((L2Character) target).getDefenseElementValue(Elementals.DARK)));
			
			if (((L2Npc) target).getSpawn() != null)
			{
				html.replace("%territory%", ((L2Npc) target).getSpawn().getSpawnTerritory() == null ? "None" : ((L2Npc) target).getSpawn().getSpawnTerritory().getName());
				if (((L2Npc) target).getSpawn().isTerritoryBased())
				{
					html.replace("%spawntype%", "Random");
					html.replace("%spawn%", ((L2Npc) target).getSpawn().getX(target) + " " + ((L2Npc) target).getSpawn().getY(target) + " " + ((L2Npc) target).getSpawn().getZ(target));
				}
				else
				{
					html.replace("%spawntype%", "Fixed");
					html.replace("%spawn%", ((L2Npc) target).getSpawn().getX() + " " + ((L2Npc) target).getSpawn().getY() + " " + ((L2Npc) target).getSpawn().getZ());
				}
				html.replace("%loc2d%", String.valueOf((int) target.calculateDistance(((L2Npc) target).getSpawn().getLocation(target), false, false)));
				html.replace("%loc3d%", String.valueOf((int) target.calculateDistance(((L2Npc) target).getSpawn().getLocation(target), true, false)));
				if (((L2Npc) target).getSpawn().getRespawnMinDelay() == 0)
				{
					html.replace("%resp%", "None");
				}
				else if (((L2Npc) target).getSpawn().hasRespawnRandom())
				{
					html.replace("%resp%", String.valueOf(((L2Npc) target).getSpawn().getRespawnMinDelay() / 1000) + "-" + String.valueOf((((L2Npc) target).getSpawn().getRespawnMaxDelay() / 1000) + " sec"));
				}
				else
				{
					html.replace("%resp%", String.valueOf(((L2Npc) target).getSpawn().getRespawnMinDelay() / 1000) + " sec");
				}
			}
			else
			{
				html.replace("%territory%", "<font color=FF0000>--</font>");
				html.replace("%spawntype%", "<font color=FF0000>--</font>");
				html.replace("%spawn%", "<font color=FF0000>null</font>");
				html.replace("%loc2d%", "<font color=FF0000>--</font>");
				html.replace("%loc3d%", "<font color=FF0000>--</font>");
				html.replace("%resp%", "<font color=FF0000>--</font>");
			}
			
			if (((L2Npc) target).hasAI())
			{
				html.replace("%ai_intention%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Intention:</font></td><td align=right width=170>" + String.valueOf(((L2Npc) target).getAI().getIntention().name()) + "</td></tr></table></td></tr>");
				html.replace("%ai%", "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>AI</font></td><td align=right width=170>" + ((L2Npc) target).getAI().getClass().getSimpleName() + "</td></tr></table></td></tr>");
				html.replace("%ai_type%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>AIType</font></td><td align=right width=170>" + String.valueOf(((L2Npc) target).getAiType()) + "</td></tr></table></td></tr>");
				html.replace("%ai_clan%", "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>Clan & Range:</font></td><td align=right width=170>" + String.valueOf(((L2Npc) target).getTemplate().getAIDataStatic().getClan()) + " " + String.valueOf(((L2Npc) target).getTemplate().getAIDataStatic().getClanRange()) + "</td></tr></table></td></tr>");
				html.replace("%ai_enemy_clan%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Enemy & Range:</font></td><td align=right width=170>" + String.valueOf(((L2Npc) target).getTemplate().getAIDataStatic().getEnemyClan()) + " " + String.valueOf(((L2Npc) target).getTemplate().getAIDataStatic().getEnemyRange()) + "</td></tr></table></td></tr>");
				html.replace("%ai_can_random_walk%", "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>Random Walk:</font></td><td align=right width=170>" + !((L2Npc) target).isNoRndWalk() + "</td></tr></table></td></tr>");
			}
			else
			{
				html.replace("%ai_intention%", "");
				html.replace("%ai%", "");
				html.replace("%ai_type%", "");
				html.replace("%ai_clan%", "");
				html.replace("%ai_enemy_clan%", "");
				html.replace("%ai_can_random_walk%", "");
			}
			
			final String routeName = WalkingManager.getInstance().getRouteName((L2Npc) target);
			if (!routeName.isEmpty())
			{
				html.replace("%route%", "<tr><td><table width=270 border=0><tr><td width=100><font color=LEVEL>Route:</font></td><td align=right width=170>" + routeName + "</td></tr></table></td></tr>");
			}
			else
			{
				html.replace("%route%", "");
			}
			
			activeChar.sendPacket(html);
		}
		else if (Config.ALT_GAME_VIEWNPC)
		{
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(target);
			
			final NpcHtmlMessage html = new NpcHtmlMessage();
			int hpMul = Math.round((float) (((L2Character) target).getStat().calcStat(Stats.MAX_HP, 1, (L2Character) target, null) / BaseStats.CON.calcBonus((L2Character) target)));
			if (hpMul == 0)
			{
				hpMul = 1;
			}
			final StringBuilder html1 = StringUtil.startAppend(1000, "<html><body>" + "<br><center><font color=\"LEVEL\">[Combat Stats]</font></center>" + "<table border=0 width=\"100%\">" + "<tr><td>Max.HP</td><td>", String.valueOf(((L2Character) target).getMaxHp() / hpMul), "*", String.valueOf(hpMul), "</td><td>Max.MP</td><td>", String.valueOf(((L2Character) target).getMaxMp()), "</td></tr>" + "<tr><td>P.Atk.</td><td>", String.valueOf(((L2Character) target).getPAtk(null)), "</td><td>M.Atk.</td><td>", String.valueOf(((L2Character) target).getMAtk(null, null)), "</td></tr>" + "<tr><td>P.Def.</td><td>", String.valueOf(((L2Character) target).getPDef(null)), "</td><td>M.Def.</td><td>", String.valueOf(((L2Character) target).getMDef(null, null)), "</td></tr>" + "<tr><td>Accuracy</td><td>", String.valueOf(((L2Character) target).getAccuracy()), "</td><td>Evasion</td><td>", String.valueOf(((L2Character) target).getEvasionRate(null)), "</td></tr>" + "<tr><td>Critical</td><td>", String.valueOf(((L2Character) target).getCriticalHit(null, null)), "</td><td>Speed</td><td>", String.valueOf(((L2Character) target).getRunSpeed()), "</td></tr>" + "<tr><td>Atk.Speed</td><td>", String.valueOf(((L2Character) target).getPAtkSpd()), "</td><td>Cast.Speed</td><td>", String.valueOf(((L2Character) target).getMAtkSpd()), "</td></tr>" + "<tr><td>Race</td><td>", ((L2Npc) target).getTemplate().getRace().toString(), "</td><td></td><td></td></tr>" + "</table>" + "<br><center><font color=\"LEVEL\">[Basic Stats]</font></center>" + "<table border=0 width=\"100%\">" + "<tr><td>STR</td><td>", String.valueOf(((L2Character) target).getSTR()), "</td><td>DEX</td><td>", String.valueOf(((L2Character) target).getDEX()), "</td><td>CON</td><td>", String.valueOf(((L2Character) target).getCON()), "</td></tr>" + "<tr><td>INT</td><td>", String.valueOf(((L2Character) target).getINT()), "</td><td>WIT</td><td>", String.valueOf(((L2Character) target).getWIT()), "</td><td>MEN</td><td>", String.valueOf(((L2Character) target).getMEN()), "</td></tr>" + "</table>");
			
			if (!((L2Npc) target).getTemplate().getDropData().isEmpty())
			{
				StringUtil.append(html1, "<br><center><font color=\"LEVEL\">[Drop Info]</font></center>" + "<br>Rates legend: <font color=\"ff9999\">50%+</font> <font color=\"00ff00\">30%+</font> <font color=\"0066ff\">less than 30%</font>" + "<table border=0 width=\"100%\">");
				for (L2DropCategory cat : ((L2Npc) target).getTemplate().getDropData())
				{
					for (L2DropData drop : cat.getAllDrops())
					{
						final L2Item item = ItemData.getInstance().getTemplate(drop.getId());
						if (item == null)
						{
							continue;
						}
						
						final String color;
						
						if (drop.getChance() >= 500000)
						{
							color = "ff9999";
						}
						else if (drop.getChance() >= 300000)
						{
							color = "00ff00";
						}
						else
						{
							color = "0066ff";
						}
						
						StringUtil.append(html1, "<tr>", "<td><img src=\"" + item.getIcon() + "\" height=32 width=32></td>" + "<td><font color=\"", color, "\">", item.getName(), "</font></td>", "<td>", (drop.isQuestDrop() ? "Quest" : (cat.isSweep() ? "Sweep" : "Drop")), "</td>", "</tr>");
					}
				}
				html1.append("</table>");
			}
			html1.append("</body></html>");
			
			html.setHtml(html1.toString());
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
