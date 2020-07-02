package com.anatawa12.fixRtm.rtm.modelpack.modelset.dummies

import com.anatawa12.fixRtm.DummyModelPackManager
import com.anatawa12.fixRtm.asm.MultiModelObject
import jp.ngt.ngtlib.io.ResourceLocationCustom
import jp.ngt.ngtlib.renderer.model.IModelNGT
import jp.ngt.ngtlib.renderer.model.Material
import jp.ngt.ngtlib.renderer.model.TextureSet
import jp.ngt.rtm.RTMResource.*
import jp.ngt.rtm.modelpack.ModelPackManager
import jp.ngt.rtm.modelpack.ResourceType
import jp.ngt.rtm.modelpack.cfg.*
import jp.ngt.rtm.modelpack.model.ModelBogie
import jp.ngt.rtm.modelpack.modelset.*
import jp.ngt.rtm.render.BasicRailPartsRenderer
import jp.ngt.rtm.render.ModelObject
import jp.ngt.rtm.render.WirePartsRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun ModelSetBase<*>.constructOnClientDummy(model: IModelNGT) {
    val name = this.config.name
    config.doCulling = true
    modelObj = ModelObject(model,
            arrayOf(
                    TextureSet(DummyModelObject.material, 0, false, false)
            ))
    modelObj.renderer.init(this, modelObj)
    buttonTexture = GeneratedResourcePack.addImage(createButtonTexture(name))
}


fun createButtonTexture(name: String): BufferedImage {
    val img = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB)
    val buttonWidth = 640
    val buttonHeight = 128
    val graphics = img.createGraphics()
    graphics.paint = Color.BLACK
    graphics.draw(Rectangle(0, 0, buttonWidth, buttonHeight))
    graphics.paint = Color.WHITE
    val fontMetrics = graphics.fontMetrics
    val nameWidth = fontMetrics.stringWidth(name)
    val nameHeight = fontMetrics.stringWidth(name)
    graphics.drawString(name, (buttonWidth - nameWidth) / 2, (buttonHeight - nameHeight) / 2)

    return img
}

