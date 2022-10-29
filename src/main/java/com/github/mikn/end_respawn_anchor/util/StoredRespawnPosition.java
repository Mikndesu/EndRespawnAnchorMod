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

package com.github.mikn.end_respawn_anchor.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record StoredRespawnPosition(ResourceKey<Level> dimension, BlockPos blockPos, float respawnAngle) {
    @Override
    public String toString() {
        return " BlockPos: " + blockPos + " in Dimension: " + provideDimension(dimension);
    }

    private String provideDimension(ResourceKey<Level> level) {
        if (level == Level.OVERWORLD) {
            return "the overworld";
        } else if (level == Level.NETHER) {
            return "the nether";
        } else if (level == Level.END) {
            return "the end";
        }
        return "Other dimension";
    }
}
