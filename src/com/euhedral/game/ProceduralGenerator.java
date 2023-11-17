package com.euhedral.game;

import com.euhedral.engine.Engine;
import com.euhedral.engine.Utility;
import com.euhedral.game.Entities.Enemy.EnemyGround;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
* todo: decrease pause after wave after difficulty rises
* */

public class ProceduralGenerator {

    public HashMap<Color, EntityID> colorMap;
    int height, width = 31;
    final int incrementMIN = Utility.intAtWidth640(1);
    int xStart = incrementMIN, xEnd = width - 3;
    int xMid = width/2;
    int spawnZone, lastZone, lastLastZone;
    int pattern, lastPattern, lastLastPattern;
    int wave, waveSinceHealth;
    int spacingHorizontal = 3;
    int level;
    int playerX = xMid*32, playerY;
    final int ENDLESS = -1;
    int spawnHeight;

    long lastSpawnTime;
    long spawnInterval;
    long spawnInterval_MIN = 2;
    long spawnInterval_MAX = 5;

    final int SPAWN_ENEMY = 0;
    final int SPAWN_HEALTH = 10;
    int spawnNext = 0;
    int MIN_WAVE_HEALTH_SPAWN = 15;
    int MIN_WAVE_BETWEEN_HEALTH_SPAWN = 10;
    int MAX_WAVE_BETWEEN_HEALTH_SPAWN = 20;

    int difficulty = 1;

    /*
    * Basic - 1/1
    * Fast - 1/2
    * Move - 1/3
    * Snake - 1/4
    * */

    // Enemy spawning chances
    int spawnFast = 5;
    int spawnMove = spawnFast * 3;
    int spawnSnake = spawnMove * 2;

    // Spawning pick-up, every 50 units of level height
    int spawnPickupRate = 50;
    int spawnPickupChance = 1; // in 10;
    int spawnedPickupCount = 0;

    // Ground enemy spawn rate
    int groundRate = 50;
    int groundChance = 1; // in 10;
    int groundCount = 0;

    int[][] enemyNumbers; // [Pattern][min/max numbers]
    final int ENEMY_MIN = 0;
    final int ENEMY_MAX = 1;

    // ground spawns
    int minG = 1;
    int maxG = 2;

    final int PATTERN_LINE = 0;
    final int PATTERN_V = 1;
    final int PATTERN_SQUARE = 2;
    final int maxPatterns = 3;

    private EntityHandler entityHandler;

    public ProceduralGenerator(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
        colorMap = VariableHandler.colorMap;

        buildMatrix();
    }

    private void buildMatrix() {
        // spawns in V pattern
        int minEnemiesV = 3;
        int maxEnemiesV = 9;

        // spawns in line pattern
        int minEnemiesL = 2;
        int maxEnemiesL = 9;

        // spawns in square pattern
        int minEnemiesS = 2;
        int maxEnemiesS = 4; // MAX 8 possible but not fun

        enemyNumbers = new int[maxPatterns][2];
        enemyNumbers[PATTERN_LINE][ENEMY_MIN] = minEnemiesL;
        enemyNumbers[PATTERN_LINE][ENEMY_MAX] = maxEnemiesL;
        enemyNumbers[PATTERN_V][ENEMY_MIN] = minEnemiesV;
        enemyNumbers[PATTERN_V][ENEMY_MAX] = maxEnemiesV;
        enemyNumbers[PATTERN_SQUARE][ENEMY_MIN] = minEnemiesS;
        enemyNumbers[PATTERN_SQUARE][ENEMY_MAX] = maxEnemiesS;
    }

    // generate a level using procedural generation
    public void generateLevel() {
        level = VariableHandler.getLevel();
        switch (level) {
            case ENDLESS:
                height = ENDLESS;
                break;
            case 1:
                height = 150; // 100 = ~4 waves
                break;
            case 2:
                height = 225;
                break;
            case 3:
                height = 275;
                break;
            case 4:
                height = 300;
                break;
        }


        System.out.printf("Width: %d, Height: %d\n", width, height);

        entityHandler.setLevelHeight(getLevelHeight());

        playerY = getLevelHeight();

        entityHandler.spawnPlayer(playerX, playerY);

        // create distance between player and first wave
        wave = 1;
        System.out.println("Wave: " + wave);

        // todo: use line for first wave here

        spawnHeight = (height - Engine.HEIGHT/32) ;//- (wave * MIN_PAUSE);
        lastSpawnTime = GameController.getCurrentTime();
        spawnInterval = 0;

//        spawnFirstWave();
    }

