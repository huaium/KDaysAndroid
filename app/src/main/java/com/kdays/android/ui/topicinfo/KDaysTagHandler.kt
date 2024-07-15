package com.kdays.android.ui.topicinfo

import android.text.Editable
import android.text.Html.TagHandler
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.xml.sax.XMLReader
import java.lang.reflect.Field


class KDaysTagHandler(val activity: TopicInfoActivity, val pid: String) : TagHandler {

    private var startIndex = 0
    private var endIndex = 0
    override fun handleTag(
        opening: Boolean,
        tag: String,
        output: Editable,
        xmlReader: XMLReader
    ) {
        if (tag.equals("button", ignoreCase = true)) {
            if (opening) {
                startIndex = output.length
            } else {
                endIndex = output.length

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val alertDialog = MaterialAlertDialogBuilder(widget.context)
                            .setTitle("购买帖子")
                            .setMessage("确定要购买吗？")
                            .setCancelable(true)
                            .setPositiveButton("确定") { dialog, _ ->
                                val callback = activity as BuyCallback
                                callback.buy(pid)

                                dialog.dismiss()
                            }
                            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }

                output.setSpan(
                    clickableSpan,
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getProperty(xmlReader: XMLReader, property: String): String? {
        try {
            val elementField: Field = xmlReader.javaClass.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element = elementField.get(xmlReader)
            val attsField: Field = element.javaClass.getDeclaredField("theAtts")
            attsField.isAccessible = true
            val atts = attsField.get(element)
            val dataField: Field = atts.javaClass.getDeclaredField("data")
            dataField.isAccessible = true
            val data = dataField.get(atts) as Array<String>
            val lengthField: Field = atts.javaClass.getDeclaredField("length")
            lengthField.isAccessible = true
            val len = lengthField.get(atts) as Int
            for (i in 0 until len) {
                if (property == data[i * 5 + 1]) {
                    return data[i * 5 + 4]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    interface BuyCallback {
        fun buy(pid: String)
    }
}