class DummyModelSetConnector(name: String) : ModelSetConnector( // ex
        ConnectorConfig.getDummy().apply { this.exName = name }) {
    override fun constructOnClient() = constructOnClientDummy(DummyModelObject(AxisAlignedBB(
            -0.5, -0.25, -0.25,
            0.25, 0.25, 0.25), config.name, setOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST), Vec3d(0.0, 0.0, 1.0), 90.0))

    override fun constructOnServer() {}

    companion object {
        var ConnectorConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyModelSetContainer(name: String) : ModelSetContainer( // ok
        ContainerConfig.getDummy().apply { this.exName = name }.apply { offset = floatArrayOf(0f, 0f, 0f) }) {
    override fun constructOnClient() = constructOnClientDummy(DummyModelObject(AxisAlignedBB(
            -1.5, 2.5, -1.5,
            1.5, 0.0, 1.5), config.name, setOf(*EnumFacing.VALUES)))

    override fun constructOnServer() {}

    companion object {
        var ContainerConfig.exName: String by ReflectValue.make("containerName")
    }
}

class DummyModelSetFirearm(name: String) : ModelSetFirearm(// ok
        FirearmConfig.getDummyConfig().apply { this.exName = name }) {
    override fun constructOnClient() = constructOnClientDummy(DummyModelObject(AxisAlignedBB(
            -1.5, 0.0, -1.5,
            1.5, 2.5, 1.5), config.name, setOf(*EnumFacing.VALUES)))

    override fun constructOnServer() {}

    companion object {
        var FirearmConfig.exName: String by ReflectValue.make("firearmName")
    }
}

class DummyModelSetMachine(name: String, val type: ResourceType<MachineConfig, ModelSetMachine>) : ModelSetMachine(// ok
        MachineConfig.getDummy().apply { this.exName = name }) {
    override fun constructOnClient() {
        println(type.name)
        println(type.subType)
        when (type) {
            MACHINE_ANTENNA_RECEIVE, MACHINE_ANTENNA_SEND ->
                constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                        -0.5, 0.0, -0.5,
                        0.5, 0.0625, 0.5), config.name, setOf(*EnumFacing.VALUES)))
            MACHINE_GATE ->
                constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                        -0.375, 0.0, -0.375,
                        0.375, 3.0, 0.375), config.name, setOf(*EnumFacing.VALUES)))
            else ->
                constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                        -0.5, 0.0, -0.5,
                        0.5, 1.0, 0.5), config.name, setOf(*EnumFacing.VALUES)))
        }
    }

    override fun constructOnServer() {}

    companion object : DummyModelPackManager.ModelPackCreator<ModelSetMachine, MachineConfig> {
        override val dummyFor: Class<out ResourceSet<MachineConfig>>
            get() = ModelSetMachine::class.java

        override fun make(name: String, type: ResourceType<MachineConfig, ModelSetMachine>): ModelSetMachine = DummyModelSetMachine(name, type)

        var MachineConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyModelSetNPC(name: String) : ModelSetNPC( // ok
        NPCConfig.getDummy().apply { this.exName = name }.apply { offset = floatArrayOf(0f, 0f, 0f) }) {
    override fun constructOnClient() = constructOnClientDummy(DummyModelObject(AxisAlignedBB(
            -0.3, 0.0, -0.3,
            0.3, 1.8, 0.3), config.name, setOf(*EnumFacing.VALUES)))

    override fun constructOnServer() {}

    companion object {
        var NPCConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyModelSetOrnament(name: String, val type: ResourceType<OrnamentConfig, ModelSetOrnament>) : ModelSetOrnament( // ok
        OrnamentConfig.getDummy().apply { this.exName = name }) {
    override fun constructOnClient() {
        when (type) {
            ORNAMENT_SCAFFOLD ->
                constructOnClientDummy(
                        MultiModelObject(
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.5, -0.4375,
                                        0.5, -0.4375, 0.4375), config.name, setOf(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST)),
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.5, -0.5,
                                        0.5, 0.5, -0.4375), config.name, setOf(EnumFacing.NORTH, EnumFacing.SOUTH)),
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.5, 0.4375,
                                        0.5, 0.5, 0.5), config.name, setOf(EnumFacing.NORTH, EnumFacing.SOUTH))
                        ))

            ORNAMENT_STAIR ->
                constructOnClientDummy(
                        MultiModelObject(
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.0, -0.4375,
                                        0.0, 0.5, 0.4375), config.name, setOf(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST)),
                                DummyModelObject(AxisAlignedBB(
                                        -0.0, -0.5, -0.4375,
                                        0.5, -0.0, 0.4375), config.name, setOf(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST)),
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.5, -0.5,
                                        0.5, 0.5, -0.4375), config.name, setOf(EnumFacing.NORTH, EnumFacing.SOUTH)),
                                DummyModelObject(AxisAlignedBB(
                                        -0.5, -0.5, 0.4375,
                                        0.5, 0.5, 0.5), config.name, setOf(EnumFacing.NORTH, EnumFacing.SOUTH))
                        ))
            else ->
                constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                        -0.5, -0.5, -0.5,
                        0.5, 0.5, 0.5), config.name, setOf(*EnumFacing.VALUES)))
        }
    }

    override fun constructOnServer() {}

    companion object : DummyModelPackManager.ModelPackCreator<ModelSetOrnament, OrnamentConfig> {
        override val dummyFor: Class<out ResourceSet<OrnamentConfig>>
            get() = ModelSetOrnament::class.java

        override fun make(name: String, type: ResourceType<OrnamentConfig, ModelSetOrnament>): ModelSetOrnament = DummyModelSetOrnament(name, type)

        var OrnamentConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyModelSetRail(name: String) : ModelSetRail(
        RailConfig.getDummy().apply { this.exName = name }) {
    override fun constructOnClient() {
        constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                -0.5, 0.0, -0.5,
                0.5, 1.0, 0.5), config.name, setOf(*EnumFacing.VALUES)))
        val f = modelObj::class.java.getField("renderer")
        f.isAccessible = true
        f.set(modelObj, BasicRailPartsRenderer())
        modelObj.renderer.init(this, modelObj)
    }

    override fun constructOnServer() {}

    companion object {
        var RailConfig.exName: String by ReflectValue.make("railName")
    }
}

