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
package quests.Q00077_SagaOfTheDominator;

import l2r.gameserver.model.Location;

import quests.AbstractSagaQuest;

/**
 * Saga of the Dominator (77)
 * @author Emperorc
 */
public class Q00077_SagaOfTheDominator extends AbstractSagaQuest
{
	public Q00077_SagaOfTheDominator()
	{
		super(77, Q00077_SagaOfTheDominator.class.getSimpleName(), "Saga of the Dominator");
		_npc = new int[]
		{
			31336,
			31624,
			31371,
			31290,
			31636,
			31646,
			31648,
			31653,
			31654,
			31655,
			31656,
			31290
		};
		Items = new int[]
		{
			7080,
			7539,
			7081,
			7492,
			7275,
			7306,
			7337,
			7368,
			7399,
			7430,
			7100,
			0
		};
		Mob = new int[]
		{
			27294,
			27226,
			27262
		};
		classid = new int[]
		{
			115
		};
		prevclass = new int[]
		{
			0x33
		};
		npcSpawnLocations = new Location[]
		{
			new Location(162898, -76492, -3096),
			new Location(47429, -56923, -2383),
			new Location(47391, -56929, -2370)
		};
		Text = new String[]
		{
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try and fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};
		registerNPCs();
	}
}
