package com.euhedral.game;

import com.euhedral.engine.Engine;
import com.euhedral.engine.Utility;

public class EnemyGenerator {
    // Level
    int level;
    int cutoffHeight = 0, cutoffWidth = 0;
    int height = Engine.HEIGHT - cutoffHeight, width = Engine.WIDTH - cutoffWidth;

    final int incrementMIN = Utility.intAtWidth640(1);
    int xStart = incrementMIN, xEnd = width;
    int xMid = (xEnd - xStart)/2 + xStart;

    // Player
    int playerX = xMid, playerY;

    // Enemy Spawning
    protected EntityHandler entityHandler;
    int difficulty;
    int minWavesDifficultyIncrease;
    int num;
    long lastSpawnTime;
    long spawnInterval;
    long spawnInterval_MIN = 2;
    long spawnInterval_MAX = 5;
    int enemiesSpawned; // todo: Why do we need this?
    int spawnX, spawnY;

    // Pickup
    long spawnIntervalPickups;

    // Enemy Types
    int enemytype;
    final int TYPE_BASIC = EntityHandler.TYPE_BASIC;
    final int TYPE_DRONE = EntityHandler.TYPE_DRONE;
    final int TYPE_HEAVY = EntityHandler.TYPE_HEAVY;
    final int maxTypes = TYPE_DRONE + 1;

    // Wave
    int wave, waveSinceHealth, waveSincePower, waveSinceShield;
    int wavesSinceDifficultyIncrease = 0;
    int waveSinceHeavy = 0;
    final int MINWaveSinceHeavy = 1;

    public EnemyGenerator(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
    }

    // todo: SpawnHealth
    public void update() {
        long timeNowMillis = GameController.getCurrentTime();
        long timeSinceLastSpawnMillis = timeNowMillis - lastSpawnTime;
        boolean canSpawn = spawnInterval <= timeSinceLastSpawnMillis;
        // todo: SpawnInterval for different types

        if (canSpawn) {
            spawnEnemies();
            lastSpawnTime = GameController.getCurrentTime();
            enemiesSpawned = 0;
        }
    }

    public void generateLevel() {
        level = VariableHandler.getLevel();

        System.out.printf("Width: %d, Height: %d\n", width, height);

        difficulty = 1;
        minWavesDifficultyIncrease = 10;
        entityHandler.setLevelHeight(getLevelHeight());

        playerY = getLevelHeight();

        entityHandler.spawnPlayer(playerX, playerY);

        // create distance between player and first wave
        enemiesSpawned = 0;
        wave = 1;
        resetWaveSinceHealth();
        resetWaveSincePower();
        resetWaveSinceShield();
        System.out.println("Wave: " + wave);

        // todo: use line for first wave here

        spawnY = (height - Engine.HEIGHT) ;//- (wave * MIN_PAUSE);
        lastSpawnTime = GameController.getCurrentTime();
        spawnInterval = spawnInterval_MIN;
        spawnIntervalPickups = spawnInterval_MAX;

//        spawnFirstWave();
    }

    protected void spawnEnemies() {
        increment();
        determinePattern(); // move down later
        determineNum();
        determineType();
        determineZone();
        spawnEnemiesHelper();
        incrementDifficulty();
        incrementWave();
        determineSpawnInterval();
    }

    // Increment Wave Count for Non-Enemy Spawns
    protected void increment() {

    }

    protected void determineNum() {
        num = 1; // stub
    }

    protected void determineType() {
        // experimental

        int temp = 1;
        int total = 0;
        while (temp <= maxTypes) {
            total += temp;
            temp++;
        }

        int calculatedDifficulty = difficulty;

//        if (waveSinceHeavy > MINWaveSinceHeavy) {
//            calculatedDifficulty -= 1;
//        }

        int rand = Utility.randomRangeInclusive(0, calculatedDifficulty);
        enemytype = rand;


        // determine type
//        enemytype = TYPE_BASIC; // stub
//        enemytype = TYPE_HEAVY; // stub
//        enemytype = TYPE_DRONE; // stub
//        int temp = Utility.randomRangeInclusive(0, WEIGHT_TOTAL);
//        enemytype = Utility.randomRangeInclusive(0,1); // type;
    }

    protected void determineZone() {
        if (enemytype == TYPE_DRONE) {
            int adjustment = 32;
            int zone = Utility.randomRangeInclusive(0, 1);
            if (zone == 0) {
                spawnX = -adjustment;
            } else {
                spawnX = width + adjustment;
            }
            spawnY = Utility.randomRangeInclusive(0, height * 2/3);
        } else {
            spawnX = Utility.randomRangeInclusive(xStart, xEnd);
            spawnY = (height - Engine.HEIGHT) ;
        }
    }

    protected void determinePattern() {

    }

    protected void spawnEnemiesHelper() {
        spawnOneEnemy();

        if (enemytype != TYPE_HEAVY) {
            waveSinceHeavy++;
        } else {
            waveSinceHeavy = 0;
        }
    }

    protected void incrementDifficulty() {
        if (wavesSinceDifficultyIncrease >= minWavesDifficultyIncrease) {
//            difficulty = Math.min(difficulty + 1, maxTypes - 1);
            difficulty = Math.min(difficulty + 1, 8);
            minWavesDifficultyIncrease += 5;
            wavesSinceDifficultyIncrease = 0;
            Utility.log("Diff: " + difficulty);
            Utility.log("Level: " + (wave / minWavesDifficultyIncrease + 1));
        } else {
            wavesSinceDifficultyIncrease++;
        }
    }

    protected void incrementWave() {
//        System.out.printf("Wave: %d, LastHeight: %d\n", wave, spawnHeight);
        wave++;
        System.out.println("Wave: " + wave);
    }

    protected void determineSpawnInterval() {
        spawnInterval = spawnInterval_MIN;
        float spawnIntervalFloat = spawnInterval;

        if (enemytype == TYPE_HEAVY) {
//            if (spawnIntervalFloat > spawnInterval_MIN)
                spawnInterval = (long) (spawnIntervalFloat*1.1);
        }
    }

    public int getLevelHeight() {
        return height;
    }

    // Helpers
    protected void resetWaveSinceHealth() {
        waveSinceHealth = 0;
    }

    protected void resetWaveSincePower() {
        waveSincePower = 0;
    }

    protected void resetWaveSinceShield() {
        waveSinceShield = 0;
    }

    protected void spawnOneEnemy() {
        entityHandler.spawnEntity(spawnX, spawnY, enemytype);
//        System.out.println("Enemy spawned");
    }
}
