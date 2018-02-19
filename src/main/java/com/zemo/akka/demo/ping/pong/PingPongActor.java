package com.zemo.akka.demo.ping.pong;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by zemi on 05/06/2017.
 */
public class PingPongActor extends AbstractActor {

  static public Props props() {
    return Props.create(PingPongActor.class, () -> new PingPongActor());
  }

  static public class PingMsg {
    public final String payload;

    public PingMsg(String payload) {
      this.payload = payload;
    }
  }

  static public class PongMsg {
    public final String payload;

    public PongMsg(String payload) {
      this.payload = payload;
    }
  }

  static public class StartMsg {
    public final ActorRef actor;

    public StartMsg(ActorRef actor) {
      this.actor = actor;
    }
  }
  static public class StopMsg {}

  static public class StopDoneMsg {}

  private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private long countPing = 0;

  private long countPong = 0;

  @Override
  public void postStop() throws Exception {
    super.postStop();
    log.info(PingPongActor.class.getSimpleName()+ "actor with name: "+ this.self().path().name()+ " is going to stop!");
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(StartMsg.class, startMsgMsg -> {
          log.info("StartMsg: "+ this.self().path().name());
          startMsgMsg.actor.tell(new PingMsg("assadas"), self());
        })
        .match(PingMsg.class, pingMsg -> {
          log.info("Ping -> Actor name: " +this.self().path().name()+ " with payload: " + pingMsg.payload);
          countPing += 1;
          getSender().tell(new PongMsg("asdasds"), self());
        })
        .match(PongMsg.class, pongMsg -> {
          log.info("Pong -> name: "+ this.self().path().name()+ " with payload: " + pongMsg.payload);
          countPong += 1;
        })
        .match(StopMsg.class, stopMsg -> {
          log.info("StopMsg: "+ this.self().path().name());
          getContext().stop(self());
          getSender().tell(new StopDoneMsg(), self());
        })
        .build();
  }


}
