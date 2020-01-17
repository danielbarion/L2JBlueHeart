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
package quests.Q00072_SagaOfTheSwordMuse;

import l2r.gameserver.model.Location;

import quests.AbstractSagaQuest;

/**
 * Saga of the Sword Muse (72)
 * @author Emperorc
 */
public class Q00072_SagaOfTheSwordMuse extends AbstractSagaQuest
{
	public Q00072_SagaOfTheSwordMuse()
	{
		super(72, Q00072_SagaOfTheSwordMuse.class.getSimpleName(), "Saga of the Sword Muse");
		_npc = new int[]
		{
			30853,
			31624,
			31583,
			31537,
			31618,
			31646,
			31649,
			31652,
			31654,
			31655,
			31659,
			31281
		};
		Items = new int[]
		{
			7080,
			7536,
			7081,
			7487,
			7270,
			7301,
			7332,
			7363,
			7394,
			7425,
			7095,
			6482
		};
		Mob = new int[]
		{
			27288,
			27221,
			27280
		};
		classid = new int[]
		{
			100
		};
		prevclass = new int[]
		{
			0x15
		};
		npcSpawnLocations = new Location[]
		{
			new Location(161719, -92823, -1893),
			new Location(124355, 82155, -2803),
			new Location(124376, 82127, -2796)
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
