package ca.skynetcloud.cobblescheduler.commands;

import ca.skynetcloud.cobblescheduler.CobbleScheduler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class SchedulerCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("cobblescheduler")
                        .requires(source -> source.hasPermissionLevel(2)) // OP only
                        .then(literal("reload")
                                .executes(SchedulerCommands::reloadConfig)
                        )
        );
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        CobbleScheduler.reloadConfig();
        context.getSource().sendFeedback(
                () -> Text.literal("§aCobbleScheduler config reloaded!"),
                true
        );
        return 1;
    }
}