    // todo: Reimplement health
    public void update() {
        long timeNowMillis = GameController.getCurrentTime();
        long timeSinceLastSpawnMillis = timeNowMillis - lastSpawnTime;
        boolean canSpawn = spawnInterval <= timeSinceLastSpawnMillis;

        if (canSpawn) {
            spawnNext = Utility.randomRangeInclusive(SPAWN_ENEMY, SPAWN_HEALTH);
            if (wave == MIN_WAVE_HEALTH_SPAWN) {
                spawnHealth();
            } else if (waveSinceHealth >= MAX_WAVE_BETWEEN_HEALTH_SPAWN)
                spawnHealth();
            else if (spawnNext == SPAWN_HEALTH && wave >= MIN_WAVE_HEALTH_SPAWN && waveSinceHealth >= MIN_WAVE_BETWEEN_HEALTH_SPAWN)
                spawnHealth();
            else
                spawnEnemies();
            lastSpawnTime = GameController.getCurrentTime();
        }
//        System.out.println("Spawn Code: " + spawnNext);
    }

    private void spawnFirstWave() {
        lastLastPattern = lastPattern;
        lastPattern = pattern;
        pattern = PATTERN_LINE;

        int minEnemies = enemyNumbers[pattern][ENEMY_MIN];
        int maxEnemies = enemyNumbers[pattern][ENEMY_MAX];
        int currentMax = Math.min(minEnemies + difficulty, maxEnemies);

        int num = Utility.randomRangeInclusive(minEnemies, currentMax);

        spawnLine(num);
        wave++;
        calculateSpawnInterval(num, maxEnemies);
    }

    private void spawnEnemies() {
            // for every wave
        waveSinceHealth++;

        // for every zone
        lastLastZone = lastZone;
        lastZone = spawnZone;
        spawnZone = Utility.randomRange(1, 3);

        // determine that no zone spawns more than twice in a row
        while (spawnZone == lastZone && spawnZone == lastLastZone) {
            spawnZone = Utility.randomRange(1, 3);
        }

        // choose spawn pattern
        if (wave == 1) {
            lastLastPattern = lastPattern;
            lastPattern = pattern;
            pattern = PATTERN_LINE;
        } else {
            nextPattern();
        }

//        pattern = PATTERN_SQUARE;

        int minEnemies = enemyNumbers[pattern][ENEMY_MIN];
        int maxEnemies = enemyNumbers[pattern][ENEMY_MAX];
        int currentMax = Math.min(minEnemies + difficulty, maxEnemies);

        int num = Utility.randomRangeInclusive(minEnemies, currentMax);

        // spawn enemies
        switch (pattern) {
            case PATTERN_LINE:
                spawnLine(num);
                break;
            case PATTERN_V:
                spawnV(num);
                break;
            case PATTERN_SQUARE:
                spawnSquare(num);
                break;
        }

        if (wave % 10 == 0) {
            difficulty++;
        }

//        System.out.printf("Wave: %d, LastHeight: %d\n", wave, spawnHeight);

        wave++;
        System.out.println("Wave: " + wave);
        calculateSpawnInterval(num, maxEnemies);
    }

    private void spawnHealth() {
        spawnZone = Utility.randomRange(1, 3);
        int spawnHeight = this.spawnHeight;
        spawnPickupHelper2(spawnHeight, EntityID.PickupHealth);
        System.out.println("Health Spawned");
        wave++;
        waveSinceHealth = 0;
        spawnInterval = spawnInterval_MIN;
    }

    // Pickup Spawner Helped
    private void spawnPickupHelper2(int spawnHeight, EntityID id) {
        entityHandler.spawnPickup(xMid * 32, spawnHeight * 32, id);
    }

    /* Spawn Pattern Functions*/

    // Pickup Spawner Helped
    private void spawnPickupHelper(int remainingHeight, int num, int spawnHeight, EntityID id) {
        if (spawnedPickupCount == num && height - remainingHeight >= spawnHeight) {
            entityHandler.spawnPickup(xMid * 32, remainingHeight * 32, id);
            spawnedPickupCount++;
        }
    }

