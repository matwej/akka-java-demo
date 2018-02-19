package com.zemo.akka.demo.ping.pong;

import static org.junit.Assert.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by zemi on 09/06/2017.
 */
public class PingPongActorTest {

  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void testPingStart() {
    final TestKit actorPingPongTestProbe = new TestKit(system);

    final ActorRef actorPingPongStart = system.actorOf(PingPongActor.props(), PingPongActor.class.getCanonicalName()+"StartMsg");

    actorPingPongStart.tell(new PingPongActor.StartMsg(actorPingPongTestProbe.getRef()), ActorRef.noSender());

    PingPongActor.PingMsg msg = actorPingPongTestProbe.expectMsgClass(PingPongActor.PingMsg.class);

    assertFalse(msg.payload.isEmpty());
  }

  @Test
  public void testPingPong_Stop() throws InterruptedException {
    final TestKit actorPingPongTestProbe = new TestKit(system);

    final ActorRef actorPingPongStart = system.actorOf(PingPongActor.props(), PingPongActor.class.getCanonicalName()+"StartNoPingMsg");

    actorPingPongStart.tell(new PingPongActor.PingMsg("Ping Test msg"), actorPingPongTestProbe.getRef());
    PingPongActor.PongMsg msg = actorPingPongTestProbe.expectMsgClass(PingPongActor.PongMsg.class);
    assertFalse(msg.payload.isEmpty());

    actorPingPongStart.tell(new PingPongActor.StopMsg(), actorPingPongTestProbe.getRef());
    actorPingPongTestProbe.expectMsgClass(PingPongActor.StopDoneMsg.class);

    actorPingPongStart.tell(new PingPongActor.PingMsg("Ping Test msg after StopMsg"), actorPingPongTestProbe.getRef());
    actorPingPongTestProbe.getSystem().eventStream().subscribe(actorPingPongTestProbe.getRef(), DeadLetter.class);
    DeadLetter deadLetter = actorPingPongTestProbe.expectMsgAnyClassOf(DeadLetter.class);
    //deadLetter.message() // check what message, should be Stop
  }
}
