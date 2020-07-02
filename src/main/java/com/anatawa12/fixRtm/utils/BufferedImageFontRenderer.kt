package com.anatawa12.fixRtm.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.DataInputStream
import java.io.IOException
import javax.imageio.ImageIO

@Suppress("DEPRECATION")
object BufferedImageFontRenderer : IResourceManagerReloadListener {
    private val glyphWidth = ByteArray(65536)

    init {
        readGlyphSizes()
        Minecraft.getMinecraft().resourceManager.let { it as IReloadableResourceManager }.registerReloadListener(this)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        readGlyphSizes()
    }

    private fun readGlyphSizes() {
        var iresource: IResource? = null
        try {
            iresource = getResource(ResourceLocation("font/glyph_sizes.bin"))
            DataInputStream(iresource.inputStream).readFully(glyphWidth)
        } catch (ioexception: IOException) {
            throw RuntimeException(ioexception)
        } finally {
            IOUtils.closeQuietly(iresource as Closeable?)
        }
    }

    @Throws(IOException::class)
    private fun getResource(location: ResourceLocation): IResource {
        return Minecraft.getMinecraft().resourceManager.getResource(location)
    }

    @Suppress("unused")
    fun drawString(graphics2D: Graphics2D, text: String, x: Int, y: Int): Int {
        return renderString(graphics2D, text, x, y)
    }

    private fun renderString(graphics2D: Graphics2D, text: String, x: Int, y: Int): Int {
        var posX = x

        for (char in text) {
            posX += renderChar(graphics2D, char, posX, y)
        }

        return posX
    }

    /**
     * Render the given char
     */
    private fun renderChar(graphics2D: Graphics2D, ch: Char, x: Int, y: Int): Int {
        // U+00A0: nbsp
        if (ch == ' ' || ch == '\u00A0') return 8

        val glyphWidth: Int = glyphWidth[ch.toInt()].toInt() and 255
        if (glyphWidth == 0) return 0

        val page = getUnicodePage(ch.toInt() / 256)

        val left = glyphWidth ushr 4
        val right = (glyphWidth and 15) + 1
        val width = right - left

        val charIndexInPage = ch.toInt() and 255
        val charIndexU = charIndexInPage % 16
        val charIndexV = charIndexInPage / 16

        val leftU = charIndexU * 16 + left
        val topV = charIndexV * 16

        graphics2D.drawImage(page,
                x, y,
                x + width, y + 16,
                (leftU).scaleFrom256(page.width), (topV).scaleFrom256(page.height),
                (leftU + width).scaleFrom256(page.width), (topV + 16).scaleFrom256(page.height),
                null)

        return width + 2
    }

    private fun getUnicodePage(page: Int): BufferedImage {
        if (UNICODE_PAGE_IMAGES[page] == null) {
            val location = ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page))
            UNICODE_PAGE_IMAGES[page] = getResource(location).use { ImageIO.read(it.inputStream) }
        }
        return UNICODE_PAGE_IMAGES[page]!!
    }

    private fun Int.scaleFrom256(max: Int): Int {
        if (max % 256 != 0) throw IllegalStateException("max is not multiples of 256")
        return this * max/256
    }

    /**
     * Returns the width of this string
     */
    @Suppress("unused")
    fun getStringWidth(text: String): Int = text.sumBy { getCharWidth(it) }

    /**
     * Returns the width of this character.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getCharWidth(character: Char): Int {
        // U+00A0: nbsp
        if (character == ' ' || character == '\u00A0') return 8

        val glyphWidth: Int = glyphWidth[character.toInt()].toInt() and 255
        if (glyphWidth == 0) return 0

        val left = glyphWidth ushr 4
        val right = (glyphWidth and 15) + 1

        val width = right - left

        return width + 2
    }

    private val UNICODE_PAGE_IMAGES = arrayOfNulls<BufferedImage>(256)
}
