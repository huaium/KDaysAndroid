package com.kdays.android.ext

import android.net.Uri
import com.kdays.android.ui.editor.EditorActivity
import com.kdays.android.ui.editor.GlideEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.yalantis.ucrop.UCrop
import java.io.File

object EditorActivityExts {
    fun EditorActivity.selectPicture(onUpload: (List<LocalMedia?>) -> Unit) {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSandboxFileEngine { context, srcPath, mineType, call ->
                val sandboxPath = SandboxTransformUtils.copyPathToSandbox(
                    context,
                    srcPath,
                    mineType
                )
                call.onCallback(srcPath, sandboxPath)
            }
            .setEditMediaInterceptListener { fragment, currentLocalMedia, requestCode ->
                val currentEditPath = currentLocalMedia.availablePath
                val currentMimeType = currentLocalMedia.mimeType
                val sandboxPath = SandboxTransformUtils.copyPathToSandbox(
                    fragment.requireContext(),
                    currentEditPath,
                    currentMimeType
                )
                val inputUri = if (PictureMimeType.isContent(sandboxPath)) {
                    Uri.parse(sandboxPath)
                } else {
                    Uri.fromFile(File(sandboxPath))
                }
                val destinationUri = Uri.fromFile(
                    File(
                        sandboxPath.substringBeforeLast("/"),
                        DateUtils.getCreateFileName("CROP_") + ".jpeg"
                    )
                )
                val uCrop = UCrop.of<Any>(inputUri, destinationUri)
                val options = UCrop.Options()
                options.setHideBottomControls(false)
                uCrop.withOptions(options)
                uCrop.startEdit(fragment.requireActivity(), fragment, requestCode)
            }
            .setMaxVideoSelectNum(0)
            .setMaxSelectNum(9)
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>) {
                    onUpload(result)
                }

                override fun onCancel() {}
            })
    }
}