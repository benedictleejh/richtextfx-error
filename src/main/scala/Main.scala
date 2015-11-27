import java.util.function.Function
import javafx.scene.input.{KeyEvent => JfxKeyEvent}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.BorderPane

import org.fxmisc.richtext.InlineStyleTextArea

object Main extends JFXApp {
  class BoldUnderline(val styled: Boolean) {
    val style = "-fx-font-weight: bold; -fx-underline: true;"
    def toCss = if (styled) style else ""
  }

  val styleBtn = new Button("Style") {
    onAction = { _ =>
      richtextbox.setStyle(richtextbox.getSelection.getStart,
                           richtextbox.getSelection.getEnd,
                           new BoldUnderline(true))
    }
  }

  lazy val richtextbox = new InlineStyleTextArea[BoldUnderline](
    new BoldUnderline(false),
    new Function[BoldUnderline, String] {
      override def apply(t: BoldUnderline): String = t.toCss
    }) {
    setPrefSize(400, 300)

    addEventFilter(KeyEvent.KeyTyped, { keyEvent: JfxKeyEvent =>
      if (keyEvent.shiftDown && keyEvent.character == " ") {
        setUseInitialStyleForInsertion(true)

        insertText(caretPosition(), " ")

        setUseInitialStyleForInsertion(false)

        // Shift-space also inserts a space on its own, so we consume the event to prevent double space insertion
        keyEvent.consume()
      }
    })

    def styleAt(position: Int) = getStyleAtPosition(position)
    def caretPosition = caretPositionProperty()
  }

  stage = new PrimaryStage {
    scene = new Scene {
      root = new BorderPane {
        top = styleBtn
        center = richtextbox
      }
    }
  }
}
