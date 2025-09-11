package com.herdlicka.igneousmachines.block.entity;

import com.herdlicka.igneousmachines.IgneousMachinesMod;
import com.herdlicka.igneousmachines.block.IgneousCrafterBlock;
import com.herdlicka.igneousmachines.inventory.ImplementedInventory;
import com.herdlicka.igneousmachines.screen.IgneousCrafterScreenHandler;
import com.herdlicka.igneousmachines.screen.NothingScreenHandler;
import com.herdlicka.igneousmachines.util.ItemStackUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IgneousCrafterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, RecipeInputInventory, SidedInventory, RecipeUnlocker, RecipeInputProvider {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(29, ItemStack.EMPTY);
    private final CraftingInventory craftingInventory;

    private static final int[] TOP_SLOTS = new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
    private static final int[] BOTTOM_SLOTS = new int[]{10};
    private static final int[] SIDE_SLOTS = new int[]{9};
    private static final int[] OUTPUT_SLOTS = new int[]{9,10};

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int CRAFT_TIME_PROPERTY_INDEX = 2;
    public static final int CRAFT_TIME_TOTAL_PROPERTY_INDEX = 3;
    public static final int PROPERTY_COUNT = 4;
    public static final int DEFAULT_CRAFT_TIME = 80;
    public static final float FUEL_MULTIPLIER = 0.5f;
    int burnTime;
    int fuelTime;
    int craftTime;
    int craftTimeTotal = DEFAULT_CRAFT_TIME;

    Recipe recipe = null;
    ItemStack recipeOutput = ItemStack.EMPTY;

    boolean recipeInitialized = false;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case BURN_TIME_PROPERTY_INDEX: {
                    return burnTime;
                }
                case FUEL_TIME_PROPERTY_INDEX: {
                    return fuelTime;
                }
                case CRAFT_TIME_PROPERTY_INDEX: {
                    return craftTime;
                }
                case CRAFT_TIME_TOTAL_PROPERTY_INDEX: {
                    return craftTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case BURN_TIME_PROPERTY_INDEX: {
                    burnTime = value;
                    break;
                }
                case FUEL_TIME_PROPERTY_INDEX: {
                    fuelTime = value;
                    break;
                }
                case CRAFT_TIME_PROPERTY_INDEX: {
                    craftTime = value;
                    break;
                }
                case CRAFT_TIME_TOTAL_PROPERTY_INDEX: {
                    craftTimeTotal = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return PROPERTY_COUNT;
        }
    };
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private final RecipeManager.MatchGetter<RecipeInputInventory, ? extends Recipe<RecipeInputInventory>> matchGetter;

    public IgneousCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(IgneousMachinesMod.IGNEOUS_CRAFTER_BLOCK_ENTITY, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.CRAFTING);
        this.craftingInventory = new CraftingInventory(new NothingScreenHandler(), 3, 3);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return (int) (AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0) * FUEL_MULTIPLIER);
    }

    private static int getCraftTime() {
        return DEFAULT_CRAFT_TIME;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new IgneousCrafterScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        // for 1.19+
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
        // for earlier versions
        // return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.craftTime = nbt.getShort("CraftTime");
        this.craftTimeTotal = nbt.getShort("CraftTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(9));
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("BurnTime", (short) this.burnTime);
        nbt.putShort("CraftTime", (short) this.craftTime);
        nbt.putShort("CraftTimeTotal", (short) this.craftTimeTotal);
        Inventories.writeNbt(nbt, this.inventory);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), (int) count));
        nbt.put("RecipesUsed", nbtCompound);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IgneousCrafterBlockEntity blockEntity) {
        boolean hasFuel;
        boolean wasBurning = blockEntity.isBurning();
        boolean stateChanged = false;

        int blockEntity_stack_size=blockEntity.getMaxCountPerStack();

        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
            //if this cause it to unlit, it will be detected at bottom of tick function
            //CTRL+F: wasBurning
        }

        ItemStack fuelStack = blockEntity.inventory.get(9);
        if (!blockEntity.recipeInitialized) {
            blockEntity.inputSlotsChanged();
        }

        boolean canCraft=canAcceptRecipeOutput(world.getRegistryManager(), blockEntity.recipe, blockEntity.inventory, blockEntity_stack_size);
        //refuel if it can process but out of fuel
        if( canCraft && !blockEntity.isBurning() && !fuelStack.isEmpty() && blockEntity.getFuelTime(fuelStack)> 0 )
        {
            stateChanged = true;
            blockEntity.fuelTime = blockEntity.burnTime = blockEntity.getFuelTime(fuelStack);
            Item item_fuel = fuelStack.getItem();
            Item item_remainder=item_fuel.getRecipeRemainder();
            fuelStack.decrement(1);
            if( item_remainder != null )
            {
                insertOrDrop(world,pos,blockEntity.inventory,blockEntity_stack_size,new ItemStack(item_remainder),SIDE_SLOTS);
            }
        }

        //uncraft if unfueld
        if( !blockEntity.isBurning() ) { blockEntity.craftTime = MathHelper.clamp(blockEntity.craftTime - 2, 0, blockEntity.craftTimeTotal); }
        //processing crafting
        if( canCraft && blockEntity.isBurning())
        {
            ++blockEntity.craftTime;
            if (blockEntity.craftTime == blockEntity.craftTimeTotal)
            {
                stateChanged = true;
                blockEntity.craftTime = 0;
                blockEntity.craftTimeTotal = getCraftTime();
                craftRecipe(world.getRegistryManager(), blockEntity.recipe, blockEntity.inventory, blockEntity_stack_size);
                blockEntity.setLastRecipe(blockEntity.recipe);
                List<ItemStack> remainder_list=blockEntity.recipe.getRemainder(blockEntity.craftingInventory);
                for(ItemStack remainderStack: remainder_list)
                {
                    insertOrDrop(world,pos,blockEntity.inventory,blockEntity_stack_size,remainderStack,TOP_SLOTS);
                }
            }
        }
       
        
        if (wasBurning != blockEntity.isBurning()) {
            stateChanged = true;
            state = state.with(IgneousCrafterBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (stateChanged) {
            markDirty(world, pos, state);
        }
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    private static boolean canAcceptRecipeOutput(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int blockEntity_stack_size) {
        if (recipe == null) {
            return false;
        }

        ItemStack resultStack = recipe.getOutput(registryManager);
        if (resultStack.isEmpty()) {
            return false;
        }

        //inverse the logic, now it take each ingredient slot, then check every grid slot
        boolean whole_checked=false;
        boolean[] checked=new boolean[9];
        for(int slot_index : TOP_SLOTS)
        {
            ItemStack ingredient_slot=slots.get(slot_index);
            if(ingredient_slot.isEmpty()) { continue; }

            int stack_state=ingredient_slot.getCount();
            for(int i=0;i<9;i++)
            {
                //if ingredient slot is now empty, skip to search next ingredient slot
                if(stack_state == 0) { break; }
                //if grid slot is checked, skip
                if(checked[i]) { continue; }
                //if the slot is empty, check
                ItemStack grid_slot=slots.get(i);
                if( grid_slot.isEmpty() )
                {
                    checked[i]=true;
                    continue;
                }
                //now both slot have item, now check if we can still affort it;
                if( ItemStack.canCombine(grid_slot,ingredient_slot) )
                {
                    stack_state--;
                    checked[i]=true;
                    continue;
                }
            }
            //finally check if grid is done
            whole_checked=true;
            for(int i=0;i<9;i++){ whole_checked = whole_checked && checked[i]; }
            if(whole_checked){ break; }
        }
        //now we know if we have enough ingredient
        if( whole_checked == false ){ return false; }
        
        ItemStack outputSlotStack = slots.get(10);
        if (outputSlotStack.isEmpty()) { return true; }
        if (!ItemStack.canCombine(outputSlotStack, resultStack)) { return false; }

        int combined_count=resultStack.getCount() + outputSlotStack.getCount();
        int stack_size=Math.min(outputSlotStack.getItem().getMaxCount(),blockEntity_stack_size);
        if (combined_count <= stack_size) { return true; }

        return false;
    }

    private static boolean craftRecipe(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int blockEntity_stack_size) {
        if (recipe == null || !canAcceptRecipeOutput(registryManager, recipe, slots, blockEntity_stack_size)) {
            return false;
        }
        boolean canCraft=canAcceptRecipeOutput(registryManager, recipe, slots, blockEntity_stack_size);
        if( canCraft==false )
        {
            return false;
        }
        
        boolean[] checked=new boolean[9];
        for(int slot_index : TOP_SLOTS)
        {
            ItemStack ingredient_slot=slots.get(slot_index);
            for(int i=0;i<9;i++)
            {
                //if ingredient slot is now empty, skip to search next ingredient slot
                if( ingredient_slot.isEmpty() ) { break; }
                //if grid slot is checked, skip
                if(checked[i]) { continue; }
                //if the slot is empty, check
                ItemStack grid_slot=slots.get(i);
                if( grid_slot.isEmpty() )
                {
                    checked[i]=true;
                    continue;
                }
                //now both slot have item, now check if we can still affort it;
                if( ItemStack.canCombine(grid_slot,ingredient_slot) )
                {
                    ingredient_slot.decrement(1);
                    checked[i]=true;
                    continue;
                }
            }
            //finally check if grid is done
            boolean whole_checked=true;
            for(int i=0;i<9;i++){ whole_checked = whole_checked && checked[i]; }
            if(whole_checked){ break; }
        }

        ItemStack resultStack = recipe.getOutput(registryManager);
        ItemStack outputSlotStack = slots.get(10);
        if (outputSlotStack.isEmpty()) {
            slots.set(10, resultStack.copy());
        } else {
            outputSlotStack.increment(resultStack.getCount());
        }
        return true;
    }

    private static void insertOrDrop(World world, BlockPos pos, DefaultedList<ItemStack> slots,int blockEntity_stack_size, ItemStack item_to_insert, int[] slot_range)
    {
        for(int slot_index : slot_range)
        {
            ItemStack try_stack = slots.get(slot_index);
            if( try_stack.isEmpty() ) { continue; }
            if( ItemStack.canCombine(try_stack,item_to_insert) )
            {
                int combined_count=item_to_insert.getCount() + try_stack.getCount();
                int stack_size=Math.min(try_stack.getItem().getMaxCount(),blockEntity_stack_size);
                if( combined_count <= stack_size )
                {
                    try_stack.setCount(combined_count);
                    return;
                }
                try_stack.setCount(stack_size);
                item_to_insert.setCount(combined_count-stack_size);
            }
        }
        for(int slot_index : slot_range)
        {
            ItemStack try_stack = slots.get(slot_index);
            if( try_stack.isEmpty() )
            {
                slots.set(slot_index, item_to_insert.copy());
                return;
            }
        }
        //gonna do some server works
        if (world.isClient()) {return;}
        Direction dir = ( (Direction) new BlockPointerImpl((ServerWorld)world,pos).getBlockState().get(IgneousCrafterBlock.FACING) ).getOpposite();
        double sx= pos.getX() + dir.getOffsetX() + 0.5 ;
        double sy= pos.getY() + dir.getOffsetY() + 0.5 ;
        double sz= pos.getZ() + dir.getOffsetZ() + 0.5 ;
        ItemEntity item_to_drop=new ItemEntity(world,sx,sy,sz,item_to_insert);
        world.spawnEntity(item_to_drop);
        return;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return OUTPUT_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot == 9) {
            return ItemStackUtils.isFuel(stack);
        } else if (slot > 10 && slot <= 28) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 10) { return true; }

        boolean is_outputable_item=stack.isIn( TagKey.of(RegistryKeys.ITEM,new Identifier("igneous-machines","fuel_remainder")) );
        if ( slot == 9 && is_outputable_item ) { return true; }

        return false;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (int i = 0; i < 9; i++) {
            finder.addUnenchantedInput(inventory.get(i));
        }
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<ItemStack> getInputStacks() {
        return inventory.subList(0, 9);
    }

    @Override
    public void inputSlotsChanged() {
        for (int i = 0; i < 9; i++) {
            craftingInventory.setStack(i, inventory.get(i));
        }
        recipe = matchGetter.getFirstMatch(craftingInventory, world).orElse(null);
        if (recipe == null) {
            recipeOutput = ItemStack.EMPTY;
        } else {
            recipeOutput = recipe.getOutput(world.getRegistryManager());
        }
        this.recipeInitialized = true;

        if (world.isClient()) {
            return;
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeItemStack(this.recipeOutput);
            ServerPlayNetworking.send(player, IgneousMachinesMod.RECIPE_CHANGE_PACKET_ID, buf);
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeItemStack(this.recipeOutput);
    }
}
