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
package ai.grandboss.Baium;

import l2r.Config;
import l2r.gameserver.enums.CategoryType;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.enums.MountType;
import l2r.gameserver.enums.audio.Music;
import l2r.gameserver.instancemanager.GrandBossManager;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2GrandBossInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.variables.NpcVariables;
import l2r.gameserver.model.zone.type.L2NoRestartZone;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.Earthquake;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Baium AI.
 * @author St3eT
 */
public final class Baium extends AbstractNpcAI
{
	// NPCs
	private static final int BAIUM = 29020; // Baium
	private static final int BAIUM_STONE = 29025; // Baium
	private static final int ANG_VORTEX = 31862; // Angelic Vortex
	private static final int ARCHANGEL = 29021; // Archangel
	private static final int TELE_CUBE = 31842; // Teleportation Cubic
	// Skills
	private static final SkillHolder BAIUM_ATTACK = new SkillHolder(4127, 1); // Baium: General Attack
	private static final SkillHolder ENERGY_WAVE = new SkillHolder(4128, 1); // Wind Of Force
	private static final SkillHolder EARTH_QUAKE = new SkillHolder(4129, 1); // Earthquake
	private static final SkillHolder THUNDERBOLT = new SkillHolder(4130, 1); // Striking of Thunderbolt
	private static final SkillHolder GROUP_HOLD = new SkillHolder(4131, 1); // Stun
	private static final SkillHolder SPEAR_ATTACK = new SkillHolder(4132, 1); // Spear: Pound the Ground
	private static final SkillHolder ANGEL_HEAL = new SkillHolder(4133, 1); // Angel Heal
	private static final SkillHolder HEAL_OF_BAIUM = new SkillHolder(4135, 1); // Baium Heal
	private static final SkillHolder BAIUM_PRESENT = new SkillHolder(4136, 1); // Baium's Gift
	private static final SkillHolder ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	// Items
	private static final int FABRIC = 4295; // Blooded Fabric
	// Zone
	private static final L2NoRestartZone zone = ZoneManager.getInstance().getZoneById(70051, L2NoRestartZone.class); // Baium zone
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int IN_FIGHT = 2;
	private static final int DEAD = 3;
	// Locations
	private static final Location BAIUM_GIFT_LOC = new Location(115910, 17337, 10105);
	private static final Location BAIUM_LOC = new Location(116033, 17447, 10107, -25348);
	private static final Location TELEPORT_CUBIC_LOC = new Location(115017, 15549, 10090);
	private static final Location TELEPORT_IN_LOC = new Location(114077, 15882, 10078);
	private static final Location[] TELEPORT_OUT_LOC =
	{
		new Location(108784, 16000, -4928),
		new Location(113824, 10448, -5164),
		new Location(115488, 22096, -5168),
	};
	private static final Location[] ARCHANGEL_LOC =
	{
		new Location(115792, 16608, 10136, 0),
		new Location(115168, 17200, 10136, 0),
		new Location(115780, 15564, 10136, 13620),
		new Location(114880, 16236, 10136, 5400),
		new Location(114239, 17168, 10136, -1992)
	};
	// Misc
	private L2GrandBossInstance _baium = null;
	private static long _lastAttack = 0;
	
