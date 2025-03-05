package ca.skynetcloud.cobblescheduler.commands;

import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class DebugMode {

    private static boolean debugEnabled = false;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("toggledebug")
                        .requires(source -> source.hasPermission(2))
                        .requires(Permissions.require("cobblescheduler.toggledebug"))
                        .executes(DebugMode::toggleDebug)
        );
    }

    private static int toggleDebug(CommandContext<CommandSourceStack> context) {
        debugEnabled = !debugEnabled;
        setDebugMode(debugEnabled);
        context.getSource().sendSuccess(() -> Component.literal("Debug mode " + (debugEnabled ? "enabled" : "disabled")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void setDebugMode(boolean enabled) {
        try {
            java.lang.reflect.Field debugField = DateUtils.class.getDeclaredField("Debug");
            debugField.setAccessible(true);
            debugField.set(null, enabled);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}


