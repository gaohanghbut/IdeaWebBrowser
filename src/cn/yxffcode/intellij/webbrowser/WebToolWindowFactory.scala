package cn.yxffcode.intellij.webbrowser

import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.embed.swing.JFXPanel
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{Button, TextField}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout.{HBox, VBox}
import javafx.scene.web.WebView
import javafx.scene.{Group, Scene}

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.content.impl.ContentImpl
import cn.yxffcode.intellij.webbrowser.Conversions.asEventHandler

/**
  * @author gaohang on 10/16/16.
  */
class WebToolWindowFactory extends ToolWindowFactory {
  var width: Int = 1000

  var height: Int = 1000

  val initUrl: String = "http://www.baidu.com"

  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {
    val webBrowser: JFXPanel = new JFXPanel
    Platform.runLater(new Runnable() {
      override def run() {
        createWebBrowser(webBrowser);
      }
    })
    val content = new ContentImpl(webBrowser, "Web Browser", true)
    toolWindow.getContentManager.addContent(content)
  }

  val forwardButtonWidth: Int = 200

  private def createWebBrowser(webBrowser: JFXPanel): Unit = {
    val root = new Group()
    val scene = new Scene(root)
    webBrowser.setScene(scene)
    val widthDouble = new Integer(width).doubleValue()
    val heightDouble = new Integer(height).doubleValue()

    val box = new VBox(10)
    val urlBox = new HBox(10)

    val webView = new WebView()

    val back: Button = new Button("back")
    setBackEvent(urlBox, back)(webView)

    val urlTextField = new TextField()

    setUrlText(webView, urlTextField)

    urlBox.getChildren().addAll(urlTextField)

    val go = new Button("go")
    setGoButton(go, urlTextField)(webView)
    urlBox.getChildren.add(go)

    webView.setMinSize(widthDouble, heightDouble)
    webView.setPrefSize(widthDouble, heightDouble)

    setAutoResize(scene, webView, urlTextField)

    val eng = webView.getEngine()
    eng.load(initUrl)
    root.getChildren().add(webView)

    box.getChildren().add(urlBox)
    box.getChildren().add(webView)
    root.getChildren().add(box)

  }

  def setGoButton(go: Button, urlTextField: TextField)(implicit webView: WebView): Unit = {
    go.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent) = {
        webView.getEngine.load(urlTextField.getText())
      }
    })
  }

  def setUrlText(webView: WebView, urlTextField: TextField): Unit = {
    urlTextField.setText(initUrl)

    webView.getEngine.locationProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        urlTextField.setText(newValue)
      }
    })
    urlTextField.setPrefWidth(width - forwardButtonWidth)

    urlTextField.setOnKeyTyped(new EventHandler[KeyEvent] {
      override def handle(event: KeyEvent): Unit = {
        if (event.getCode == KeyCode.ENTER) {
          webView.getEngine.load(urlTextField.getText)
        }
      }
    })
  }

  def setBackEvent(urlBox: HBox, back: Button)(implicit webView: WebView): Unit = {
    urlBox.getChildren.add(back)
    back.setOnMouseClicked(asEventHandler(e => {
      webView.getEngine.getHistory.go(-1)
    }))
  }

  private def setAutoResize(scene: Scene, view: WebView, urlText: TextField): Unit = {
    scene.widthProperty().addListener(new ChangeListener[Number]() {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        view.setPrefWidth(newValue.doubleValue())
        urlText.setPrefWidth(newValue.doubleValue() - forwardButtonWidth)
      }
    })
    scene.heightProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        view.setPrefHeight(newValue.doubleValue())
      }
    })
  }
}
