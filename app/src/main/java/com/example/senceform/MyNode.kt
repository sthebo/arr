package com.example.senceform

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import java.util.concurrent.CompletableFuture

@RequiresApi(Build.VERSION_CODES.N)
class MyNode(context:Context, modelId:Int):AnchorNode(){
    companion object {
        private var modelRender:CompletableFuture<ModelRenderable>?=null
    }
    var image:AugmentedImage?=null
    @RequiresApi(Build.VERSION_CODES.N)
    set(image){
        field=image
        if (!modelRender!!.isDone)
        {
            CompletableFuture.allOf(modelRender).thenAccept {
                aVoid:Void->this@MyNode.image = image
            }.exceptionally { throwable -> null }
        }

        anchor = image!!.createAnchor(image.centerPose)
        val node = Node()
        val pose = Pose.makeTranslation(0f,0f,0.25f)
        node.setParent(this)
        node.localPosition = Vector3(pose.tx(),pose.ty(),pose.tz())
        node.localRotation = Quaternion(pose.qx(),pose.qy(),pose.qz(),pose.qw())
        node.renderable = modelRender!!.getNow(null)

    }
    init {
        if (modelRender == null)
            modelRender = ModelRenderable.builder().setRegistryId("my_model")
                    .setSource(context,modelId)
                .build()
    }
}