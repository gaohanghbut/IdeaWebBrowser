package cn.yxffcode.intellij.webbrowser

import javafx.event.{Event, EventHandler}

/**
  * @author gaohang on 10/20/16.
  */
object Conversions {

  implicit def asEventHandler[A <: Event](function1: Function1[A, Unit]): EventHandler[A] = {
    new EventHandler[A] {
      override def handle(event: A): Unit = function1(event)
    }
  }

}
