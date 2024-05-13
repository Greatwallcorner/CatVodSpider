package com.github.catvod.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.github.catvod.crawler.SpiderDebug
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CompletableFuture
import javax.swing.JDialog
import javax.swing.SwingUtilities

/**
 * 同时只能打开一个对话框 阻塞当前线程
 */
object DialogUtil {
    private var dialog:JDialog? = null
    fun close(){
        dialog?.isVisible = false
        dialog?.dispose()
    }
    fun showDialog(content:@Composable ()->Unit, title: String = "TV") {
        val completableFuture = CompletableFuture<String>()
        SwingUtilities.invokeLater{
            try {
                val jDialog = JDialog(null as Frame?)
                val panel = ComposePanel()
                panel.setContent(content)

                jDialog.contentPane = panel
                jDialog.title = title
                jDialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
                jDialog.pack()
                jDialog.setLocationRelativeTo(null)
                jDialog.location = Swings.getCenter(panel.width, panel.height)
                jDialog.isVisible = true
                dialog = jDialog
                dialog?.addWindowListener(object : WindowAdapter() {
//                    override fun windowOpened(e: WindowEvent?) {
//                    }
//
//                    override fun windowClosing(e: WindowEvent?) {
//                    }
//
//                    override fun windowClosed(e: WindowEvent?) {
//                    }
//
//                    override fun windowIconified(e: WindowEvent?) {
//                    }
//
//                    override fun windowDeiconified(e: WindowEvent?) {
//                    }
//
//                    override fun windowActivated(e: WindowEvent?) {
//                    }

                    override fun windowClosed(e: WindowEvent?) {
                        SpiderDebug.log("DialogUtils window state change closed" + e?.window?.isActive + " " + e?.window?.isVisible)
                            completableFuture.complete("normal")
                    }

                })
            } catch (e: Exception) {
                completableFuture.completeExceptionally(e)
            }
        }
        completableFuture.get()
        SpiderDebug.log("DialogUtil closed")
    }
}