    private void spawnLine(int num) {
//        System.out.println(spawnZone + " " + num);

        switch (spawnZone) {
            case 1:
                spawnFromLeft(num, spawnHeight, xStart);
                break;
            case 2:
                spawnFromMiddle(num, spawnHeight);
                break;
            case 3:
                spawnFromRight(num, spawnHeight, xEnd);
                break;
        }
    }

    private void spawnV(int num) {
        int xStart, xMid, xLast;
        xStart = 6;
        xMid = (width - 1) / 2;
        xLast = width - xStart;

        switch (spawnZone) {
            case 1:
                spawnFromMiddleV(xStart, num, spawnHeight);
                break;
            case 2:
                spawnFromMiddleV(xMid, num, spawnHeight);
                break;
            case 3:
                spawnFromMiddleV(xLast, num, spawnHeight);
                break;
        }
    }

    private void spawnSquare(int num) {
        num--;
        int x0 = 0;
        int y0 = spawnHeight - num*spacingHorizontal;
        int xFin = 0;
        switch (spawnZone) {
            case 1:
                x0 = xStart;
                xFin = x0 + num*spacingHorizontal;
                y0 = spawnHeight - num*spacingHorizontal;
                break;
            case 2:
                x0 = xMid;
                xFin = x0 + num*spacingHorizontal;
                y0 = spawnHeight - num*spacingHorizontal;
                break;
            case 3:
                x0 = xEnd - num*spacingHorizontal;
                xFin = x0 + num*spacingHorizontal;
                y0 = spawnHeight - num*spacingHorizontal;
                break;
        }

        spawnFromLeft(num, y0, x0);
        spawnFromTop(num, y0, xFin);
        spawnFromBottom(num, spawnHeight, x0);
        spawnFromRight(num, spawnHeight, xFin);
    }

    /* Recursive Spawning Functions */

    private void spawnFromLeft(int num, int y, int x) {
        // Base Case

        if (num > 1) {
            int skip = calculateSkip();
            int x0 = x + (spacingHorizontal + skip);
            spawnFromLeft(num - 1, y, x0);
        }

        Color c = Color.RED; // stub
        spawnHelper(x, y, c);
    }

    private void spawnFromRight(int num, int y, int x) {
        // Base Case

        if (num > 1) {
            int skip = calculateSkip();
            int x0 = x - (spacingHorizontal + skip);
            spawnFromRight(num - 1, y, x0);
        }

        Color c = Color.RED; // stub
        spawnHelper(x, y, c);
    }

    private void spawnFromMiddle(int num, int spawnHeight) {
        int tileSize = Utility.intAtWidth640(32);
        int x = (width - 1) / 2;
        int y = spawnHeight;
        Color c = Color.RED; // stub

        boolean odd = num% 2 != 0;
        if (odd) {
            spawnHelper(x, y, c);

            if (num > 1) {
                num -= 1;
                num = num / 2;
                int skip = calculateSkip();

                spawnFromLeft(num, y, x + (spacingHorizontal + skip));

                skip = calculateSkip();

                spawnFromRight(num, y, x - (spacingHorizontal + skip));
            }
        } else {
            num = num /2;

            spawnFromLeft(num, y, x + incrementMIN);
            spawnFromRight(num, y, x - incrementMIN);

        }
    }

    private void spawnFromMiddleV(int x, int num, int spawnHeight) {
        int y = spawnHeight;
        Color c = Color.RED; // stub

        int incrementX = 2*incrementMIN;

        int newY = y - spacingHorizontal;

        boolean odd = (num % 2) != 0;
        if (odd) {
            spawnHelper(x, y, c);

            if (num > 1) {
                num -= 1;
                num = num / 2;

                int xLeft = x + (incrementX);
                spawnFromLeftV(num, newY, xLeft);

                int xRight = x - (incrementX);
                spawnFromRightV(num, newY, xRight);
            }
        } else {
            num = num /2;

            incrementX = incrementMIN;
            int xLeft = x + (incrementX);
            spawnFromLeftV(num, newY, xLeft);

            int xRight = x - (incrementX);
            spawnFromRightV(num, newY, xRight);


        }
    }

