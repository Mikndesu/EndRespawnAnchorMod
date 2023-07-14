/*
 Copyright (c) 2022 Mikndesu

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mikn.end_respawn_anchor.command;

import com.github.mikn.end_respawn_anchor.EndRespawnAnchor;
import com.github.mikn.end_respawn_anchor.util.StoredRespawnPosition;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class RespawnPositionCheckCommand {
    public static void register(CommandDispatcher<CommandSourceStack> p_136559_) {
        p_136559_.register(Commands.literal("respawn_position_check")
                .requires((p_136563_) -> p_136563_.hasPermission(4))
                .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                        .executes((p_136569_) -> PositionCheck(p_136569_.getSource(),
                                GameProfileArgument.getGameProfiles(p_136569_, "targets")))));
    }

    private static int PositionCheck(CommandSourceStack source, Collection<GameProfile> targets) {
        for (var gameProfile : targets) {
            if (EndRespawnAnchor.spawnPositions.entrySet().stream()
                    .anyMatch(entry -> entry.getKey().equals(gameProfile.getId()))) {
                StoredRespawnPosition data = EndRespawnAnchor.spawnPositions.get(gameProfile.getId());
                BlockPos pos = data.blockPos();
                source.sendSuccess(() -> {
                    return Component.literal(gameProfile.getName() + " will spawn at " + pos.getX()
                            + ", " + pos.getY() + ", " + pos.getZ() + " in " + data.dimension());
                }, true);
            }
        }
        return 1;
    }
}
