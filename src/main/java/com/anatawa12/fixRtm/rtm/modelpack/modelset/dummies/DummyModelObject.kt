package com.anatawa12.fixRtm.rtm.modelpack.modelset.dummies

import com.anatawa12.fixRtm.DummyModelPackManager
import com.anatawa12.fixRtm.FixRtm
import com.anatawa12.fixRtm.drawCenterString
import com.anatawa12.fixRtm.utils.BufferedImageFontRenderer
import jp.ngt.ngtlib.io.FileType
import jp.ngt.ngtlib.renderer.model.GroupObject
import jp.ngt.ngtlib.renderer.model.IModelNGT
import jp.ngt.ngtlib.renderer.model.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.Buffer
import javax.imageio.ImageIO
import kotlin.math.max

class DummyModelObject(val aabb: AxisAlignedBB,
                       val name: String,
                       val drawFaces: Set<EnumFacing>,
                       val rotate: Vec3d = Vec3d.ZERO,
                       val rotate1: Double = 0.0,
                       val nameOnly: Boolean = false) : IModelNGT {
    private val nameTexture = ResourceLocation("fix-rtm", "textures/generated/modelname_$name.png")

    init {
        if (drawFaces.isNotEmpty()) {
            GeneratedResourcePack.addImageGenerator(nameTexture.path) {
                val renderer = BufferedImageFontRenderer
                val width = renderer.getStringWidth(name)

                val img = BufferedImage(
                        width,
                        16,
                        BufferedImage.TYPE_INT_ARGB)

                val graphics = img.createGraphics()

                renderer.drawString(graphics, name, 0, 0)

                img
            }
        }
    }

    override fun getMaterials() = Companion.materials

    val aabbGlListId by lazy { getGlListId(aabb) }

    override fun renderAll(smoothing: Boolean) {
        GL11.glPushMatrix()
        if (smoothing) {
            GL11.glShadeModel(GL11.GL_SMOOTH)
        }
        GL11.glRotated(rotate1, rotate.x, rotate.y, rotate.z)

        aabb.apply {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation)

            if (!nameOnly) {
                GL11.glCallList(aabbGlListId)
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(nameTexture)

            if (EnumFacing.DOWN in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(center.x, minY - 0.01, center.z)
                GL11.glRotated(-90.0, 1.0, 0.0, 0.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
            if (EnumFacing.UP in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(center.x, maxY + 0.01, center.z)
                GL11.glRotated(90.0, 1.0, 0.0, 0.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
            if (EnumFacing.SOUTH in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(center.x, center.y, maxZ + 0.01)
                GL11.glRotated(180.0, 0.0, 1.0, 0.0)
                GL11.glRotated(180.0, 0.0, 0.0, 1.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
            if (EnumFacing.NORTH in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(center.x, center.y, minZ - 0.01)
                GL11.glRotated(0.0, 0.0, 1.0, 0.0)
                GL11.glRotated(180.0, 0.0, 0.0, 1.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
            if (EnumFacing.EAST in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(maxX + 0.01, center.y, center.z)
                GL11.glRotated(270.0, 0.0, 1.0, 0.0)
                GL11.glRotated(180.0, 0.0, 0.0, 1.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
            if (EnumFacing.WEST in drawFaces) {
                GL11.glPushMatrix()
                GL11.glTranslated(minX - 0.01, center.y, center.z)
                GL11.glRotated(90.0, 0.0, 1.0, 0.0)
                GL11.glRotated(180.0, 0.0, 0.0, 1.0)
                GL11.glScaled(0.01, 0.01, 0.01)
                drawName()
                GL11.glPopMatrix()
            }
        }

        GL11.glPopMatrix()
        return
    }

    private fun drawName() {
        val width = BufferedImageFontRenderer.getStringWidth(name)

        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)

        GL11.glBegin(GL11.GL_QUADS)

        GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(+width / 2.0, +8.0, 0.0)
        GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(+width / 2.0, -8.0, 0.0)
        GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(-width / 2.0, -8.0, 0.0)
        GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(-width / 2.0, +8.0, 0.0)

        GL11.glEnd()

    }

    override fun renderPart(smoothing: Boolean, partName: String) {
        renderAll(smoothing)
    }

    override fun renderOnly(smoothing: Boolean, vararg groupNames: String) {
        renderAll(smoothing)
    }

    override fun getGroupObjects(): List<GroupObject> = listOf()

    private val type = FileType("dummy_model_type", "dummy model made by anatawa12")

    override fun getType(): FileType = type

    override fun getDrawMode(): Int = 4

    override fun getSize(): FloatArray = floatArrayOf(
        aabb.minX.toFloat(),
        aabb.minY.toFloat(),
        aabb.minZ.toFloat(),
        aabb.maxX.toFloat(),
        aabb.maxY.toFloat(),
        aabb.maxZ.toFloat()
    )

    companion object {
        val resourceLocation = ResourceLocation("fix-rtm", "textures/ajwm5.png")
        lateinit var baseImage: BufferedImage
        fun init() {
            baseImage = ImageIO.read(Minecraft.getMinecraft().resourceManager.getResource(resourceLocation).inputStream)
        }
        val material = Material(0, resourceLocation)
        val materials = mapOf(DummyModelPackManager.getDummyName("") to Material(0, null))

        val glListIds = mutableMapOf<AxisAlignedBB, Int>()
        fun getGlListId(aabb: AxisAlignedBB): Int {
            if (glListIds[aabb] != null) return glListIds[aabb]!!

            val id = GL11.glGenLists(1)
            GL11.glNewList(id, GL11.GL_COMPILE)

            GL11.glBegin(GL11.GL_QUADS)

            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)
            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)

            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)
            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)

            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)
            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)
            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)

            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)

            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)
            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)

            GL11.glTexCoord2d(1.0, 0.0); GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
            GL11.glTexCoord2d(1.0, 1.0); GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 1.0); GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
            GL11.glTexCoord2d(0.0, 0.0); GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)

            GL11.glEnd()

            GL11.glEndList()

            glListIds[aabb] = id
            return id
        }
    }
}
