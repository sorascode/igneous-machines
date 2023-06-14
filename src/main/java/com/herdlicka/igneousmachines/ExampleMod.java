package com.herdlicka.igneousmachines;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ExampleMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.

    public static final Block IGNEOUS_PLACER_BLOCK;
    public static final BlockItem IGNEOUS_PLACER_BLOCK_ITEM;
    public static final BlockEntityType<IgneousPlacerBlockEntity> IGNEOUS_PLACER_BLOCK_ENTITY;
    public static final ScreenHandlerType<IgneousPlacerScreenHandler> IGNEOUS_PLACER_SCREEN_HANDLER;

    public static final String MOD_ID = "igneous-machines";
    // a public identifier for multiple parts of our bigger chest
    public static final Identifier IGNEOUS_PLACER = new Identifier(MOD_ID, "igneous_placer");

    static {
        IGNEOUS_PLACER_BLOCK = Registry.register(Registries.BLOCK, IGNEOUS_PLACER, new IgneousPlacerBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)));
        IGNEOUS_PLACER_BLOCK_ITEM = Registry.register(Registries.ITEM, IGNEOUS_PLACER, new BlockItem(IGNEOUS_PLACER_BLOCK, new Item.Settings()));

        //The parameter of build at the very end is always null, do not worry about it
        IGNEOUS_PLACER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, IGNEOUS_PLACER, FabricBlockEntityTypeBuilder.create(IgneousPlacerBlockEntity::new, IGNEOUS_PLACER_BLOCK).build(null));
        IGNEOUS_PLACER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(IGNEOUS_PLACER, IgneousPlacerScreenHandler::new);
    }

    @Override
    public void onInitialize() {

    }
}