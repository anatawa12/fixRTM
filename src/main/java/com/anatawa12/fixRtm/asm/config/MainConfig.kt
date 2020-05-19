package com.anatawa12.fixRtm.asm.config

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Loader

object MainConfig {
    private val configFile = Loader.instance().configDir.resolve("fix-rtm.cfg")
    private val config = Configuration(configFile)

    private const val categoryModelLoading = "model_loading"
    private const val categoryBetterRtm = "better_rtm"
    private const val categoryBetterNgtLib = "better_ngtlib"

    @JvmField
    val multiThreadModelConstructEnabled = config.getBoolean(
            "multiThreadConstructEnabled", categoryModelLoading,
            true,
            "constructs models using a thread with a number of logical cores")

    @JvmField
    val cachedPolygonModel = config.getBoolean(
            "cachedPolygonModelEnabled", categoryModelLoading,
            true,
            "caches obj, mqo model.")

    @JvmField
    val cachedScripts = config.getBoolean(
            "cachedScriptsEnabled", categoryModelLoading,
            true,
            "caches compiled script and executed environment")

    @JvmField
    val dummyModelPackEnabled = config.getBoolean(
            "dummyModelPackEnabled", categoryBetterRtm,
            true,
            "use dummy ModelPack generated by fixRTM for not loaded models")

    @JvmField
    val markerDistanceMoreRealPosition = config.getBoolean(
            "markerDistancesMoreRealPosition", categoryBetterRtm,
            true,
            "shows distance signs of marker at more real position")

    @JvmField
    val changeTestTrainTextureEnabled = config.getBoolean(
            "changeTestTrainTexture", categoryBetterRtm,
            true,
            "change texture for test train to make easy to identify test train and electric train")

    @JvmField
    val addAllowAllPermissionEnabled = config.getBoolean(
            "addAllowAllPermission", categoryBetterNgtLib,
            true,
            "adds a permission meaning all permissions are approved")

    init {
        if (config.hasChanged()) {
            config.save()
        }
    }
}
