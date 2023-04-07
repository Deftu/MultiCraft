package xyz.deftu.multi

//#if MC>=11400
import net.minecraft.client.util.GlfwUtil
//#endif

import net.minecraft.client.MinecraftClient

object MultiClient {
    @JvmStatic
    val isRunningOnMac: Boolean
        get() = MinecraftClient.IS_SYSTEM_MAC
    @JvmStatic
    val isRunningOnMainThread: Boolean
        //#if MC>=11500
        get() = getInstance().isOnThread
        //#else
        //$$ get() = getInstance().isCallingFromMinecraftThread()
        //#endif

    @JvmStatic
    fun getInstance() = MinecraftClient.getInstance()
    @JvmStatic
    fun getWorld() = getInstance().world
    @JvmStatic
    fun getServer() = getInstance().server
    @JvmStatic
    fun getPlayer() = getInstance().player
    @JvmStatic
    fun getHud() = getInstance().inGameHud
    @JvmStatic
    fun getChat() = getHud().chatHud
    @JvmStatic
    fun getCurrentServerInfo() = getInstance().currentServerEntry
    @JvmStatic
    fun getNetworkHandler() = getInstance().networkHandler
    @JvmStatic
    fun getSoundManager() = getInstance().soundManager
    @JvmStatic
    fun getFontRenderer() = getInstance().textRenderer
    @JvmStatic
    fun getOptions() = getInstance().options
    @JvmStatic
    fun getCurrentScreen() = getInstance().currentScreen
    @JvmStatic
    fun getTextureManager() = MultiTextureManager.INSTANCE

    @JvmStatic fun execute(runnable: () -> Unit) {
        //#if MC>=11502
        getInstance().execute(runnable)
        //#else
        //$$ getInstance().addScheduledTask(runnable::invoke)
        //#endif
    }

    @JvmStatic fun execute(runnable: Runnable) = execute(runnable::run)

    @JvmStatic
    fun getTime(): Long {
        //#if MC>=11400
        return (GlfwUtil.getTime() * 1000).toLong()
        //#else
        //#if MC>=11400
        //$$ return MinecraftClient.getSystemTime()
        //#else
        //$$ return Minecraft.getSystemTime()
        //#endif
        //#endif
    }

    object Multiplayer {
        @JvmStatic fun getServerBrand() = getPlayer()?.serverBrand
        @JvmStatic fun getCurrentServerAddress() = getCurrentServerInfo()?.address

        @JvmStatic
        fun isMultiplayerEnabled() =
            //#if MC>=11902
            getInstance().isMultiplayerEnabled
        //#else
        //$$ true // TODO - Find a way to fetch this value in earlier versions
        //#endif

        @JvmStatic
        fun isMultiplayerBanned() =
            //#if MC>=11902
            getInstance().isMultiplayerBanned
        //#else
        //$$ false // TODO - Find a way to fetch this value in earlier versions
        //#endif

        @JvmStatic
        fun isInMultiplayer() = getWorld() != null && getServer() != null && isMultiplayerEnabled() && !isMultiplayerBanned() && run {
            if (getInstance().isInSingleplayer) return@run false

            val serverInfo = getInstance().currentServerEntry
            serverInfo?.address != null
        }
    }
}
