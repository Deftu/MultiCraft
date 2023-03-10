package xyz.deftu.multi.shader

//#if MC>=11700
import net.minecraft.client.gl.GlBlendState
//#endif

//#if MC>=11500
import org.lwjgl.opengl.GL20
//#endif

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import xyz.deftu.multi.MultiGlStateManager

/**
 * Adapted from EssentialGG UniversalCraft under LGPL-3.0
 * https://github.com/EssentialGG/UniversalCraft/blob/f4917e139b5f6e5346c3bafb6f56ce8877854bf1/LICENSE
 */
data class BlendState(
    val equation: BlendEquation,
    val srcRgb: BlendFactor,
    val dstRgb: BlendFactor,
    val srcAlpha: BlendFactor = srcRgb,
    val dstAlpha: BlendFactor = dstRgb,
    val enabled: Boolean = true
) {
    companion object {
        @JvmField
        val DISABLED = BlendState(BlendEquation.ADD, BlendFactor.ONE, BlendFactor.ZERO, enabled = false)
        @JvmField
        val NORMAL = BlendState(BlendEquation.ADD, BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)

        @JvmStatic fun active() = BlendState(
            //#if MC>=11500
            BlendEquation.fromId(GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB)) ?: BlendEquation.ADD,
            //#else
            //$$ BlendEquation.fromId(GL11.glGetInteger(GL14.GL_BLEND_EQUATION)) ?: BlendEquation.ADD,
            //#endif
            BlendFactor.fromId(GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB)) ?: BlendFactor.ONE,
            BlendFactor.fromId(GL11.glGetInteger(GL14.GL_BLEND_DST_RGB)) ?: BlendFactor.ZERO,
            BlendFactor.fromId(GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA)) ?: BlendFactor.ONE,
            BlendFactor.fromId(GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)) ?: BlendFactor.ZERO,
            GL11.glGetBoolean(GL11.GL_BLEND)
        )
    }

    val separate = srcRgb != srcAlpha || dstRgb != dstAlpha

    //#if MC>=11700
    private inner class VanillaBlendState : GlBlendState {
        constructor() : super()
        constructor(
            srcRgb: Int,
            dstRgb: Int,
            func: Int
        ) : super(srcRgb, dstRgb, func)
        constructor(
            srcRgb: Int,
            dstRgb: Int,
            srcAlpha: Int,
            dstAlpha: Int,
            func: Int
        ) : super(srcRgb, dstRgb, srcAlpha, dstAlpha, func)

        override fun enable() {
            super.enable()
            this@BlendState.apply()
        }
    }

    val vanilla: GlBlendState = if (enabled) {
        if (separate) VanillaBlendState(srcRgb.value, dstRgb.value, srcAlpha.value, dstAlpha.value, equation.value)
        else VanillaBlendState(srcRgb.value, dstRgb.value, equation.value)
    } else {
        VanillaBlendState()
    }

    fun activate() = vanilla.enable()
    //#else
    //$$ fun activate() = apply()
    //#endif

    private fun apply() {
        MultiGlStateManager.toggleBlend(enabled)
        MultiGlStateManager.blendEquation(equation.value)
        MultiGlStateManager.blendFuncSeparate(srcRgb.value, dstRgb.value, srcAlpha.value, dstAlpha.value)
    }

    enum class BlendEquation(
        internal val mcStr: String,
        internal val value: Int
    ) {
        ADD("add", GL14.GL_FUNC_ADD),
        SUBTRACT("subtract", GL14.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT("reverse_subtract", GL14.GL_FUNC_REVERSE_SUBTRACT),
        MIN("min", GL14.GL_MIN),
        MAX("max", GL14.GL_MAX);

        companion object {
            private val byGlId = values().associateBy { it.value }
            @JvmStatic fun fromId(id: Int) = byGlId[id]
        }
    }

    enum class BlendFactor(
        internal val mcStr: String,
        internal val value: Int
    ) {
        ZERO("0", GL11.GL_ZERO),
        ONE("1", GL11.GL_ONE),
        SRC_COLOR("srccolor", GL11.GL_SRC_COLOR),
        ONE_MINUS_SRC_COLOR("1-srccolor", GL11.GL_ONE_MINUS_SRC_COLOR),
        DST_COLOR("dstcolor", GL11.GL_DST_COLOR),
        ONE_MINUS_DST_COLOR("1-dstcolor", GL11.GL_ONE_MINUS_DST_COLOR),
        SRC_ALPHA("srcalpha", GL11.GL_SRC_ALPHA),
        ONE_MINUS_SRC_ALPHA("1-srcalpha", GL11.GL_ONE_MINUS_SRC_ALPHA),
        DST_ALPHA("dstalpha", GL11.GL_DST_ALPHA),
        ONE_MINUS_DST_ALPHA("1-dstalpha", GL11.GL_ONE_MINUS_DST_ALPHA);

        companion object {
            private val byGlId = values().associateBy { it.value }
            @JvmStatic fun fromId(id: Int) = byGlId[id]
        }
    }
}