class DummyModelSetSignal(name: String) : ModelSetSignal(
        SignalConfig.getDummyConfig().apply { this.exName = name }) {
    override fun constructOnClient() = constructOnClientDummy(DummyModelObject(AxisAlignedBB(
        -0.5, 0.0, -0.5,
        0.5, 1.0, 0.5), config.name, setOf(*EnumFacing.VALUES)  ))
    override fun constructOnServer() {}

    companion object {
        var SignalConfig.exName: String by ReflectValue.make("signalName")
    }
}

class DummyModelSetTrain(name: String) : ModelSetTrain( // ok
        TrainConfig.getDummyConfig().apply { this.exName = name }) {
    override fun constructOnClient() {
        constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                -1.375, -1.1875, -1.375,
                1.375, 0.0, 1.375), config.name, setOf(*EnumFacing.VALUES)))
        this.bogieModels = arrayOfNulls(2)
        val tex = TextureSet(Material(0.toByte(), ModelPackManager.INSTANCE.getResource("textures/train/hoge.png")), 0, false, false)
        val model = ModelObject(ModelBogie(), arrayOf(tex))
        model.renderer.init(this, model)
        this.bogieModels[1] = model
        this.bogieModels[0] = model
    }

    override fun constructOnServer() {}

    companion object {
        var TrainConfig.exName: String by ReflectValue.make("trainName")
    }
}

class DummyModelSetVehicle(name: String) : ModelSetVehicle( // ok
        VehicleConfig.getDummy().apply { this.exName = name }) {
    override fun constructOnClient() =
            constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                    -1.5, 0.0, -1.5,
                    1.5, 1.125, 1.5), config.name, setOf(*EnumFacing.VALUES)))

    override fun constructOnServer() {}

    companion object {
        var VehicleConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyModelSetWire(name: String) : ModelSetWire( // ok
        WireConfig.getDummy()
                .apply { this.exName = name }
                .apply { sectionLength = 1.0f }) {
    override fun constructOnClient() {
        constructOnClientDummy(DummyModelObject(AxisAlignedBB(
                -0.0, -0.0625, -0.0625,
                1.0, 0.0625, 0.0625),
                config.name, setOf(), Vec3d(0.0, 0.0, 1.0),
                90.0))
        val f = modelObj::class.java.getField("renderer")
        f.isAccessible = true
        f.set(modelObj, WirePartsRenderer(false))
        modelObj.renderer.init(this, modelObj)
    }

    override fun constructOnServer() {}

    companion object {
        var WireConfig.exName: String by ReflectValue.make("name")
    }
}

class DummyTextureSetRRS(name: String) : TextureSetRRS(RRSConfig(name)) {
    override fun constructOnClient() {

        this.texture = ResourceLocationCustom("minecraft", this.config.texture)
    }

    override fun constructOnServer() {}
}

class DummyTextureSetSignboard(name: String) : TextureSetSignboard(TextureSetSignboard().dummyConfig.also { it.texture = name }.also { it.init() }) {
    override fun constructOnClient() {
        this.texture = ResourceLocationCustom("minecraft", this.config.texture)
    }

    override fun constructOnServer() {}
}

class DummyTextureSetFlag(name: String) : TextureSetFlag(TextureSetFlag().dummyConfig.also { it.texture = name }.also { it.init() }) {
    override fun constructOnClient() {
        this.texture = ResourceLocationCustom("minecraft", this.config.texture)
    }

    override fun constructOnServer() {}
}

@Suppress("UNCHECKED_CAST")
class ReflectValue<in R, T>(private val reflect: Field) : ReadWriteProperty<R, T> {

    override fun getValue(thisRef: R, property: KProperty<*>): T = reflect[thisRef] as T

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        reflect[thisRef] = value
    }

    companion object {
        fun <R, T> make(clazz: Class<R>, name: String) = ReflectValue<R, T>(clazz.getDeclaredField(name).apply { isAccessible = true })
        inline fun <reified R, T> make(name: String) = ReflectValue<R, T>(R::class.java.getDeclaredField(name).apply { isAccessible = true })
    }
}