    private void spawnFromTop(int num, int y, int x) {
        // Base Case

        if (num > 1) {
            int skip = calculateSkip();
            int y0 = y + (spacingHorizontal + skip);
            spawnFromTop(num - 1, y0, x);
        }

        Color c = Color.RED; // stub
        spawnHelper(x, y, c);
    }

    private void spawnFromBottom(int num, int y, int x) {
        // Base Case

        if (num > 1) {
            int skip = calculateSkip();
            int y0 = y - (spacingHorizontal + skip);
            spawnFromBottom(num - 1, y0, x);
        }

        Color c = Color.RED; // stub
        spawnHelper(x, y, c);
    }

    private void spawnVHelper(int num, int y, int x, int increment) {
        Color c = Color.RED; // stub

        // Base Case

        int y0 = y - spacingHorizontal;

        if (num > 1) {
            int x0 = x + (increment);
            spawnVHelper(num - 1, y0, x0, increment);
        }

        spawnHelper(x, y, c);
    }

    private void spawnFromLeftV(int num, int y, int x) {
        int incrementX = 2 * incrementMIN;
        spawnVHelper(num, y, x, incrementX);
    }

    private void spawnFromRightV(int num, int y, int x) {
        int incrementX = -2 * incrementMIN;
        spawnVHelper(num, y, x, incrementX);
    }

    private void spawnHelper(int x, int y, Color c) {

        EntityID id = colorMap.get(Color.RED);

        if (level >= 2) {
            int spawnChance = Utility.randomRange(0, spawnFast);
            if (spawnChance == 0)
                id = EntityID.EnemyFast;
        }

        if (level >= 3) {
            int spawnChance = Utility.randomRange(0, spawnMove);
            if (spawnChance == 0)
                id = EntityID.EnemyMove;
        }

        if (level >= 4) {
            int spawnChance = Utility.randomRange(0, spawnSnake);
            if (spawnChance == 0)
                id = EntityID.EnemySnake;
        }

        c = getKey(id);

        entityHandler.spawnEntity(x*32, y*32, id, c);
//        System.out.println("Enemy spawned");
    }

    private void spawnGround(int remainingHeight, int spawnHeight, int num, String spawnFrom) {
        if (groundCount == num && height - remainingHeight >= spawnHeight) {
            EnemyGround eG = new EnemyGround(xStart * 32, remainingHeight * 32, getLevelHeight());
            groundCount++;
            if (spawnFrom == "left") {
                eG.setHMove("right");
            } else if (spawnFrom == "right") {
                eG.setHMove("left");
                eG.setX(xEnd*32);
            }
            entityHandler.addEnemy(eG);
        }
    }

    private void spawnGroundLeft(int remainingHeight, int spawnHeight, int num) {
        spawnGround(remainingHeight, spawnHeight, num, "left");
    }

    private void spawnGroundRight(int remainingHeight, int spawnHeight, int num) {
        spawnGround(remainingHeight, spawnHeight, num, "right");
    }

    private void spawnBoss(int x, int y) {
        entityHandler.spawnBoss(level, x*32, y*32);
    }

    private void nextPattern() {
        lastLastPattern = lastPattern;
        lastPattern = pattern;
        pattern = Utility.randomRange(0, maxPatterns);

        while (pattern == lastPattern && pattern == lastLastPattern) {
            pattern = Utility.randomRange(0, maxPatterns);
        }
    }

    private int calculateSkip() {
        int skip = Utility.randomRange(0, 2);
        if (pattern == PATTERN_SQUARE) {
            skip = 0;
        }
        return skip;
    }

    private void calculateSpawnInterval(int num, int maxEnemies) {
        float minPause = (float) spawnInterval_MIN;
        float maxPause = (float) spawnInterval_MAX;

        float spawnIntervalFloat = (maxPause - minPause) * (num - 1)/(maxEnemies - 1) + minPause;
        spawnInterval = (long) spawnIntervalFloat;

        if (pattern == PATTERN_V) {
            spawnInterval *= 1.5;
        }

        // todo: adjust
        if (pattern == PATTERN_SQUARE) {
            spawnInterval *= 1.8;
        }
    }

    private void determineSpawn() {
//        if level
    }

    public int getLevelHeight() {
        return height * 32;
    }

    private Color getKey(EntityID id) {
        for (Map.Entry<Color, EntityID> entry : colorMap.entrySet()) {
            if (Objects.equals(id, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
