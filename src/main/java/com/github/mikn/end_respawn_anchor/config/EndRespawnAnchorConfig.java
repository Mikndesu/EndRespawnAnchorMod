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

package com.github.mikn.end_respawn_anchor.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class EndRespawnAnchorConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> isExplode;
    public static final ForgeConfigSpec.ConfigValue<Boolean> shouldChangeSpawnInfo;

    static {
        BUILDER.push("Config for EndRespawnAnchor Mod");
        isExplode = BUILDER.comment("This defines whether it explodes or not in dimensions other than the End.")
                .define("isExplode", false);
        shouldChangeSpawnInfo = BUILDER.comment(
                "This defines whether respawn position should be changed in particular situation. \n See description for more details.")
                .define("shouldChangeSpawnInfo", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
