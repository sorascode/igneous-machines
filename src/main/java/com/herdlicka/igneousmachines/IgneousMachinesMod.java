package com.herdlicka.igneousmachines;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class IgneousMachinesMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.

    public static final Block IGNEOUS_CRAFTER_BLOCK;
    public static final BlockItem IGNEOUS_CRAFTER_BLOCK_ITEM;
    public static final BlockEntityType<IgneousCrafterBlockEntity> IGNEOUS_CRAFTER_BLOCK_ENTITY;
    public static final ScreenHandlerType<IgneousCrafterScreenHandler> IGNEOUS_CRAFTER_SCREEN_HANDLER;

    public static final Block DEPOSITER_BLOCK;
    public static final BlockItem DEPOSITER_BLOCK_ITEM;
    public static final BlockEntityType<DepositerBlockEntity> DEPOSITER_BLOCK_ENTITY;
    public static final ScreenHandlerType<DepositerScreenHandler> DEPOSITER_SCREEN_HANDLER;

    public static final String MOD_ID = "igneous-machines";
    // a public identifier for multiple parts of our bigger chest
    public static final Identifier IGNEOUS_CRAFTER = new Identifier(MOD_ID, "igneous_crafter");
    public static final Identifier DEPOSITER = new Identifier(MOD_ID, "depositer");

    static {
        IGNEOUS_CRAFTER_BLOCK = Registry.register(Registries.BLOCK, IGNEOUS_CRAFTER, new IgneousCrafterBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)));
        IGNEOUS_CRAFTER_BLOCK_ITEM = Registry.register(Registries.ITEM, IGNEOUS_CRAFTER, new BlockItem(IGNEOUS_CRAFTER_BLOCK, new FabricItemSettings()));
        IGNEOUS_CRAFTER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, IGNEOUS_CRAFTER, FabricBlockEntityTypeBuilder.create(IgneousCrafterBlockEntity::new, IGNEOUS_CRAFTER_BLOCK).build(null));
        IGNEOUS_CRAFTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(IGNEOUS_CRAFTER, IgneousCrafterScreenHandler::new);

        DEPOSITER_BLOCK = Registry.register(Registries.BLOCK, DEPOSITER, new DepositerBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)));
        DEPOSITER_BLOCK_ITEM = Registry.register(Registries.ITEM, DEPOSITER, new BlockItem(DEPOSITER_BLOCK, new FabricItemSettings()));
        DEPOSITER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, DEPOSITER, FabricBlockEntityTypeBuilder.create(DepositerBlockEntity::new, DEPOSITER_BLOCK).build(null));
        DEPOSITER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(DEPOSITER, DepositerScreenHandler::new);
    }

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(IGNEOUS_CRAFTER_BLOCK_ITEM);
            content.add(DEPOSITER_BLOCK_ITEM);
        });
    }
}