package ca.skynetcloud.cobblescheduler.commands;

import ca.skynetcloud.cobblescheduler.CobbleScheduler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class SchedulerCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("CobbleScheduler")
                        .requires(source -> source.hasPermission(2)).requires(Permissions.require("cobblescheduler.reload"))
                        .then(Commands.literal("reload")
                                .executes(SchedulerCommands::reloadConfig))
        );
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CobbleScheduler.reloadConfig();
        context.getSource().sendSuccess(() -> Component.literal("CobbleScheduler config reloaded!"), true);
        return Command.SINGLE_SUCCESS;
    }
}
