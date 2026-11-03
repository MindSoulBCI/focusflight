package com.example.picosmokedemo.p7c91e42a.domain
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random
class CoinEngineTest {
 @Test fun startsWithTwentyReachableCoins(){repeat(30){seed->val e=CoinEngine(Random(seed));assertEquals(20,e.state.coins.size);assertEquals(20,e.state.coins.map{it.slot}.toSet().size);e.state.coins.forEach{assertTrue(it.x in -.27f.. .27f);assertTrue(it.y in -.144f.. .162f);assertTrue(it.z in -.27f.. .27f)}}}
 @Test fun stationaryCarDoesNotCollect(){val e=CoinEngine(Random(3));repeat(100){assertEquals(0,e.update(FlightState()))};assertEquals(0,e.state.score)}
 @Test fun collisionScoresOnlyOnce(){val e=CoinEngine(Random(2));val c=e.state.coins.first();e.teleport(FlightState());val s=FlightState(x=c.x/.18f,y=c.y/.18f+1.5f,z=c.z/.18f-3f);assertTrue(e.update(s)>0);val score=e.state.score;assertFalse(e.state.coins.any{it.slot==c.slot});assertEquals(0,e.update(s));assertEquals(score,e.state.score);assertEquals(20,e.state.score+e.state.coins.size)}
 @Test fun addIsCappedAndRestartClears(){val e=CoinEngine(Random(5));repeat(10){e.add(20,FlightState())};assertEquals(60,e.state.coins.size);assertEquals(0,e.add(20,FlightState()));e.restart(FlightState());assertEquals(20,e.state.coins.size);assertEquals(0,e.state.score)}
 @Test fun teleportDoesNotSweepCoins(){val e=CoinEngine(Random(2));val c=e.state.coins.first();val s=FlightState(x=c.x/.18f,y=c.y/.18f+1.5f,z=c.z/.18f-3f);e.teleport(s);assertEquals(0,e.update(s));assertEquals(0,e.state.score)}
}
