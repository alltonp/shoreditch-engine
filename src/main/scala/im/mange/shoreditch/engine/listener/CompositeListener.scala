package im.mange.shoreditch.engine.listener

import org.joda.time.LocalDateTime
import im.mange.shoreditch.engine.hipster._
import im.mange.shoreditch.engine.ScriptEventListener
import im.mange.shoreditch.engine.hipster.VersionedService
import im.mange.shoreditch.engine.hipster.Check
import im.mange.shoreditch.engine.hipster.Action

case class CompositeListener(listeners: Seq[ScriptEventListener]) extends ScriptEventListener {
  override def beforeStarted(script: Script, testId: String) { listeners.foreach(_.beforeStarted(script, testId)) }
  override def validated(testRunId: String, versionedServices: Seq[VersionedService]) { listeners.foreach(_.validated(testRunId, versionedServices)) }
  override def started(when: LocalDateTime, script: Script) { listeners.foreach(_.started(when, script)) }
  override def stopped(when: LocalDateTime, script: Script) { listeners.foreach(_.stopped(when, script)) }
  override def running(step: Step) { listeners.foreach(_.running(step)) }
  override def failure(action: Action, reasons: Seq[String]) { listeners.foreach(_.failure(action, reasons)) }
  override def success(action: Action) { listeners.foreach(_.success(action)) }
  override def failure(check: Check, reasons: Seq[String]) { listeners.foreach(_.failure(check, reasons)) }
  override def success(check: Check) { listeners.foreach(_.success(check)) }
}