	public Baium()
	{
		super(Baium.class.getSimpleName(), "ai/grandboss");
		addFirstTalkId(ANG_VORTEX);
		addTalkId(ANG_VORTEX, TELE_CUBE, BAIUM_STONE);
		addStartNpc(ANG_VORTEX, TELE_CUBE, BAIUM_STONE);
		addAttackId(BAIUM, ARCHANGEL);
		addKillId(BAIUM);
		addSeeCreatureId(BAIUM);
		addSpellFinishedId(BAIUM);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(BAIUM);
		final int curr_hp = info.getInt("currentHP");
		final int curr_mp = info.getInt("currentMP");
		final int loc_x = info.getInt("loc_x");
		final int loc_y = info.getInt("loc_y");
		final int loc_z = info.getInt("loc_z");
		final int heading = info.getInt("heading");
		final long respawnTime = info.getLong("respawn_time");
		
		switch (getStatus())
		{
			case WAITING:
			{
				setStatus(ALIVE);
				break;
			}
			case ALIVE:
			{
				addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0);
				break;
			}
			case IN_FIGHT:
			{
				_baium = (L2GrandBossInstance) addSpawn(BAIUM, loc_x, loc_y, loc_z, heading, false, 0);
				_baium.setCurrentHpMp(curr_hp, curr_mp);
				_lastAttack = System.currentTimeMillis();
				addBoss(_baium);
				
				for (Location loc : ARCHANGEL_LOC)
				{
					final L2Npc archangel = addSpawn(ARCHANGEL, loc, false, 0, true);
					startQuestTimer("SELECT_TARGET", 5000, archangel, null);
				}
				startQuestTimer("CHECK_ATTACK", 60000, _baium, null);
				break;
			}
			case DEAD:
			{
				final long remain = respawnTime - System.currentTimeMillis();
				if (remain > 0)
				{
					startQuestTimer("CLEAR_STATUS", remain, null, null);
				}
				else
				{
					notifyEvent("CLEAR_STATUS", null, null);
				}
				break;
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "31862-04.html":
			{
				return event;
			}
			case "enter":
			{
				String htmltext = null;
				if (getStatus() == DEAD)
				{
					htmltext = "31862-03.html";
				}
				else if (getStatus() == IN_FIGHT)
				{
					htmltext = "31862-02.html";
				}
				else if (!hasQuestItems(player, FABRIC))
				{
					htmltext = "31862-01.html";
				}
				else
				{
					takeItems(player, FABRIC, 1);
					player.teleToLocation(TELEPORT_IN_LOC);
				}
				return htmltext;
			}
			case "teleportOut":
			{
				final Location destination = TELEPORT_OUT_LOC[getRandom(TELEPORT_OUT_LOC.length)];
				player.teleToLocation(destination.getX() + getRandom(100), destination.getY() + getRandom(100), destination.getZ());
				break;
			}
			case "wakeUp":
			{
				if (getStatus() == ALIVE)
				{
					npc.deleteMe();
					setStatus(IN_FIGHT);
					_baium = (L2GrandBossInstance) addSpawn(BAIUM, BAIUM_LOC, false, 0);
					_baium.disableCoreAI(true);
					addBoss(_baium);
					_lastAttack = System.currentTimeMillis();
					startQuestTimer("WAKEUP_ACTION", 50, _baium, null);
					startQuestTimer("MANAGE_EARTHQUAKE", 2000, _baium, null);
					startQuestTimer("SOCIAL_ACTION", 10000, _baium, player);
					startQuestTimer("CHECK_ATTACK", 60000, _baium, null);
				}
				break;
			}
			case "WAKEUP_ACTION":
			{
				if (npc != null)
				{
					zone.broadcastPacket(new SocialAction(_baium.getObjectId(), 2));
				}
				break;
			}
			case "MANAGE_EARTHQUAKE":
			{
				if (npc != null)
				{
					zone.broadcastPacket(new Earthquake(npc.getX(), npc.getY(), npc.getZ(), 40, 10));
					zone.broadcastPacket(Music.BS02_A_6000.getPacket());
				}
				break;
			}
			case "SOCIAL_ACTION":
			{
				if (npc != null)
				{
					zone.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
					startQuestTimer("PLAYER_PORT", 6000, npc, player);
				}
				break;
			}
			case "PLAYER_PORT":
			{
				if (npc != null)
				{
					if ((player != null) && player.isInsideRadius(npc, 16000, true, false))
					{
						player.teleToLocation(BAIUM_GIFT_LOC);
						startQuestTimer("PLAYER_KILL", 3000, npc, player);
					}
					else
					{
						L2PcInstance randomPlayer = getRandomPlayer(npc);
						if (randomPlayer != null)
						{
							randomPlayer.teleToLocation(BAIUM_GIFT_LOC);
							startQuestTimer("PLAYER_KILL", 3000, npc, randomPlayer);
						}
						else
						{
							startQuestTimer("PLAYER_KILL", 3000, npc, null);
						}
					}
				}
				break;
			}
			case "PLAYER_KILL":
			{
				if ((player != null) && player.isInsideRadius(npc, 16000, true, false))
				{
					zone.broadcastPacket(new SocialAction(npc.getObjectId(), 1));
					broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.HOW_DARE_YOU_WAKE_ME_NOW_YOU_SHALL_DIE, player.getName());
					npc.setTarget(player);
					npc.doCast(BAIUM_PRESENT.getSkill());
				}
				
				for (L2PcInstance insidePlayer : zone.getPlayersInside())
				{
					if (insidePlayer.isHero())
					{
						zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.NOT_EVEN_THE_GODS_THEMSELVES_COULD_TOUCH_ME_BUT_YOU_S1_YOU_DARE_CHALLENGE_ME_IGNORANT_MORTAL, 2, 4000, insidePlayer.getName()));
						break;
					}
				}
				startQuestTimer("SPAWN_ARCHANGEL", 8000, npc, player);
				break;
			}
			case "SPAWN_ARCHANGEL":
			{
				_baium.disableCoreAI(false);
				
				for (Location loc : ARCHANGEL_LOC)
				{
					final L2Npc archangel = addSpawn(ARCHANGEL, loc, false, 0, true);
					startQuestTimer("SELECT_TARGET", 5000, archangel, null);
				}
				
				if ((player != null) && !player.isDead())
				{
					addAttackDesire(npc, player);
				}
				else
				{
					L2PcInstance randomPlayer = getRandomPlayer(npc);
					if (randomPlayer != null)
					{
						addAttackDesire(npc, randomPlayer);
					}
				}
				break;
			}
			case "SELECT_TARGET":
			{
				if (npc != null)
				{
					final L2Attackable mob = (L2Attackable) npc;
					final L2Character mostHated = mob.getMostHated();
					
					if ((_baium == null) || _baium.isDead())
					{
						mob.deleteMe();
						break;
					}
					
					if ((mostHated != null) && mostHated.isPlayer() && zone.isInsideZone(mostHated))
					{
						if (mob.getTarget() != mostHated)
						{
							mob.clearAggroList();
						}
						addAttackDesire(mob, mostHated);
					}
					else
					{
						boolean found = false;
						for (L2Character creature : mob.getKnownList().getKnownCharactersInRadius(1000))
						{
							if ((creature != null) && creature.isPlayable() && zone.isInsideZone(creature) && !creature.isDead())
							{
								if (mob.getTarget() != creature)
								{
									mob.clearAggroList();
								}
								addAttackDesire(mob, creature);
								found = true;
								break;
							}
						}
						
						if (!found)
						{
							if (mob.isInsideRadius(_baium, 40, true, false))
							{
								if (mob.getTarget() != _baium)
								{
									mob.clearAggroList();
								}
								mob.setIsRunning(true);
								mob.addDamageHate(_baium, 0, 999);
								mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _baium);
							}
							else
							{
								mob.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _baium);
							}
						}
					}
					startQuestTimer("SELECT_TARGET", 5000, npc, null);
				}
				break;
			}
			case "CHECK_ATTACK":
			{
				if (npc != null)
				{
					if ((_lastAttack + 1800000) < System.currentTimeMillis())
					{
						cancelQuestTimers("SELECT_TARGET");
						notifyEvent("CLEAR_ZONE", null, null);
						addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0);
						setStatus(ALIVE);
					}
					else
					{
						if (((_lastAttack + 300000) < System.currentTimeMillis()) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.75)))
						{
							npc.setTarget(npc);
							npc.doCast(HEAL_OF_BAIUM.getSkill());
						}
						startQuestTimer("CHECK_ATTACK", 60000, npc, null);
					}
				}
				break;
			}
			case "CLEAR_STATUS":
			{
				setStatus(ALIVE);
				addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0);
				break;
			}
			case "CLEAR_ZONE":
			{
				for (L2Character charInside : zone.getCharactersInside())
				{
					if (charInside != null)
					{
						if (charInside.isNpc())
						{
							charInside.deleteMe();
						}
						else if (charInside.isPlayer())
						{
							notifyEvent("teleportOut", null, (L2PcInstance) charInside);
						}
					}
				}
				break;
			}
			case "RESPAWN_BAIUM":
			{
				if (getStatus() == DEAD)
				{
					setRespawn(0);
					cancelQuestTimer("CLEAR_STATUS", null, null);
					notifyEvent("CLEAR_STATUS", null, null);
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't respawn Baium while Baium is alive!");
				}
				break;
			}
			case "ABORT_FIGHT":
			{
				if (getStatus() == IN_FIGHT)
				{
					_baium = null;
					notifyEvent("CLEAR_ZONE", null, null);
					notifyEvent("CLEAR_STATUS", null, null);
					player.sendMessage(getClass().getSimpleName() + ": Aborting fight!");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't abort attack right now!");
				}
				cancelQuestTimers("CHECK_ATTACK");
				cancelQuestTimers("SELECT_TARGET");
				break;
			}
			case "DESPAWN_MINIONS":
			{
				if (getStatus() == IN_FIGHT)
				{
					for (L2Character charInside : zone.getCharactersInside())
					{
						if ((charInside != null) && charInside.isNpc() && (charInside.getId() == ARCHANGEL))
						{
							charInside.deleteMe();
						}
					}
					if (player != null)
					{
						player.sendMessage(getClass().getSimpleName() + ": All archangels has been deleted!");
					}
				}
				else if (player != null)
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't despawn archangels right now!");
				}
				break;
			}
			case "MANAGE_SKILLS":
			{
				if (npc != null)
				{
					manageSkills(npc);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		_lastAttack = System.currentTimeMillis();
		
		if (npc.getId() == BAIUM)
		{
			if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTI_STRIDER.getSkillId()))
			{
				if (!npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
				{
					npc.setTarget(attacker);
					npc.doCast(ANTI_STRIDER.getSkill());
				}
			}
			
			if (skill == null)
			{
				refreshAiParams(attacker, npc, (damage * 1000));
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
			{
				refreshAiParams(attacker, npc, ((damage / 3) * 100));
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				refreshAiParams(attacker, npc, (damage * 20));
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
			{
				refreshAiParams(attacker, npc, (damage * 10));
			}
			else
			{
				refreshAiParams(attacker, npc, ((damage / 3) * 20));
			}
			manageSkills(npc);
		}
		else
		{
			final L2Attackable mob = (L2Attackable) npc;
			final L2Character mostHated = mob.getMostHated();
			
			if ((getRandom(100) < 10) && mob.checkDoCastConditions(SPEAR_ATTACK.getSkill()))
			{
				if ((mostHated != null) && (npc.calculateDistance(mostHated, true, false) < 1000) && zone.isCharacterInZone(mostHated))
				{
					mob.setTarget(mostHated);
					mob.doCast(SPEAR_ATTACK.getSkill());
				}
				else if (zone.isCharacterInZone(attacker))
				{
					mob.setTarget(attacker);
					mob.doCast(SPEAR_ATTACK.getSkill());
				}
			}
			
			if ((getRandom(100) < 5) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5)) && mob.checkDoCastConditions(ANGEL_HEAL.getSkill()))
			{
				npc.setTarget(npc);
				npc.doCast(ANGEL_HEAL.getSkill());
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (zone.isCharacterInZone(killer))
		{
			setStatus(DEAD);
			addSpawn(TELE_CUBE, TELEPORT_CUBIC_LOC, false, 900000);
			zone.broadcastPacket(Music.BS01_D_10000.getPacket());
			long respawnTime = (Config.BAIUM_SPAWN_INTERVAL + getRandom(-Config.BAIUM_SPAWN_RANDOM, Config.BAIUM_SPAWN_RANDOM)) * 3600000;
			setRespawn(respawnTime);
			startQuestTimer("CLEAR_STATUS", respawnTime, null, null);
			startQuestTimer("CLEAR_ZONE", 900000, null, null);
			cancelQuestTimer("CHECK_ATTACK", npc, null);
			cancelQuestTimers("SELECT_TARGET");
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (!zone.isInsideZone(creature) || (creature.isNpc() && (creature.getId() == BAIUM_STONE)))
		{
			return super.onSeeCreature(npc, creature, isSummon);
		}
		
		if (creature.isInCategory(CategoryType.CLERIC_GROUP))
		{
			if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
			{
				refreshAiParams(creature, npc, 10000);
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				refreshAiParams(creature, npc, 10000, 6000);
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
			{
				refreshAiParams(creature, npc, 10000, 3000);
			}
			else
			{
				refreshAiParams(creature, npc, 10000, 2000);
			}
		}
		else
		{
			refreshAiParams(creature, npc, 10000, 1000);
		}
		manageSkills(npc);
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		startQuestTimer("MANAGE_SKILLS", 1000, npc, null);
		
		if (!zone.isCharacterInZone(npc) && (_baium != null))
		{
			_baium.teleToLocation(BAIUM_LOC);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public boolean unload(boolean removeFromList)
	{
		if (_baium != null)
		{
			_baium.deleteMe();
		}
		return super.unload(removeFromList);
	}
	
	private final void refreshAiParams(L2Character attacker, L2Npc npc, int damage)
	{
		refreshAiParams(attacker, npc, damage, damage);
	}
	
	private final void refreshAiParams(L2Character attacker, L2Npc npc, int damage, int aggro)
	{
		final int newAggroVal = damage + getRandom(3000);
		final int aggroVal = aggro + 1000;
		final NpcVariables vars = npc.getVariables();
		for (int i = 0; i < 3; i++)
		{
			if (attacker == vars.getObject("c_quest" + i, L2Character.class))
			{
				if (vars.getInt("i_quest" + i) < aggroVal)
				{
					vars.set("i_quest" + i, newAggroVal);
				}
				return;
			}
		}
		final int index = Util.getIndexOfMinValue(vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2"));
		vars.set("i_quest" + index, newAggroVal);
		vars.set("c_quest" + index, attacker);
	}
	
	private int getStatus()
	{
		return GrandBossManager.getInstance().getBossStatus(BAIUM);
	}
	
	private void addBoss(L2GrandBossInstance grandboss)
	{
		GrandBossManager.getInstance().addBoss(grandboss);
	}
	
	private void setStatus(int status)
	{
		GrandBossManager.getInstance().setBossStatus(BAIUM, status);
	}
	
	private void setRespawn(long respawnTime)
	{
		GrandBossManager.getInstance().getStatsSet(BAIUM).set("respawn_time", (System.currentTimeMillis() + respawnTime));
	}
	
	private void manageSkills(L2Npc npc)
	{
		if (npc.isCastingNow() || npc.isCoreAIDisabled() || !npc.isInCombat())
		{
			return;
		}
		
		final NpcVariables vars = npc.getVariables();
		for (int i = 0; i < 3; i++)
		{
			final L2Character attacker = vars.getObject("c_quest" + i, L2Character.class);
			if ((attacker == null) || ((npc.calculateDistance(attacker, true, false) > 9000) || attacker.isDead()))
			{
				vars.set("i_quest" + i, 0);
			}
		}
		final int index = Util.getIndexOfMaxValue(vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2"));
		final L2Character player = vars.getObject("c_quest" + index, L2Character.class);
		final int i2 = vars.getInt("i_quest" + index);
		if ((i2 > 0) && (getRandom(100) < 70))
		{
			vars.set("i_quest" + index, 500);
		}
		
		SkillHolder skillToCast = null;
		if ((player != null) && !player.isDead())
		{
			if (npc.getCurrentHp() > (npc.getMaxHp() * 0.75))
			{
				if (getRandom(100) < 10)
				{
					skillToCast = ENERGY_WAVE;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = EARTH_QUAKE;
				}
				else
				{
					skillToCast = BAIUM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() > (npc.getMaxHp() * 0.5))
			{
				if (getRandom(100) < 10)
				{
					skillToCast = GROUP_HOLD;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = ENERGY_WAVE;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = EARTH_QUAKE;
				}
				else
				{
					skillToCast = BAIUM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() > (npc.getMaxHp() * 0.25))
			{
				if (getRandom(100) < 10)
				{
					skillToCast = THUNDERBOLT;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = GROUP_HOLD;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = ENERGY_WAVE;
				}
				else if (getRandom(100) < 10)
				{
					skillToCast = EARTH_QUAKE;
				}
				else
				{
					skillToCast = BAIUM_ATTACK;
				}
			}
			else if (getRandom(100) < 10)
			{
				skillToCast = THUNDERBOLT;
			}
			else if (getRandom(100) < 10)
			{
				skillToCast = GROUP_HOLD;
			}
			else if (getRandom(100) < 10)
			{
				skillToCast = ENERGY_WAVE;
			}
			else if (getRandom(100) < 10)
			{
				skillToCast = EARTH_QUAKE;
			}
			else
			{
				skillToCast = BAIUM_ATTACK;
			}
		}
		
		if ((skillToCast != null) && npc.checkDoCastConditions(skillToCast.getSkill()))
		{
			npc.setTarget(player);
			npc.doCast(skillToCast.getSkill());
		}
	}
	
	private L2PcInstance getRandomPlayer(L2Npc npc)
	{
		for (L2Character creature : npc.getKnownList().getKnownCharactersInRadius(2000))
		{
			if ((creature != null) && creature.isPlayer() && zone.isInsideZone(creature) && !creature.isDead())
			{
				return (L2PcInstance) creature;
			}
		}
		return null;
	}
}