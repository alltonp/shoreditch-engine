package im.mange.shoreditch.engine

import im.mange.shoreditch.engine.services.Services
import akka.actor.{Props, ActorSystem}
import im.mange.shoreditch.engine.listener.{CompositeListener, TestRunReportListener, LoggingListener}
import im.mange.shoreditch.hipster.Script
import im.mange.shoreditch.engine.model.{TestPackRunReport, TestRunReport, TestPack, Test}

//TODO: a little try catch wouldnt go amiss
//TODO: not to mention a little actor/actor system safety - share across tests etc
case class SimpleTestRunner(testRunReportOutputDirectory: Option[String] = None) {
  def run(test: Test, services: Services): TestRunReport = {
    //TODO: hide this, so that users see no akka, hear no akka
    val actorSystem = ActorSystem.create()
    val engine = actorSystem.actorOf(Props(new EngineActor))
    val clock = RealClock
    val listeners = LoggingListener()(clock) :: testRunReportOutputDirectory.fold(List.empty[ScriptEventListener])(d => List(TestRunReportListener(test, d)))

    val script = Script.parse(
      CompositeListener(listeners),
      services, test.content, test.name
    )(clock)

    script.beforeRun(test.id)
    engine ! script

    while (!script.isCompleted) { Thread.`yield` }

    //TODO: ultimately we don't want to this rubbish either
    actorSystem.shutdown()

//    script.abortedBecause.map("Test: " + test.id + " - " + test.name + " failed:\n" + _)
    TestRunReport.create(test, script, script.versionedServices.getOrElse(Nil))
  }

  def run(testPack: TestPack, services: Services, inParallel: Boolean = false): TestPackRunReport = {
    val tests = testPack.tests
    val toRun = if (inParallel) tests.par else tests
    val reports = toRun.map(run(_, services)).toList
//    if (failures.isEmpty) None else Some("TestPack: " + testPack.id + " - " + testPack.name + " failed:\n" + failures.mkString("\n"))
    TestPackRunReport(testPack, reports)
  }
}