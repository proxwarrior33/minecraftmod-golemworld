package com.solonarv.golemworld.golem;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.solonarv.golemworld.util.BlockWithMeta;

/**
 * 
 * @author Solonarv
 * 
 */
public class GolemRegistry {
    protected static ArrayList<GolemRegistration> entries = new ArrayList<GolemRegistration>();

    public static void registerGolem(GolemFactory factory,
            BlockWithMeta upperBody, BlockWithMeta lowerBody,
            BlockWithMeta shoulders, BlockWithMeta arms, BlockWithMeta legs) {
        entries.add(new GolemRegistration(factory, upperBody, lowerBody,
                shoulders, arms, legs));
    }

    public static void registerGolem(GolemFactory factory, Block upperBody,
            Block lowerBody, Block shoulders, Block arms, Block legs) {
        entries.add(new GolemRegistration(factory, upperBody, lowerBody,
                shoulders, arms, legs));
    }

    public static void registerGolem(GolemFactory factory, Block mat,
            GolemShapes shape) {
        switch (shape) {
            case DEFAULT:
                registerGolem(factory, mat, mat, null, mat, null);
                break;
            case FULL:
                registerGolem(factory, mat, mat, mat, mat, mat);
                break;
            case PILLAR:
                registerGolem(factory, mat, mat, null, null, null);
                break;
            default:
                return;

        }
    }

    public static GolemFactory findMatch(World world, int x, int y, int z) {
        for (GolemRegistration gr : entries) {
            if (gr.checkAt(world, x, y, z, true)) {
                return gr.factory;
            }
        }
        return null;
    }

    public static EntityCustomGolem trySpawn(World world, int x, int y, int z) {
        GolemFactory f = findMatch(world, x, y, z);
        if (f != null) {
            return f.make(world, x, y, z);
        }
        return null;
    }
}