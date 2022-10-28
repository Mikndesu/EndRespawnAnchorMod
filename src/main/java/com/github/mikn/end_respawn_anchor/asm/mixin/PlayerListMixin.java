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

package com.github.mikn.end_respawn_anchor.asm.mixin;

import com.github.mikn.end_respawn_anchor.EndRespawnAnchor;
import com.github.mikn.end_respawn_anchor.block.EndRespawnAnchorBlock;
import com.github.mikn.end_respawn_anchor.util.OtherDimensionSpawnPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Overwrite
    public ServerPlayer respawn(ServerPlayer p_11237_, boolean p_11238_) {
        PlayerList playerList = (PlayerList) (Object) this;
        boolean isDead = p_11237_.isDeadOrDying();
        boolean isAlive = !isDead;
        boolean isDifferentWithDefault = false;
        ResourceKey<Level> dimension = p_11237_.getLevel().dimension();
        OtherDimensionSpawnPosition spawnPosition = null;

        ServerLevel serverlevel = playerList.server.getLevel(p_11237_.getRespawnDimension());
        Optional<Vec3> optional;
        float f = p_11237_.getRespawnAngle();
        boolean flag = p_11237_.isRespawnForced();
        playerList.players.remove(p_11237_);
        p_11237_.getLevel().removePlayerImmediately(p_11237_, Entity.RemovalReason.DISCARDED);
        BlockPos blockpos = p_11237_.getRespawnPosition();
        ServerLevel serverlevel1;

        //
        if (serverlevel == null || blockpos == null) {
            optional = Optional.empty();
            serverlevel1 = playerList.server.overworld();
        } else if ((isDead && dimension == Level.END && serverlevel.dimension() == Level.END) || (isDead && dimension == Level.END && serverlevel.dimension() != Level.END) || (isDead && dimension != Level.END && serverlevel.dimension() != Level.END))  {
            // At this line, I expect that the player is died in the dimension where his respawn position is set.
            optional = Player.findRespawnPositionAndUseSpawnBlock(serverlevel, blockpos, f, flag, p_11238_);
            serverlevel1 = optional.isPresent() ? serverlevel : playerList.server.overworld();
        } else if ((isAlive && dimension == Level.END && serverlevel.dimension() == Level.END && EndRespawnAnchor.spawnPositions.entrySet().stream().anyMatch(entry -> entry.getKey().equals(p_11237_.getUUID()))) || (isDead && dimension != Level.END && serverlevel.dimension() == Level.END && EndRespawnAnchor.spawnPositions.entrySet().stream().anyMatch(entry -> entry.getKey().equals(p_11237_.getUUID())))) {
            // At this line, I expect that the player uses end portal and respawn positions is set for him, or he died in the dimension other than end and his respawn position is in the end.
            OtherDimensionSpawnPosition position = EndRespawnAnchor.spawnPositions.get(p_11237_.getUUID());
            spawnPosition = new OtherDimensionSpawnPosition(p_11237_.getRespawnDimension(), p_11237_.getRespawnPosition(), p_11237_.getRespawnAngle());
            optional = EndRespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, playerList.server.getLevel(position.dimension()), position.blockPos());
            serverlevel1 = playerList.server.getLevel(position.dimension());
            isDifferentWithDefault = true;
        } else if(isAlive && dimension == Level.END && serverlevel.dimension() != Level.END) {
            // At this line, I expect that the player uses end portal and dimension where is his respawn position is not the end
            if (serverlevel.dimension() == Level.NETHER) {
                p_11237_.sendMessage(new TextComponent("You spawn in Nether because you used RespawnAnchor"), p_11237_.getUUID());
            }
            optional = Player.findRespawnPositionAndUseSpawnBlock(serverlevel, blockpos, f, flag, p_11238_);
            serverlevel1 = optional.isPresent() ? serverlevel : playerList.server.overworld();
        } else {
            optional = Optional.empty();
            serverlevel1 = playerList.server.overworld();
        }
        //

        ServerPlayer serverplayer = new ServerPlayer(playerList.server, serverlevel1, p_11237_.getGameProfile());
        serverplayer.connection = p_11237_.connection;
        serverplayer.restoreFrom(p_11237_, p_11238_);
        serverplayer.setId(p_11237_.getId());
        serverplayer.setMainArm(p_11237_.getMainArm());

        for (String s : p_11237_.getTags()) {
            serverplayer.addTag(s);
        }

        boolean flag2 = false;
        if (optional.isPresent()) {
            BlockState blockstate = serverlevel1.getBlockState(blockpos);
            boolean flag1 = blockstate.is(Blocks.RESPAWN_ANCHOR);
            Vec3 vec3 = optional.get();
            float f1;
            if (!blockstate.is(BlockTags.BEDS) && !flag1) {
                f1 = f;
            } else {
                Vec3 vec31 = Vec3.atBottomCenterOf(blockpos).subtract(vec3).normalize();
                f1 = (float) Mth.wrapDegrees(Mth.atan2(vec31.z, vec31.x) * (double) (180F / (float) Math.PI) - 90.0D);
            }

            serverplayer.moveTo(vec3.x, vec3.y, vec3.z, f1, 0.0F);

            //
            if(isDifferentWithDefault) {
                serverplayer.setRespawnPosition(spawnPosition.dimension(), spawnPosition.blockPos(), spawnPosition.respawnAngle(), flag, false);
            } else {
                serverplayer.setRespawnPosition(serverlevel1.dimension(), blockpos, f, flag, false);
            }
            //

            flag2 = !p_11238_ && flag1;
        } else if (blockpos != null) {
            serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        while (!serverlevel1.noCollision(serverplayer) && serverplayer.getY() < (double) serverlevel1.getMaxBuildHeight()) {
            serverplayer.setPos(serverplayer.getX(), serverplayer.getY() + 1.0D, serverplayer.getZ());
        }

        LevelData leveldata = serverplayer.level.getLevelData();
        serverplayer.connection.send(new ClientboundRespawnPacket(serverplayer.level.dimensionTypeRegistration(), serverplayer.level.dimension(), BiomeManager.obfuscateSeed(serverplayer.getLevel().getSeed()), serverplayer.gameMode.getGameModeForPlayer(), serverplayer.gameMode.getPreviousGameModeForPlayer(), serverplayer.getLevel().isDebug(), serverplayer.getLevel().isFlat(), p_11238_));
        serverplayer.connection.teleport(serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), serverplayer.getYRot(), serverplayer.getXRot());
        serverplayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverlevel1.getSharedSpawnPos(), serverlevel1.getSharedSpawnAngle()));
        serverplayer.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
        serverplayer.connection.send(new ClientboundSetExperiencePacket(serverplayer.experienceProgress, serverplayer.totalExperience, serverplayer.experienceLevel));
        playerList.sendLevelInfo(serverplayer, serverlevel1);
        playerList.sendPlayerPermissionLevel(serverplayer);
        serverlevel1.addRespawnedPlayer(serverplayer);
        playerList.addPlayer(serverplayer);
        playerList.playersByUUID.put(serverplayer.getUUID(), serverplayer);
        serverplayer.initInventoryMenu();
        serverplayer.setHealth(serverplayer.getHealth());
        net.minecraftforge.event.ForgeEventFactory.firePlayerRespawnEvent(serverplayer, p_11238_);
        if (flag2) {
            serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 1.0F, 1.0F));
        }
        return serverplayer;
    }
}
