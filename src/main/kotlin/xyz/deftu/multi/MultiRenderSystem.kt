package xyz.deftu.multi

//#if MC>=11700
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.ShaderProgram
//#endif

import net.minecraft.util.Identifier
import java.util.function.Supplier
import org.lwjgl.opengl.GL13

object MultiRenderSystem {
    //#if MC>=11700
    fun setShader(supplier: Supplier<ShaderProgram?>) {
        RenderSystem.setShader(supplier)
    }

    fun removeShader() {
        setShader { null }
    }

    fun setShaderTexture(index: Int, texture: Identifier) {
        RenderSystem.setShaderTexture(index, texture)
    }
    //#endif

    fun setTexture(index: Int, texture: Identifier) {
        //#if MC>=11700
        setShaderTexture(index, texture)
        //#else
        //$$ MultiGlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + index)
        //$$ MultiClient.getTextureManager().bindTexture(texture)
        //#endif
    }
}
